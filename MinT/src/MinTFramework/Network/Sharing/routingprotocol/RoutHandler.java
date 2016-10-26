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
package MinTFramework.Network.sharing.routingprotocol;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol.ROUTING_PHASE;
import MinTFramework.storage.ResourceStorage;
import MinTFramework.storage.datamap.Information;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RoutHandler {

    private MinT frame;
    private NetworkManager networkManager;
    private ResourceStorage resStorage;
    private RoutingProtocol rout;
    private ConcurrentHashMap<ROUTING_PHASE, Phase> routingPhase;
    
    public RoutHandler(RoutingProtocol rprotocol) {
        frame = MinT.getInstance();
        networkManager = frame.getNetworkManager();
        resStorage = frame.getResStorage();
        rout = rprotocol;
        routingPhase = rout.phases;
    }

    void receiveHandle(PacketDatagram rv_packet, ReceiveMessage recvmsg) {
        if(rv_packet.getHeader_Code().isRequest())
            requestHandle(rv_packet, recvmsg);
        else if(rv_packet.getHeader_Code().isResponse())
            responsehandle(rv_packet, recvmsg);
    }

    private void requestHandle(PacketDatagram rv_packet, Request req) {
        Information data = req.getResourcebyName(Request.MSG_ATTR.Routing);
        /**
         * Operate a message according to routing phase
         */
        for(Phase cp : routingPhase.values()){
            if(cp.hasMessage(data.getResourceInt())){
                cp.requestHandle(rv_packet, req);
                break;
            }
        }
    }

    private void responsehandle(PacketDatagram rv_packet, Request req) {
        Information rdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
        
        for(Phase cp : routingPhase.values()){
            if(cp.hasMessage(rdata.getResourceInt())){
                cp.responseHandle(rv_packet, req);
                break;
            }
        }
    }
}
