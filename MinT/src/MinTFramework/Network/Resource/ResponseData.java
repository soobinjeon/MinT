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
package MinTFramework.Network.Resource;

import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.MessageProtocol.CoAPPacket;
import MinTFramework.storage.datamap.Information;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResponseData extends Information{
    private CoAPPacket recv_packet;
    private NetworkProfile source;
    public ResponseData(CoAPPacket recv_packet) {
        this(recv_packet, recv_packet.getMsgData());
    }
    public ResponseData(CoAPPacket recv_packet, Object data) {
        super(data);
        source = recv_packet.getSource();
        this.recv_packet = recv_packet;
    }
    
    @Override
    public void setResource(Object setres){
        super.setResource(setres);
    }
    
    public NetworkProfile getSourceInfo(){
        return source;
    }
    
    public NetworkProfile getDestination(){
        return recv_packet.getDestinationNode();
    } 
    
    public CoAPPacket getPacketProtocol(){
        return this.recv_packet;
    }
}
