/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MinTFramework.Network.Protocol.UDP;

import MinTFramework.Schedule.Service;
import MinTFramework.Util.DebugLog;
import MinTFramework.Util.TypeCaster;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin
 */
public class UDPRecvListener extends Service{
    Selector selector;
    DatagramChannel channel;
    UDP udp = null;
    DebugLog dl = new DebugLog("UDPRecvAdaptor");
    
    public UDPRecvListener(InetSocketAddress isa, UDP udp) throws IOException{
        dl.printMessage("Set UDP Recv! - " + isa.toString() + ", port : "+isa.getPort());
        this.udp = udp;
        selector = Selector.open();
        channel = DatagramChannel.open();
        channel.socket().bind(isa);
        channel.configureBlocking(false);
        channel.setOption(StandardSocketOptions.SO_RCVBUF, 1024*1024*10);
        channel.register(selector, SelectionKey.OP_READ);
    }
    
    public UDPRecvListener(DatagramChannel channel, UDP udp) throws IOException{
        this.udp = udp;
        selector = Selector.open();
        this.channel = channel;
        channel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public void execute() {
        try{
            while(!Thread.currentThread().isInterrupted()){
//                dl.printMessage(this.getID()+"-wait selector..");
                int KeysReady = selector.select(500);
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
            try {
                SelectionKey key = (SelectionKey) selectedKeys.next();
                selectedKeys.remove();
                if(!key.isValid())
                    continue;
                if(key.isReadable())
                    read(key);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void read(SelectionKey key) throws IOException{
//        dl.printMessage("in the read");
        ByteBuffer req = ByteBuffer.allocate(512);
//        ByteBuffer req = bytepool.getMemoryBuffer();
        DatagramChannel chan = (DatagramChannel)key.channel();
        chan.receive(req);
        req.flip();
        udp.putReceiveHandler(req.array());
//        String str = TypeCaster.unzipStringFromBytes(req.array());
//        System.out.println(this.getID()+" = "+str);
    }
}
