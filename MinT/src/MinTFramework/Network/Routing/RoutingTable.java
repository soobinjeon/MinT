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

import java.util.concurrent.ConcurrentHashMap;

/**
 * Routing Table Manager
 * 
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RoutingTable {
    //routing table for nodes
    private ConcurrentHashMap<String, Node> routingTable;
    //table history, all node information are stored here
    private ConcurrentHashMap<String, Node> nodeHistory;
    private Node HeaderNode = null;
    
    public RoutingTable(){
        routingTable = new ConcurrentHashMap<>();
        nodeHistory = new ConcurrentHashMap<>();
    }
    
    public void addRoutingTable(Node n){
        routingTable.put(n.gettoAddr(), n);
        nodeHistory.put(n.gettoAddr(), n);
    }
    
    public void clearRoutingTable(){
        routingTable.clear();
    }
}
