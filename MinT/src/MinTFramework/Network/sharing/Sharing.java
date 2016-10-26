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
package MinTFramework.Network.sharing;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.storage.ResourceStorage;
import java.util.ArrayList;

/**
 *
 * @author soobin
 */
public class Sharing {
    private MinT frame = null;
    private ResourceStorage resStorage = null;
    private SystemScheduler sysSched = null;
    private NetworkManager networkManager = null;
    private RoutingProtocol routingprotocol = null;
    private SharingHandler shandle = null;
    public Sharing(){
        frame = MinT.getInstance();
        resStorage = frame.getResStorage();
        sysSched = frame.getSystemScheduler();
        
    }
    
    /**
     * Initialization Sharing approach 
     * @param aThis 
     */
    public void init(NetworkManager aThis) {
        networkManager = aThis;
        routingprotocol = networkManager.getRoutingProtocol();
        shandle = new SharingHandler(this);
    }
    
    /**
     * 
     * @param packet
     * @param receivemsg 
     */
    public void sharingHandle(PacketDatagram packet, ReceiveMessage receivemsg) {
        shandle.receiveHandle(packet, receivemsg);
    }
    
    /********************************************************
     * CHILD NODE WORKING
     ********************************************************/
    
    public void GET_SHARING_RESOURCE(Request requestdata, ResponseHandler resHandle){
        
    }
    
    
    /********************************************************
     * HEADER WORKING
     ********************************************************/
    
    /**
     * search Nodes had request resource
     * @param resource
     * @return 
     */
    public ArrayList<Node> searchResource(String resource){
        return null;
    }
    
    /**
     * Request resource to other Header
     * 주변 모든 그룹 헤더에게 데이터 요청 후 기다림
     * 받은 데이터를 종합 하여 Child Node에게 전달
     * @param resource 
     */
    public void requestResource(String resource){
        
    }

    
}
