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
package MinTFramework.storage.datamap;

import MinTFramework.ExternalDevice.DeviceType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author soobin
 */
public class Cache<T> implements CacheMap<T>{
    protected HashMap<String,T> resources = new HashMap();
    
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
                return result;
//                return (T)result.getClass().getMethod("getClone").invoke(result);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
    
    /**
     * delete Selected value
     * @param name
     * @return 
     */
    public boolean delete(String name) {
        T value = resources.remove(name);
        if(value == null)
            return false;
        else
            return true;
    }

    @Override
    public ArrayList<T> getResourcebyDeviceType(DeviceType type) {
        ArrayList<T> res = new ArrayList<>();
        for(T cd : resources.values()){
            try{
                if(cd.getClass().getMethod("getDeviceType").invoke(cd).equals(type)){
//                    res.add((T)cd.getClass().getMethod("getClone").invoke(cd));    
                    res.add(cd);    
                }
            }catch(Exception e){
                
            }
            
        }
        return res;
    }

    /**
     * 
     * @return 
     */
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
    public HashMap<String, T> getAllResourceHashMap() {
        return this.resources;
    }
    
    @Override
    public List<T> getAllResources(){
        return new ArrayList<T>(resources.values());
    }
    
    /**
     * delete Resource
     * @param res
     * @return 
     */
    public boolean deleteResource(String res){
        T ret = resources.remove(res);
        if(ret != null)
            return true;
        else
            return false;
    }
}
