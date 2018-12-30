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
package MinTFramework.Network.Sharing.routingprotocol;

import MinTFramework.MinT;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Sharing.node.Node;
import MinTFramework.storage.datamap.Information;
import java.util.concurrent.Callable;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Phase implements Callable{
    private Phase prevPhase = null;
    private Phase nextphase = null;
    
    protected MinT frame;
    protected NetworkManager networkmanager;
    protected RoutingProtocol routing;
    protected RoutingTable rtable;
    
    private boolean workingPhase = false;
    private boolean isInturrupted = false;
    
    public Phase(RoutingProtocol _rp, Phase _prevPhase){
        routing = _rp;
        prevPhase = _prevPhase;
        if(prevPhase != null)
            prevPhase.setnextPhase(this);
        
        frame = MinT.getInstance();
        networkmanager = frame.getNetworkManager();
        rtable = routing.routingtable;
    }
    
    public void setnextPhase(Phase nphase){
        nextphase = nphase;
    }
    
    public Phase getPrevPhase(){
        return prevPhase;
    }
    
    public Phase getNextPhase(){
        return nextphase;
    }
    
    /**
     * set Routing Table from other Node
     * @param rv_packet 
     */
    protected Node addRoutingTable(PacketDatagram rv_packet, ReceiveMessage req) {
        Node pnode = rtable.getNodebyAddress(rv_packet.getSource().getAddress());
        if(pnode != null)
            return null;
        Information rweight = req.getResourcebyName(Request.MSG_ATTR.RoutingWeight);
        Information rgroup = req.getResourcebyName(Request.MSG_ATTR.RoutingGroup);
        Information rheader = req.getResourcebyName(Request.MSG_ATTR.RoutingisHeader);
        String gn = rgroup != null ? rgroup.getResourceString() : "";
        double rw = rweight != null ? rweight.getResourceDouble() : 0;
        boolean rh = rheader != null ? rheader.getResourceBoolean() : false;
        Node node = new Node(rv_packet.getSource(), rv_packet.getPreviosNode(), rh, rw, gn);
        //same network exeption
        Network cnetwork = networkmanager.getNetwork(rv_packet.getPreviosNode().getNetworkType());
        if(cnetwork != null && cnetwork.getProfile().getAddress().equals(node.gettoAddr().getAddress()))
            return null;
        
        //add address to routing table
        rtable.addRoutingTable(node);
        
        return node;
    }
    
    /**
     * set phase to working mode
     * @param wp 
     */
    protected void setWorkingPhase(boolean wp){
        workingPhase = wp;
    }
    
    public boolean isWorkingPhase(){
        return workingPhase;
    }
    
    public void inturrupt() {
        isInturrupted = true;
    }
    
    protected boolean isInturrupted(){
        return isInturrupted;
    }
    
    protected boolean handleIdentify(RT_MSG rtmsg, Information recv_msg){
        return recv_msg != null && rtmsg.isEqual(recv_msg.getResourceInt());
    }
    
    public abstract boolean hasMessage(int msg);
    public abstract void requestHandle(PacketDatagram rv_packet, ReceiveMessage req);
    public abstract void responseHandle(PacketDatagram rv_packet, ReceiveMessage req);

}
