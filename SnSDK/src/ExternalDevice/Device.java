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
package ExternalDevice;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Device {
    private int[] port;
    private String Library_Name = null;
    
    public Device(String _LibName, int[] port){
        Library_Name = _LibName;
        initPort(port);
    }

    private void initPort(int[] _port) {
        port = _port;
    }
    
    /**
     * Load Library
     * @return true, Library Load Success
     */
    protected boolean LoadLibrary(){
//        if(Library_Name == null)
//            return false;
//        
        try{
            System.loadLibrary(Library_Name);
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    protected String getLibraryName(){
        return this.Library_Name;
    }
    
    protected boolean hasPort(){
        if(port != null && port.length > 0)
            return true;
        else
            return false;
    }
}
