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
package MinTFramework.Network.Resource;

import MinTFramework.Network.NetworkProfile;
import MinTFramework.storage.datamap.Information;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin
 */
public class Request {
    public static enum MSG_ATTR {
        WellKnown(".well-known"), 
        ResourceName("rn"),
        ResourceData("rd"),
        Routing("ro"),
        RoutingGroup("rog"),
        RoutingWeight("row"),
        RoutingisHeader("roh"),
        Sharing("sh");
        private String resName;
        
        // Reverse-lookup map for getting a MSG_ATTR from an resName
        private static final Map<String, MSG_ATTR> lookup = new HashMap<String, MSG_ATTR>();

        static {
            for (MSG_ATTR d : MSG_ATTR.values()) {
                lookup.put(d.getName(), d);
            }
        }

        MSG_ATTR(String name){
            resName = name;
        }
        
        public String getName(){
            return resName;
        }
        
        /**
         * @deprecated 
         * @param name
         * @return 
         */
        public static MSG_ATTR getbyName(String name){
            for(MSG_ATTR k : MSG_ATTR.values()){
                if(k.getName().equals(name))
                    return k;
            }
            return null;
        }
        
        public static MSG_ATTR get(String name){
            return lookup.get(name);
        }

        private boolean isResourceName() { return this == ResourceName; }
        private boolean isResourceData() { return this == ResourceData; }
    }
    
    protected NetworkProfile RequestNode = null;
    protected JSONObject resObject;
    
    protected String messageString = "";
    
    protected final ConcurrentHashMap<MSG_ATTR, Information> resources;
    protected Information ResourceName = null;
    protected Information ResourceData = null;
    
    public Request(NetworkProfile tn){
        resources = new ConcurrentHashMap<>();
        RequestNode = tn;
    }
    /**
     * Constructor
     * @param res Resource Name or ID
     * @param _getResource 
     * @param tn 
     */
    public Request(String res_name, Object _getResource, NetworkProfile tn) {
        this(tn);
        setResourceName(res_name);
        setResourceData(_getResource);
    }
    
    private void setResourceName(Object res){
        Information nres = new Information(res);
        ResourceName = nres;
        resources.put(MSG_ATTR.ResourceName, nres);
    }
    
    private void setResourceData(Object _data){
        Information nres = new Information(_data);
        ResourceData = nres;
        resources.put(MSG_ATTR.ResourceData, nres);
    }
    
    protected void addResource(MSG_ATTR attr, Object info){
        if(attr.isResourceName()){
            setResourceName(info);
        }else if(attr.isResourceData()){
            setResourceData(info);
        }else
            resources.put(attr, new Information(info));
    }
    /**
     * get Target Node
     * @return 
     */
    public NetworkProfile getRequestNode(){
        return RequestNode;
    }
    
    public String getMessageString(){
        return messageString;
    }
    
    public Information getResourcebyName(MSG_ATTR attr){
        return resources.get(attr);
    }
    
    public Information getResourceData(){
        return ResourceData;
    }
    
    public String getResourceName(){
        return ResourceName.getResourceString();
    }
}
