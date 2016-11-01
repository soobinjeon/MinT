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

import MinTFramework.Network.ResponseHandler;
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
    private Queue<ResponseHandler> reshandle = null;
    private Queue<ResData> responseData;
    private int ResponseSize = 0;
    private int currentResponseSize = 0;
    public ResponseWaiter(){
        reshandle = new ConcurrentLinkedQueue<ResponseHandler>();
        responseData = new ConcurrentLinkedQueue<ResData>();
        initHandler();
    }
    
    private void initHandler(){
        if(reshandle == null)
            return;
        
    }

    /**
     * event handler of ResponseNode
     * @param resdata 
     */
    public synchronized void responsed(ResData resdata) {
        responseData.add(resdata);
        currentResponseSize ++;
        notifyAll();
    }
    
    /**
     * get Response resources after waiting all response of request
     * @return 
     */
    public synchronized Queue<ResData> get() {
        try {
            while (currentResponseSize < ResponseSize) {
                wait();
            }
            reshandle.clear();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return responseData;
    }
    
    /**
     * add ResponseHandler and return added handler
     * @param res
     * @return 
     */
    public ResponseHandler putResponseHandler(ThingProperty res){
        ResponseNode resnode = new ResponseNode(this, res);
        reshandle.add(resnode);
        ResponseSize = reshandle.size();
//        System.out.println("put ResponseHandler - RESSIZE: "+ResponseSize);
        return resnode;
    }
    
    public void printResponseSize(){
        System.out.println("cur: "+currentResponseSize+", ResSize: "+ResponseSize);
    }
}
