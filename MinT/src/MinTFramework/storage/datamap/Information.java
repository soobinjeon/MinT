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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Information {
    protected Object res;
    protected boolean isStringvalue = true;
    
    public Information(Object _getResource){
        setResource(_getResource);
    }
    
    public void setResource(Object setres){
        try{
            if(setres == null){
                isStringvalue = true;
                setres = new String("");
            }
            
            if(setres instanceof Boolean){
                isStringvalue = false;
            }else if(setres instanceof String){
                isStringvalue = true;
            }else{
                isStringvalue = false;
            }
            res = setres;
        }catch(Exception e){
            e.printStackTrace();
            /**/
        }
    }
    
    /**
     * get Information by Object
     * @return object Type Information
     */
    public Object getResource(){
        return res;
    }
    
    /**
     * get String type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public String getResourceString(){
        if(isStringvalue){
            System.out.println("String value : "+res);
            return (String)res;
        }
        else
            return String.valueOf(getResource());
    }
    
    /**
     * get Integer type Information
     if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public int getResourceInt() {
        try {
            if (isStringvalue)
                return Integer.parseInt(getResourceString());
            else{
                if(res instanceof Long)
                    return ((Long)res).intValue();
                else
                    return (int) res;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return (int)0;
        }
    }
    
    /**
     * get Float type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public float getResourceFloat(){
        try {
            if (isStringvalue)
                return Float.parseFloat(getResourceString());
            else{
                if(res instanceof Long)
                    return ((Long)res).floatValue();
                else
                    return (float) res;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return (float)0;
        }
    }
    
    /**
     * get Double type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public double getResourceDouble(){
        try {
            if (isStringvalue)
                return Double.parseDouble(getResourceString());
            else{
                if(res instanceof Long)
                    return ((Long)res).doubleValue();
                else
                    return (double) res;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return (double)0;
        }
    }
    
    /**
     * Not Use Long Type
     * get Long type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public long getResourceLong(){
        try {
            if (isStringvalue)
                return Long.parseLong(getResourceString());
            else
                return (long) res;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * get Short type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public short getResourceShort(){
        try {
            if (isStringvalue)
                return Short.parseShort(getResourceString());
            else{
                if(res instanceof Long)
                    return ((Long)res).shortValue();
                else
                    return (short) res;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return (short)0;
        }
    }
    
    /**
     * get Character type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public char getResourceChar() {
        try {
            if (isStringvalue) {
                return 0;
            } else {
                return (char) getResourceDouble();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * get Boolean type Information
 if it is not matched to input data type(numeric <-> String, return null 
     * @return null, if it is not matched to input data type
     */
    public boolean getResourceBoolean(){
        try {
            if (isStringvalue) {

                return Boolean.parseBoolean(getResourceString());

            } else {
                return Boolean.parseBoolean(getResourceString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//        else{
//            int tf = (int)getResourceDouble();
//            if(tf == 0)
//                return false;
//            else
//                return true;
//        }
    }
}
