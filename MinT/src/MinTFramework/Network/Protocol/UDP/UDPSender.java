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
import MinTFramework.Util.Benchmarks.PacketPerform;
import MinTFramework.Util.ByteBufferPool;
import MinTFramework.Util.DebugLog;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class UDPSender {
    MinT frame = MinT.getInstance();
    NetworkManager nmanager;
    UDP udp;
    Selector selector;
    DatagramChannel channel;
    DebugLog dl = new DebugLog("UDPSender");
    PacketPerform ppf = null;
    
    public UDPSender(DatagramChannel channel, UDP udp) throws IOException{
        selector = Selector.open();
        this.channel = channel;
        channel.register(selector, SelectionKey.OP_WRITE);
        nmanager = frame.getNetworkManager();
        this.udp = udp;
        //for bench
        if(frame.isBenchMode()){
            ppf = new PacketPerform("UDP SENDER");
            frame.addPerformance(MinT.PERFORM_METHOD.UDP_SEND, ppf);
        }
    }

    public void SendMsg(byte[] _msg, SocketAddress add) throws UnknownHostException, IOException{
        if(ppf != null)
            ppf.startPerform();
        int bsize = 0;
        ByteBufferPool bbp = nmanager.getByteBufferPool();
        ByteBuffer out = null;
        try{        
            out = bbp.getMemoryBuffer();
            out.put(_msg);
            out.flip();
            bsize = channel.send(out, add);
        }finally{
            bbp.putBuffer(out);
            if(ppf != null)
                ppf.endPerform(bsize);
        }
    }
}
