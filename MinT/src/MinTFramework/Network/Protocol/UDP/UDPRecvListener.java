/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MinTFramework.Network.Protocol.UDP;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.RecvMSG;
import MinTFramework.Util.Benchmarks.PacketPerform;
import MinTFramework.Util.ByteBufferPool;
import MinTFramework.Util.DebugLog;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 *
 * @author soobin
 */
public class UDPRecvListener extends Thread{
    Selector selector;
    DatagramChannel channel;
    UDP udp = null;
    NetworkManager networkmanager = null;
    DebugLog dl = new DebugLog("UDPRecvAdaptor");
    private PacketPerform bench = null;
    private MinT parent;
    public UDPRecvListener(DatagramChannel channel, UDP udp) throws IOException{
        this.udp = udp;
        networkmanager = udp.getNetworkManager();
        selector = Selector.open();
        this.channel = channel;
        channel.register(selector, SelectionKey.OP_READ);
        parent = MinT.getInstance();
        if(parent.isBenchMode()){
            bench = new PacketPerform("UDP Recv");
            parent.addPerformance(MinT.PERFORM_METHOD.UDP_RECV, bench);
        }
    }

    @Override
    public void run() {
        try{
            while(!Thread.currentThread().isInterrupted()){
//                dl.printMessage(this.getID()+"-wait selector..");
                int KeysReady = selector.select();
//                dl.printMessage("Selector Accepted");
                RequestPendingConnection();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void RequestPendingConnection(){
//        dl.printMessage("in the RequestPending..");
        Iterator selectedKeys = selector.selectedKeys().iterator();
        while(selectedKeys.hasNext()){
                SelectionKey key = (SelectionKey) selectedKeys.next();
                selectedKeys.remove();
                if(!key.isValid())
                    continue;
                if(key.isReadable())
                    read(key);
        }
    }
    
    private void read(SelectionKey key){
        if(bench != null)
            bench.startPerform();
        ByteBufferPool bbp = networkmanager.getByteBufferPool();
        ByteBuffer req = null;
        byte[] fwdbyte = null;
        SocketAddress rd = null;
        try{
            req = bbp.getMemoryBuffer();
            DatagramChannel chan = (DatagramChannel)key.channel();
            //read
            rd = chan.receive(req);
            //sort pointer
            req.flip();
            
            //make received byte
            fwdbyte = new byte[req.limit()];
            req.get(fwdbyte, 0, req.limit());
            udp.putReceiveHandler(new RecvMSG(fwdbyte,rd, NetworkType.UDP));
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            bbp.putBuffer(req);
            if(bench != null)
                bench.endPerform(req.limit());
        }
    }
}
