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

import MinTFramework.Exception.*;
import MinTFramework.MinT;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Network {

    protected MinT frame;
    private RoutingProtocol routing;
    private Profile profile;
    
    private boolean isworking = true;
    /***
     * set destination of packet
     * @param dst 
     */
    abstract protected void setDestination(Profile dst);
    /***
     * send packet
     * @param packet 
     */
    abstract protected void send(byte[] packet);

    /**
     * *
     * Constructor
     *
     * @param frame MinT Framework Object
     */
    public Network(MinT frame, Profile npro, RoutingProtocol _routing) {
        this.frame = frame;
        routing = _routing;
        profile = npro;
    }

    /***
     * 
     * Setting RoutingProtocol 
 !!! Not Network Procotol !!! 
 Default: "MinTApplicationProtocol"
     *
     * @param ap RoutingProtocol
     */
    public void setApplicationProtocol(RoutingProtocol routing) {
        this.routing = routing;
    }

    /**
     * *
     * Check Destination of packet if destination is here, call NetworkHandler
     * but, forwarding
     *
     * @param packet
     */
    public void MatcherAndObservation(byte[] packet) {
        PacketProtocol matchedPacket = new PacketProtocol(packet);
        if (isFinalDestiny(matchedPacket.getDestinationNode())) {
            frame.getNetworkHandler().callhadler(matchedPacket.getSrc(), matchedPacket.getMessage());
        } else {
            /**
             * Todo: 라우팅 테이블에서 다음 목표를 받아와야함
             */
            frame.sendMessage(matchedPacket.getDst(), matchedPacket.getMessage());
        }
    }

    /**
     * *
     * send message to dst
     *
     * @param dst destination of message
     * @param msg message to send
     * @throws MinTFramework.Exception.NetworkException
     */
    public void send(PacketProtocol packet) throws NetworkException{
        /**
         * *
         * Todo : dst로 가는 경로 중 다음 목적지를 설정하는 루틴 필요
         */
        if(!isWorking())
            throw new NetworkException(NetworkException.NE.NetworkNotWorking);
        else{
            this.setDestination(packet.getNextNode());
            this.send(packet.getPacket());
        }
    }

    /**
     * Check whether destination is here
     *
     * @param dst
     * @return
     */
    private boolean isFinalDestiny(Profile dst) {
        return frame.getNodeName().equals(dst);
    }
    
    protected void isWorking(boolean is){
        this.isworking = is;
    }
    
    protected boolean isWorking(){
        return isworking;
    }
}
