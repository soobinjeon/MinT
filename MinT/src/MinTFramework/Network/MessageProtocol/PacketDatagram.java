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
package MinTFramework.Network.MessageProtocol;

import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.RecvMSG;
import MinTFramework.Network.SendMSG;
import java.util.TreeMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class PacketDatagram {
    private ApplicationProtocol ptype;
    private byte[] packetmessages = null;
    private boolean isReceivedPacket = false;
    private boolean isUDPMulticast = false;
    protected String message="";
    
    protected TreeMap<ROUTE, NetworkProfile> routelist= null;
    
    protected enum ROUTE{
        SOURCE, PREV, NEXT, DESTINATION;
    }
    
    protected SendMSG sendMsg = null;
    
    public PacketDatagram(NetworkProfile src, NetworkProfile prev, NetworkProfile next, NetworkProfile dest
            ,boolean isMulticast, String msg, ApplicationProtocol ptype){
        routelist = new TreeMap<>();
        routelist.put(ROUTE.SOURCE,src);
        routelist.put(ROUTE.PREV,prev);
        routelist.put(ROUTE.NEXT,next);
        routelist.put(ROUTE.DESTINATION,dest);
        this.ptype = ptype;
        isUDPMulticast = isMulticast;
        message = msg;
    }
    
    /**
     * initialization Received Packet
     * @param recvmsg
     * @param ptype 
     */
    public PacketDatagram(RecvMSG recvmsg, ApplicationProtocol ptype){
        this.ptype = ptype;
        packetmessages = recvmsg.getRecvBytes();
        isReceivedPacket = true;
        isUDPMulticast = recvmsg.isUDPMulticast();
        
        ReceiveAttribute ratt = makeReceivedPacket(recvmsg);
        routelist = ratt.getRouteList();
        message = ratt.getMessage();
    }
    
    protected abstract ReceiveAttribute makeReceivedPacket(RecvMSG recvmsg); 
    protected abstract SendAttribute makeSendedPacket(); 
    
    /**
     * Make Data Packet
     * this method is only used in Network Send Method
     */
    public void makeBytes() {
        SendAttribute sattr = makeSendedPacket();
        packetmessages = sattr.getPacketMessage();
    }
    
    /***
     * Get Message Protocol Types
     * e.g.) COAP, MQTT, and so on
     * @return Message protocol type
     */
    public ApplicationProtocol getMessageProtocolType(){
        return ptype;
    }
    
    /**
     * return byte Packet Data
     * @return 
     */
    public byte[] getPacket(){
        return packetmessages;
    }
    
    public String getPacketString(){
        return String.valueOf(packetmessages);
    }
    
    public NetworkProfile getSource(){
        return routelist.get(ROUTE.SOURCE);
    }
    
    public NetworkProfile getPreviosNode(){
        return routelist.get(ROUTE.PREV);
    }
    
    public NetworkProfile getNextNode(){
        return routelist.get(ROUTE.NEXT);
    }
    
    public NetworkProfile getDestinationNode(){
        return routelist.get(ROUTE.DESTINATION);
    }
    
    public void setSource(NetworkProfile src){
        routelist.put(ROUTE.SOURCE, src);
//        makePacketData(this.HEADER_MSGID,this.h_direction, this.h_instruction, data);
    }
    
    public void setPrevNode(NetworkProfile prev){
        routelist.put(ROUTE.PREV, prev);
//        makePacketData(this.HEADER_MSGID, this.h_direction, this.h_instruction, data);
    }
    
    public String getMsgData(){
        return message;
    }
    
    /**
     * is this packet is received from other node.
     * @return true if, false else
     */
    public boolean isReceivedPacket(){
        return isReceivedPacket;
    }
    
    /**
     * Will this packet send to other node ?
     * @return true if, false else
     */
    public boolean isSendPacket(){
        return !isReceivedPacket;
    }
    
    public boolean hasMulticast(){
        return isUDPMulticast;
    }
    
    public boolean hasUnicast(){
        return !isUDPMulticast;
    }
    
    public SendMSG getSendMSG(){
        return sendMsg;
    }
}
