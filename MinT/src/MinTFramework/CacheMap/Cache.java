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

import MinTFramework.*;
import MinTFramework.CacheMap.*;
import MinTFramework.ExternalDevice.DeviceType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author soobin
 */
public class Cache<T> implements CacheMap<T>{
    private HashMap<String,T> resources = new HashMap();
    
    public Cache(){}

    @Override
    public synchronized void put(String name, T data) {
        resources.put(name, data);
    }

    @Override
    public T get(String name) {
        T result = (T)resources.get(name);
        if(result == null){
            return null;
        }
        else{
            try {
                return (T)result.getClass().getMethod("getClone").invoke(result);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public ArrayList<T> getResourcebyDeviceType(DeviceType type) {
        ArrayList<T> res = new ArrayList<>();
        for(T cd : resources.values()){
            try{
                if(cd.getClass().getMethod("getDeviceType").invoke(cd).equals(type))
                    res.add((T)cd.getClass().getMethod("getClone").invoke(cd));    
            }catch(Exception e){
                
            }
            
        }
        return res;
    }

    @Override
    public List<DeviceType> getAllDeviceType() {
        List<DeviceType> res = new ArrayList<>();
        try{
            for(T cd : resources.values()){
                DeviceType td = (DeviceType)cd.getClass().getMethod("getDeviceType").invoke(cd);
                if(res.isEmpty())
                    res.add(td);
                else{
                    boolean pass = false;
                    for(int i=0;i<res.size();i++){
                        if(res.get(i).equals(td))
                            pass = true;
                    }

                    if(!pass)
                        res.add(td);
                }
            }
        }catch(Exception e){
            /**/
        }
        return res;
    }

    @Override
    public ArrayList<String> getAllResourceName() {
        ArrayList<String> res = new ArrayList<>();
        for(Object o : resources.keySet()){
            res.add((String)o);
        }
        return res;
    }

    /**
     * get AllResource list in HashMap
     * !!Caution!!
     * always return getResource() at 2th param instead of res
     * @return new extended Resource(DeviceType, getResource())
     */
    @Override
    public HashMap<String, T> getAllResource() {
        return this.resources;
    }
}
