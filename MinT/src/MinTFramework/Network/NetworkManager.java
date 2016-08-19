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
package MinTFramework.Network;

import MinTFramework.*;
import MinTFramework.Network.Protocol.BLE.BLE;
import MinTFramework.Network.Routing.MinTSharing.MinTRoutingProtocol;
import MinTFramework.Network.Protocol.UDP.UDP;
import MinTFramework.Network.Routing.RoutingProtocol;
import MinTFramework.Util.ByteBufferPool;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResourceStorage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NetworkManager {
    private MinT frame = null;
    private ResourceStorage resourceStorage = null;
    private final ArrayList<NetworkType> networkList;
    private final ConcurrentHashMap<NetworkType,Network> networks;
    private String NodeName = null;
    
    private RoutingProtocol routing;
    
    //Network Recv Adaptor Pool for Handling the Recv Data
    private ReceiveAdaptPool NetworkRecvAdaptPool;
    
    //Network Send Adaptor Pool for Send Data
    private SendAdaptPool NetworkSendPool;
    
    //for Network Recv ByteBuffer
    private ByteBufferPool bytepool = null;
    
    //Message Response List
    private final ConcurrentHashMap<Long,SendMSG> ResponseList = new ConcurrentHashMap<>();
    private PacketIDManager idmaker;
    
    //Temporary properties for check
    private int tempHandlerCnt = 0;
    private int sendHandlerCnt = 0;
    private final DebugLog dl;
    
    /**
     * Auto Set Network Manager as possible
     *
     * @param frame
     */
    public NetworkManager() {
        this.dl = new DebugLog("NetworkManager",true);
        this.networkList = new ArrayList<>();
        this.networks = new ConcurrentHashMap<>();
        this.frame = MinT.getInstance();
        resourceStorage = frame.getResStorage();
        setNodeName();
        dl.printMessage("set ByteBuffer");
        makeBytebuffer();
        
        routing = new MinTRoutingProtocol();
        
        idmaker = new PacketIDManager(ResponseList);
        
        NetworkRecvAdaptPool = new ReceiveAdaptPool("Receive Adaptor Pool", 
                MinTConfig.NETWORK_WAITING_QUEUE, MinTConfig.NETWORK_THREADPOOL_NUM);
        
        NetworkSendPool = new SendAdaptPool("Send Adaptor Pool", 
                MinTConfig.NETWORK_WAITING_QUEUE, 1);
    }
    
    /**
     * Init Routing Algorithm
     */
    private void initRoutingSetup(){
        dl.printMessage("routing init");
        if(resourceStorage == null)
            dl.printMessage("resource null");
        routing.setParents(this, frame, resourceStorage);
    }

    /**
     * add network
     *
     * @param ntype
     */
    public void AddNetwork(NetworkType ntype) {
        networkList.add(ntype);
    }
    
    /**
     * Start when MinT Start
     */
    public void onStart() {
        initRoutingSetup();
        TurnOnNetwork();
    }

    /**
     * Turn on All Networks!
     */
    private void TurnOnNetwork() {
        //Setting on Networks
        for (NetworkType ty : networkList) {
            setOnNetwork(ty);
        }
        
        //run Threadpool for network
        NetworkRecvAdaptPool.StartPool();
        NetworkSendPool.StartPool();
    }

    /**
     * *
     * Set Up the networks Available Networks : UDP, BLE, COAP(asap)
     *
     * @param ntype type of Network NetworkType
     * @param port Internet port for (UDP,TCP/IP,COAP), null for others
     */
    public void setOnNetwork(NetworkType ntype) {
        if(ntype == NetworkType.UDP){
            dl.printMessage("Starting UDP... "+ntype.getPort());
            networks.put(ntype, new UDP(frame.getNodeName(),ntype.getPort()));
            dl.printMessage("Turned on UDP: "+ntype.getPort());
        }
        else if(ntype == NetworkType.BLE){
            dl.printMessage("Starting BLE...");
            networks.put(ntype, new BLE(frame.getNodeName()));
            dl.printMessage("Turned on BLE");
        } else if (ntype == NetworkType.COAP) { // for CoAP, need to add
            dl.printMessage("Turned on COAP");
        }
        
        //Turn On All Network
        Iterator it = networks.values().iterator();
        while(it.hasNext()){
            Network nn = (Network)it.next();
            nn.TurnOnNetwork();
        }
    }

    /**
     * set Routing Protocol
     * @param ap 
     */
    public void setRoutingProtocol(RoutingProtocol ap) {
        this.routing = ap;

        for (Network n : networks.values()) {
            n.setApplicationProtocol(ap);
        }
    }
    
    /**
     * get Routing Protocol
     * @return 
     */
    public RoutingProtocol getRoutingProtocol(){
        return routing;
    }
    
    /**
     * Network Send Method
     * @param smsg 
     */
    public void SEND(SendMSG smsg){
        NetworkSendPool.putResource(smsg);
    }
    
    /**
     * get PacketID 
     * @return 
     */
    public PacketIDManager getIDMaker(){
        return idmaker;
    }
    
    /**
     * set Node Name
     *
     * @param name
     */
    public void setNodeName(String name) {
        if (name != null) {
            this.NodeName = name;
        }
    }

    /**
     * Return node name
     *
     * @return
     */
    public String getNodeName() {
        return NodeName;
    }

    /**
     * set automatically Node Name
     */
    private void setNodeName() {
        NodeName = "temporary Node";
    }
    
    /**
     * get Routing group of this node
     * @return 
     */
    public String getCurrentRoutingGroup(){
        return this.routing.getCurrentRoutingGroup();
    }
    
    /**
     * get Response Data matched by Response ID
     * @param num
     * @return 
     */
    public synchronized ResponseHandler getResponseDataMatchbyID(long num){
        SendMSG smsg = ResponseList.get(num);
        ResponseHandler resd = smsg.getResponseHandler();
        if(resd != null){
            ResponseList.remove(num);
        }
        return resd;
    }

    /**
     * Temporary Method
     */
    public synchronized void setHandlerCount(){
        this.tempHandlerCnt++;
    }
    
    public int getHandlerCount(){
        return tempHandlerCnt;
    }
    public synchronized void setSendHandlerCnt(){
        this.sendHandlerCnt++;
    }
    public int getSendHandlercnt(){
        return sendHandlerCnt;
    }
    
    /**
     * get Response Msg List
     * @return 
     */
    public synchronized ConcurrentHashMap<Long, SendMSG> getResponseList(){
        return ResponseList;
    }
    
    /**
     * get NetworkScheduler for operate network receiver
     * @return 
     */
    protected ReceiveAdaptPool getNetworkAdaptorPool(){
        if(NetworkRecvAdaptPool == null){
            dl.printMessage("NRA NULL");
        }
        return NetworkRecvAdaptPool;
    }
    
    /**
     * get Adapted Networks
     * @return 
     */
    protected ConcurrentHashMap<NetworkType,Network> getNetworks(){
        return this.networks;
    }
    
    public Network getNetwork(NetworkType ntype){
        return networks.get(ntype);
    }
    
    /**
     * get Network Queue waiting Length
     * @return 
     */
    public int getNetworkAdaptorQueueWaitingLength(){
        return NetworkRecvAdaptPool.getQueueWaitingLength();
    }
    
    /**
     * get Sender Waiting Queue
     * @return 
     */
    public int getNetworkSenderQueueWaitingLength(){
        return NetworkSendPool.getQueueWaitingLength();
    }
    
    
    
    /**
     * get ByteBufferPool
     * @return 
     */
    public ByteBufferPool getByteBufferPool(){
        return bytepool;
    }
    
    /**
     * Make ByteBuffer for Recv Byte Data
     */
    private void makeBytebuffer() {
        try {
            String fileName = "bufferpool.dat";
            File bfile = new File(fileName);;

            if (!existFile(fileName)) {
                bfile.createNewFile();
            }
            bfile.deleteOnExit();

            bytepool = new ByteBufferPool(20*1024, 40 * 2048, bfile);
        } catch (IOException ex) {
        }
    }
    
    private Boolean existFile(String isLivefile) {
        File f1 = new File(isLivefile);

        if (f1.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public void putResponse(long responseKey, SendMSG sendmsg) {
        ResponseList.put(responseKey, sendmsg);
//        System.out.println("size : "+ResponseList.size());
    }
    
    public int getResponseSize(){
        return ResponseList.size();
    }
}
