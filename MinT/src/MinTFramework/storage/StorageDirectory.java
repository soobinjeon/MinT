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

import MinTFramework.Network.NetworkProfile;

/**
 * Storage Directory
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class StorageDirectory {
    protected static final String LOCAL_SOURCE = "local";
    private NetworkProfile networkProfile;
    private String source = "";
    private String group = "";
    private String resource = "";
    
    private final String spliter = "/";
    
    /**
     * init Directory
     * @param source source address
     * @param group group address
     * @param resource resource Name
     */
    public StorageDirectory(NetworkProfile src, String group, String resource){
        if(src != null){
            this.networkProfile = src;
            this.source = src.getAddress();
        }else{
            networkProfile = null;
            source = LOCAL_SOURCE;
        }
        this.group = group;
        this.resource = resource;
    }
    
    /**
     * new Storage Directory with source Location
     * @param srcLoc 
     */
    public StorageDirectory(String srcLoc){
        if(srcLoc != null){
            String sp[] = srcLoc.split(spliter);
            if(sp.length > 0){
                group = sp[0];
                source = sp[1];
                resource = sp[2];
            }
        }
    }
    
    /**
     * set Source address
     * @param source 
     */
    public void setSource(String source){
        this.source =source;
    }
    
    public String getSourceLocation(){
        return group + spliter + source + spliter + resource;
    }
    
    public String getGroup(){
        return group;
    }
    
    public String getSource(){
        return source;
    }
    
    public String getResourceName(){
        return resource;
    }
    
    public boolean isLocalSource(){
        return this.source.equals(StorageDirectory.LOCAL_SOURCE);
    }
    
    public NetworkProfile getSrouceProfile(){
        return this.networkProfile;
    }
}
