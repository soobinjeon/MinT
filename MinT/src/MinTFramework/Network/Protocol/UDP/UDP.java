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

import MinTFramework.MinT;
import MinTFramework.MinTConfig;
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.ThreadsPool.MinTthreadPools;
import MinTFramework.Util.DebugLog;
import MinTFramework.Util.OSUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDP extends Network {
    public static enum UDP_Thread_Pools {UDP_RECV_LISTENER;};
    private UDPSender sender;
    private final int PORT;
    
    private InetSocketAddress isa;
    private DatagramChannel channel;
    
    private InetSocketAddress sendisa;
    private DatagramChannel sendchannel;
    
    private final int NUMofRecv_Listener_Threads = MinTConfig.UDP_NUM_OF_LISTENER_THREADS;
    private final DebugLog log = new DebugLog("UDP.java");

    /**
     * UDP communication structor
     *
     * @param port port that want to use
     * @param _ap Protocol
     * @param frame MinT Frame
     * @param nm Network Manager
     */
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
        System.out.println("Set up UDP : "+profile.getProfile());
    }
    
    /**
     * set Thing's IP Address
     * example :
     * "111.111.111.111"
     * @param addr 
     */
    public static void setIPAdress(String addr){
        MinTConfig.IP_ADDRESS = addr;
    }
    
    /**
     * set DatagramSocket make Sender and Receiver
     */
    private void setUDPSocket() throws IOException {
        isa = new InetSocketAddress(PORT);
        channel = DatagramChannel.open();
        channel.socket().bind(isa);
        channel.configureBlocking(false);
        channel.setOption(StandardSocketOptions.SO_RCVBUF, MinTConfig.UDP_RECV_BUFF_SIZE);
        
        sendisa = new InetSocketAddress(PORT+1);
        sendchannel = DatagramChannel.open();
        sendchannel.socket().bind(sendisa);
        sendchannel.configureBlocking(false);
        channel.setOption(StandardSocketOptions.SO_SNDBUF, MinTConfig.UDP_RECV_BUFF_SIZE);
        
        sender = new UDPSender(sendchannel, this);
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
        sysSched.registerThreadPool(UDP_Thread_Pools.UDP_RECV_LISTENER.toString()
                , Executors.newCachedThreadPool());
        for(int i=0;i<NUMofRecv_Listener_Threads;i++){
            try {
                sysSched.submitProcess(UDP_Thread_Pools.UDP_RECV_LISTENER.toString()
                        , new UDPRecvListener(channel, this));
                System.out.println("listener opened");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("UDP - Receive Listeners are started");
    }

    @Override
    protected void interrupt() {
    }
}