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
import MinTFramework.Network.Request;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Resource{
    public static enum Authority {Private, Protected, Public;}
    public static enum StoreCategory {Local, Network;}
    
    //new property
    protected int id;
    protected StorageDirectory sourcelocation;
    protected Authority auth;
    protected StoreCategory scate;
    
    //existing property
    protected String name;
    protected DeviceType dtype;
    protected MinT frame = null;
    protected ResData data;
    
    public Resource(String name, DeviceType dtype, Authority auth, StoreCategory sc) {
        this.auth = auth;
        this.name = name;
        data = new ResData(0);
        this.dtype = dtype;
        this.scate = sc;
    }
    
    /**
     * set Resource in Local Storage
     * @param name
     * @param dtype
     * @param auth 
     */
    public Resource(String name, DeviceType dtype, Authority auth) {
        this(name,dtype,auth,StoreCategory.Local);
    }
    
    /**
     * set Resource with default authority value (public)
     * @param name
     * @param dtype 
     */
    public Resource(String name, DeviceType dtype) {
        this(name,dtype,Authority.Public);
    }
    
    /**
     * JSON String to Resource from other Network
     * @param jtores 
     */
    public Resource(String jtores, StoreCategory sc){
        setJSONtoResource(jtores);
        scate = sc;
    }
    
    public void put(Request _data){
        data.setResource(_data.getResource());
    }
    
    abstract public void set(Request req);
    abstract public Object get(Request req);

    /**
     * get Resource Name
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * get Device Type
     * @return 
     */
    public DeviceType getDeviceType(){
        return dtype;
    }
    
    
    /**
     * set Frame
     * !!Caution!! just use in frame
     * @param frame 
     */
    public void setFrame(MinT frame){
        this.frame = frame;
    }
    
    /**
     * get resource Data
     * @return 
     */
    public ResData getResourceData(){
        return data;
    }
    
    /**
     * !!Caution!! just use in Resource Storage
     * set ID from Resource Storage
     * @param id 
     */
    public void setID(int id){
        this.id = id;
    }
    
    /**
     * !!Caution!! just use in Resource Storage
     * set Source Location from Resource Storage
     * @param loc 
     */
    public void setLocation(StorageDirectory loc){
        this.sourcelocation = loc;
    }
    
    /**
     * get Storage Category
     * @return Local or Network
     */
    public StoreCategory getStorageCategory() {
        return this.scate;
    }
    
    /**
     * get Storage Directory
     * @return 
     */
    public StorageDirectory getStorageDirectory(){
        return sourcelocation;
    }
    
    /**
     * get Group Name
     * @return 
     */
    public String getGroup(){
        return sourcelocation.getGroup();
    }
    
    /**
     * get Resource to JSON
     * @return 
     */
    public JSONObject getResourcetoJSON(){
        JSONObject resObject = new JSONObject();
        resObject.put("name", this.name);
        resObject.put("group", this.getGroup());
        resObject.put("auth", this.auth.toString());
        resObject.put("devicetype", this.dtype.toString());
        return resObject;
    }
    
    /**
     * set JSON to Resource
     * @param jtor 
     */
    private void setJSONtoResource(String jtor){
        try{
            JSONParser jsonParse = new JSONParser();
            JSONObject jo = (JSONObject)jsonParse.parse(jtor);
            this.name = (String)jo.get("name");
            this.auth = Authority.valueOf((String)jo.get("auth"));
            this.dtype = DeviceType.valueOf((String)jo.get("devicetype"));
        }catch(Exception e){
            
        }
    }
}    