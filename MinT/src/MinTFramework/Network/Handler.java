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
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResourceStorage;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Handler {
    protected MinT frame;
    protected ResourceStorage resStorage;
    protected NetworkManager nmanager;
    protected DebugLog dl = new DebugLog("Handler");
    
    public Handler(){
        this.frame = MinT.getInstance();
        resStorage = frame.getResStorage();
        nmanager = frame.getNetworkManager();
    }
    
    public void startHandle(RecvMSG recvmsg){
        PacketDatagram packet = recvmsg.getPacketDatagram();
        ReceiveMessage receivemsg = new ReceiveMessage(packet.getMsgData(), packet.getSource(), recvmsg);
        
        if(packet.getRoleDirection().isRequest())
            HandleRequest(packet, receivemsg);
        else{
            HandleResponse(packet, receivemsg);
            
            //Response Handle
            ResponseHandler reshandle = packet.getRecvHandler();
            if (reshandle != null) {
                ResponseData resdata = new ResponseData(packet, receivemsg.getResourceData().getResource());
                reshandle.Response(resdata);
            }
        }
    }
    
    protected abstract void HandleRequest(PacketDatagram packet, ReceiveMessage receivemsg);
    protected abstract void HandleResponse(PacketDatagram packet, ReceiveMessage receivemsg);
}
