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
package MinTFramework.Network.MessageProtocol.coap;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.SendMSG;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class CoAPLeisureTask implements Runnable{
    private SendMSG sendmsg = null;
    private MinT mint = MinT.getInstance();
    private NetworkManager nmanager = null;
    
    public CoAPLeisureTask(SendMSG _msg){
        sendmsg = _msg;
        nmanager = mint.getNetworkManager();
    }

    @Override
    public void run() {
//        System.out.println("send leisure Data to "+sendmsg.getDestination().getAddress()
//        +sendmsg.Message()+", "+sendmsg.getHeader_Type());
        
        nmanager.SEND(sendmsg);
    }
    
}
