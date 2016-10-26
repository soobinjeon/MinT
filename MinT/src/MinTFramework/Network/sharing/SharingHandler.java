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
package MinTFramework.Network.sharing;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.storage.ResourceStorage;
import MinTFramework.storage.datamap.Information;

/**
 *
 * @author soobin
 */
public class SharingHandler {
    private MinT frame = null;
    private NetworkManager networkManager = null;
    private ResourceStorage resStorage = null;
    private Sharing sharing = null;
    private RoutingProtocol routing = null;
    
    public SharingHandler(Sharing _sharing) {
        frame = MinT.getInstance();
        networkManager = frame.getNetworkManager();
        resStorage = frame.getResStorage();
        sharing = _sharing;
        routing = networkManager.getRoutingProtocol();
    }

    /**
     * 
     * @param rv_packet
     * @param recvmsg 
     */
    public void receiveHandle(PacketDatagram rv_packet, ReceiveMessage recvmsg) {
        if(rv_packet.getHeader_Code().isRequest())
            requestHandle(rv_packet, recvmsg);
        else if(rv_packet.getHeader_Code().isResponse())
            responsehandle(rv_packet, recvmsg);
    }

    private void requestHandle(PacketDatagram rv_packet, Request req) {
//        Information data = req.getResourcebyName(Request.MSG_ATTR.Routing);
    }

    private void responsehandle(PacketDatagram rv_packet, Request req) {
//        Information rdata = req.getResourcebyName(Request.MSG_ATTR.Routing);
    }    
}
