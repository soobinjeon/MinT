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
import org.json.simple.JSONObject;

/**
 *
 * @author soobin
 */
public class Request_OLD extends Information {
    String targetRes;
    NetworkProfile RequestNode = null;
    
    protected final String RESOURCENAME = "rn";
    protected final String REQUESTMETHOD = "rm";
    
    protected String messageString = "";
    /**
     * set Request for SystemHandler
     * @param res Resource Name or ID
     * @param _getResource 
     * @param tn 
     */
    public Request_OLD(String res, Object _getResource, NetworkProfile tn) {
        super(_getResource);
        if(res == null)
            targetRes = "";
        else
            targetRes = res;
        RequestNode = tn;
    }
    
    /**
     * set Request for Sender
     * @param res Resource Name for requesting
     * @param _getResource Request Method
     */
    public Request_OLD(String res, Object _getResource){
        this(res, _getResource, null);
        setMessageString();
    }
    
    /**
     * get Resource Name
     * @return 
     */
    public String getResourceName(){
        return targetRes;
    }
    
    /**
     * get Target Node
     * @return 
     */
    public NetworkProfile getRequestNode(){
        return RequestNode;
    }
    
    private void setMessageString(){
        JSONObject resObject = new JSONObject();
        resObject.put(RESOURCENAME, targetRes);
        resObject.put(REQUESTMETHOD, super.getResourceString());
        messageString = resObject.toJSONString();
    }
    
    public String getMessageString(){
        return this.messageString;
    }
}
