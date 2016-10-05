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
        ResourceData("rd");
        private String resName;
        
        MSG_ATTR(String name){
            resName = name;
        }
        
        public String getName(){
            return resName;
        }
        
        public static MSG_ATTR getbyName(String name){
            for(MSG_ATTR k : MSG_ATTR.values()){
                if(k.getName().equals(name))
                    return k;
            }
            return null;
        }
    }
    
    protected NetworkProfile RequestNode = null;
    protected JSONObject resObject;
    
    protected String messageString = "";
    
    protected final ConcurrentHashMap<MSG_ATTR, Information> resources;
    
    public Request(NetworkProfile tn){
        this(null, null, tn);
    }
    public Request(){
        resources = new ConcurrentHashMap<>();
    }
    /**
     * Constructor
     * @param res Resource Name or ID
     * @param _getResource 
     * @param tn 
     */
    public Request(String res_name, Object _getResource, NetworkProfile tn) {
        this();
        RequestNode = tn;
        resources.put(MSG_ATTR.ResourceName, new Information(res_name));
        resources.put(MSG_ATTR.ResourceData, new Information(_getResource));
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
        return resources.get(MSG_ATTR.ResourceData);
    }
    
    public String getResourceName(){
        return resources.get(MSG_ATTR.ResourceName).getResourceString();
    }
}
