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

import java.net.SocketAddress;

/**
 *
 * @author soobin
 */
public class RecvMSG {
    private NetworkType ntype;
    private byte[] recvbytes;
    private SocketAddress addr;
    private String address;
    
    public RecvMSG(byte[] recvb, String address, NetworkType type){
        recvbytes = recvb;
        this.address = address;
        ntype = type;
    }
    
    public RecvMSG(byte[] recvb, SocketAddress prevsocket, NetworkType type){
        this(recvb, "", type);
        this.addr = prevsocket;
        address = getIPAddress(addr);
    }
    
    private String getIPAddress(SocketAddress recvadd){
        return recvadd.toString().substring(1);
    }
    
    public byte[] getRecvBytes(){
        return recvbytes;
    }
    
    public SocketAddress getSocketAddr(){
        return addr;
    }
    
    public NetworkType getNetworkType(){
        return ntype;
    }

    public String getAddress() {
        return this.address;
    }
}
