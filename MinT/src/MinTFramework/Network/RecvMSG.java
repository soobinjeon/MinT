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

import MinTFramework.Util.Benchmarks.Performance;
import java.net.SocketAddress;

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
    
    public RecvMSG(byte[] recvb, String address, NetworkType type){
        recvbytes = recvb;
        address = address;
        ntype = type;
    }
    
    public RecvMSG(byte[] recvb, SocketAddress prevsocket, NetworkType type){
        this(recvb, "", type);
//        System.out.println("recv len : "+recvb.length);
//        System.out.println(type.toString());
        addr = prevsocket;
        address = getIPAddress(addr);
    }
    
    @Override
    public void run() {
        ReceiveAdapter recvA = (ReceiveAdapter)Thread.currentThread();
        MatcherAndSerialization matcher = recvA.getMatcher();
        Performance bench = recvA.getBench();
        //put the recvmsg to matcher
        if(bench != null)
            bench.startPerform();
        matcher.EndPointReceive(this);
        if(bench != null)
            bench.endPerform();
    }
    
    /**
     * get IP Address
     * @param recvadd
     * @return 
     */
    private String getIPAddress(SocketAddress recvadd){
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

}
