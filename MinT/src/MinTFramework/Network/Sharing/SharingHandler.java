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
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.storage.datamap.Information;

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
    public void HandleRequest(PacketDatagram rv_packet, ReceiveMessage recvmsg) {
        //request analysis ( child node or other header)
//        System.out.print("this Node Info: HEADER("+routing.getCurrentNode().isHeaderNode()+")");
        Information svalue = recvmsg.getResourcebyName(Request.MSG_ATTR.Sharing);
//        if(svalue != null)
//            System.out.println(" REQUEST_info: "+svalue.getResourceInt());
        
        if(routing.hasChildNode(rv_packet.getSource())){ //Requested from Child Node
            System.out.println("requrest from Client");
            sharing.executeResponse(new ChildResponce(rv_packet, recvmsg));
        }//Requested from other Header
        else if(routing.getCurrentNode().isHeaderNode()
                && svalue != null && svalue.getResourceInt() == SharingMessage.HEADER_REQUEST.getValue()){
            System.out.println("requrest from Other Header");
            sharing.executeResponse(new HeaderReponse(rv_packet, recvmsg));
        }else if(!routing.getCurrentNode().isHeaderNode()
                && svalue != null && svalue.getResourceInt() == SharingMessage.CLIENT_REQUEST.getValue()
                && routing.hasHeaderNode(rv_packet.getSource())){
            System.out.println("request from Header for gathering child's resource");
            if(sharing.getSystemHandler() != null)
                sharing.getSystemHandler().HandleRequest(rv_packet, recvmsg);
            else
                System.out.println("System Handler is Null");
            //시스템 스케쥴러 가져와서 실행시켜줘야함......
        }
    }

    @Override
    public void HandleResponse(PacketDatagram packet, ReceiveMessage receivemsg) {
    }
}
