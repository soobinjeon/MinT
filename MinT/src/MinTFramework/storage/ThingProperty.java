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
import MinTFramework.Network.Resource.Request;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class ThingProperty extends Resource{

    public static enum PropertyRole{
        PERIODIC, APERIODIC;
    }
    
    private PropertyRole pr;
    private int period = 1000;
    
    /**
     * new ThingProperty from other Network
     * @param jtores
     * @param sc
     * @param src 
     */
    public ThingProperty(JSONObject jtores, StoreCategory sc){
        super(jtores, sc);
    }
    
    /**
     * This class can provide the resource data from thing's devices
     * Property role is aperiodic(default)
     * @param name Resource Name (it would be resource id)
     * @param dtype DeviceType
     */
    public ThingProperty(String name, DeviceType dtype){
        this(name,dtype,PropertyRole.APERIODIC, 0);
    }
    
    /**
     * This class can provide the resource data from thing's devices
     * You can choose the property role
     * @param name Resource Name
     * @param dtype Device Type
     * @param pr Property Role (Periodic, aperiodic)
     * @param time period time (ms)
     */
    public ThingProperty(String name, DeviceType dtype, PropertyRole pr, int time) {
        super(name, dtype);
        this.pr = pr;
        period = time;
    }
    
    /**
     * get Resource Period
     * @return 
     */
    public int getPeriod(){
        return period;
    }
    
    /**
     * get Property Role
     * @return 
     */
    public PropertyRole getPropertyRole(){
        return pr;
    }

    /**
     * post or put resource data
     * @param req
     */
    @Override
    abstract public void set(Request req);

    /**
     * Method to provide resource data
     * @param req
     * @return
     */
    @Override
    abstract public Object get(Request req);
    
}
