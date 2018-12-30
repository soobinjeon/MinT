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
package MinTFramework.Network.Sharing.routingprotocol;

import MinTFramework.Network.Handler;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Sharing.routingprotocol.RoutingProtocol.ROUTING_PHASE;
import MinTFramework.storage.datamap.Information;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RoutHandler extends Handler{
    private RoutingProtocol rout;
    private ConcurrentHashMap<ROUTING_PHASE, Phase> routingPhase;
    
    public RoutHandler(RoutingProtocol rprotocol) {
        rout = rprotocol;
        routingPhase = rout.phases;
    }
    
    @Override
    public void HandleRequest(PacketDatagram rv_packet, ReceiveMessage receivemsg) {
        Information data = receivemsg.getResourcebyName(Request.MSG_ATTR.Routing);
        /**
         * Operate a message according to routing phase
         */
        for (Phase cp : routingPhase.values()) {
            if (cp.hasMessage(data.getResourceInt())) {
                cp.requestHandle(rv_packet, receivemsg);
                break;
            }
        }
    }

    @Override
    public void HandleResponse(PacketDatagram rv_packet, ReceiveMessage req) {
        Information rdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
        
        for(Phase cp : routingPhase.values()){
            if(cp.hasMessage(rdata.getResourceInt())){
                cp.responseHandle(rv_packet, req);
                break;
            }
        }
    }
}
