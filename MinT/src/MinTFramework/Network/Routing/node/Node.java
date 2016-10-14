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
package MinTFramework.Network.Routing.node;

import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkProfile;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Node {
    private NetworkProfile toAddr = null;
    private NetworkProfile nextAddr = null;
    private Network network = null;
    private boolean Header = false;
    private double SpecWeight = 0;
    private String GroupName = "";
    public Node(NetworkProfile _toAddr, NetworkProfile _nextAddr
            , boolean Hd, double _specWeight, String _gn){
        toAddr = _toAddr;
        Header = Hd;
        nextAddr = _nextAddr;
        SpecWeight = _specWeight;
        GroupName = _gn;
    }
    
    public NetworkProfile gettoAddr(){
        return toAddr;
    }
    
    public NetworkProfile getNextAddr(){
        return nextAddr;
    }
    
    public Network getNetwork(){
        return network;
    }
    
    public boolean isHeader(){
        return Header;
    }
    
    public double getSpecWeight(){
        return SpecWeight;
    }
    
    public String getGroupName(){
        return GroupName;
    }
    
    @Override
    public String toString(){
        return "toAddr: "+toAddr.getAddress()+", nextAddr: "+nextAddr.getAddress();
    }
}
