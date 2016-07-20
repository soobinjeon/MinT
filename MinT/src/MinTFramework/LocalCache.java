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
package MinTFramework;

import MinTFramework.CacheMap.*;
import MinTFramework.ExternalDevice.DeviceType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author soobin
 */
public class LocalCache implements CacheMap{
    private HashMap<String,CacheData> resources = new HashMap();
    
    public LocalCache(){
        
    }

    @Override
    public synchronized void put(String name, CacheData data) {
        resources.put(name, data);
    }

    @Override
    public CacheData get(String name) {
        if(resources.get(name) == null)
            return null;
        else
            return resources.get(name).getClone();
    }

    @Override
    public ArrayList<CacheData> getResourcebyDeviceType(DeviceType type) {
        ArrayList<CacheData> res = new ArrayList<>();
        for(CacheData cd : resources.values()){
            if(cd.getDeviceType().equals(type))
                res.add(cd.getClone());
        }
        return res;
    }

    @Override
    public ArrayList<DeviceType> getAllDeviceType() {
        ArrayList<DeviceType> res = new ArrayList<>();
        for(CacheData cd : resources.values()){
            if(res.isEmpty())
                res.add(cd.getDeviceType());
            else{
                boolean pass = false;
                for(int i=0;i<res.size();i++){
                    if(res.get(i).equals(cd.getDeviceType()))
                        pass = true;
                }
                
                if(!pass)
                    res.add(cd.getDeviceType());
            }
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
     * @deprecated 
     * @return 
     */
    @Override
    public ArrayList<HashMap> getAllResource() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
