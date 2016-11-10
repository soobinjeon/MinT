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
import MinTFramework.Network.PacketDatagram;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDPSendThread extends UDPThread{
    private DatagramSocket socket;
    private DatagramPacket datagram;
    private final String MulticastAddress = MinTConfig.CoAP_MULTICAST_ADDRESS;
    private final int MultiPort = MinTConfig.INTERNET_COAP_PORT;
    private Runnable r;
    
    private int d_size = 0;
    
    public UDPSendThread(Runnable _r, DatagramSocket _socket, UDP udp, int cnt){
        super("UDP_SEND_"+cnt, udp, UDP.UDP_Thread_Pools.UDP_RECV_LISTENER);
        socket = _socket;
        datagram = new DatagramPacket(new byte[0], 0);
        r = _r;
//        this.setPriority(MAX_PRIORITY);
    }
    
    @Override
    public void run(){
        /**
        * Sender Bench Check
        * Not Use some problem
        * 보내는쪽 버퍼가 낮으면 벤치가 동작하지 않음
        * start 후 Send 네트워크 문제로 endbench가 실행되지 않아 MinTAnalize에서 Performance check 할때 기다림
        */
        //checkBench();
        try {
            d_size = 0;
            //startPerform();
            r.run();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            //endPerform(d_size);
        }
    }
    
    public void setByteSize(int size){
        d_size = size;
    }
    
    public void sendData(PacketDatagram packet) throws IOException{
        InetSocketAddress des;
        
        if(socket instanceof MulticastSocket){
            des = new InetSocketAddress(MulticastAddress,MultiPort);
        }
        else
            des = new InetSocketAddress(packet.getNextNode().getIPAddr()
                , packet.getNextNode().getPort());
        datagram.setData(packet.getPacket());
        datagram.setAddress(des.getAddress());
        datagram.setPort(des.getPort());
        socket.send(datagram);
        setByteSize(datagram.getLength());
    }
}
