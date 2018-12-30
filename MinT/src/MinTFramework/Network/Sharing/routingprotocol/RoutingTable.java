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
package MinTFramework.Network.Sharing.routingprotocol;

import MinTFramework.Network.Sharing.node.Node;
import java.util.ArrayList;
import java.util.List;
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
        routingTable.put(n.gettoAddr().getAddress(), n);
        nodeHistory.put(n.gettoAddr().getAddress(), n);
    }
    
    public void clearRoutingTable(){
        routingTable.clear();
    }
    
    public void setHeaderNode(Node hn){
        HeaderNode = hn;
    }
    
    public Node getHeaderNodeofCurrentNode(){
        return HeaderNode;
    }
    
    public ConcurrentHashMap<String, Node> getRoutingTable(){
        return routingTable;
    }
    
    /**
     * Routing Table Size
     * @return 
     */
    public int Size(){
        return routingTable.size();
    }

    /**
     * get Node by Address
     * @param address
     * @return 
     */
    public Node getNodebyAddress(String address) {
        for(Node n : routingTable.values()){
            if(n.gettoAddr().getAddress().equals(address))
                return n;
        }
        return null;
    }
    
    public Node getChildNodebyAddress(String address) {
        Node n = getNodebyAddress(address);
        if(n == null || !n.isClientNode())
            return null;
        else
            return n;
    }

    public boolean isdoneIdentifyAllChildNode() {
       for(Node n : routingTable.values()){
           if(!n.isHeaderNode() && !n.isClientNode())
               return false;
       }
       return true;
    }

    /**
     * get ChildNodes
     */
    public List<Node> getChildNodes() {
        ArrayList<Node> childs = new ArrayList<>();
        for(Node n : routingTable.values()){
            if(n.isClientNode())
                childs.add(n);
        }
        return childs;
    }
    
    /**
     * get Header Nodes
     * @return 
     */
    public List<Node> getHeaderNodes(){
        ArrayList<Node> headers = new ArrayList<>();
        for(Node n : routingTable.values()){
            if(n.isHeaderNode())
                headers.add(n);
        }
        return headers;
    }
}
