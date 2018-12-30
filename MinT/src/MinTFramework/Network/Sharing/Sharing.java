/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package MinTFramework.Network.Sharing;

import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.MinT;
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket.CoAPConfig;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.RecvMSG;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.SystemHandler;
import MinTFramework.Network.Sharing.node.Node;
import MinTFramework.Network.Sharing.routingprotocol.RoutingProtocol;
import MinTFramework.SystemScheduler.MinTthreadPools;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.storage.ResData;
import MinTFramework.storage.Resource;
import MinTFramework.storage.ResourceStorage;
import MinTFramework.storage.datamap.Information;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author soobin
 */
public class Sharing {
    private MinT frame = null;
    private ResourceStorage resStorage = null;
    private SystemScheduler sysSched = null;
    private NetworkManager networkManager = null;
    private RoutingProtocol routingprotocol = null;
    private SharingHandler shandle = null;
    private SystemHandler syshandle = null;
    private static String SHARING_TP = "sharing_threadpool";
    private boolean isActivated = false;
    public Sharing(){
        frame = MinT.getInstance();
        resStorage = frame.getResStorage();
        sysSched = frame.getSystemScheduler();
    }
   
    /**
     * Initialization Sharing approach 
     * @param aThis 
     */
    public void init(NetworkManager aThis) {
        networkManager = aThis;
        routingprotocol = networkManager.getRoutingProtocol();
        shandle = new SharingHandler(this);
//        initScheduler();
    }
    
//    public void initScheduler(){
//        if(sysSched != null){
//            sysSched.registerThreadPool(SHARING_TP, new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS
//                    ,new ArrayBlockingQueue<Runnable>(MinTConfig.NETWORK_RECEIVE_WAITING_QUEUE)));
//            sysSched.registerThreadPool(SHARING_TP, Executors.newSingleThreadScheduledExecutor());
//        }
//    }
    
    /**
     * execute Response operation
     * @param sr 
     */
    public void executeResponse(SharingResponse sr){
        sr.preRun();
        ScheduledFuture<?> f = sysSched.submitSchedule(MinTthreadPools.SHARING_HANDLE, sr, CoAPConfig.NON_LIFETIME*1000);
        sr.setScheduleHandler(f);
    }
    
    /**
     * activate sharing operation
     * @param ac 
     */
    public void setActivate(boolean ac){
        System.out.println("Sharing activated");
        isActivated = ac;
    }
    
    /**
     * is sharing protocol activated?
     * @return 
     */
    public boolean isActivated(){
        return isActivated;
    }
    
    /**
     * 
     * @param packet
     * @param receivemsg 
     */
    public void sharingHandle(RecvMSG recvmsg, SystemHandler _syshandle) {
        shandle.startHandle(recvmsg);
        syshandle = _syshandle;
    }
    
    public void sendNetwork(List<ResponseWaiter> reswaiter){
        boolean isMulticast = routingprotocol.isMulticastMode();
        for(ResponseWaiter wt : reswaiter){
            if(isMulticast){
                if (wt.getResourceType().isChildResource() && !wt.getPackets().isEmpty()) {
                    sendtoMemberNode(wt.getPackets().peek(), isMulticast);
                } else if (wt.getResourceType().isHeaderResource() && !wt.getPackets().isEmpty()) {
                    sendtoHeaderNode(wt.getPackets().peek(), isMulticast);
                }
            }else{
                //unicast
                for(SharingPacket sp : wt.getPackets()) {
//                    try {
                        if (wt.getResourceType().isChildResource()) {
                            sendtoMemberNode(sp, isMulticast);
                        } else if (wt.getResourceType().isHeaderResource()) {
                            sendtoHeaderNode(sp, isMulticast);
                        }
//                        Information info = sp.getReceiveMessage().getResourcebyName(Request.MSG_ATTR.Sharing_EX);
//                        System.out.println(sp.getNodeInfo().gettoAddr().getAddress()+", sended ("+info.getResourceString()+")");
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        System.out.println("sleep Exception..");
//                    }
                }
            }
        }
    }
    
    /********************************************************
     * CHILD NODE WORKING
     ********************************************************/
    /**
     * 
     * @param restype
     * @param resOpt
     * @param resHandle
     * @param checkvalue value for Experiment
     */
    public void getResource(DeviceType restype, ResourceOption resOpt, ResponseHandler resHandle, int checkvalue){
        if(resOpt == null)
                resOpt = ResourceOption.LIST;
        
        SendMessage sendmsg = new SendMessage(restype.getDeviceTypeString(),resOpt.toOption());
        
        if(checkvalue != -1)
            sendmsg.AddAttribute(Request.MSG_ATTR.Sharing_EX, checkvalue);
        getResource(sendmsg, resHandle);
    }
    /**
     * Request Resource to Header node from Client
     * @param requestdata
     * @param resHandle 
     */
    private void getResource(SendMessage requestdata, ResponseHandler resHandle){
        if(!isActivated)
            return;
        
        if(routingprotocol.isHeaderNode()){
            //get child node resource -> use other method in here
        }else{
            Node header = routingprotocol.getHeaderNodeofCurrentNode();
            requestdata.AddAttribute(Request.MSG_ATTR.Sharing, SharingMessage.HEADER_REQUEST.getValue());
            frame.REQUEST_GET(header.gettoAddr().setCON(true), requestdata, resHandle);
//            frame.REQUEST_GET_MULTICAST(requestdata, resHandle);
        }
    }
    
    private void sendtoMemberNode(SharingPacket sp, boolean ismulticast){
        if(!isActivated)
            return;
        SendMessage requestdata = new SendMessage(sp.getProperty().getName(), null);
        requestdata.AddAttribute(Request.MSG_ATTR.Sharing, SharingMessage.CLIENT_REQUEST.getValue());
        //for Experiments
        Information exv = sp.getReceiveMessage().getResourcebyName(Request.MSG_ATTR.Sharing_EX);
        if(exv != null)
            requestdata.AddAttribute(Request.MSG_ATTR.Sharing_EX,exv.getResourceInt());
        
        if(ismulticast){
            frame.REQUEST_GET_MULTICAST(requestdata, sp.getResponseHandler());
        }else{
            frame.REQUEST_GET(sp.getNodeInfo().gettoAddr().setCON(true)
                            , requestdata
                            , sp.getResponseHandler());
        }
    }
    
    
    /********************************************************
     * HEADER WORKING
     ********************************************************/
    public List<ResData> getLocalResource(Request recvmsg) {
        return resStorage.getPropertybyResourceType(recvmsg, Resource.StoreCategory.Local);
    }
    
    public void sendtoHeaderNode(SharingPacket sp, boolean isMulticast){
        if(!isActivated)
            return;
        
        if(routingprotocol.isHeaderNode()){
            //fix me average가 안됨
            ResourceOption resOpt = ResourceOption
                    .getResourceOptionbyOpt(sp.getProperty().getResourceData().getResourceString());
            SendMessage requestdata = new SendMessage(sp.getProperty().getDeviceType().getDeviceTypeString(),resOpt.toOption());
            requestdata.AddAttribute(Request.MSG_ATTR.Sharing, SharingMessage.HEADER_REQUEST.getValue());
            
            Information exv = sp.getReceiveMessage().getResourcebyName(Request.MSG_ATTR.Sharing_EX);
            if (exv != null) {
                requestdata.AddAttribute(Request.MSG_ATTR.Sharing_EX, exv.getResourceInt());
            }
            
            if(!isMulticast)
                frame.REQUEST_GET(sp.getNodeInfo().gettoAddr().setCON(true), requestdata, sp.getResponseHandler());
            else
                frame.REQUEST_GET_MULTICAST(requestdata, sp.getResponseHandler());
        }
    }
    
    /**
     * @deprecated 
     * @param requestdata
     * @param resHandle
     * @param header 
     */
    public void getHeaderResource(SendMessage requestdata, ResponseHandler resHandle, Node header){
        if(!isActivated)
            return;
        
        if(routingprotocol.isHeaderNode()){
            requestdata.AddAttribute(Request.MSG_ATTR.Sharing, SharingMessage.HEADER_REQUEST.getValue());
            frame.REQUEST_GET(header.gettoAddr().setCON(true), requestdata, resHandle);
        }
    }
    
    public SystemHandler getSystemHandler(){
        return syshandle;
    }
    
    /**
     * search Nodes had request resource
     * @param resource
     * @return 
     */
    private ArrayList<Node> searchResource(String resource){
        return null;
    }
    
    /**
     * Request resource to other Header
     * 주변 모든 그룹 헤더에게 데이터 요청 후 기다림
     * 받은 데이터를 종합 하여 Child Node에게 전달
     * @param resource 
     */
    private void requestResource(String resource){
        
    }
    
    public static enum RESOURCE_TYPE {
        LOCALRESOURCE,
        CHILDRESOURCE,
        HEADERRESOURCE;
        
        RESOURCE_TYPE(){
        }
        
        public boolean isLocalResource(){return this == LOCALRESOURCE;}
        public boolean isChildResource(){return this == CHILDRESOURCE;}
        public boolean isHeaderResource(){return this == HEADERRESOURCE;}
    }
}
