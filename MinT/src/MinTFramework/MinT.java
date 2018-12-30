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
import MinTFramework.Network.Sharing.routingprotocol.RoutingProtocol;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.SendMSG;
import MinTFramework.Network.Sharing.ResourceOption;
import MinTFramework.Network.Sharing.Sharing;
import MinTFramework.Network.Sharing.node.NodeSpecify;
import MinTFramework.SystemScheduler.Service;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.Util.Benchmarks.MinTBenchmark;
import MinTFramework.storage.ResData;
import MinTFramework.storage.Resource;
import MinTFramework.storage.Resource.StoreCategory;
import MinTFramework.storage.ThingInstruction;
import MinTFramework.storage.ThingProperty;
import java.util.ArrayList;
import java.util.List;
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
    private DeviceClassification deviceClassification;
    private DeviceType deviceType;
    
    private MinTConfig mintConfig;
    
    /**
     * 
     * @param serviceQueueLength Maximym service queue length
     * @param numOfThread number of workerthread in framework
     */
    public MinT(int serviceQueueLength, int numOfThread, String AndroidFilePath) {
        MinTFrame = this;
        if(AndroidFilePath != null)
            MinTConfig.ANDROID_FILE_PATH = AndroidFilePath;
        mintConfig = new MinTConfig();
        sched = new SystemScheduler();
        devicemanager = new DeviceManager();
        resourceStorage = new ResourceStorage();
        PM = new PropertyManager();
        IM = new InstructionManager();
        NTWmanager = new NetworkManager();
        mintBench = new MinTBenchmark(getSystemScheduler(), 500);
    }
    
    /**
     * Framework Constructor
     * Default number of WorkerThread and Servicequeuelength : 5
     */
    public MinT() {
        this(MinTConfig.DEFAULT_REQEUSTQUEUE_LENGTH, MinTConfig.DEFAULT_THREAD_NUM, null);
    }
    
    /**
     * Set up for Android Platform
     * @param AndroidFilePath set Android File Path
     */
    public MinT(String AndroidFilePath){
        this(MinTConfig.DEFAULT_REQEUSTQUEUE_LENGTH, MinTConfig.DEFAULT_THREAD_NUM, AndroidFilePath);
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
        this(MinTConfig.DEFAULT_REQEUSTQUEUE_LENGTH, MinTConfig.DEFAULT_THREAD_NUM, null);
        this.deviceClassification = deviceClassification;
        this.deviceType = deviceType;
        }
    
    /**
     * get MinT Config
     * @return 
     */
    public MinTConfig getConfig(){
        return mintConfig;
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
    public SystemScheduler getSystemScheduler(){
        return sched;
    }
    
    /**
     * Add service in SchedulePool
     * @param service Service_OLD object to add to scheduler
     */
    public void putService(Service service){
        sched.addService(service);
    }
    
    /**
     * Add Service after MinT
     * @param service 
     */
    public void executeService(Service service){
        sched.addExecuteService(service);
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
    
//    /**
//     * add Network
//     * @param ntype 
//     */
//    private void addNetwork(NetworkType ntype){
//        NTWmanager.AddNetwork(ntype);
//    }
    
//    /**
//     * @deprecated 
//     * add Network with Port
//     * !!Caution!! Network Port was fixed
//     * @param ntype
//     * @param port  Internet Port for UDP, TCP/IP or CoAP
//     */
//    public void addNetwork(NetworkType ntype, Integer port){
//        ntype.setPort(port);
//        addNetwork(ntype);
//    }
    
    /**
     * 
     * @param ntype type of Network
     * @param addr Current Internet Address for UDP, TCP/IP or CoAP
     */
    public void addNetwork(NetworkType ntype, String addr){
        MinTConfig.IP_ADDRESS = addr;
        NTWmanager.AddNetwork(ntype);
    }
    
    /**
     * @deprecated 
     * @param ntype type of Network
     * @param addr Current Internet Address for UDP, TCP/IP or CoAP
     * @param port Internet Port for UDP, TCP/IP or CoAP
     */
    public void addNetwork(NetworkType ntype, String addr, Integer port){
        ntype.setPort(port);
        addNetwork(ntype, addr);
    }
    
    /**
     * Activate RoutingMode to join the region routing group
     * @param groupName Routing Group Name
     * @param ns <Platforms> Operating Platform
     */
    public void activateRoutingProtocol(String groupName, NodeSpecify ns){
        NTWmanager.activeRoutingProtocol(groupName, ns, false);
    }
    
    public void activateRoutingProtocol(){
        activateRoutingProtocol(false);
    }
    
    public void activateRoutingProtocol(boolean isVirtualPower){
        activateRoutingProtocol(isVirtualPower, false);
    }
    
    public void activateRoutingProtocol(boolean isVirtualPower, boolean isMulticast){
        if(getConfig().getGroupName().isEmpty())
            System.err.println("Sharing Group Name is Empty - Routing Protocol is not activated");
        else{
            if(isVirtualPower)
                NTWmanager.activeRoutingProtocol(getConfig().getGroupName()
                    , new NodeSpecify(getConfig().getSpecPower(), 0), isMulticast);
            else
                NTWmanager.activeRoutingProtocol(getConfig().getGroupName()
                    , new NodeSpecify(null, 0), isMulticast);
        }
    }
    
    
    
    /**
     * GET Resource Data matched to filled Resource
     * @deprecated
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
     * 
     * @param dst
     * @param requestdata (new Request(Resource Name, Resource Method)
     * @param resHandle 
     */
    public void REQUEST_PUT(NetworkProfile dst, Request requestdata, ResponseHandler resHandle){
        NTWmanager.SEND(new SendMSG(dst.getHeaderType(), 2
                ,CoAPPacket.HEADER_CODE.PUT, dst,requestdata, resHandle));
    }
    
    /**
     * 
     * @param dst
     * @param requestdata (new Request(Resource Name, Resource Method)
     * @param resHandle 
     */
    public void REQUEST_POST(NetworkProfile dst, Request requestdata, ResponseHandler resHandle){
        NTWmanager.SEND(new SendMSG(dst.getHeaderType(), 2
                ,CoAPPacket.HEADER_CODE.POST, dst,requestdata, resHandle));
    }
    
    /**
     * 
     * @param dst
     * @param requestdata (new Request(Resource Name, Resource Method)
     * @param resHandle 
     */
    public void REQUEST_DELETE(NetworkProfile dst, Request requestdata, ResponseHandler resHandle){
        NTWmanager.SEND(new SendMSG(dst.getHeaderType(), 2
                ,CoAPPacket.HEADER_CODE.DELETE, dst,requestdata, resHandle));
    }
    
    /**
     * Request directly Resource Data to other Node
     * @param dst destination Node Information
     * @param requestdata Resource Name
     * @param resHandle Response Handler
     */
    public void REQUEST_GET(NetworkProfile dst, Request requestdata, ResponseHandler resHandle){
        NTWmanager.SEND(new SendMSG(dst.getHeaderType(), 2
                ,CoAPPacket.HEADER_CODE.GET, dst,requestdata, resHandle));
    }
    
    /**
     * Request get type Resource to other Node by Multicast
     * @param requestdata
     * @param resHandle 
     */
    public void REQUEST_GET_MULTICAST(SendMessage requestdata, ResponseHandler resHandle){
        NTWmanager.SEND_Multicast(new SendMSG(CoAPPacket.HEADER_TYPE.NON, 2
                , CoAPPacket.HEADER_CODE.GET, null, requestdata, resHandle, true));
    }
    
    /**
     * PUT Resource to other Node by Multicast
     * @param requestdata
     * @param resHandle 
     */
    public void REQUEST_PUT_MULTICAST(SendMessage requestdata, ResponseHandler resHandle){
        NTWmanager.SEND_Multicast(new SendMSG(CoAPPacket.HEADER_TYPE.NON, 2
                , CoAPPacket.HEADER_CODE.PUT, null, requestdata, resHandle, true));
    }
    
    /**
     * Post Resource to other Node by Multicast
     * @param requestdata
     * @param resHandle 
     */
    public void REQUEST_POST_MULTICAST(SendMessage requestdata, ResponseHandler resHandle){
        NTWmanager.SEND_Multicast(new SendMSG(CoAPPacket.HEADER_TYPE.NON, 2
                , CoAPPacket.HEADER_CODE.POST, null, requestdata, resHandle, true));
    }
    
    /**
     * Request delete  to other Node by Multicast
     * @param requestdata
     * @param resHandle 
     */
    public void REQUEST_DELETE_MULTICAST(SendMessage requestdata, ResponseHandler resHandle){
        NTWmanager.SEND_Multicast(new SendMSG(CoAPPacket.HEADER_TYPE.NON, 2
                , CoAPPacket.HEADER_CODE.DELETE, null, requestdata, resHandle, true));
    }
    
    /**
     * 
     * @param dtype
     * @param resOpt
     * @param resHandle 
     */
    public void GET_SHARING_RESOURCE(DeviceType dtype, ResourceOption resOpt, ResponseHandler resHandle){
        GET_SHARING_RESOURCE(dtype, resOpt, resHandle, -1);
    }
    
    /**
     * 
     * @param dtype
     * @param resOpt
     * @param resHandle 
     */
    public void GET_SHARING_RESOURCE(DeviceType dtype, ResourceOption resOpt, ResponseHandler resHandle, int checkvalue){
        Sharing sharing = NTWmanager.getSharing();
        try{
            sharing.getResource(dtype, resOpt, resHandle, checkvalue);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
//    public void GET_SHARING_RESOURCE(DeviceType dtype, List<ResourceOption> resOpt, ResponseHandler resHandle){
//        Sharing sharing = NTWmanager.getSharing();
//        try{
//            sharing.getResource(dtype, resHandle);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
    
    
    /**
     * @deprecated 
     * set Application Protocol
     * @param ap 
     */
//    public void setRoutingProtocol(RoutingProtocol ap){
//        NTWmanager.setRoutingProtocol(ap);
//    }
    
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
    
    /**
     * get Properties by StoreCategory (Local, Network)
     * @param sc
     * @return 
     */
    public List<Resource> getProperties(StoreCategory sc){
        return this.getResStorage().getProperties(sc);
    }
    
    /**
     * get Instructions by StoreCategory (Local, Network)
     * @param sc
     * @return 
     */
    public List<Resource> getInstructions(StoreCategory sc){
        return this.getResStorage().getInstructions(sc);
    }
    
    /**
     * Get Local Resource by Resource Name
     * @param resName
     * @return 
     */
    public ResData GETLocalResource(String resName){
        Request req = new SendMessage(resName, 0);
        return this.resourceStorage.getProperty(req, StoreCategory.Local);
    }
    
    public List<String> GETLocalPropertyList(){
        return this.getResStorage().getPropertyList();
    }
    
    public List<String> GETLocalInstructionList(){
        return this.getResStorage().getInstructionList();
    }
    
    public void printPropertyLists(){
        for(Resource tp : getResStorage().getProperties()){
            ThingProperty tpp = (ThingProperty)tp;
            System.out.println(tp.getName()+ " : " + tp.getStorageDirectory().getSourceLocation()+
                    ", "+tp.getDeviceType().toString() +", "+tpp.getPropertyRole().toString());
        }
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
    private MinTBenchmark mintBench = null;
    
    public MinTBenchmark getBenchmark(){
        return mintBench;
    }
    
    public void startBenchmark(String filename){
//        mintBench.setBenchMode(isbench, period);
        mintBench.startBench(filename);
    }
    
    public void endBenchmark(){
        if(mintBench.isBenchMode()){
            mintBench.endBench();
        }
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
        sched.StartScheduler();
        if(mintBench != null)
            mintBench.makeBenchMark();
    }
    
    protected void isDebug(boolean isdebug){
        MinTConfig.DebugMode = isdebug;
        System.out.println();
    }
    
}
