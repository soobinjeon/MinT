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
 * MinT Matcher and Serialization
 * Matcher
 *  - Match packet from end point network
 *  - 
 * Serialization
 *  - 
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MatcherAndSerialization implements NetworkLayers{
    private Transportation transportation = null;
    private MinT frame;
    private NetworkManager networkManager;
    private DebugLog dl = new DebugLog("MatcherAndSerialization");
//    private Performance bench_send = null;
    
    public MatcherAndSerialization(NetworkLayers.LAYER_DIRECTION layerDirection){
        frame = MinT.getInstance();
        networkManager = frame.getNetworkManager();
        
        if(layerDirection == NetworkLayers.LAYER_DIRECTION.RECEIVE)
            transportation = new Transportation(layerDirection);
        
        if(layerDirection == NetworkLayers.LAYER_DIRECTION.SEND){
//            if(frame.isBenchMode()){
//                bench_send = new PacketPerform("M/S-sender");
//                frame.addPerformance(MinT.PERFORM_METHOD.MaS_Sender, bench_send);
//            }
        }
    }

    /**
     * get Packet from Endpoint Receiver
     * @param packet 
     */
    @Override
    public void EndPointReceive(RecvMSG packet) {
        PacketDatagram_coap matchedPacket = new PacketDatagram_coap(packet);
        transportation.Receive(matchedPacket);
    }
    
    /**
     * send Packet to End point network
     * @param packet 
     */
    @Override
    public void Send(PacketDatagram_coap packet) {
//        if(bench_send != null)
//            bench_send.startPerform();
        NetworkType nnodetype = packet.getNextNode().getNetworkType();
        Network sendNetwork = networkManager.getNetworks().get(nnodetype);
        
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
            try {
//                if(bench_send != null)
//                    bench_send.endPerform();
                
                sendNetwork.send(packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Error : There are no Networks");
        }
    }

    /**
     * @deprecated 
     * @param packet 
     */
    @Override
    public void Receive(PacketDatagram_coap packet) {
    }

    /**
     * @deprecated 
     * @param sendmsg 
     */
    @Override
    public void EndPointSend(SendMSG sendmsg) {
    }
    
}
