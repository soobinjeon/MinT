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
import MinTFramework.ThreadsPool.RejectedExecutionHandlerImpl;
import MinTFramework.Util.DebugLog;
import MinTFramework.Util.OSUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDP extends Network {
    private final int PORT;
    
    public static enum UDP_Thread_Pools {UDP_RECV_LISTENER, UDP_SENDER, UDP_MULTICAST_SENDER;};
    //UDP
    static final int UDP_SENDER_THREAD_CORE = 3;
    static final int UDP_SENDER_THREAD_MAX = 3;
    static final int UDP_SENDER_THREAD_QUEUE = 200000;
    static public final int UDP_NUM_OF_LISTENER_THREADS = 1;
    static public final int UDP_RECV_BUFF_SIZE = 1024*1024*10;
    
    private UDPSender sender;
    private InetSocketAddress isa;
    private DatagramChannel channel;
    private final int NUMofRecv_Listener_Threads = UDP_NUM_OF_LISTENER_THREADS;
    
    //Group Communication (Multicast)
    private DatagramChannel groupchannel;
    NetworkInterface interf;
    private InetAddress mulAddress;
    
    private final DebugLog log = new DebugLog("UDP.java");

    /**
     * UDP communication structure
     * UDP ports are divided to receiver and sender depending on each role
     * Receiver
     *    port number : set by MinTConfig (CoAP default : 5683)
     *    Datagram channel constructor is created by UDP
     * 
     * Sender
     *    port number : Receiver port + n
     *    Datagram Channel constructor is created by UDPSendFactory.java
     *    fix it: need to create in here
     * 
     * @param port port that want to use
     * @param _ap Protocol
     * @param frame MinT Frame
     * @param nm Network Manager
     */
    public UDP(String nodeName, NetworkType ntype) {
        super(new NetworkProfile(nodeName,OSUtil.getIPAddress()+":"+ntype.getPort(),ntype));
        if(!MinTConfig.IP_ADDRESS.equals("")){
            profile.setAddress(MinTConfig.IP_ADDRESS);
            log.printMessage(profile.getProfile());
        }
        PORT = ntype.getPort();
        this.portOpen();
        try {
            this.setUDPSocket();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        MakeUDPReceiveListeners();
        MakeUDPSender();
        MakeUDPMulticastSender();
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
        //Data Communication with Multicast
        InetAddress inetaddress = InetAddress.getByName(profile.getIPAddr());
        interf= NetworkInterface.getByInetAddress(inetaddress);
        mulAddress = InetAddress.getByName(MinTConfig.CoAP_MULTICAST_ADDRESS);
        isa = new InetSocketAddress(PORT);
        channel = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(isa)
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, interf)
                .setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
        channel.configureBlocking(false);
        channel.join(mulAddress, interf);
        
//        channel.setOption(StandardSocketOptions.SO_RCVBUF, UDP_RECV_BUFF_SIZE);
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
        //if Data Packet
        NetworkProfile dst = packet.getNextNode();
        SocketAddress add = new InetSocketAddress(dst.getIPAddr(), dst.getPort());
        sysSched.submitProcess(UDP_Thread_Pools.UDP_SENDER.toString()
                , new UDPSender(this, packet.getPacket(), add));
    }
    
    /**
     * Send Multi cast
     * @param packet 
     */
    @Override
    protected void sendMulticast(PacketDatagram packet) {
        try {
            //send to CoAP Group Address with CoAP port
            SocketAddress add = new InetSocketAddress(MinTConfig.CoAP_MULTICAST_ADDRESS, PORT);
            sysSched.submitProcess(UDP_Thread_Pools.UDP_MULTICAST_SENDER.toString()
                    , new UDPSender(this, packet.getPacket(), add));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void MakeUDPSender(){
        sysSched.registerThreadPool(UDP_Thread_Pools.UDP_SENDER.toString()
                , new ThreadPoolExecutor(UDP_SENDER_THREAD_CORE
                , UDP_SENDER_THREAD_MAX, 10
                , TimeUnit.SECONDS
                , new ArrayBlockingQueue<Runnable>(UDP_SENDER_THREAD_QUEUE)
                , new UDPSendFactory(PORT)
                , new RejectedExecutionHandlerImpl()));
    }
    
    private void MakeUDPMulticastSender(){
        System.out.println("Register Multicast Sender");
        sysSched.registerThreadPool(UDP_Thread_Pools.UDP_MULTICAST_SENDER.toString()
                , new ThreadPoolExecutor(1
                , 1, 10
                , TimeUnit.SECONDS
                , new ArrayBlockingQueue<Runnable>(UDP_SENDER_THREAD_QUEUE)
                , new UDPSendFactory(PORT, true, profile)
                , new RejectedExecutionHandlerImpl()));
    }

    /**
     * Make UDP Receive Listeners
     */
    private void MakeUDPReceiveListeners() {
        sysSched.registerThreadPool(UDP_Thread_Pools.UDP_RECV_LISTENER.toString()
                , Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new UDPRecvThread(r, "UDP_Receive_Listener");
            }
        }));
        for(int i=0;i<NUMofRecv_Listener_Threads;i++){
            try {
                sysSched.submitProcess(UDP_Thread_Pools.UDP_RECV_LISTENER.toString()
                        , new UDPRecvListener(channel, this));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("UDP - Receive Listeners are started");
    }
    
    public int getPort(){
        return PORT;
    }

    @Override
    protected void interrupt() {
    }
}