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
import MinTFramework.Network.Profile;
import MinTFramework.Network.Request;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class ThingInstruction extends Resource{
    
    /**
     * new ThingInstruction from other network
     * @param jtores
     * @param sc
     * @param src 
     */
    public ThingInstruction(JSONObject jtores, StoreCategory sc, Profile src){
        super(jtores, sc, src);
    }
    
    /**
     * init instruction set
     * @param name Resource Name (it is id of this resource)
     * @param dtype DeviceType
     */
    public ThingInstruction(String name, DeviceType dtype) {
        super(name, dtype);
    }

    /**
     * instruction method to operate the thing
     * @param info
     */
    @Override
    abstract public void set(Request info);

    /**
     * !!Warning!!
     * Do not use in this class
     * @param info
     * @return
     */
    @Override
    abstract public Object get(Request info);
}
