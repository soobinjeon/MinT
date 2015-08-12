/*
 * Copyright (C) 2015 soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>, youngtak Han <gksdudxkr@gmail.com>
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
package MinTFramework.Network.UDP;

import MinTFramework.Network.Network;
import MinTFramework.Network.Observation;
import MinTFramework.Network.MinTPacket;
import java.io.IOException;
import java.net.*;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDPReceiver implements Runnable {

    /**
     * @param args the command line arguments
     */
    DatagramSocket socket;
    DatagramPacket inPacket;
    MessageReceiveImpl msgReceiveImpl;
    UDP udp;
    byte[] inbuf;
    byte[] data;
    byte[] mintPacket;

    /***
     * UDP Receiver Thread Constructor
     * @param socket
     * @param udp
     * @throws SocketException 
     */
    public UDPReceiver(DatagramSocket socket, UDP udp) throws SocketException {
        this.socket = socket;
        this.udp = udp;
        //this.observation = ob;
    }

    /***
     * make new thread msg impl
     * @param msimpl 
     */
    public void setReceive(MessageReceiveImpl msimpl) {
        this.msgReceiveImpl = msimpl;
    }

    /***
     * Waiting until something received
     * when message received, make new Thread using msgReceivedImpl
     */
    @Override
    public void run() {
        try {
            inbuf = new byte[512];
            inPacket = new DatagramPacket(inbuf, inbuf.length);
            socket.receive(inPacket);
            msgReceiveImpl.makeNewReceiver();
            
            String recvPacketString = new String(inPacket.getData(), 0, inPacket.getLength());
            
            mintPacket = MinTPacket.makeMinTPacket(inPacket.getAddress().getHostAddress()+":"+inPacket.getPort(), recvPacketString);
            
            udp.MatcherSerializerAndObservation(mintPacket);
            
        } catch (IOException e) {
        }

    }
}
