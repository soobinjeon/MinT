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
package MinTFramework.Network.Routing;

import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.Routing.node.Node;
import MinTFramework.storage.datamap.Information;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PhaseDiscover extends Phase{
    
    public PhaseDiscover(RoutingProtocol rp, Phase pp){
        super(rp,pp);
    }
    
    @Override
    public boolean hasMessage(int msg) {
        return RT_MSG.DIS_BROADCAST.isSamePhase(msg);
    }

    @Override
    public void run() {
        setWorkingPhase(true);
        try {
            
            while (!super.isInturrupted() && !Thread.currentThread().isInterrupted()) {
                System.out.println("BroadCast Information");
                networkmanager.SEND_UDP_Multicast(new SendMessage()
                        .AddAttribute(Request.MSG_ATTR.Routing, RT_MSG.DIS_BROADCAST.getValue())
                        .AddAttribute(Request.MSG_ATTR.RoutingGroup, routing.getCurrentRoutingGroup())
                        .AddAttribute(Request.MSG_ATTR.RoutingWeight, routing.getCurrentNode().getSpecWeight()));
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setWorkingPhase(false);
            System.out.println("Discover Phase Interrupt Exception");
        } finally{
            setWorkingPhase(false);
            System.out.println("Discover Phase Finally");
        }
    }
    
    @Override
    public void requestHandle(PacketDatagram rv_packet, Request req) {
        Information resdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
        Information gdata = req.getResourcebyName(Request.MSG_ATTR.RoutingGroup);
        String gn = gdata != null ? gdata.getResourceString() : "";
        
        if(RT_MSG.DIS_BROADCAST.isEqual(resdata.getResourceInt())){
            System.out.println("Discovery Mode -- DIS BROADCAST DATA: "+rv_packet.getSource().getAddress()+", "+req.getMessageString());
            
            //if Same Group or this is header node
            if(routing.isHeaderNode() || gn.equals(routing.getCurrentRoutingGroup()))
                setRoutingTable(rv_packet, req);
            
            //debug
            System.out.println("Node List");
            for(Node n : rtable.getRoutingTable().values()){
                System.out.println("-----Node: "+n.gettoAddr().getAddress()+", gr:"+n.getGroupName()+", sw: "+n.getSpecWeight());
            }
            
            //note that new node is added in our group
            if(!isWorkingPhase()){
               //to Execute Routing or HeaderElection Phase
               
            }
        }
    }

    @Override
    public void responseHandle(PacketDatagram rv_packet, Request req) {
    }
}
