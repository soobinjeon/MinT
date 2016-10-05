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
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.Resource.StoreCategory;
import MinTFramework.storage.ThingProperty.PropertyRole;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Resource Storage
 * All Resource in local and network are stored in this Storage
 * Resources are divided in property and instruction. this class manages each resource by role
 * local and network resources are stored in same storage, they are separated by StoreCategory class in Resource class
 * you can use to store local and network resource in here
 * local
 *   - ThingInstruction
 *   - Thing Property
 *   - MinT.addResource()
 * network
 *   - same with local
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResourceStorage {
    private ResourceManagerHandle PMhandle = null;
    private ResourceManagerHandle IMhandle = null;
    
    private final Repository<ThingProperty> property;
    private final Repository<ThingInstruction> instruction;
    
    private MinT frame = null;
    private DebugLog dl = new DebugLog("ResourceStorage");
    public ResourceStorage(){
        this.instruction = new Repository<>();
        this.property = new Repository<>();
        this.frame = MinT.getInstance();
    }
    
    public void setPropertyHandler(ResourceManagerHandle pmhandle) {
        PMhandle = pmhandle;
    }
    
    public void setInstructionHandler(ResourceManagerHandle imhandle){
        IMhandle = imhandle;
    }
    
    /**
     * Add All Type Resource
     * @param res Resource
     */
    public void addResource(Resource res){
        //set Storage Location for Local
        if(res.getStorageCategory() == StoreCategory.Local)
            res.setLocation(setLocalLocation(res));
        //set Storage Location for Network
        //fix me
        else if(res.getStorageCategory() == StoreCategory.Network){
            /*do Something*/
        }
        
        //set Resource ID
//        res.setResourceID();
        
        if(res instanceof ThingProperty)
            property.put(res.getID(), (ThingProperty)res);
        else if(res instanceof ThingInstruction)
            instruction.put(res.getID(), (ThingInstruction)res);
    }
    
    /**
     * add NetworkResource
     * @param rtype
     * @param rdata
     * @param resdata 
     */
    public void addNetworkResource(RESOURCE_TYPE rtype, JSONObject rdata, ResponseData resdata){
        Resource nr;
        if(rtype == RESOURCE_TYPE.property){
            nr = new ThingProperty(rdata, Resource.StoreCategory.Network) {
                @Override
                public void set(Request req) {}
                @Override
                public Object get(Request req) {return null;}
            };
        }else{
            nr = new ThingInstruction(rdata, Resource.StoreCategory.Network) {
                @Override
                public void set(Request req) {}
                @Override
                public Object get(Request req) {return null;}
            };
        }
        if(!nr.isSameLocation(resdata.getDestination()))
            addResource(nr);
    }
    
    /**
     * set Local Location
     * @param res
     * @return 
     */
    private StorageDirectory setLocalLocation(Resource res) {
        return new StorageDirectory(null, frame.getResourceGroup(), res.getName());
    }
    
    /**
     * fix me
     * need to update
     * @return 
     * @see 
     * @param req 
     */
    public ResData getProperty(Request req){
//        dl.printMessage("request RES : "+req.getResourceName());
        List<ThingProperty> rs = property.getbyResourceName(req.getResourceName());
        ArrayList<ResData> ol = new ArrayList<>();
        for(ThingProperty tp : rs){
//            dl.printMessage("finded : "+tp.getName()+", "+tp.getID()+", "+tp.getPropertyRole());
            ol.add(getProperty(req, tp));
        }
        
        /*Fix me!!
        * 여러개면 배열로 보내야함
        */
        if(ol.size() > 0)
            return ol.get(0);
        else
            return null;
    }
    
    /**
     * get each Property
     * @param req
     * @param rs
     * @return 
     */
    private ResData getProperty(Request req, ThingProperty rs){
        ResData ret = null;
        if(rs != null){
            //resource is local and Aperiod Property
            if(rs.getStorageCategory().isLocal() && rs.getPropertyRole() == PropertyRole.APERIODIC){
                ret = PMhandle.get(req, rs);
            }
            else{ //network or period property
                ret = rs.getResourceData();
            }
        }
        else
            ret = null;
//        dl.printMessage("Last Pro : "+ret.getResourceString());
        return ret;
    }
    
    /**
     * fix me
     * need to add set operator for network
     * set Instruction
     * @param req 
     */
    public void setInstruction(Request req){
        List<ThingInstruction> rs = instruction.getbyResourceName(req.getResourceName());
        for(ThingInstruction tp : rs){
            IMhandle.set(req, tp);
        }
    }
    
    public List<String> getPropertyList(){
        return property.getAllResourceName();
    }
    
    public static enum RESOURCE_TYPE {property, instruction;}
    /**
     * get Discover Resource Data
     * @return 
     */
    public JSONObject DiscoverLocalResource(NetworkProfile currentNode){
        JSONObject obs = new JSONObject();
        JSONArray jpr = new JSONArray();
        JSONArray jis = new JSONArray();
        
        for(Resource res : getProperties()){
            addJSONArray(jpr, res, currentNode);
        }
        obs.put(RESOURCE_TYPE.property, jpr);
        
        for(Resource res : getInstruction()){
            addJSONArray(jis, res, currentNode);
        }
        obs.put(RESOURCE_TYPE.instruction, jis);
        
        return obs;
    }
    
    /**
     * add resource to JSONArray
     * @param ja
     * @param res
     * @param currentNode 
     */
    private void addJSONArray(JSONArray ja, Resource res, NetworkProfile currentNode){
        Resource nr = res.getCloneforDiscovery();
        if(res.getStorageCategory().isLocal()){
            nr.setLocation(new StorageDirectory(currentNode,
                    res.getStorageDirectory().getGroup(), res.getName()));
//            dl.printMessage("new class : "+nr.getName()+", "+nr.getStorageDirectory().getSourceLocation());
            ja.add(nr.getResourcetoJSON());
        }else
            ja.add(res.getResourcetoJSON());
    }
    
    /**
     * String data to Discovery Data
     * @param data
     * @return 
     */
    public JSONObject getDiscoveryResource(String data){
        try{
            System.out.println("json data: "+data);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<String> getInstructionList(){
        return instruction.getAllResourceName();
    }
    
    public List<ThingProperty> getProperties(){
        return property.getAllResources();
    }
    
    public List<ThingInstruction> getInstruction(){
        return instruction.getAllResources();
    }
}
