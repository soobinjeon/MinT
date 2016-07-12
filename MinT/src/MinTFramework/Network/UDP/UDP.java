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

import MinTFramework.MinT;
import MinTFramework.Network.RoutingProtocol;
import MinTFramework.Network.Network;
import MinTFramework.Network.Profile;
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
    UDP self;
    int dstPort;

    private final DebugLog log = new DebugLog("UDP.java");

    /**
     * UDP communication structor
     *
     * @param port port that want to use
     * @param frame
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public UDP(int port, RoutingProtocol _ap, MinT frame) {
        super(frame,new Profile(frame.getNodeName(),OSUtil.getIPAddress()+":"+port),_ap);

        PORT = port;
        this.setUDPSocket();
        this.setReceiverCallback();
        this.portOpen();
        this.startReceiveThread();

        self = this;
    }

    /**
     * set Receiver Callback Msg !!Important!! ** must call after 'setSocket'
     * method
     *
     **
     * @param frame
     * @param handler
     */
    private void setReceiverCallback() {
        msgimpl = new MessageReceiveImpl() {
            @Override
            public void makeNewReceiver() {
                try {
                    UDPReceiver receiver = new UDPReceiver(socket, self);
                    Thread nThread;
                    receiver.setReceive(msgimpl);

                    nThread = new Thread(receiver);
                    nThread.start();

                } catch (SocketException ex) {
                }
            }
        };
        receiver.setReceive(msgimpl);
    }

    /**
     * set DatagramSocket make Sender and Receiver
     */
    private void setUDPSocket() {
        try {
            socket = new DatagramSocket(PORT);
            receiver = new UDPReceiver(socket, this);
            sender = new UDPSender(socket);
        } catch (SocketException ex) {
            Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * For Linux make port open
     *
     * @param port
     */
    private void portOpen() {
        String OS = System.getProperty("os.name").toLowerCase();
        log.printMessage(OS);

        if (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0) {
            log.printMessage(OS);
            OSUtil.linuxShellCommand("iptables -I INPUT 1 -p udp --dport " + PORT + " -j ACCEPT");
            OSUtil.linuxShellCommand("iptables -I OUTPUT 1 -p udp --dport " + PORT + " -j ACCEPT");
        }
    }

    /***
     * Make and start Receiver thread
     */
    private void startReceiveThread() {
        receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    /**
     * Setting Destination
     *
     * @param dst destination for msg {ip}:{port}/ example "192.168.7.2:55"
     */

    @Override
    public void setDestination(Profile dst) {
        String[] adst = dst.split(":");
        this.dstIP = adst[0];
        this.dstPort = Integer.parseInt(adst[1]);
    }
    /***
     * Sending Message
     * @param packet 
     */
    @Override
    protected void send(byte[] packet) {
        try {
            sender.SendMsg(packet, dstIP, dstPort);
        } catch (SocketException ex) {
        } catch (IOException ex) {
        }
    }
}
