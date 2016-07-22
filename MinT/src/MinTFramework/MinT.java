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

import MinTFramework.CacheMap.Resource;
import MinTFramework.ExternalDevice.DeviceClassification;
import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.ExternalDevice.DeviceManager;
import MinTFramework.ExternalDevice.Device;
import MinTFramework.ExternalDevice.DeviceBLE;
import MinTFramework.Network.RoutingProtocol;
import MinTFramework.Network.Handler;
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.Profile;
import java.util.ArrayList;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class MinT {

    private DeviceManager devicemanager;
    private NetworkManager networkmanager;
    private Scheduler scheduler;
    private LocalCache sharedcache;
    DeviceClassification deviceClassification;
    DeviceType deviceType;
    
    /**
     * 
     * @param requestQueueLength Maximym request queue length
     * @param numOfThread number of workerthread in framework
     */
    public MinT(int requestQueueLength, int numOfThread) {
        scheduler = new Scheduler(requestQueueLength, numOfThread);
        devicemanager = new DeviceManager();
        networkmanager = new NetworkManager(this);
        sharedcache = new LocalCache();
    }
    
    /**
     * Framework Constructor
     * Default number of WorkerThread and Requestqueuelength : 5
     */
    public MinT() {
        this(MinTConfig.DEFAULT_REQEUSTQUEUE_LENGTH, MinTConfig.DEFAULT_THREAD_NUM);
    }

    /**
     * @deprecated not yet used
     * @param deviceClassification Class of deivce (Sensor or Network)
     * @param deviceType Type of device (Temperature, Humidity, etc)
     */
    public MinT(DeviceClassification deviceClassification, DeviceType deviceType) {
        this();
        devicemanager = new DeviceManager();
        this.deviceClassification = deviceClassification;
    }
    
    /**
     * @deprecated not yet used
     * @param requestQueueLength Maximym request queue length
     * @param numOfThread number of workerthread in framework
     * @param deviceClassification Class of deivce (Sensor or Network)
     * @param deviceType Type of device (Temperature, Humidity, etc)
     */
    public MinT(int requestQueueLength, int numOfThread, 
            DeviceClassification deviceClassification, DeviceType deviceType) {
        this(MinTConfig.DEFAULT_REQEUSTQUEUE_LENGTH, MinTConfig.DEFAULT_THREAD_NUM);
        this.deviceClassification = deviceClassification;
        this.deviceType = deviceType;
        }
    
    /**
     * Add device to device manager
     * @param device device that want to add
     */
    public void addDevice(Device device) {
        devicemanager.addDevice(device);
    }
    /**
     * Add device to device manager
     * @param device device that wnat to add
     * @param name name that want to name
     */
    public void addDevice(Device device, String name){
        devicemanager.addDevice(device, name);
    }
    
    /**
     * Return device for the ID.
     * @param DeviceID 
     * @return Device for ID
     */
    public Device getDevice(int DeviceID) {
        return devicemanager.getDevice(DeviceID);
    }
    /**
     * Return device for name
     * @param name name for device
     * @return Device for name
     */
    public Device getDevice(String name){
        return devicemanager.getDevice(name);
    }
    
    /**
     * return device list by DeviceType
     * @param dtype
     * @return 
     */
    public ArrayList<Device> getDevices(DeviceType dtype){
        return devicemanager.getDevicesbyDeviceType(dtype);
    }
    
    /**
     * get BLE Device
     * @return 
     */
    public DeviceBLE getBLEDevice(){
        ArrayList<Device> dlist = devicemanager.getDevicesbyDeviceType(DeviceType.BLE);
        if(dlist != null){
            return (DeviceBLE)dlist.get(0);
        }
        else
            return null;
    }
    
    /**
     * Remove device for the ID.
     * @param deviceID 
     */
    public void removeDevice(int deviceID) {
        devicemanager.removeDevice(deviceID);
    }
    
    /**
     * Get Array of device IDs
     * @return Array of device IDs
     */
    public int[] getDeviceIDList() {
        return devicemanager.getDeviceList();
    }
    /**
     * not completed source
     * @deprecated 
     * Return all devices in device manager
     * @return array of device
     */
    public Device[] getAllDevices(){
        return devicemanager.getAllDevices();
    }
    /**
     * Check whether device manager has device for ID.
     * @param deviceID Device ID to check
     * @return 
     */
    public boolean hasDevice(int deviceID) {
        return devicemanager.hasDevice(deviceID);
    }
    /**
     * Remove all of the devices in the device manager
     */
    public void clearDeviceList() {
        devicemanager.clearDeviceList();
    }
    /**
     * Remove request in Scheduler
     * @param request Request object to remove from scheduler
     */
    public void stopRequest(Request request) {
        scheduler.stopRequest(request);
    }
    /**
     * Add request in Scheduler
     * @param request Request object to add to scheduler
     */
    public void putRequest(Request request) {
        scheduler.putRequest(request);
    }

    /**
     * Print request name and id in thread in scheduler
     */
    public void showWorkingThreads() {
        scheduler.showWorkingThreads();
    }
    
    /**
     * get Number of Working Threads
     * @return 
     */
    public int getNumberofWorkingThreads(){
        return scheduler.getNumberofWorkingThreads();
    }
    
    /*************************
     * Network
     ************************/
   
    /**
     * set network user configuration
     * @param name node Name
     * @param nhandler User Handler
     */
    public void setNetwork(String name, Handler nhandler){
        networkmanager.setNetworkHandler(nhandler);
        networkmanager.setNodeName(name);
    }
    
    /**
     * set network user configuration (only Name)
     * @param name node Name 
     */
    public void setNetwork(String name){
        setNetwork(name, null);
    }
    
    /**
     * set network user configuration (only Name)
     * @param nhandler user Handler
     */
    public void setNetwork(Handler nhandler){
        setNetwork(null,nhandler);
    }
    
    /**
     * set Network Handler for APP
     * @param nhandler 
     */
    public Handler getNetworkHandler(){
        return networkmanager.getNetworkHandler();
    }
    
    /**
     * get NodeName
     * @return 
     */
    public String getNodeName(){
        return networkmanager.getNodeName();
    }
    
    /**
     * add Network
     * @param ntype 
     */
    public void addNetwork(NetworkType ntype){
        networkmanager.AddNetwork(ntype);
    }
    
    /**
     * add Network with Port
     * !!Caution!! Network Port was fixed
     * @param ntype
     * @param port  Internet Port for UDP, TCP/IP or CoAP
     */
    public void addNetwork(NetworkType ntype, Integer port){
        ntype.setPort(port);
        addNetwork(ntype);
    }
    
    /**
     * Send Message
     * @deprecated 
     * @param dst
     * @param msg 
     */
    public void sendMessage(String dst, NetworkType nt, String msg){
        sendDirectMessage(new Profile("noname",dst,nt),msg);
    }
    
    public void sendDirectMessage(Profile dst, String msg){
        networkmanager.sendDirectMessage(dst,msg);
    }
    
    
    /**
     * set Application Protocol
     * @param ap 
     */
    public void setRoutingProtocol(RoutingProtocol ap){
        networkmanager.setRoutingProtocol(ap);
    }
    
    /***************************
     * Local Shared Cache
     ****************************/
    
    /**
     * put cache data to Shared Memory
     * @param name
     * @param cdata 
     */
    public void putLocalResource(String name, lResource cdata){
        sharedcache.put(name, cdata);
    }
    
    /**
     * get Cache Data from Shared Memory
     * @param cache name
     * @return 
     */
    public lResource getLocalResource(String name){
        System.out.println("Mem Size : "+sharedcache.getAllResource().size());
        return sharedcache.get(name);
    }
    
    /**
     * delete shared data
     * @param name
     * @return 
     */
    public boolean deleteLocalResource(String name){
        return sharedcache.delete(name);
    }
    
    /***************************
     * Frame Operation
     ****************************/
    
    /**
     * Start framework
     */
    public void Start() {
        devicemanager.initAllDevice();
        networkmanager.TurnOnNetwork();
        scheduler.SchedulerRunning();
    }
    
    protected void isDebug(boolean isdebug){
        MinTConfig.DebugMode = isdebug;
    }
    
}
