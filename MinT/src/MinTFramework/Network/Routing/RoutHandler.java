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
package MinTFramework.Network.Routing;

import MinTFramework.MinT;
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.SendMSG;
import MinTFramework.storage.ResourceStorage;
import MinTFramework.storage.datamap.Information;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RoutHandler {

    protected MinT frame;
    protected NetworkManager networkManager;
    protected ResourceStorage resStorage;
    protected RoutingProtocol rout;
    
    public RoutHandler(RoutingProtocol rprotocol) {
        frame = MinT.getInstance();
        networkManager = frame.getNetworkManager();
        resStorage = frame.getResStorage();
        rout = rprotocol;
    }

    void receiveHandle(PacketDatagram rv_packet) {
        Request req = new ReceiveMessage(rv_packet.getMsgData(), rv_packet.getSource());
        
        if(rv_packet.getHeader_Code().isRequest())
            requestHandle(rv_packet, req);
        else if(rv_packet.getHeader_Code().isResponse())
            responsehandle(rv_packet, req);
    }

    private void requestHandle(PacketDatagram rv_packet, Request req) {
        Request ret = null;
        Information data = req.getResourcebyName(Request.MSG_ATTR.Routing);
        if(R_MSG.NODE_BROADCAST.isEqual(data.getResourceInt())){
            System.out.println("receive node broadcast");
        }
        if(isDiscovery(req)){
            System.out.println("Request out in routing handler");
            Network cnet = frame.getNetworkManager().getNetwork(rv_packet.getSource().getNetworkType());
            String redata = resStorage.DiscoverLocalResource(cnet.getProfile()).toJSONString();
            ret = new SendMessage(null, redata)
                    .AddAttribute(Request.MSG_ATTR.Routing, null)
                    .AddAttribute(Request.MSG_ATTR.WellKnown, null);
        }
        
        if(ret != null){
            networkManager.SEND(new SendMSG(PacketDatagram.HEADER_TYPE.NON, 0
                    , PacketDatagram.HEADER_CODE.CONTENT, rv_packet.getSource(), ret, rv_packet.getMSGID()));
        }
    }

    private void responsehandle(PacketDatagram rv_packet, Request req) {
        ResponseData resdata = new ResponseData(rv_packet, req.getResourceData().getResource());
        if(isDiscovery(req)){
            System.out.println("update discovery data in Routing Handler");
            resStorage.updateDiscoverData(resdata);
        }
    }

    private boolean isDiscovery(Request req) {
        if(req.getResourcebyName(Request.MSG_ATTR.WellKnown) != null)
            return true;
        else
            return false;
    }
    
}
