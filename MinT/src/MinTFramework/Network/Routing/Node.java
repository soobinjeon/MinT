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

import MinTFramework.Network.Network;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Node {
    private String toAddr = null;
    private String nextAddr = null;
    private Network network = null;
    private boolean Header = false;
    
    public Node(String _toAddr, String _nextAddr, Network _network, boolean Hd){
        toAddr = _toAddr;
        Header = Hd;
        nextAddr = _nextAddr;
        network = _network;
    }
    
    public String gettoAddr(){
        return toAddr;
    }
    
    public String getNextAddr(){
        return nextAddr;
    }
    
    public Network getNetwork(){
        return network;
    }
    
    public boolean isHeader(){
        return Header;
    }
}
