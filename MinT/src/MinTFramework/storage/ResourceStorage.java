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

import MinTFramework.Network.Request;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ThingProperty.PropertyRole;
import java.util.List;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResourceStorage {
    private ResourceManagerHandle PMhandle = null;
    private ResourceManagerHandle IMhandle = null;
    
    private Cache<Resource> property = new Cache<>();
    private Cache<Resource> instruction = new Cache<>();
    private DebugLog dl = new DebugLog("ResourceStorage");
    public ResourceStorage(){
        
    }
    
    public void setPropertyHandler(ResourceManagerHandle pmhandle) {
        PMhandle = pmhandle;
    }
    
    public void setInstructionHandler(ResourceManagerHandle imhandle){
        IMhandle = imhandle;
    }
    
    public void addResource(Resource res){
        if(res instanceof ThingProperty)
            property.put(res.getName(), res);
        else if(res instanceof ThingInstruction)
            instruction.put(res.getName(), res);
    }
    
    /**
     * fix me
     * @return 
     * @see 
     * @param req 
     */
    public Object getProperty(Request req){
        ThingProperty rs = (ThingProperty)property.get(req.getResourceName());
        
        if(rs != null){
            if(rs.getPropertyRole() == PropertyRole.APERIODIC)
                return PMhandle.get(req, rs);
            else
                return rs.getResourceData().getResource();
        }
        else
            return null;
        
        /*response method (send to requester)*/
    }
    
    public void setInstruction(Request req){
        Resource rs = instruction.get(req.getResourceName());
        if(rs != null)
            IMhandle.set(req, rs);
    }
    
    public List<String> getPropertyList(){
        return property.getAllResourceName();
    }
    
    public List<String> getInstructionList(){
        return instruction.getAllResourceName();
    }
}
