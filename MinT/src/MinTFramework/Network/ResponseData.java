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

import MinTFramework.storage.datamap.Information;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResponseData extends Information{
    private PacketProtocol recv_packet;
    private Profile source;
    public ResponseData(PacketProtocol recv_packet) {
        this(recv_packet, recv_packet.getMsgData());
    }
    public ResponseData(PacketProtocol recv_packet, Object data) {
        super(data);
        source = recv_packet.getSource();
        this.recv_packet = recv_packet;
    }
    
    @Override
    public void setResource(Object setres){
        super.setResource(setres);
    }
    
    public Profile getSourceInfo(){
        return source;
    }
    
    public Profile getDestination(){
        return recv_packet.getDestinationNode();
    } 

    @Override
    public Object getClone() {
        return new ResponseData(recv_packet, getResource());
    }
    
}
