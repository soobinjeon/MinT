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
import MinTFramework.Util.TypeCaster;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cache Data Class for Local Cache
 * Insert DataType
 *   - All DataType
 * return
 *   - incorrect type : null
 * @author soobin
 */
public class CacheData {
    private DeviceType dtype;
    private byte[] res;
    private boolean isStringvalue = true;
    
    public CacheData(DeviceType dtype, Object _res){
        this.dtype = dtype;
        try {
            setResource(_res);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setResource(Object setres) throws IOException{
        if(setres instanceof Boolean){
            res = TypeCaster.zipStringToBytes(Boolean.toString((Boolean)setres));
        }else if(setres instanceof String)
            res = TypeCaster.zipStringToBytes((String) setres);
        else{
            res = TypeCaster.ObjectTobyte(setres, ByteOrder.BIG_ENDIAN);
            isStringvalue = false;
        }
    }
    
    private double getNumberResource(){
        return TypeCaster.byteToDouble(res, ByteOrder.BIG_ENDIAN);
    }
    
    private String getStringResource(){
        String str = null;
        try {
            str =  TypeCaster.unzipStringFromBytes(res);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return str;
    }

    /**
     * get Resource by Object
     * @return object Type Resource
     */
    public Object getResource(){
        if(isStringvalue)
            return getStringResource();
        else
            return getNumberResource();
    }
    
    /**
     * get String type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public String getResourceString(){
        if(isStringvalue)
            return getStringResource();
        else
            return String.valueOf(getResource());
    }
    
    /**
     * get Integer type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Integer getResourceInt(){
        if(isStringvalue)
            return null;
        else
            return (int)getNumberResource();
    }
    
    /**
     * get Float type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Float getResourceFloat(){
        if(isStringvalue)
            return null;
        else
            return (float)getNumberResource();
    }
    
    /**
     * get Double type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Double getResourceDouble(){
        if(isStringvalue)
            return null;
        else
            return (double)getNumberResource();
    }
    
    /**
     * get Long type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Long getResourceLong(){
        if(isStringvalue)
            return null;
        else
            return (long)getNumberResource();
    }
    
    /**
     * get Short type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Short getResourceShort(){
        if(isStringvalue)
            return null;
        else
            return (short)getNumberResource();
    }
    
    /**
     * get Character type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Character getResourceChar(){
        if(isStringvalue)
            return null;
        else
            return (char)getNumberResource();
    }
    
    /**
     * get Boolean type Resource
     * if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Boolean getResourceBoolean(){
        if(isStringvalue){
            return Boolean.parseBoolean(getStringResource());
        }else{
            int tf = (int)getNumberResource();
            if(tf == 0)
                return false;
            else
                return true;
        }
    }
    
    /**
     * get Length
     * @return 
     */
    public int getLength(){
        return res != null ? res.length : 0;
    }
    
    /**
     * get Device Type
     * @return type of devices
     */
    public DeviceType getDeviceType(){
        return dtype;
    }

    public CacheData getClone() {
        return new CacheData(this.dtype, this.getResource());
    }
}
