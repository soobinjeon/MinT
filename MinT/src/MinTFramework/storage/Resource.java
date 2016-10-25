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
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Routing.node.Node;
import MinTFramework.Util.DebugLog;
import static MinTFramework.storage.Resource.StoreCategory.Network;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Resource{
    private DebugLog dl = new DebugLog("Resource");
    
    public static enum Authority {Private, Protected, Public;}
    public static enum StoreCategory {Local, Network;
        boolean isLocal() {return this == Local;}
        boolean isNetwork() {return this == Network;}
    }
    
    protected String id;
    protected StorageDirectory sourcelocation;
    protected Authority auth;
    protected StoreCategory scate;
    
    protected String name;
    protected DeviceType dtype;
    protected ResData data;
    
    protected Node connectedNode = null; // for Routing Protocol
    
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
    public Resource(JSONObject jtores, StoreCategory sc){
        setJSONtoResource(jtores);
        scate = sc;
        data = new ResData(0);
        this.setResourceID();
    }
    
    public void put(Request _data){
        data.setResource(_data.getResourceData().getResource());
    }
    
    abstract public void set(Request req);
    abstract public Object get(Request req);

    public void connectRoutingNode(Node n){
        connectedNode = n;
    }
    
    public Node getConnectedRoutingNode(){
        return connectedNode;
    }
    
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
     * get resource Data
     * @return 
     */
    public ResData getResourceData(){
        return data;
    }
    
    /**
     * !!Caution!! just use in Resource Storage
     * set Resource ID
     */
    public void setResourceID() {
        this.id = this.sourcelocation.getSourceLocation();
    }
    
    /**
     * !!Caution!! just use in Resource Storage
     * set Source Location from Resource Storage
     * @param loc 
     */
    public void setLocation(StorageDirectory loc){
        this.sourcelocation = loc;
        this.setResourceID();
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
     * getID
     * @return 
     */
    public String getID() {
        return this.id;
    }
    
    /**
     * get Resource Profile
     * @return 
     */
    public NetworkProfile getSourceProfile(){
        return this.sourcelocation.getSrouceProfile();
    }
    
    boolean isSameLocation(NetworkProfile destination) {
        MinT frame = MinT.getInstance();
        Network cnet = frame.getNetworkManager().getNetwork(destination.getNetworkType());
        if(cnet != null){
//            System.out.println("src :"+sourcelocation.getSource()+", des : "+cnet.getProfile().getAddress());
            return this.sourcelocation.getSource().equals(cnet.getProfile().getAddress());
        }else
            return true;
    }
    
    public Resource getCloneforDiscovery() {
        return new Resource(this.name, DeviceType.valueOf(this.dtype.toString())
                , Authority.valueOf(auth.toString()), StoreCategory.valueOf(scate.toString())) {
            @Override
            public void set(Request req) {}

            @Override
            public Object get(Request req) {return null;}
        };
    }
    
    /**
     * get Resource to JSON
     * @return 
     */
    public JSONObject getResourcetoJSON(){
        JSONObject resObject = new JSONObject();
        resObject.put(JSONKEY.SOURCELOC, this.sourcelocation.getSrouceProfile().getProfile());
        resObject.put(JSONKEY.NAME, this.name);
        resObject.put(JSONKEY.GROUP, this.getGroup());
        resObject.put(JSONKEY.AUTH, this.auth.toString());
        resObject.put(JSONKEY.DEVICETYPE, this.dtype.toString());
        return resObject;
    }
    
    /**
     * set JSON to Resource
     * @param jtor 
     */
    private void setJSONtoResource(JSONObject jtor){
        try{
            this.name = (String)jtor.get(JSONKEY.NAME.toString());
            this.auth = Authority.valueOf((String)jtor.get(JSONKEY.AUTH.toString()));
            this.dtype = DeviceType.valueOf((String)jtor.get(JSONKEY.DEVICETYPE.toString()));
            
            sourcelocation = new StorageDirectory(new NetworkProfile((String)jtor.get(JSONKEY.SOURCELOC.toString())), 
                    (String)jtor.get(JSONKEY.GROUP.toString()), name);
        }catch(Exception e){
            
        }
    }
    
    private enum JSONKEY {NAME, GROUP, AUTH, DEVICETYPE, SOURCELOC;}
}    