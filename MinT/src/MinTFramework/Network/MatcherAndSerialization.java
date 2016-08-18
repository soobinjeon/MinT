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
public class MatcherAndSerialization implements NetworkLayers{
    private Transportation transportation = null;
    private MinT frame;
    private NetworkManager networkManager;
    private DebugLog dl = new DebugLog("MatcherAndSerialization");
    
    public MatcherAndSerialization(NetworkLayers.LAYER_DIRECTION layerDirection){
        frame = MinT.getInstance();
        networkManager = frame.getNetworkManager();
        
        if(layerDirection == NetworkLayers.LAYER_DIRECTION.RECEIVE)
            transportation = new Transportation(layerDirection);
    }

    @Override
    public void EndPointReceive(byte[] packet) {
        PacketDatagram matchedPacket = new PacketDatagram(packet);
        transportation.Receive(matchedPacket);
    }
    
    /**
     * @param packet 
     */
    @Override
    public void Send(PacketDatagram packet) {
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
            //        this.networkHandler.
            try {
                dl.printMessage("Send Network-" + sendNetwork.getNetworkType());
                dl.printMessage("Packet :" + packet.getPacketString());
                sendNetwork.send(packet);
//                networkManager.setSendHandlerCnt();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            dl.printMessage("Error : There are no Networks");
            System.out.println("Error : There are no Networks");
        }
    }

    /**
     * @deprecated 
     * @param packet 
     */
    @Override
    public void Receive(PacketDatagram packet) {
    }

    /**
     * @deprecated 
     * @param sendmsg 
     */
    @Override
    public void EndPointSend(SendMSG sendmsg) {
    }
    
}
