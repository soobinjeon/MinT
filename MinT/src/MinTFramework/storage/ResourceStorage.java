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

import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.MinT;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.Resource.StoreCategory;
import MinTFramework.storage.ThingProperty.PropertyRole;
import java.util.ArrayList;
import java.util.HashMap;
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
    public Resource getResourcefromJSON(RESOURCE_TYPE rtype, JSONObject rdata, ResponseData resdata){
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
        return nr;
    }
    
    /**
     * add NetworkResource
     * @param rtype
     * @param rdata
     * @param resdata 
     */
    public void addNetworkResource(RESOURCE_TYPE rtype, JSONObject rdata, ResponseData resdata){
        Resource nr = getResourcefromJSON(rtype, rdata, resdata);
        if(!nr.isSameLocation(resdata.getSourceInfo()))
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
     * Search a property on All locations
     * @param req Resource Name
     * @return 
     */
    public ResData getProperty(Request req){
        return getProperty(req, null);
    }
    
    /**
     * fix
     * 리소스 이름으로 검색하면 1개밖에 안나옮
     * Search a property on specific location
     * @return 
     * @param req Resource Name
     */
    public ResData getProperty(Request req, StoreCategory sc){
//        dl.printMessage("request RES : "+req.getResourceName());
        List<ThingProperty> rs = property.getbyResourceName(req.getResourceName());
        ArrayList<ResData> ol = new ArrayList<>();
        for(ThingProperty tp : rs){
//            dl.printMessage("finded : "+tp.getName()+", "+tp.getID()+", "+tp.getPropertyRole());
            if(sc == null || tp.getStorageCategory().equals(sc))
                ol.add(getPropertyfromResources(req, tp));
        }
        
        if(ol.size() > 0)
            return ol.get(0);
        else
            return null;
    }
    
    public List<ResData> getPropertybyResourceType(Request resourceType, StoreCategory sc){
        List<ThingProperty> rs = property.getbyResourceType(resourceType.getResourceName());
        ArrayList<ResData> ol = new ArrayList<>();
        for(ThingProperty tp : rs){
            if(sc == null || tp.getStorageCategory().equals(sc))
                ol.add(getPropertyfromResources(resourceType, tp));
        }
        
        return ol;
    }
    
    /**
     * get each Property
     * @param req
     * @param rs
     * @return 
     */
    private ResData getPropertyfromResources(Request req, ThingProperty rs){
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
    
    /************************************************************************
     * Make Resource group for discovery
     ************************************************************************/
    
    /**
     * get Discover Resource Data
     * @return 
     */
    public JSONObject DiscoverLocalResource(NetworkProfile currentNode){
        JSONObject obs = new JSONObject();
        JSONArray jpr = new JSONArray();
        JSONArray jis = new JSONArray();
        
        for(Resource res : getProperties()){
            addJSONArray(jpr, res, currentNode, false);
        }
        obs.put(RESOURCE_TYPE.property, jpr);
        
        for(Resource res : getInstructions()){
            addJSONArray(jis, res, currentNode, false);
        }
        obs.put(RESOURCE_TYPE.instruction, jis);
        
        return obs;
    }
    
    /**
     * Discover delegated Resources
     * @param currentNode
     * @return 
     */
    public JSONObject DiscoverDelegateResource(NetworkProfile currentNode){
        JSONObject obs = new JSONObject();
        JSONArray jpr = new JSONArray();
        JSONArray jis = new JSONArray();
        
        HashMap<DeviceType, Integer> PropDtype = new HashMap<>();
        HashMap<DeviceType, Integer> InstDtype = new HashMap<>();
        
        for(Resource res : getProperties()){
            if((res.getStorageCategory().isLocal() || (res.getConnectedRoutingNode() != null 
                    && (!res.getConnectedRoutingNode().isHeaderNode())))
                    && PropDtype.get(res.getDeviceType()) == null){
                PropDtype.put(res.getDeviceType(), 0);
                addJSONArray(jpr, res, currentNode, true);
            }
        }
        obs.put(RESOURCE_TYPE.property, jpr);
        
        for(Resource res : getInstructions()){
            if((res.getStorageCategory().isLocal() || (res.getConnectedRoutingNode() != null 
                    && (!res.getConnectedRoutingNode().isHeaderNode())))
                    && InstDtype.get(res.getDeviceType()) == null){
                InstDtype.put(res.getDeviceType(), 0);
                addJSONArray(jis, res, currentNode, true);
            }
        }
        obs.put(RESOURCE_TYPE.instruction, jis);
        
        if(PropDtype.size() == 0 && InstDtype.size() == 0)
            return null;
        else
            return obs;
    }
    
    /**
     * add resource to JSONArray
     * @param ja
     * @param res
     * @param currentNode 
     */
    private void addJSONArray(JSONArray ja, Resource res, NetworkProfile currentNode, boolean delegateMode){
        Resource nr = res.getCloneforDiscovery();
        if(res.getStorageCategory().isLocal() || delegateMode){
            nr.setLocation(new StorageDirectory(currentNode,
                    res.getStorageDirectory().getGroup(), res.getName()));
//            dl.printMessage("new class : "+nr.getName()+", "+nr.getStorageDirectory().getSourceLocation());
            ja.add(nr.getResourcetoJSON());
        }else
            ja.add(res.getResourcetoJSON());
    }
    
    /************************************************************************
     * Received Discover Data Updating
     ************************************************************************/
    /**
     * String data to Discovery Data
     * @param data
     * @return 
     */
    public JSONObject getDiscoveryResource(String data){
        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    synchronized public void updateDiscoverData(ResponseData resdata) {
        JSONObject discovery = getDiscoveryResource(resdata.getResourceString());
        JSONArray jpr = (JSONArray) discovery.get(ResourceStorage.RESOURCE_TYPE.property.toString());
        for (int i = 0; i < jpr.size(); i++) {
            addNetworkResource(ResourceStorage.RESOURCE_TYPE.property, (JSONObject) jpr.get(i), resdata);
        }

        JSONArray jis = (JSONArray) discovery.get(ResourceStorage.RESOURCE_TYPE.instruction.toString());
        for (int i = 0; i < jis.size(); i++) {
            addNetworkResource(ResourceStorage.RESOURCE_TYPE.instruction, (JSONObject) jis.get(i), resdata);
        }
    }
    
    public List<String> getInstructionList(){
        return instruction.getAllResourceName();
    }
    
    public List<ThingProperty> getProperties(){
        return getProperties(null);
    }
    
    public List<ThingInstruction> getInstructions(){
        return getInstructions(null);
    }
    
    public List<ThingProperty> getProperties(StoreCategory sc){
        if(sc == null)
            return property.getAllResources();
        else
            return property.getbyStoreCategory(sc);
    }
    
    public List<ThingInstruction> getInstructions(StoreCategory sc){
        if(sc == null)
            return instruction.getAllResources();
        else
            return instruction.getbyStoreCategory(sc);
    }
}
