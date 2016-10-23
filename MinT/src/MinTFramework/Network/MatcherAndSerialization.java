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
        try{
        PacketDatagram matchedPacket = new PacketDatagram(packet);
        transportation.Receive(matchedPacket);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * @deprecated 
     * send Packet to End point network
     * @param packet 
     */
    @Override
    public void Send(PacketDatagram packet) {
    }

    /**
     * @deprecated 
     * @param packet 
     */
    @Override
    public void Receive(PacketDatagram packet) {
    }

    /**
     * @param sendmsg 
     */
    @Override
    public PacketDatagram EndPointSend(SendMSG sendmsg) {
        PacketDatagram packet = null;
        packet = new PacketDatagram(sendmsg);
//        PacketDatagram packet = new PacketDatagram(sendmsg.getResponseKey(), sendmsg.getHeader_Direction()
//        ,sendmsg.getHeader_Instruction(), null, null, sendmsg.getNextNode()
//        ,sendmsg.getFinalDestination(), sendmsg.Message());
        return packet;
    }
}
