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

import MinTFramework.Network.Resource.Request;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ThingProperty.PropertyRole;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PropertyManager extends ResourceManager implements ResourceManagerHandle{
    DebugLog dl = new DebugLog("Property Manager");
    
    /**
     * Property Manager
     * @param _frame MinT frame
     * @param rs Resource Storage
     */
    public PropertyManager(){
        super();
        initHandler(this);
    }
    
    @Override
    public void set(Request req, Resource res) {

    }

    /**
     * get aperiodic resource data
     * @param req request data
     * @param res resource data
     * @return 
     */
    @Override
    public ResData get(Request req, Resource res) {
        return getResource(req, res);
    }

    @Override
    public void put(Request req, Resource res) {

    }
    
    /**
     * Add Property Resources
     * it can operate resources by their role
     * Periodic : new Thread and loop by period time
     * aPeriodic : just added to resource storage
     * @param pr 
     */
    public void addProperty(ThingProperty pr){
//        dl.printMessage(pr.getName());
        
        /**
         * fix me
         * need to get id and stop processor
        */
        if(pr.getPropertyRole() == PropertyRole.PERIODIC){
            this.executeResource(new ResourceProcExecutor(pr, null));
        }
        
        RS.addResource(pr);
    }
    
    /**
     * get aperiodic resource data
     * @param req
     * @param res
     * @return 
     */
    private ResData getResource(Request req, Resource res){
        ResourceProcCallable rt = new ResourceProcCallable(res,req);
        Future<Object> getable = submitResource(rt);
        
        try {
            Resource retdata = (Resource)getable.get();
            return retdata.getResourceData();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        } catch (ExecutionException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
//    private ResData getResource(Request req, Resource res){
//        ResourceProcExecutor rt = new ResourceProcExecutor(res,req);
//        this.executeResource(rt);
//        
//        boolean isStarted = false;
//        
//        while(!isStarted){
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException ex) {
//            }
//            if(!rt.isRunning())
//                isStarted = true;
//        }
//        return rt.getResource().getResourceData();
//    }
}
