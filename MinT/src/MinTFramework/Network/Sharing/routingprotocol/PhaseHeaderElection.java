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
package MinTFramework.Network.sharing.routingprotocol;

import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.sharing.node.CurrentNode;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.SystemScheduler.Service;
import MinTFramework.storage.datamap.Information;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PhaseHeaderElection extends Phase implements Callable{
    RoutingProtocol current_protocol;
    private final int HeaderWaitTime = 60000;
    private boolean doneElection = false; //election check
    private boolean doneIdentify = false;
    private boolean isBroadcasting = false; //Header notify to client nodes
    private boolean isHeaderNotification = false; // header notification
    public PhaseHeaderElection(RoutingProtocol rp, Phase pp){
        super(rp,pp);
    }
    
    @Override
    public boolean hasMessage(int msg) {
        for(RT_MSG rtmsg : RT_MSG.values()){
            if(rtmsg.isSamePhase(RT_MSG.HE.getValue()) && rtmsg.isSamePhase(msg))
                return true;
        }
        return false;
    }

    @Override
    public Object call() throws Exception {
        setWorkingPhase(true);        
        doneElection = false;
        try {
            System.out.println("Header Election Started");
            
            //wait, if header is broadcasting
            while (isBroadcasting || isHeaderNotification) {
                Thread.sleep(1000);
            }
            //elect and broadcast header info
            doElectHeader();
            
            //respite for client response
            int waitcnt = 1000;
            while(!doneIdentify && waitcnt <= HeaderWaitTime){
                Thread.sleep(1000);
                waitcnt += 1000;
            }
            
            //Start Header Notifying
            //1. 클러스터 형성할때 한번만 각 그룹 헤더들간 통신을 통해 정보 전달 방법
            //2. 주기적으로 헤더 정보를 Notifying 해줘야함 (!!선택!!)
            //초반엔 짦은 주기, 일정 시간 이후엔 긴 주기로 정보 전달
            //리소스 정보 포함!? 안포함!? 하지말까 아직??
            //헤더 정보만 보내주고 리소스는 추후에 라우팅에서 하는게 더 좋을 것 같기도 함.
            //이부분을 Execute Routing 클래스에서 시도하는것도 좋은 방법일듯함. 아닌가? 아닌거같아..
            //다른방법!
            //3.Aggregation할 때 Multicast로 Header들을 찾아서
            //업데이트 하는 방법!? 근데 이건 또 Aggregation 할때마다 해야하는구나....
            PeriodicHeaderNotification();
            
            
        } catch (Exception e) {
            e.printStackTrace();
            setWorkingPhase(false);
            System.out.println("Discover Phase Interrupt Exception");
        } finally{
            setWorkingPhase(false);
            System.out.println("Header Election Run Phase Finally");
        }
        
        return true;
    }
    
    /**
    * Header Election Request Handle
    * @param rv_packet
    * @param req 
    */
    @Override
    public void requestHandle(PacketDatagram rv_packet, Request req) {
        Information resdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
        
        //process for broadcast of header node
        if(doneElection && handleIdentify(RT_MSG.HE_BROADCASTTOCLIENT, resdata)){
            responseToHeader(rv_packet);
        }
        /**
         * received a information of other header node
         * add header info to routing table
         */
        else if(doneElection && routing.isHeaderNode() && handleIdentify(RT_MSG.HE_HEADERNOTIFYING, resdata)){
            //do something for Header notifying
            //헤더 정보 교환을 해야하나?
            //Notifying 메세지를 받은 후 반송 메세지로 리소스 정보들으 보내줘야하나? (선택!)
            //아니면 Notifying 시 리소스 정보들을 올려줘야하는가??
            addRoutingTable(rv_packet, req);
            addHeaderResource(rv_packet, req);
        }
    }
    
    private void addHeaderResource(PacketDatagram rv_packet, Request req) {
        if (isDiscovery(req)) {
            System.out.println("update discovery data in Routing Handler");
            Information discoverdata = req.getResourcebyName(Request.MSG_ATTR.WellKnown);
            ResponseData resdata = new ResponseData(rv_packet, discoverdata.getResource());
            frame.getResStorage().updateDiscoverData(resdata);
            Node hn = rtable.getNodebyAddress(rv_packet.getSource().getAddress());
            if(hn != null)
                hn.setResources();
        }
    }
    
    private boolean isDiscovery(Request req) {
        if(req.getResourcebyName(Request.MSG_ATTR.WellKnown) != null)
            return true;
        else
            return false;
    }
    
    /**
     * Process for broadcast of header node
     * @param rv_packet 
     */
    private void responseToHeader(PacketDatagram rv_packet){
        if(!routing.isHeaderNode()) { // Client Node Operation
            Node hNode = rtable.getNodebyAddress(rv_packet.getSource().getAddress());
            //set Header Node in routing table
            if(hNode != null){
                System.out.println("Header Node: "+hNode.toString()+" is updated");
                hNode.setHeaderNode(true);
            }
            
            SendMessage ret = new SendMessage()
                    .AddAttribute(Request.MSG_ATTR.Routing, RT_MSG.HE_CLIENTRESPONSE.getValue());

            //response client info to header
            networkmanager.SEND_RESPONSE(rv_packet, ret, MinTMessageCode.CONTENT);
//            networkmanager.SEND(new SendMSG(CoAPPacket.HEADER_TYPE.NON, 0, CoAPPacket.HEADER_CODE.CONTENT
//                    , rv_packet.getSource(), ret, rv_packet.getMSGID()));
            doneIndentify();
        } else { //When is this header node, to do
            //re calculate header election

        }
    }
    
    /**
     * Header Election Response Handle
     * @param rv_packet
     * @param req 
     */
    @Override
    public void responseHandle(PacketDatagram rv_packet, Request req) {
        if(!routing.isHeaderNode() && !isWorkingPhase())
            return;
        Information resdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
        
        //Header Node Operation
        //response client info and set client node in routing table
        if(doneElection && routing.isHeaderNode() && handleIdentify(RT_MSG.HE_CLIENTRESPONSE, resdata)){
            Node snode = rtable.getNodebyAddress(rv_packet.getSource().getAddress());
            if(snode != null){
                System.out.println("client Node: "+snode.toString()+" is updated");
                snode.setClientNode(true);
                ClientIdentifying();
            }
        }
    }
    
    /**
     * Header Mode
     * check All Client Node Identifying
     */
    private void ClientIdentifying() {
        //close all header election process
        if(rtable.isdoneIdentifyAllChildNode()){
            doneIndentify();
            
            //notifying
        }
    }
    private void doneIndentify(){
        doneIdentify = true;
    }
    
    private void ConfirmDoneElection() {
        doneElection = true;
    }
    
    /**
     * Header Election and Broadcast Header info to client
     */
    private void doElectHeader(){
        //get Node that has the highest weight value
        Node hn = routing.getCurrentNode();
        double highvalue = hn.getSpecWeight();
      
        //Select a header, which has high weight value.
        for(Node cn : rtable.getRoutingTable().values()){
            if(cn.getSpecWeight() > highvalue){
                hn = cn;
                highvalue = hn.getSpecWeight();
            }else if(cn.getSpecWeight() == highvalue){
                //if weight is same with high, do something
            }
        }
        
        if(hn == null)
            return;
        
        rtable.setHeaderNode(hn);
        
        //Do process for header node, if current node is header
        if(hn instanceof CurrentNode) {
            routing.setHeaderNode(true);
            //broadcast
            NotifyHeaderInfotoClient(hn);
        } else { //else client node
            ;
        }
        
        ConfirmDoneElection();
    }

    /**
     * Broadcast Header info to client Node
     * 계속해서 응답이 없을 때 처리하는 부분 없음
     * @param hn 
     */
    public void NotifyHeaderInfotoClient(Node hn) {
        final int BroadCastTotalCnt = 10;
        if(isBroadcasting)
            return;
        
        isBroadcasting = true;
        
        Service broadcast = new Service("BroadCast Header Info") {
            @Override
            public void execute() {
                //do broad cast
                //fix -> rtable에서 profile list 정보 받는게 좋을 것 같음
                try {
                    doneIdentify = false;
                    ArrayList<NetworkProfile> dstlist = new ArrayList<>();
                    int bcCnt = 0;
                    for (Node cn : rtable.getRoutingTable().values()) {
                        if(!cn.isHeaderNode())
                            dstlist.add(cn.gettoAddr());
                    }
                    if(dstlist.size() == 0)
                        doneIndentify();
                    //broadcast Header Info
                    while (!doneIdentify && bcCnt < BroadCastTotalCnt && !Thread.currentThread().isInterrupted()) {
                        for(NetworkProfile dst : dstlist){
                            //if client response is updated
                            Node snode = rtable.getNodebyAddress(dst.getAddress());
                            if(snode != null && snode.isClientNode())
                                continue;
                            
                            frame.REQUEST_GET(dst.setCON(true), new SendMessage()
                                    .AddAttribute(Request.MSG_ATTR.Routing, RT_MSG.HE_BROADCASTTOCLIENT.getValue()), new ResponseHandler() {
                                @Override
                                public void Response(ResponseData resdata) {
                                    routing.getClientResourceInfo(resdata.getSourceInfo().getAddress());
                                }
                            });
                        }
                        bcCnt ++;
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Header Election Interrupt Exception");
                } finally {
                    System.out.println("Header Info Notifying - Finally");
                    isBroadcasting = false;
                }
            }
        };
        
        frame.executeService(broadcast);
    }
    
    /**
     * Periodic Header Notification to header of other group after completing header election
     * Notification Information list
     * Header address
     * Group Name
     */
    private void PeriodicHeaderNotification(){
        if(!routing.isHeaderNode())
            return;
        Service headerNotification = new Service("Header Notification") {
            @Override
            public void execute() {
                long NotPeriod = 5; //sec
                long DiscoverPeriod = 20;
                try{
                    isHeaderNotification = true;
                    while (!Thread.currentThread().isInterrupted()) {
                        //notifying header info and group name by UDP multicast
                        
                        Network cnet = frame.getNetworkManager().getNetwork(NetworkType.UDP);
                        JSONObject discoverydata = frame.getResStorage().DiscoverDelegateResource(cnet.getProfile());
                        
                        SendMessage smsg = new SendMessage()
                                .AddAttribute(Request.MSG_ATTR.Routing, RT_MSG.HE_HEADERNOTIFYING.getValue())
                                .AddAttribute(Request.MSG_ATTR.RoutingGroup, routing.getCurrentRoutingGroup())
                                .AddAttribute(Request.MSG_ATTR.RoutingisHeader, true);
                        
                        if(cnet != null && discoverydata != null){
//                            System.out.println("header brd: "+discoverydata.toJSONString());
                            //NotPeriod = DiscoverPeriod;
                            smsg.AddAttribute(Request.MSG_ATTR.WellKnown, discoverydata.toJSONString());
                        }
//                        networkmanager.SEND_UDP_Multicast(smsg);
                        frame.REQUEST_POST_MULTICAST(smsg, null);
                        Thread.sleep(NotPeriod * 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Header Notification is finished");
                    isHeaderNotification = false;
                }
            }
        };
        frame.executeService(headerNotification);
    }
}
