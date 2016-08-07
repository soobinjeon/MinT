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
package MinTFramework.Network.UDP;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDPReceiver extends Thread {

    /**
     * @param args the command line arguments
     */
    DatagramSocket socket;
    DatagramPacket inPacket;
    MessageReceiveImpl msgReceiveImpl;
    UDP udp;
    byte[] inbuf;
    byte[] mintPacket;
    String name;
    /***
     * UDP Receiver Thread Constructor
     * @param socket
     * @param udp
     * @throws SocketException 
     */
    public UDPReceiver(DatagramSocket socket, UDP udp) throws SocketException {
        this.socket = socket;
        this.udp = udp;
    }
    
    public UDPReceiver(DatagramSocket socket, UDP udp, String name) throws SocketException {
        this.socket = socket;
        this.udp = udp;
        this.name = name;
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
//            System.out.println(name + " : waiting");
            inbuf = new byte[512];
            inPacket = new DatagramPacket(inbuf, inbuf.length);
            socket.receive(inPacket);
//            System.out.println(name + " : recved!");
            msgReceiveImpl.makeNewReceiver(name);            
            udp.MatcherAndObservation(inPacket.getData());
            
            
        } catch (IOException e) {
        }

    }
}
