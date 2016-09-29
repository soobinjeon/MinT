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

import MinTFramework.MinT;
import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Transportation implements NetworkLayers{
    private MinT frame;
    private NetworkManager networkManager;
    private SystemHandler syshandle = null;
    private MatcherAndSerialization serialization = null;
    
    DebugLog dl = new DebugLog("Transportation");
//    private Performance bench_send = null;
    
    public Transportation(NetworkLayers.LAYER_DIRECTION layerDirection){
        frame = MinT.getInstance();
        this.networkManager = frame.getNetworkManager();        
        
        if(layerDirection == NetworkLayers.LAYER_DIRECTION.RECEIVE)
            syshandle = new SystemHandler();
        
        if(layerDirection == NetworkLayers.LAYER_DIRECTION.SEND){
            serialization = new MatcherAndSerialization(layerDirection);
//            if(frame.isBenchMode()){
//                bench_send = new PacketPerform("Trans-sender");
//                frame.addPerformance(MinT.PERFORM_METHOD.Trans_Sender, bench_send);
//            }
        }
    }

    @Override
    public void Receive(PacketDatagram_coap packet) {
        if(isFinalDestination(packet.getDestinationNode())){
            syshandle.startHandle(packet);
//            networkManager.setHandlerCount();
        }
        else{
            stopOver(packet);
        }
    }

    private boolean isFinalDestination(NetworkProfile destinationNode) {
        for(Network cn : this.networkManager.getNetworks().values()){
            if(cn.getProfile().equals(destinationNode))
                return true;
        }
        return false;//currnetProfile.equals(destinationNode);
    }

    /**
     * Stop Over Method
     * @param packet 
     */
    private void stopOver(PacketDatagram_coap packet) {
        
    }
    
    @Override
    public void EndPointSend(SendMSG sendmsg) {
        //Find Final Destination from Routing
//        if(bench_send != null)
//            bench_send.startPerform();
        NetworkProfile fdst = getFinalDestination(sendmsg.getDestination());
        
        PacketDatagram_coap npacket = null;
        if(sendmsg.isResponse()){
            npacket = new PacketDatagram_coap(sendmsg.getResponseKey(), sendmsg.getVersion()
                    ,sendmsg.getHeader_Type(), sendmsg.getTokenLength()
                    ,sendmsg.getHeader_Code(), null, null, getNextNode(sendmsg.getDestination())
                    ,fdst, sendmsg.Message());
        }else if(sendmsg.isRequest()){
            npacket = new PacketDatagram_coap(sendmsg.getResponseKey(), sendmsg.getVersion()
                    ,sendmsg.getHeader_Type(), sendmsg.getTokenLength()
                    ,sendmsg.getHeader_Code(), null, null, getNextNode(sendmsg.getDestination())
                    ,fdst, sendmsg.Message());
        }
        else if(sendmsg.isRequestGET()){
            //check resend information
            if(sendmsg.getSendHit() == 0){
                sendmsg.setResKey(networkManager.getIDMaker().makePacketID());
                networkManager.putResponse(sendmsg.getResponseKey(), sendmsg);
            }
            
            npacket = new PacketDatagram_coap(sendmsg.getResponseKey(), sendmsg.getVersion()
                    ,sendmsg.getHeader_Type(), sendmsg.getTokenLength()
                    ,sendmsg.getHeader_Code(), null, null, getNextNode(sendmsg.getDestination())
                    ,fdst, sendmsg.Message());
        }
        //msg sending count
        sendmsg.Sended();
//        if(bench_send != null)
//            bench_send.endPerform();
        //send packet
        Send(npacket);
    }
    
    /**
     * Routing Protocol
     *
     * @param fdst
     * @return
     */
    private NetworkProfile getNextNode(NetworkProfile fdst) {
        //Serch Routing Protocol
        return fdst;
    }
    
    /**
     * get Final Destination using Routing Protocol
     * @param dst
     * @return 
     */
    private NetworkProfile getFinalDestination(NetworkProfile dst) {
        NetworkProfile fdst = null;
//        if (dst.isNameProfile()) {
//            //라우팅 스토어에서 검색
//            fdst = dst;
//        } else {
            fdst = dst;
//        }
        return fdst;
    }
    
    @Override
    public void Send(PacketDatagram_coap packet) {
        serialization.Send(packet);
    }
    
    /**
     * @deprecated 
     * @param packet 
     */
    @Override
    public void EndPointReceive(RecvMSG packet) {
    }
}
