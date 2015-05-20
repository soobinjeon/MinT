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

import SnSDK.Util.DebugLog;
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
     * App Request Interpreter -Analysis the App request information -Process
     * controlling the device or initializing the device
     *
     * Device initialization -Initialize the device -Set the device port
     * -Confirm the Library Location -Initialize the BBBIO and Sensor’s setting
     *
     * Device Communication -Connect the Device and Application -getDevice(int
     * Device.id)
     *
     * Device state Control -Manage the device state -Add -Remove -HasDevice
     * -isDevice -getDeviceID - ~
     */
    /**
     * devicemap : 디바이스 리스트를 저장할 해시맵
     */
    private final HashMap<Integer, Device> devicemap;
    private final HashMap<String, Integer> namemap;
    private DebugLog log;

    public DeviceManager() {
        devicemap = new HashMap<>();
        namemap = new HashMap<>();
    }

    public void loggerOn() {
        log = new DebugLog("DeviceManager");
    }

    private void log(String str) {
        if (log != null) {
            log.printMessage(str);
        }
    }

    /**
     * 디바이스의 아이디 리스트를 반환
     *
     * @return Device ID List
     */
    public int[] getDeviceList() {
        int idlist[] = new int[devicemap.size()];
        int i = 0;
        Set<Entry<Integer, Device>> set = devicemap.entrySet();
        Iterator<Entry<Integer, Device>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Device> e = (Map.Entry<Integer, Device>) it.next();
            idlist[i] = e.getKey();
            i++;
        }
        return idlist;
    }

    /**
     * Refurn device for ID
     *
     * @param _deviceid
     * @return ID에 해당하는 디바이스 객체
     */
    public Device getDevice(int _deviceid) {
        Device d;
        if (devicemap.containsKey(_deviceid)) {
            log("getDevice : Device founded");
            d = devicemap.get(_deviceid);
        } else {
            System.out.println("getDevice : No device id " + _deviceid);
            d = null;
        }
        return d;
    }
    /**
     * Return All devices in device manager
     * @return Device array
     */
    public Device[] getAllDevices(){
        Device[] dlist = new Device[devicemap.size()];
        return dlist;
    }

    /**
     * 디바이스를 리스트에 추가
     *
     * @param _device 추가할 디바이스 객체
     */
    public void addDevice(Device _device) {
        int DeviceID;
        DeviceID = makeID();
        devicemap.put(DeviceID, _device);
        System.out.println(getClass().getName());
        log("addDevice : ID : " + DeviceID + ", Library Name : " + _device.getLibraryName() + " ");
    }
    
    /**
     * 디바이스맵에서 디바이스 제거
     *
     * @param _deviceid 제거할 디바이스 아이디
     */
    public void removeDevice(int _deviceid) {
        log("removeDevice : containsKey = " + devicemap.containsKey(_deviceid));
        if (devicemap.containsKey(_deviceid)) {
            log("removeDevice : " + _deviceid + ", " + devicemap.get(_deviceid).getLibraryName() + " is deleted");
            devicemap.remove(_deviceid);
        } else {
            System.out.println("removeDevice : Threre is no device id : " + _deviceid);
        }
    }

    /**
     * 디바이스 리스트를 콘솔에 출력
     */
    public void showDeviceList() {
        log("showDeviceList : map size is " + devicemap.size());
        if (devicemap.isEmpty()) {
            System.out.println("----- Device List --------");
            System.out.println("There is no Device");
            System.out.println("--------------------------");
            return;
        }
        Set<Entry<Integer, Device>> set = devicemap.entrySet();
        Iterator<Entry<Integer, Device>> it = set.iterator();
        System.out.println("----- Device List --------");
        while (it.hasNext()) {
            Map.Entry<Integer, Device> e = (Map.Entry<Integer, Device>) it.next();
            System.out.println("Device ID : " + e.getKey() + "   LibName : " + e.getValue().getLibraryName());
        }
        System.out.println("--------------------------");
    }

    /**
     * Device의 ID를 생성 후 반환
     *
     * @return 디바이스의 ID
     */
    private int makeID() {
        int i = 1;
        while (devicemap.containsKey(i) || i < devicemap.size()) {
            i++;
        }
        return i;
    }

    /**
     * 디바이스 리스트의 모든 디바이스 init 작업 실행
     */
    public void initAllDevice() {
        if (devicemap.isEmpty()) {
            System.out.println("There is no Device");
            return;
        }
        Set<Entry<Integer, Device>> set = devicemap.entrySet();
        Iterator<Entry<Integer, Device>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Device> e = (Map.Entry<Integer, Device>) it.next();
            e.getValue().initDevice();
        }
        System.out.println("initAllDevice: " + devicemap.size() + " devices Init Complete");
    }

    /**
     * 디바이스 리스트 초기화
     */
    public void clearDeviceList() {
        devicemap.clear();
        log("clearDeviceList: 디바이스리스트 초기화");
    }

    /**
     * 디바이스 리스트에 deviceID에 해당하는 디바이스가 있는지 검사
     *
     * @param key
     * @return
     */
    public boolean hasDevice(int deviceID) {
        return devicemap.containsKey(deviceID);
    }
}
