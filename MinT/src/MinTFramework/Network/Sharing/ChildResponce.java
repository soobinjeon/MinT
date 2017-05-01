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

import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.sharing.Sharing.RESOURCE_TYPE;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.storage.ThingProperty;
import java.util.List;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ChildResponce extends SharingResponse{

    public ChildResponce(PacketDatagram _rv_packet, ReceiveMessage _recvmsg) {
        super(_rv_packet, _recvmsg);
    }

    @Override
    public void getNetworkResource() {
        System.out.println("child Response Activated!");
        
        //get Group Resource
        getGroupResource();
        
        //get other Header Resource
        getHeaderResource();
    }

    private void getHeaderResource() {
        System.out.println("get Header Resource");
        List<Node> cnodes = routing.getHeaderNodes();
        ResponseWaiter waiter = new ResponseWaiter(this, RESOURCE_TYPE.HEADERRESOURCE);
        resWaiterList.add(waiter);
        
        for(Node n : cnodes){
            for(ThingProperty p: n.getProperties().values()){
                if(p.getDeviceType().isSameDeivce(recvmsg.getResourceName())){
                    System.out.println("setUP HEADER for send: "+n.gettoAddr().getAddress());
                    waiter.putPacket(n, p);
//                    sharing.getHeaderResource(new SendMessage(p.getDeviceType().getDeviceTypeString(),null)
//                            , waiter.putResponseHandler(p), n);
                }
            }
        }
    }
    
}
