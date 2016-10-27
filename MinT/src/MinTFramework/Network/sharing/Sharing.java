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
package MinTFramework.Network.sharing;

import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.MinT;
import MinTFramework.MinTConfig;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.storage.ResData;
import MinTFramework.storage.Resource;
import MinTFramework.storage.ResourceStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        initScheduler();
    }
    
    public void initScheduler(){
        if(sysSched != null){
            sysSched.registerThreadPool(SHARING_TP, new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS
                    ,new ArrayBlockingQueue<Runnable>(MinTConfig.NETWORK_RECEIVE_WAITING_QUEUE)));
        }
    }
    
    /**
     * execute Response operation
     * @param sr 
     */
    public void executeResponse(SharingResponse sr){
        sysSched.executeProcess(SHARING_TP, sr);
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
    public void sharingHandle(PacketDatagram packet, ReceiveMessage receivemsg) {
        shandle.receiveHandle(packet, receivemsg);
    }
    
    /********************************************************
     * CHILD NODE WORKING
     ********************************************************/
    public void getResource(DeviceType restype, ResponseHandler resHandle){
        getResource(new SendMessage(restype.getDeviceTypeString(),null), resHandle);
    }
    /**
     * get Resource from Header Node
     * @param requestdata
     * @param resHandle 
     */
    private void getResource(SendMessage requestdata, ResponseHandler resHandle){
        if(!isActivated)
            return;
        
        if(routingprotocol.isHeaderNode()){
            //get child node resource -> use other method in here
        }else{
            Node header = routingprotocol.getHeaderNode();
            requestdata.AddAttribute(Request.MSG_ATTR.Sharing, null);
            frame.REQUEST_GET(header.gettoAddr(), requestdata, resHandle);
        }
    }
    
    
    /********************************************************
     * HEADER WORKING
     ********************************************************/
    
    public List<ResData> getLocalResource(Request recvmsg) {
        return resStorage.getPropertybyResourceType(recvmsg, Resource.StoreCategory.Local);
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

    
}
