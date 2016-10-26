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

import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Sharing.node.Node;
import MinTFramework.storage.ThingProperty;
import java.util.concurrent.Callable;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ExecuteRouting extends Phase implements Callable{
    RoutingProtocol current_protocol;
    
    public ExecuteRouting(RoutingProtocol rp, Phase pp){
        super(rp,pp);
    }
    
    @Override
    public boolean hasMessage(int msg) {
        for(RT_MSG rtmsg : RT_MSG.values()){
            if(rtmsg.isSamePhase(RT_MSG.RT.getValue()) && rtmsg.isSamePhase(msg))
                return true;
        }
        return false;
    }
    
    @Override
    public Object call() throws Exception {
        try{
            System.out.println("Execute Routing Protocol");
            
//            if(routing.isHeaderNode()){
//                System.out.println("get All Clients Resources");
//                routing.getAllClientsResource();
//            }
            
            while(!Thread.currentThread().isInterrupted()){
                System.out.println("\n--Routing Table");
                for (Node n : rtable.getRoutingTable().values()) {
                    System.out.println("-----Node: " + n.gettoAddr().getAddress() + ", gr:" + n.getGroupName()
                            + ", sw: " + n.getSpecWeight()+", hd: "+n.isHeaderNode() + " client: "+n.isClientNode());
                    for(ThingProperty tp : n.getProperties().values()){
                        System.out.println("----------"+tp.getID()+", "+tp.getName()+", "+tp.getGroup() + ", "+tp.getDeviceType().getDeviceTypeString()
                            +", "+tp.getStorageCategory().toString()
                            +", data: "+tp.getResourceData().getResourceString());
                    }
                }
                
                System.out.println("--Thing property lists");
                for(ThingProperty tp : frame.getProperties(null)){
                    System.out.println("-----"+tp.getID()+", "+tp.getName()+", "+tp.getGroup() + ", "+tp.getDeviceType().getDeviceTypeString()
                            +", "+tp.getStorageCategory().toString()
                            +", data: "+tp.getResourceData().getResourceString());
                }
                
                Thread.sleep(5000);
            }
        }catch(Exception e){
            
        }
        return true;
    }

    @Override
    public void requestHandle(PacketDatagram rv_packet, Request req) {
        if(!isWorkingPhase())
            return;
    }

    @Override
    public void responseHandle(PacketDatagram rv_packet, Request req) {
        if(!isWorkingPhase())
            return;
    }
}
