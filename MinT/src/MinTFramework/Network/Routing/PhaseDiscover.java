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

import MinTFramework.Network.Network;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
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
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("BroadCast Information");
                networkmanager.SEND_UDP_Multicast(new SendMessage()
                        .AddAttribute(Request.MSG_ATTR.Routing, RT_MSG.DIS_BROADCAST.getValue()));
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setWorkingPhase(false);
        } finally{
            setWorkingPhase(false);
        }
    }
    
    @Override
    public void requestHandle(PacketDatagram rv_packet, Request req) {
        Information resdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
        if(RT_MSG.DIS_BROADCAST.isEqual(resdata.getResourceInt())){
            System.out.println("Discovery Mode -- DIS BROADCAST DATA: "+rv_packet.getSource().getAddress()+", "+req.getMessageString());
            setRoutingTable(rv_packet);
        }
    }

    @Override
    public void responseHandle(PacketDatagram rv_packet, Request req) {
    }

    private void setRoutingTable(PacketDatagram rv_packet) {
        Node node = new Node(rv_packet.getSource(), rv_packet.getPreviosNode(), true);
        //same network exeption
        Network cnetwork = networkmanager.getNetwork(rv_packet.getPreviosNode().getNetworkType());
        if(cnetwork != null && cnetwork.getProfile().getAddress().equals(node.gettoAddr().getAddress()))
            return;
        
        //add address to routing table
        rtable.addRoutingTable(node);
    }

    
}
