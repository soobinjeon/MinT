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

import MinTFramework.Network.Handler;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;

/**
 *
 * @author soobin
 */
public class SharingHandler extends Handler{
    private Sharing sharing = null;
    private RoutingProtocol routing = null;
    
    public SharingHandler(Sharing _sharing) {
        sharing = _sharing;
        routing = nmanager.getRoutingProtocol();
    }
    
    /**
     * handler for response to child or header node
     * @param packet
     * @param receivemsg 
     */
    @Override
    protected void HandleRequest(PacketDatagram rv_packet, ReceiveMessage recvmsg) {
        //request analysis ( child node or other header)
        System.out.println("request for Header");
        if(routing.hasChildNode(rv_packet.getSource()))
            sharing.executeResponse(new ChildResponce(rv_packet, recvmsg));
        else
            sharing.executeResponse(new HeaderReponse(rv_packet, recvmsg));
    }

    @Override
    protected void HandleResponse(PacketDatagram packet, ReceiveMessage receivemsg) {
    }
}
