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
package MinTFramework.Network.Sharing;

import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class HeaderReponse extends SharingResponse{

    public HeaderReponse(PacketDatagram _rv_packet, ReceiveMessage _recvmsg) {
        super(_rv_packet, _recvmsg);
    }

    @Override
    public void getNetworkResource() {
        //get Group Resource
        getGroupResource();
    }
    
}
