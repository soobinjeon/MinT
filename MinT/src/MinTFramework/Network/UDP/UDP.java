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

import MinTFramework.Network.MinTNetworkDataPacket;
import MinTFramework.Network.Network;
import MinTFramework.Util.DebugLog;
import MinTFramework.Util.OSUtil;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDP extends Network {

    DatagramSocket socket;
    UDPReceiver receiver;
    UDPSender sender;
    final int PORT;
    MessageReceiveImpl impl;
    Thread receiverThread;
    String cmd;
    String dstIP;
    int dstPort;

    private final DebugLog log = new DebugLog("UDP.java");

    public UDP(int port) throws SocketException {
        super();
        PORT = port;
        impl = null;
        socket = new DatagramSocket(PORT);
        receiver = new UDPReceiver(socket);
        sender = new UDPSender(socket);
        
        String OS = System.getProperty("os.name").toLowerCase();
        log.printMessage(OS);
        
        if (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0) {
            log.printMessage(OS);
            OSUtil.linuxShellCommand("iptables -I INPUT 1 -p udp --dport " + port + " -j ACCEPT");
            OSUtil.linuxShellCommand("iptables -I OUTPUT 1 -p udp --dport " + port + " -j ACCEPT");
        }
        receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    public void setMessageReceiveImpl(MessageReceiveImpl impl) {
        receiver.setReceive(impl);
    }

    public void sendMessage(String msg, String dstIP, int dstPort) {
        this.dstIP = dstIP;
        this.dstPort = dstPort;
        send(msg);
    }

    @Override
    public void send(String msg) {
        try {
            
            sender.SendMsg(msg, dstIP, dstPort);
            
        } catch (SocketException ex) {
            Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void send(MinTNetworkDataPacket packet) {
        try {
            sender.SendMsg(packet, packet.getDst(), packet.getDestPort());
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
