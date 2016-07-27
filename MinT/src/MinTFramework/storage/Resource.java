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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Resource{
    private static enum Authority {Private, Protected, Public;}
    
    //new property
    protected int id;
    protected String sourcelocation;
    protected Authority auth;
    
    //existing property
    protected String name;
    protected DeviceType dtype;
    protected MinT frame = null;
    protected ResData data;
    public Resource(String name, DeviceType dtype) {
        this.name = name;
        data = new ResData(0);
        this.dtype = dtype;
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
}    