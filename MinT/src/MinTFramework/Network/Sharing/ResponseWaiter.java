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

import MinTFramework.Network.sharing.Sharing.RESOURCE_TYPE;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.storage.ResData;
import MinTFramework.storage.ThingProperty;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResponseWaiter {
//    private Queue<ResponseHandler> reshandle = null;
    private Queue<SharingPacket> spackets = null;
    private Queue<ResData> responseData;
    private int ResponseSize = 0;
    private int currentResponseSize = 0;
    private SharingResponse parent = null;
    private RESOURCE_TYPE sourceName = null;
    public ResponseWaiter(SharingResponse _parent, Sharing.RESOURCE_TYPE srcname){
        spackets = new ConcurrentLinkedQueue<SharingPacket>();
//        reshandle = new ConcurrentLinkedQueue<ResponseHandler>();
        responseData = new ConcurrentLinkedQueue<ResData>();
        parent = _parent;
        sourceName = srcname;
        initHandler();
    }
    
    private void initHandler(){
        if(spackets == null)
            return;
        
    }

    /**
     * event handler of ResponseNode
     * @param resdata 
     */
    public synchronized void responsed(ResData resdata) {
        responseData.add(resdata);
        currentResponseSize ++;
        parent.networkResourceEventHandler(sourceName, resdata);
        if(currentResponseSize == ResponseSize)
            spackets.clear();
    }
    
    /**
     * get Response resources after waiting all response of request
     * @return 
     */
    public Queue<ResData> getDatas() {
        return responseData;
    }
    
    public void putPacket(Node n, ThingProperty res){
        spackets.add(new SharingPacket(n, res, new ResponseNode(this, res)));
        ResponseSize = spackets.size();
    }
    
//    /**
//     * add ResponseHandler and return added handler
//     * @param res
//     * @return 
//     */
//    public ResponseHandler putResponseHandler(ThingProperty res){
//        ResponseNode resnode = new ResponseNode(this, res);
//        reshandle.add(resnode);
//        ResponseSize = reshandle.size();
////        System.out.println("put ResponseHandler - RESSIZE: "+ResponseSize);
//        return resnode;
//    }
    
    public void printResponseSize(){
        System.out.println("cur: "+currentResponseSize+", ResSize: "+ResponseSize);
    }
    
    public RESOURCE_TYPE getResourceType(){
        return sourceName;
    }
    
    public Queue<SharingPacket> getPackets(){
        return spackets;
    }
    
    public int getResponseSize(){
        return ResponseSize;
    }
    
    public int getCurrentResponseSize(){
        return currentResponseSize;
    }
    
    public boolean completeAllResponse(){
//        System.out.println("currentResponseSize: "+currentResponseSize+", resSize: "+ResponseSize);
        if(currentResponseSize == ResponseSize)
            return true;
        else
            return false;
    }
}
