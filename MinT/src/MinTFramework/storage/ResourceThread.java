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
package MinTFramework.storage;

import MinTFramework.MinT;
import MinTFramework.Network.Request;
import MinTFramework.SystemScheduler.Service;
import MinTFramework.storage.ThingProperty.PropertyRole;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResourceThread extends Service{
    private Resource res;
    private Request req;
    private ResourceThreadHandle rth = null;
    private boolean isRunning = true;
    public ResourceThread(MinT _frame, Resource res, Request req) {
        this(_frame,res,req,null);
    }
    
    public ResourceThread(MinT _frame, Resource res, Request req, ResourceThreadHandle rth) {
        super(_frame);
        this.res = res;
        this.req = req;
        this.rth = rth;
    }

    @Override
    public void execute() {
        if(res instanceof ThingProperty){
            ThingProperty pr = (ThingProperty)res;
            if(pr.getPropertyRole() == PropertyRole.PERIODIC){
                while(!Thread.currentThread().isInterrupted()){
                    
                    processGet();
                    
                    try {
                        Thread.sleep(pr.getPeriod());
                    } catch (InterruptedException ex) {
                       Thread.currentThread().interrupt();
                    }
                }
            }else{
                //get Processor
                processGet();
            }
        }
        
        //set Processor
        res.set(req);
        
        if(rth != null)
            rth.ThreadEndHandle();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
    
    public Resource getResource(){
        return res;
    }
    
    private void processGet() {
        Object result = res.get(req);
        if(result != null){
            res.data.setResource(result);
        }
    }
}
