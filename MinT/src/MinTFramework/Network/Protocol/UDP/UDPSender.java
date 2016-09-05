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
import MinTFramework.Network.NetworkManager;
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
    UDP udp;
//    Selector selector;
    DebugLog dl = new DebugLog("UDPSender");
    Performance ppf = null;
    
    byte[] _sendMsg;
    SocketAddress sendAddr;
    
    public UDPSender(UDP udp, byte[] _msg, SocketAddress add) throws IOException{
        frame = MinT.getInstance();
        nmanager = frame.getNetworkManager();
        this.udp = udp;
        _sendMsg = _msg;
        sendAddr = add;
    }
    
    @Override
    public void run() {
        UDPSendThread ust = (UDPSendThread)Thread.currentThread();
        DatagramChannel channel = ust.getDataChannel();
        Performance bench = ust.getBench();
        int bsize = 0;
        
        if(bench != null)
            bench.startPerform();
        ByteBufferPool bbp = nmanager.getByteBufferPool();
        ByteBuffer out = null;
        try{        
            out = bbp.getMemoryBuffer();
            out.put(_sendMsg);
            out.flip();
            bsize = channel.send(out, sendAddr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally{
            bbp.putBuffer(out);
            if(bench != null)
                bench.endPerform(out.limit());
        }
    }
}
