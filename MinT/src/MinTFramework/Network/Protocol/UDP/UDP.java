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
package MinTFramework.Network.Protocol.UDP;

import MinTFramework.MinTConfig;
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Util.DebugLog;
import MinTFramework.Util.OSUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDP extends Network {
    UDPSender sender;
    final int PORT;
    String cmd;
    
    InetSocketAddress isa;
    DatagramChannel channel;

    private final int NUMofRecv_Listener_Threads = MinTConfig.UDP_NUM_OF_LISTENER_THREADS;
    private UDPRecvListener[] UDPListener;
    private final DebugLog log = new DebugLog("UDP.java");

    /**
     * UDP communication structor
     *
     * @param port port that want to use
     * @param _ap Protocol
     * @param frame MinT Frame
     * @param nm Network Manager
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public UDP(String nodeName, int port) {
        super(new NetworkProfile(nodeName,OSUtil.getIPAddress()+":"+port,NetworkType.UDP));
//        log.printMessage(profile.getProfile());
        if(!MinTConfig.IP_ADDRESS.equals("")){
            profile.setAddress(MinTConfig.IP_ADDRESS);
            log.printMessage(profile.getProfile());
        }
        PORT = port;
        this.portOpen();
        try {
            this.setUDPSocket();
        } catch (IOException ex) {
        }
        MakeUDPReceiveListeners();
    }
    
    /**
     * set DatagramSocket make Sender and Receiver
     */
    private void setUDPSocket() throws IOException {
        InetSocketAddress isa = new InetSocketAddress(PORT);
        channel = DatagramChannel.open();
        channel.socket().bind(isa);
        channel.configureBlocking(false);
        channel.setOption(StandardSocketOptions.SO_RCVBUF, MinTConfig.UDP_RECV_BUFF_SIZE);
//        channel.setOption(StandardSocketOptions.SO_SNDBUF, MinTConfig.UDP_RECV_BUFF_SIZE);
        
        sender = new UDPSender(channel, this);
//        try {
//            
//            sender = new UDPSender(new DatagramSocket(PORT));
//        } catch (SocketException ex) {
//        }
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
     * Sending Message
     * @param packet 
     */
    @Override
    protected void sendProtocol(PacketDatagram packet) throws IOException {
            NetworkProfile dst = packet.getNextNode();
            SocketAddress add = new InetSocketAddress(dst.getIPAddr(), dst.getPort());
            sender.SendMsg(packet.getPacket(), add);
    }

    /**
     * Make UDP Receive Listeners
     */
    private void MakeUDPReceiveListeners() {
        UDPListener = new UDPRecvListener[NUMofRecv_Listener_Threads];
        for(int i=0;i<UDPListener.length;i++){
            try {
                UDPListener[i] = new UDPRecvListener(channel, this);
                UDPListener[i].start();
            } catch (IOException ex) {
            }
        }
        System.out.println("UDP - Receive Listeners are started");
    }

    @Override
    protected void interrupt() {
        //Stop All Listener
        for(int i=0;i<UDPListener.length;i++)
            UDPListener[i].interrupt();
        
        //Stop All Sender
    }
}