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

import MinTFramework.Network.NetworkType;
import MinTFramework.Network.RecvMSG;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.Arrays;

/**
 *
 * @author Administrator
 */
public class UDPRecvThread extends UDPThread{
    private MulticastSocket socket;
    private DatagramPacket datagram;
    private int ReceivedSize = 0;
    
    public UDPRecvThread(MulticastSocket _socket, UDP udp, int cnt) throws IOException{
        super("UDP_RECEIVER_"+cnt, udp, UDP.UDP_Thread_Pools.UDP_RECV_LISTENER);
        socket = _socket;
        ReceivedSize = UDP.receiverPacketSize;
        datagram = new DatagramPacket(new byte[ReceivedSize], ReceivedSize);
    }
    
    long stime = 0;
    long etime = 0;
    long total = 0;
    double cnt = 0;
    double sec = 1000000000.0;

    @Override
    public void run() {
        int size = 0;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                size = 0;
//                checkBench();
//                startPerform();
                
                datagram.setLength(ReceivedSize);
                stime = System.nanoTime();

                datagram.setLength(ReceivedSize);
                socket.receive(datagram);
                byte[] bytes = Arrays.copyOfRange(datagram.getData(), datagram.getOffset(), datagram.getLength());
                SocketAddress rd = datagram.getSocketAddress();

                etime = System.nanoTime();
                total += etime - stime;
                cnt++;
                if (cnt % 10000 == 0) {
                    double avg = (double) (total / sec);
                    double rps = cnt / avg;
                    System.out.println(name+"== Req/Sec : " + rps + ", per byte: " + bytes.length);
                    cnt = 0;
                    total = 0;
                }
//                System.out.println("recv: " + new String(bytes) + ", size: " + bytes.length);
                size = bytes.length;
                udp.putReceiveHandler(new RecvMSG(bytes, rd, NetworkType.UDP));
            } catch (Exception t) {
                System.out.println(t.getMessage());
            } finally{
//                endPerform(size);
            }
        }
    }
}
