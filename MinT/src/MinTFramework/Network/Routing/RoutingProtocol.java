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
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.storage.ResourceStorage;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RoutingProtocol implements Runnable{
    protected MinT frame;
    protected NetworkManager networkManager;
    protected ResourceStorage resStorage;
    protected RoutHandler rhandle;
    protected RoutingTable routingtable;
    
    protected String groupName = "group";
    
    public RoutingProtocol(){
        frame = MinT.getInstance();
        resStorage = frame.getResStorage();
        routingtable = new RoutingTable();
//        init(frame.getNetworkManager());
        if(resStorage == null)
            System.out.println("res Storage null");
    }
    
    public void init(NetworkManager aThis) {
        networkManager = aThis;
        rhandle = new RoutHandler(this);
    }
    
    public void setCurrentRoutingGroup(String _name){
        groupName = _name;
    }
    
    public String getCurrentRoutingGroup(){
        return groupName;
    }

    @Override
    public void run() {
        System.out.println("Running Router!");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                networkManager.SEND_UDP_Multicast(new SendMessage()
                        .AddAttribute(Request.MSG_ATTR.Routing, R_MSG.NODE_BROADCAST.getValue()));
                Thread.sleep(1000);
            }
        } catch (Exception e) {
        }
    }

    public void routingHandle(PacketDatagram rv_packet) {
        rhandle.receiveHandle(rv_packet);
    }
}
