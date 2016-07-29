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
import MinTFramework.Exception.NetworkException;
import MinTFramework.Network.BLE.BLE;
import MinTFramework.Network.PacketProtocol.HEADER_DIRECTION;
import MinTFramework.Network.PacketProtocol.HEADER_INSTRUCTION;
import MinTFramework.Network.Routing.MinTSharing.MinTRoutingProtocol;
import MinTFramework.Network.UDP.UDP;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResourceStorage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NetworkManager {
    private MinT frame = null;
    private ResourceStorage resourceStorage = null;
    private final ArrayList<NetworkType> networkList;
    private final HashMap<NetworkType,Network> networks;
    private String NodeName = null;
    
    private Handler networkHandler = null;
    private RoutingProtocol routing;
    
    private final HashMap<Integer,ResponseHandler> ResponseList = new HashMap<>();
    
    private final DebugLog dl;
    
    /**
     * Auto Set Network Manager as possible
     *
     * @param frame
     */
    public NetworkManager(MinT frame, ResourceStorage resStorage) {
        this.dl = new DebugLog("NetworkManager",true);
        this.networkList = new ArrayList<>();
        this.networks = new HashMap<>();
        this.frame = frame;
        routing = new MinTRoutingProtocol();
        resourceStorage = resStorage;
        networkHandler = new Handler(frame) {
            @Override
            public void userHandler(PacketProtocol rev_packet) {
            }
        };

        setNodeName();
    }
    
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
        for (NetworkType ty : networkList) {
            setOnNetwork(ty);
        }
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
            networks.put(ntype, new UDP(ntype.getPort(),routing,this.frame,this));
            dl.printMessage("Turned on UDP: "+ntype.getPort());
        }
        else if(ntype == NetworkType.BLE){
            dl.printMessage("Starting BLE...");
            networks.put(ntype, new BLE(routing, frame, this));
            dl.printMessage("Turned on BLE");
        } else if (ntype == NetworkType.COAP) { // for CoAP, need to add
            dl.printMessage("Turned on COAP");
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
     * for Test/
     * @deprecated 
     * @return 
     */
    public RoutingProtocol getRoutingProtocol(){
        return routing;
    }

    /**
     * get Final Destination using Routing Protocol
     * @param dst
     * @return 
     */
    private Profile getFinalDestination(Profile dst) {
        Profile fdst = null;
        if (dst.isNameProfile()) {
            //라우팅 스토어에서 검색
            fdst = dst;
        } else {
            fdst = dst;
        }
        return fdst;
    }
    
    /**
     * RESPONSE MSG
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (GET, SET, POST, PUT, DELETE, OBSERVE)
     * @param dst Destination profile
     * @param msg MSG
     * @param resKey 
     */
    public void RESPONSE(HEADER_DIRECTION hd, HEADER_INSTRUCTION hi, Profile dst, String msg, int resKey){
        Profile fdst = getFinalDestination(dst);
        PacketProtocol npacket = new PacketProtocol(resKey, hd, hi, null, null, getNextNode(fdst), fdst, msg);
        sendMsg(npacket);
    }
    
    /**
     * SEND
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (SET, POST, PUT, DELETE)
     * @param dst Destination profile
     * @param msg MSG
     */
    public void SEND(HEADER_DIRECTION hd, HEADER_INSTRUCTION hi, Profile dst, String msg){
        RESPONSE(hd,hi,dst,msg,PacketProtocol.HEADER_MSGID_INITIALIZATION);
    }
    
    /**
     * send for response
     * @param hd Direction (Request, Response)
     * @param hi Instruction (GET, OBSERVE)
     * @param dst Destination profile
     * @param msg MSG
     * @param resHandle response handler (need to GET, OBSERVE)
     */
    public void SEND_FOR_RESPONSE(HEADER_DIRECTION hd, HEADER_INSTRUCTION hi, Profile dst, String msg,
            ResponseHandler resHandle){
        Profile fdst = getFinalDestination(dst);
        PacketProtocol npacket = null;
        //if this packet need to response from target Node
        if(hd.isRequest() && hi.NeedResponse()){
            npacket = new PacketProtocol(makePacketID(),hd, hi, null, null, getNextNode(fdst), fdst, msg);
            ResponseList.put(npacket.getMSGID(), resHandle);
            dl.printMessage("Send MSG ID : "+npacket.getMSGID());
            sendMsg(npacket);
        }
    }
    
    

    /**
     * Stop Over Processor
     *
     * @param packet
     */
    public void stopOver(PacketProtocol packet) {

    }

    /**
     * Routing Protocol
     *
     * @param fdst
     * @return
     */
    private Profile getNextNode(Profile fdst) {
        //Serch Routing Protocol
        return fdst;
    }

    /**
     * *
     * 얘는 프로토콜에서 목적지의 프로토콜에 따라 보내는 네트워크를 선택
     *
     * @param dst
     * @param msg
     */
    private void sendMsg(PacketProtocol packet) {
        NetworkType nnodetype = packet.getNextNode().getNetworkType();
        Network sendNetwork = networks.get(nnodetype);
        
        //Send Message
        if (sendNetwork != null) {
                //set Source Node
            if (packet.getSource() == null) {
                packet.setSource(sendNetwork.getProfile());
            }

            //set Previous Node
            if (packet.getPreviosNode() == null) {
                packet.setPrevNode(sendNetwork.getProfile());
            }

            //set Response Handler
    //        this.networkHandler.

            try {
                dl.printMessage("Send Network" + sendNetwork.getNetworkType());
                dl.printMessage("Packet :" + packet.getPacketString());
                sendNetwork.send(packet);
            } catch (NetworkException ex) {
                ex.printStackTrace();
            }
        } else {
            dl.printMessage("Error : There are no Networks");
            System.out.println("Error : There are no Networks");
        }
    }
    
    private int makePacketID() {
        int length = MinTConfig.RESPONSE_ID_MAX;
        boolean rid[] = new boolean[length];
        int newid = 1;
        
        for(int i=1;i<length;i++){
            rid[i] = false;
        }
        
        //check serviceQueue
        for(int k : ResponseList.keySet()){
            rid[k] = true;
        }
        //Make New ID
        
        newid = 1;
        while(true){
            if(rid[newid] == false){
                break;
            }
            else{
                newid++;
            }
        }

        return newid;
    }

    /**
     * set Network Handler in network manager
     *
     * @param nhandler
     */
    public void setNetworkHandler(Handler nhandler) {
        if (nhandler != null) {
            this.networkHandler = nhandler;
        }
    }

    /**
     * get Network Handler
     *
     * @return
     */
    public Handler getNetworkHandler() {
        return this.networkHandler;
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
    public ResponseHandler getResponseDataMatchbyID(int num){
        dl.printMessage("insert key : "+num);
        ResponseHandler resd = ResponseList.get(num);
        if(resd != null){
            dl.printMessage("Remove Response List");
            ResponseList.remove(num);
        }
        
        dl.printMessage("Response List Size : "+ResponseList.size());
        for(int ks : ResponseList.keySet()){
            dl.printMessage("Key set : "+ks);
        }
        return resd;
    }
}
