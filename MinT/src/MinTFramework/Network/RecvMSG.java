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
package MinTFramework.Network;

import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;
import MinTFramework.MinTConfig;
import MinTFramework.Util.Benchmarks.Performance;
import java.net.SocketAddress;
import org.json.simple.parser.JSONParser;

/**
 * Receive Message
 *  - call Matcher when receive data is put in the receiving thread pool
 *  - 
 * @author soobin
 */
public class RecvMSG implements Runnable {
    private NetworkType ntype;
    private byte[] recvbytes;
    private SocketAddress addr;
    private String address;
    private JSONParser jparser;
    private CoAPPacket receivedPacket = null;
    private boolean isMulticast = false;
    public RecvMSG(byte[] recvb, String address, NetworkType type){
        recvbytes = recvb;
        address = address;
        ntype = type;
    }
    
    public RecvMSG(byte[] recvb, SocketAddress prevsocket, NetworkType type, boolean _isMulticast){
        this(recvb, "", type);
        addr = prevsocket;
        address = getIPAddress(addr);
        isMulticast = _isMulticast;
    }
    
    @Override
    public void run() {
        ReceiveAdapter recvA = (ReceiveAdapter)Thread.currentThread();
        recvA.checkBench();
        MatcherAndSerialization matcher = recvA.getMatcher();
        Performance bench = recvA.getBench();
        jparser = recvA.getJSONParser();
        //put the recvmsg to matcher
        if(bench != null)
            bench.startPerform();
        matcher.EndPointReceive(this);
        if(bench != null)
            bench.endPerform(0);
    }
    
    /**
     * get IP Address
     * @param recvadd
     * @return 
     */
    private String getIPAddress(SocketAddress recvadd){
        String addr = recvadd.toString().substring(1);
        String[] ipaddr = addr.split(":");
        if(ipaddr.length > 1)
            return ipaddr[0]+":"+MinTConfig.INTERNET_COAP_PORT;
        else
            return recvadd.toString().substring(1);
    }
    
    /**
     * get Received Byte Array
     * @return 
     */
    public byte[] getRecvBytes(){
        return recvbytes;
    }
    
    /**
     * get Socket Address
     * @return 
     */
    public SocketAddress getSocketAddr(){
        return addr;
    }
    
    /**
     * get Network Type
     * @return 
     */
    public NetworkType getNetworkType(){
        return ntype;
    }
    
    /**
     * get Address
     * @return 
     */
    public String getAddress() {
        return this.address;
    }
    
    public void setReceivedPacketDatagram(CoAPPacket _packet){
        receivedPacket = _packet;
    }
    
    public CoAPPacket getPacketDatagram(){
        return receivedPacket;
    }
    
    public JSONParser getJSONParser(){
        return jparser;
    }
    
    /**
     * Check for receiving data to multi-cast or uni-cast
     * @return , true if, false else
     */
    public boolean isUDPMulticast(){
        return isMulticast;
    }
}
