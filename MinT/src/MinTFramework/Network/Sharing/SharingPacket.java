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

import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.storage.ThingProperty;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SharingPacket {
    private Node node = null;
    private ThingProperty prop = null;
    private ResponseHandler reshandler = null;
    private ReceiveMessage recvmsg = null;
    
    public SharingPacket(Node n, ThingProperty pr, ResponseHandler res, ReceiveMessage _recvmsg){
        node = n;
        prop = pr;
        reshandler = res;
        recvmsg = _recvmsg;
    }
    
    public ResponseHandler getResponseHandler(){
        return reshandler;
    }
    
    public Node getNodeInfo(){
        return node;
    }
    
    public ThingProperty getProperty(){
        return prop;
    }
    
    public ReceiveMessage getReceiveMessage(){
        return recvmsg;
    }
}
