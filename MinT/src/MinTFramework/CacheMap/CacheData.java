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
package MinTFramework.CacheMap;

import MinTFramework.ExternalDevice.DeviceType;

/**
 * need to Convert to byte!!
 * @author soobin
 */
public class CacheData {
    private DeviceType dtype;
    private Object res;
    
    public CacheData(DeviceType dtype, Object _res){
        setResource(_res);
        System.out.println(dtype.toString()+" "+res);
    }
    
    public void setResource(Object setres){
        res = String.valueOf(setres);
    }

    public Object getResource(){
        return res;
    }
    public String getResourceString(){
        return String.valueOf(res);
    }
    
    public int getResourceInt(){
        return Integer.parseInt(getResourceString());
    }
    
    public float getResourceFloat(){
        return Float.parseFloat(res);
    }
    
    public double getResourceDouble(){
        return Double.parseDouble(res);
    }
    
    public long getResourceLong(){
        return Long.parseLong(res);
    }
    
    public boolean getResourceBoolean(){
        return Boolean.parseBoolean(res);
    }
}
