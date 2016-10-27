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
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.SendMSG;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.Network.sharing.Sharing;
import MinTFramework.storage.ResData;
import java.util.List;
/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class SharingResponse implements Runnable{
    protected MinT frame = null;
    protected NetworkManager networkmanager = null;
    protected Sharing sharing = null;
    protected RoutingProtocol routing = null;
    
    protected ReceiveMessage recvmsg = null;
    protected PacketDatagram rv_packet = null;
    
    public SharingResponse(PacketDatagram _rv_packet, ReceiveMessage _recvmsg){
        frame = MinT.getInstance();
        networkmanager = frame.getNetworkManager();
        sharing = networkmanager.getSharing();
        routing = networkmanager.getRoutingProtocol();
        recvmsg = _recvmsg;
        rv_packet = _rv_packet;
    }

    @Override
    public void run() {
        SendMessage sendmsg = null;
        //check cached resource in resource storage
        checkCachedResource();
        
        //get localResource
        getLocalResource();
        
        //get Resource
        getResource();
        
        //calculate resource
        calculateResource();
        
        //response message
        send(sendmsg);
    }
    
    private void checkCachedResource(){
        
    }
    
    private void calculateResource(){
        
    }
    
    protected void getGroupResource(){
        
    }
    
    public abstract void getResource();
    
    private List<ResData> getLocalResource() {
        System.out.println("get Loal Datas by DeviceType: "+recvmsg.getResourceName());
        if(sharing == null)
            System.out.println("sharing null");
        for(ResData rd: sharing.getLocalResource(recvmsg)){
            System.out.println("local data: "+rd.getResourceString());
        }
        
        return null;
    }
    
    protected void send(SendMessage sendmsg){
        //Response MSG
        if(sendmsg != null)
            networkmanager.SEND(new SendMSG(PacketDatagram.HEADER_TYPE.NON, 0
                        , PacketDatagram.HEADER_CODE.CONTENT, sendmsg.getRequestNode(), sendmsg, rv_packet.getMSGID()));
    }

}
