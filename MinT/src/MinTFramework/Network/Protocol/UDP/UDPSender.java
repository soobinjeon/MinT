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
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Util.Benchmarks.Performance;
import MinTFramework.Util.ByteBufferPool;
import MinTFramework.Util.DebugLog;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPSender implements Runnable {
    MinT frame;
    NetworkManager nmanager;
//    Selector selector;
    DebugLog dl = new DebugLog("UDPSender");
    Performance ppf = null;
    
    byte[] _sendMsg;
    SocketAddress sendAddr;
    String name;
    public UDPSender(String _name) throws IOException{
        frame = MinT.getInstance();
        nmanager = frame.getNetworkManager();
        name = _name;
//        _sendMsg = _msg;
//        sendAddr = add;
    }
    
    @Override
    public void run() {
        UDPSendThread ust = (UDPSendThread)Thread.currentThread();
        DatagramChannel channel = ust.getDataChannel();
        Performance bench = ust.getBench();
        int bsize = 0;
        ByteBufferPool bbp = nmanager.getByteBufferPool();
        ust.checkBench();
        
        while (!Thread.currentThread().isInterrupted()) {
            PacketDatagram packet = ust.getDatafromQueue();
            
            if(packet == null){
                continue;
            }
            _sendMsg = packet.getPacket();
            if(!ust.isMulticast()){
                NetworkProfile dst = packet.getNextNode();
                sendAddr = new InetSocketAddress(dst.getIPAddr(), dst.getPort());
            }else
                sendAddr = new InetSocketAddress(MinTConfig.CoAP_MULTICAST_ADDRESS, ust.getUDP().getPort());
            
            bsize = 0;
            if (bench != null) {
                bench.startPerform();
            }
            ByteBuffer out = null;
            try {
                out = bbp.getMemoryBuffer();
                out.put(_sendMsg);
                out.flip();
                bsize = channel.send(out, sendAddr);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Sender Closed by Thread Stop Interrupt");
            } finally {
                bbp.putBuffer(out);
                if (bench != null) {
                    bench.endPerform(out.limit());
                }
            }
        }
    }
}
