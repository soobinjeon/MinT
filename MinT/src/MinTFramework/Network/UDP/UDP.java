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

import MinTFramework.MinT;
import MinTFramework.Network.Handler;
import MinTFramework.Network.Network;
import MinTFramework.Util.DebugLog;
import MinTFramework.Util.OSUtil;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
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
    MessageReceiveImpl msgimpl;
    Thread receiverThread;
    String cmd;
    String dstIP;
    int dstPort;

    private final DebugLog log = new DebugLog("UDP.java");

    /**
     * UDP communication structor
     *
     * @param port port that want to use
     * @param frame
     * @param handler
     */
    public UDP(int port, MinT frame, Handler handler) {
        super(frame, handler);
        
        PORT = port;
        msgimpl = new MessageReceiveImpl() {
            @Override
            public void makenewreceiver() {
                try {
                    UDPReceiver receiver = new UDPReceiver(socket, observation);
                    Thread nThread;
                    receiver.setReceive(msgimpl);

                    nThread = new Thread(receiver);
                    nThread.start();

                } catch (SocketException ex) {
                }
            }
        };

        try {
            socket = new DatagramSocket(PORT);
            receiver = new UDPReceiver(socket, observation);
            sender = new UDPSender(socket);
        } catch (SocketException ex) {
            Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
        }

        String OS = System.getProperty("os.name").toLowerCase();
        log.printMessage(OS);

        if (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0) {
            log.printMessage(OS);
            OSUtil.linuxShellCommand("iptables -I INPUT 1 -p udp --dport " + port + " -j ACCEPT");
            OSUtil.linuxShellCommand("iptables -I OUTPUT 1 -p udp --dport " + port + " -j ACCEPT");
        }

        receiver.setReceive(msgimpl);
        receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    @Override
    public void send(String dst, String fdst, String msg) {
        try {

            String[] adst = dst.split(":");
            this.dstIP = adst[0];
            this.dstPort = Integer.parseInt(adst[1]);
            sender.SendMsg(msg, dstIP, dstPort);

        } catch (SocketException ex) {
            Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
