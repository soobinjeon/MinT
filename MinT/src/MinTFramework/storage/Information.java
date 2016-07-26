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
public abstract class Information {
    protected byte[] res;
    protected boolean isStringvalue = true;
    
    public Information(Object _getResource){
        setResource(_getResource);
    }
    
    public void setResource(Object setres){
        try{
            if(setres instanceof Boolean){
                res = TypeCaster.zipStringToBytes(Boolean.toString((Boolean)setres));
                isStringvalue = true;
            }else if(setres instanceof String){
                res = TypeCaster.zipStringToBytes((String) setres);
                isStringvalue = true;
            }else{
                res = TypeCaster.ObjectTobyte(setres, ByteOrder.BIG_ENDIAN);
                isStringvalue = false;
        }
        }catch(Exception e){
            /**/
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
     * get Information by Object
     * @return object Type Information
     */
    public Object getResource(){
        if(isStringvalue)
            return getStringResource();
        else
            return getNumberResource();
    }
    
    /**
     * get String type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public String getResourceString(){
        if(isStringvalue)
            return getStringResource();
        else
            return String.valueOf(getResource());
    }
    
    /**
     * get Integer type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Integer getResourceInt(){
        if(isStringvalue)
            return null;
        else
            return (int)getNumberResource();
    }
    
    /**
     * get Float type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Float getResourceFloat(){
        if(isStringvalue)
            return null;
        else
            return (float)getNumberResource();
    }
    
    /**
     * get Double type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Double getResourceDouble(){
        if(isStringvalue)
            return null;
        else
            return (double)getNumberResource();
    }
    
    /**
     * get Long type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Long getResourceLong(){
        if(isStringvalue)
            return null;
        else
            return (long)getNumberResource();
    }
    
    /**
     * get Short type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Short getResourceShort(){
        if(isStringvalue)
            return null;
        else
            return (short)getNumberResource();
    }
    
    /**
     * get Character type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public Character getResourceChar(){
        if(isStringvalue)
            return null;
        else
            return (char)getNumberResource();
    }
    
    /**
     * get Boolean type Information
 if it is not matched to input data type(numeric <-> String, return null 
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
    
    public abstract Object getClone();
}
