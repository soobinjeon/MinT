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
package SnSDK.ExternalDevice;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class DeviceManager {
    /** 
     * App Request Interpreter
     * -Analysis the App request information
     * -Process controlling the device or initializing the device
     * 
     * Device initialization
     * -Initialize the device
     * -Set the device port
     * -Confirm the Library Location
     * -Initialize the BBBIO and Sensor’s setting
     * 
     * Device Communication
     * -Connect the Device and Application
     * -getDevice(int Device.id)
     * 
     * Device state Control
     * -Manage the device state
     * -Add
     * -Remove
     * -HasDevice
     * -isDevice
     * -getDeviceID
     * - ~
     */
    /**
     * devicemap : 디바이스 리스트를 저장할 해시맵
     */
    private HashMap<Integer, Device> devicemap = new HashMap<Integer, Device>(); 
    
    /**
     * 디바이스맵을 반환
     * @return Device Hash Map
     */
    public HashMap<Integer, Device> getDeviceList(){
        return devicemap;
    }
    public Device getDevice(int _deviceid){
        if(!devicemap.containsKey(_deviceid)){
            System.out.println("getDevice : Device founded");
            return devicemap.get(_deviceid);
        }
        else{
            System.out.println("getDevice : No device id "+_deviceid);
            return null;
        }
    }
    /**
     * 디바이스를 리스트에 추가
     * @param _device 추가할 디바이스 객체
     */
    public void addDevice(Device _device){
        if(!devicemap.containsValue(_device)){
            devicemap.put(devicemap.size()+1, _device);
            System.out.println("Add Device : ID : "+devicemap.size()+", Library Name : "+_device.getLibraryName()+" ");
        }
        else {
            System.out.println("Add Device : "+_device.getLibraryName()+" is already exist");
        }
    }
    /**
     * 디바이스맵에서 디바이스 제거
     * @param _deviceid 제거할 디바이스 아이디
     */
    public void removeDevice(int _deviceid){
        if(devicemap.containsKey(_deviceid)){
            devicemap.remove(_deviceid);
            System.out.println("removeDevice : "+_deviceid+" is deleted");
        }
        else{
            System.out.println("removeDevice : Threre is no device id : "+_deviceid);
        }
    }
    /**
     * 디바이스 리스트 출력
     */
    public void showDeviceList(){
        if(devicemap.size()==0){
            System.out.println();
            System.out.println("----- Device List --------");
            System.out.println("There is no Device");
            System.out.println("--------------------------");
            System.out.println();
            return;
        }
        Set<Entry<Integer, Device>> set = devicemap.entrySet();
        Iterator<Entry<Integer, Device>> it = set.iterator();
        while(it.hasNext()){
            Map.Entry<Integer, Device> e = (Map.Entry<Integer, Device>)it.next();
            System.out.println();
            System.out.println("----- Device List --------");
            System.out.println("Device ID : "+e.getKey() + "   "+"Library Name : " + e.getValue().getLibraryName());   
            System.out.println("--------------------------");
            System.out.println();
        }
    }
    
}
