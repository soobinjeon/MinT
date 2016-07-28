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
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.Resource.StoreCategory;
import MinTFramework.storage.ThingProperty.PropertyRole;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResourceStorage {
    private ResourceManagerHandle PMhandle = null;
    private ResourceManagerHandle IMhandle = null;
    
    private final Cache<Resource> property;
    private final Cache<Resource> instruction;
    
    private MinT frame = null;
    private DebugLog dl = new DebugLog("ResourceStorage");
    public ResourceStorage(MinT frame){
        this.instruction = new Cache<>();
        this.property = new Cache<>();
        this.frame = frame;
    }
    
    public void setPropertyHandler(ResourceManagerHandle pmhandle) {
        PMhandle = pmhandle;
    }
    
    public void setInstructionHandler(ResourceManagerHandle imhandle){
        IMhandle = imhandle;
    }
    
    public void addResource(Resource res){
        //set Storage Location for Local
        if(res.getStorageCategory() == StoreCategory.Local)
            res.setLocation(setLocalLocation(res));
        else if(res.getStorageCategory() == StoreCategory.Network){
            /*do Something*/
        }
        
        if(res instanceof ThingProperty)
            property.put(res.getName(), res);
        else if(res instanceof ThingInstruction)
            instruction.put(res.getName(), res);
    }
    
    /**
     * set Local Location
     * @param res
     * @return 
     */
    private StorageDirectory setLocalLocation(Resource res) {
        return new StorageDirectory(StorageDirectory.LOCAL_SOURCE, frame.getResourceGroup(), res.getName());
    }
    
    /**
     * fix me
     * @return 
     * @see 
     * @param req 
     */
    public Object getProperty(Request req){
        ThingProperty rs = (ThingProperty)property.get(req.getResourceName());
        
        Object ret = null;
        if(rs != null){
            if(rs.getPropertyRole() == PropertyRole.APERIODIC)
                ret = PMhandle.get(req, rs);
            else
                ret = rs.getResourceData().getResource();
        }
        else
            ret = null;
        
        /*response method (send to requester)
        * fix me
        * should send router
        */
        frame.sendDirectMessage(req.getTargetNode(), String.valueOf(ret));
        
        return ret;
    }
    
    /**
     * set Instruction
     * @param req 
     */
    public void setInstruction(Request req){
        Resource rs = instruction.get(req.getResourceName());
        if(rs != null)
            IMhandle.set(req, rs);
    }
    
    public List<String> getPropertyList(){
        return property.getAllResourceName();
    }
    
    /**
     * get Observe Resource Data
     * @return 
     */
    public JSONObject OberveLocalResource(){
        JSONObject obs = new JSONObject();
        JSONArray jpr = new JSONArray();
        JSONArray jis = new JSONArray();
        
        for(Resource res : getProperties()){
            jpr.add(res.getResourcetoJSON());
        }
        obs.put("property", jpr);
        
        for(Resource res : getInstruction()){
            jis.add(res.getResourcetoJSON());
        }
        obs.put("instruction", jis);
        
        return obs;
    }
    
    public List<String> getInstructionList(){
        return instruction.getAllResourceName();
    }
    
    public List<Resource> getProperties(){
        return property.getAllResources();
    }
    
    public List<Resource> getInstruction(){
        return instruction.getAllResources();
    }
}
