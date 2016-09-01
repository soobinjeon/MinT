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

import MinTFramework.storage.PropertyManager;
import MinTFramework.storage.ResourceStorage;
import MinTFramework.storage.InstructionManager;
import MinTFramework.ExternalDevice.DeviceClassification;
import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.ExternalDevice.DeviceManager;
import MinTFramework.ExternalDevice.Device;
import MinTFramework.ExternalDevice.DeviceBLE;
import MinTFramework.Network.Routing.RoutingProtocol;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.Request;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.SendMSG;
import MinTFramework.SystemScheduler.Service;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.Util.Benchmarks.Performance;
import MinTFramework.storage.ResData;
import MinTFramework.storage.Resource;
import MinTFramework.storage.ThingInstruction;
import MinTFramework.storage.ThingProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import MinTFramework.Util.Benchmarks.BenchAnalize;
/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class MinT {

    private DeviceManager devicemanager;
    private NetworkManager NTWmanager;
    private static MinT MinTFrame;
    //System Scheduler
    private SystemScheduler sched;
    
    private ResourceStorage resourceStorage;
    private PropertyManager PM;
    private InstructionManager IM;
    DeviceClassification deviceClassification;
    DeviceType deviceType;
    
    public static enum PERFORM_METHOD{
        NETWORK_SEND,UDP_RECV, UDP_SEND, RECV_LAYER, SEND_LAYER, Trans_Sender, MaS_Sender;
    }
    private ConcurrentHashMap<PERFORM_METHOD, ArrayList<Performance>> benchmarks;
    private boolean BenchMode = false;
    
    /**
     * 
     * @param serviceQueueLength Maximym service queue length
     * @param numOfThread number of workerthread in framework
     */
    public MinT(int serviceQueueLength, int numOfThread) {
        MinTFrame = this;
        sched = new SystemScheduler();
        devicemanager = new DeviceManager();
        resourceStorage = new ResourceStorage();
        PM = new PropertyManager();
        IM = new InstructionManager();
        NTWmanager = new NetworkManager();
        benchmarks = new ConcurrentHashMap();
        setupBenchMark();
    }
    
    /**
     * Framework Constructor
     * Default number of WorkerThread and Servicequeuelength : 5
     */
    public MinT() {
        this(MinTConfig.DEFAULT_REQEUSTQUEUE_LENGTH, MinTConfig.DEFAULT_THREAD_NUM);
    }
    
    public static MinT getInstance(){
        return MinTFrame;
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
     * @param serviceQueueLength Maximym service queue length
     * @param numOfThread number of workerthread in framework
     * @param deviceClassification Class of deivce (Sensor or Network)
     * @param deviceType Type of device (Temperature, Humidity, etc)
     */
    public MinT(int serviceQueueLength, int numOfThread, 
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
        if(!dlist.isEmpty()){
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
     * get SystemScheduler
     * @return 
     */
    public SystemScheduler getSysteScheduler(){
        return sched;
    }
    
    /**
     * Add service in SchedulePool
     * @param service Service_OLD object to add to scheduler
     */
    public void putService(Service service){
        sched.addService(service);
    }

//    /**
//     * Print service name and id in thread in scheduler
//     */
//    public void showWorkingThreads() {
//        scheduler.showWorkingThreads();
//    }
    
    /**
     * @deprecated 
     * get Number of Working Threads
     * @return 
     */
    public int getNumberofWorkingThreads(){
        return 0;
    }
    
    /*************************
     * Network
     ************************/
   
    /**
     * @deprecated 
     * Use setNetworkName
     * set network user configuration
     * @param name node Name
     * @param nhandler User Handler
     */
    public void setNetwork(String name){
        NTWmanager.setNodeName(name);
    }
    
    /**
     * set network Name
     * @param name node Name
     */
    public void setNetworkName(String name){
        NTWmanager.setNodeName(name);
    }
    
    /**
     * get NodeName
     * @return 
     */
    public String getNodeName(){
        return NTWmanager.getNodeName();
    }
    
    /**
     * get NetworkManager
     * @return 
     */
    public NetworkManager getNetworkManager(){
        return NTWmanager;
    }
    
    /**
     * add Network
     * @param ntype 
     */
    public void addNetwork(NetworkType ntype){
        NTWmanager.AddNetwork(ntype);
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
     * 
     * @param ntype type of Network
     * @param addr Current Internet Address for UDP, TCP/IP or CoAP
     * @param port Internet Port for UDP, TCP/IP or CoAP
     */
    public void addNetwork(NetworkType ntype, String addr, Integer port){
        MinTConfig.IP_ADDRESS = addr;
        addNetwork(ntype, port);
    }
    
    /**
     * GET Resource Data matched to filled Resource
     * @deprecated 
     * @param msg 
     */
    public void GET(String ResourceName){
        
    }
    
    /**
     * GET Resource data matched to Device Type
     * @deprecated 
     * @param dt 
     */
    public void GETbyDeviceType(DeviceType dt){
        
    }
    
    /**
     * Request directly Resource Data to other Node
     * @param dst destination Node Information
     * @param resName Resource Name
     * @param resHandle Response Handler
     */
    public void REQUEST_GET(NetworkProfile dst, String resName, ResponseHandler resHandle){
        NTWmanager.SEND(new SendMSG(PacketDatagram.HEADER_DIRECTION.REQUEST
                ,PacketDatagram.HEADER_INSTRUCTION.GET, dst,resName, resHandle));
    }
    
    /**
     * Discover resources from other Node
     * @param dst 
     * @param resHandle Response Handler
     */
    public void DISCOVERY(NetworkProfile dst, ResponseHandler resHandle){
        NTWmanager.SEND(new SendMSG(PacketDatagram.HEADER_DIRECTION.REQUEST,
                PacketDatagram.HEADER_INSTRUCTION.DISCOVERY, dst,"",resHandle));
    }
    
    /**
     * Get Local Resource by Resource Name
     * @param resName
     * @return 
     */
    public ResData GETLocalResource(String resName){
        Request req = new Request(resName, 0, null);
        return this.resourceStorage.getProperty(req);
    }
    
    public List<String> GETLocalPropertyList(){
        return this.getResStorage().getPropertyList();
    }
    
    public List<String> GETLocalInstructionList(){
        return this.getResStorage().getInstructionList();
    }
    
    public void printPropertyLists(){
        for(ThingProperty tp : getResStorage().getProperties()){
            System.out.println(tp.getName()+ " : " + tp.getStorageDirectory().getSourceLocation()+
                    ", "+tp.getDeviceType().toString() +", "+tp.getPropertyRole().toString());
        }
    }
    
    /**
     * set Application Protocol
     * @param ap 
     */
    public void setRoutingProtocol(RoutingProtocol ap){
        NTWmanager.setRoutingProtocol(ap);
    }
    
    /**
     * get RoutingProtocol for test
     * @deprecated 
     * @return 
     */
    public RoutingProtocol getRoutingProtocol(){
        return NTWmanager.getRoutingProtocol();
    }
    
    /***************************
     * Resource & Sharing
     ****************************/
    
    /**
     * add Resource to Frame
     * @param res 
     */
    public void addResource(Resource res){
        res.setFrame(this);
        if(res instanceof ThingProperty)
            PM.addProperty((ThingProperty)res);
        else if(res instanceof ThingInstruction)
            IM.addInstruction((ThingInstruction)res);
    }
    
    /**
     * for Test get Resource Storage
     * @return 
     */
    public ResourceStorage getResStorage(){
        return this.resourceStorage;
    }
    
    /**
     * get Routing group of this node
     * @return 
     */
    public String getResourceGroup(){
        return this.NTWmanager.getCurrentRoutingGroup();
    }
//    /**
//     * put cache data to Shared Memory
//     * @param name
//     * @param cdata 
//     */
//    public void putLocalResource(String name, lResource cdata){
//        sharedcache.put(name, cdata);
//    }
//    
//    /**
//     * get Cache Data from Shared Memory
//     * @param cache name
//     * @return 
//     */
//    public lResource getLocalResource(String name){
//        System.out.println("Mem Size : "+sharedcache.getAllResource().size());
//        return sharedcache.get(name);
//    }
//    
//    /**
//     * delete shared data
//     * @param name
//     * @return 
//     */
//    public boolean deleteLocalResource(String name){
//        return sharedcache.delete(name);
//    }
    
    /***************************
     * BenchMarks
     ****************************/
    public void setBenchMode(boolean bm){
        BenchMode = bm;
    }
    
    public boolean isBenchMode(){
        return BenchMode;
    }
    
    private void setupBenchMark() {
        for(PERFORM_METHOD pm : PERFORM_METHOD.values()){
            ArrayList<Performance> na = new ArrayList();
            benchmarks.put(pm, na);
        }
    }
    
    public void addPerformance(PERFORM_METHOD pm, Performance p){
        ArrayList<Performance> pl = benchmarks.get(pm);
        if(pl != null){
            pl.add(p);
        }
    }
    
//    public ConcurrentHashMap<PERFORM_METHOD, ArrayList<Performance>> getBenchmarks(){
//        return benchmarks;
//    }
    
    public ArrayList<Performance> getBenchmarks(PERFORM_METHOD pm){
        return benchmarks.get(pm);
    }
    
    public BenchAnalize getBenchAnalize(PERFORM_METHOD pm){
        ArrayList<Performance> pl = benchmarks.get(pm);
        if(pl == null)
            return null;
        else
            return new BenchAnalize(pm, pl);
    }
    
    /***************************
     * Frame Operation
     ****************************/
    
    /**
     * Start framework
     */
    public void Start() {
        devicemanager.initAllDevice();
        NTWmanager.onStart();
        sched.startService();
    }
    
    protected void isDebug(boolean isdebug){
        MinTConfig.DebugMode = isdebug;
        System.out.println();
    }
    
}
