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

import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.Network.SendMSG;
import MinTFramework.storage.datamap.Information;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PhaseDiscover extends Phase{
    DiscoverRole disRole = null;
    private final int Default_Time = 10;
    public PhaseDiscover(RoutingProtocol rp, Phase pp){
        super(rp,pp);
        disRole = new DiscoverRole(Default_Time);
    }
    
    @Override
    public boolean hasMessage(int msg) {
        for(RT_MSG rtmsg : RT_MSG.values()){
            if(rtmsg.isSamePhase(RT_MSG.DIS.getValue()) && rtmsg.isSamePhase(msg))
                return true;
        }
        return false;
    }

    @Override
    public Object call() throws Exception {
        setWorkingPhase(true);
        try {
            while (!super.isInturrupted() && !Thread.currentThread().isInterrupted()) {
                networkmanager.SEND_UDP_Multicast(new SendMessage()
                        .AddAttribute(Request.MSG_ATTR.Routing, RT_MSG.DIS_BROADCAST.getValue())
                        .AddAttribute(Request.MSG_ATTR.RoutingGroup, routing.getCurrentRoutingGroup())
                        .AddAttribute(Request.MSG_ATTR.RoutingWeight, routing.getCurrentNode().getSpecWeight()));
                if(disRole.doDiscoveryTimeRole()){
                    Thread.sleep(1000);
                }else
                    inturrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setWorkingPhase(false);
            System.out.println("Discover Phase Interrupt Exception");
        } finally{
            setWorkingPhase(false);
            System.out.println("Discover Phase Finally");
        }
        return true;
    }
    
    @Override
    public void requestHandle(PacketDatagram rv_packet, Request req) {
        Information resdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
        Information gdata = req.getResourcebyName(Request.MSG_ATTR.RoutingGroup);
        String gn = gdata != null ? gdata.getResourceString() : "";
        
        if(RT_MSG.DIS_BROADCAST.isEqual(resdata.getResourceInt()) || 
                RT_MSG.DIS_BROADCAST_STOP.isEqual(resdata.getResourceInt())){
//            System.out.println("Discovery Mode -- DIS BROADCAST DATA: "+rv_packet.getSource().getAddress()+", "+req.getMessageString());
            
            //if Same Group or this is header node
            if (isWorkingPhase() && isSameGroup(gn)) {
                Node isadded = addRoutingTable(rv_packet, req);
                
                if (isadded != null) {
                    //헤더 선출이 끝나고 추가적으로 이노드가 그룹에 추가되었을 때 헤더로부터 DIS_BROADCAST_STOP 메시지가 넘어온다.
                    //넘어온 메세지에 따라 처리 후 헤더에게 정보 반송
                    //이노드의 페이즈는 HeaderElection 페이즈로 넘어감
                    if (RT_MSG.DIS_BROADCAST_STOP.isEqual(resdata.getResourceInt())) {
                        System.out.println("Stop Message");
                        disRole.interrupt();

                        Request ret = new SendMessage(null,RT_MSG.DIS_BROADCAST_STOP.getValue());
                        networkmanager.SEND(new SendMSG(PacketDatagram.HEADER_TYPE.NON, 0, PacketDatagram.HEADER_CODE.CONTENT
                                , rv_packet.getSource(), ret, rv_packet.getMSGID()));
                    }
                    
                    else
                        disRole.addedNewNode();
                }
            }            
            
            //note that new node is added in our group, if this is header node
            else if(routing.isHeaderNode() && isSameGroup(gn)){
               //to Execute Routing or HeaderElection Phase
               Node nnode = addRoutingTable(rv_packet, req);
               //추가되었던 노드가 다시 재추가 될때 프로시저 없음. (addRoutingTable 변경 해야할 것 같음)
                if (nnode != null) {
                    System.out.println("new Node Added!!");
                    frame.REQUEST_GET(nnode.gettoAddr(), new SendMessage()
                            .AddAttribute(Request.MSG_ATTR.Routing, RT_MSG.DIS_BROADCAST_STOP.getValue())
                            .AddAttribute(Request.MSG_ATTR.RoutingGroup, routing.getCurrentRoutingGroup())
                            .AddAttribute(Request.MSG_ATTR.RoutingWeight, routing.getCurrentNode().getSpecWeight()), new ResponseHandler() {
                        @Override
                        public void Response(ResponseData resdata) {
                            //새로운 노드로부터 응답이 왔을 때 처리
                            PhaseHeaderElection he = (PhaseHeaderElection)routing.getPhasebyName(RoutingProtocol.ROUTING_PHASE.HEADERELECTION);
                            if(he != null){
                                System.out.println("notify HeaderInfoClient");
                                he.NotifyHeaderInfotoClient(null);
                            }
                        }
                    });
                }
            }
            
            //debug
//            System.out.println("Node List");
//            for (Node n : rtable.getRoutingTable().values()) {
//                System.out.println("-----Node: " + n.gettoAddr().getAddress() + ", gr:" + n.getGroupName() + ", sw: " + n.getSpecWeight());
//            }
        }
    }

    @Override
    public void responseHandle(PacketDatagram rv_packet, Request req) {
    }
    
    private boolean isSameGroup(String gname){
        return gname.equals(routing.getCurrentRoutingGroup());
    }
}
