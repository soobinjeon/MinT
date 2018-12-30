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
package MinTFramework.Network.Sharing;

import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.MinT;
import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.Sharing.routingprotocol.RoutingProtocol;
import MinTFramework.Network.Sharing.Sharing;
import MinTFramework.Network.Sharing.Sharing.RESOURCE_TYPE;
import MinTFramework.Network.Sharing.node.Node;
import MinTFramework.storage.ResData;
import MinTFramework.storage.ThingProperty;
import MinTFramework.storage.datamap.Information;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
/**
 * Sharing Response Class
 * fix : resource type 과 resource option을 여러 개 입력 받아 동시에 보내줄 수 있도록 변경 해야함
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class SharingResponse implements Runnable{
    protected MinT frame = null;
    protected NetworkManager networkmanager = null;
    protected Sharing sharing = null;
    protected RoutingProtocol routing = null;
    
    protected ReceiveMessage recvmsg = null;
    protected PacketDatagram rv_packet = null;
    
//    protected List<ResData> LocalResources = null;
//    protected List<ResData> ChildResources = null;
//    protected List<ResData> HeaderResources = null;
    protected HashMap<RESOURCE_TYPE, List<ResData>> resources = new HashMap<>();
    
    protected List<DeviceType> resourceTypes = null;
    protected List<ResourceOption> resourceOptions = null;
    
    protected ScheduledFuture<?> currentScheduler = null;
    
    protected List<ResponseWaiter> resWaiterList = null;
    
    public SharingResponse(PacketDatagram _rv_packet, ReceiveMessage _recvmsg){
        frame = MinT.getInstance();
        networkmanager = frame.getNetworkManager();
        sharing = networkmanager.getSharing();
        routing = networkmanager.getRoutingProtocol();
        recvmsg = _recvmsg;
        rv_packet = _rv_packet;
//        LocalResources = new ArrayList<>();
//        ChildResources = new ArrayList<>();
//        HeaderResources = new ArrayList<>();
        resources.put(RESOURCE_TYPE.LOCALRESOURCE, new ArrayList<ResData>());
        resources.put(RESOURCE_TYPE.CHILDRESOURCE, new ArrayList<ResData>());
        resources.put(RESOURCE_TYPE.HEADERRESOURCE, new ArrayList<ResData>());
        
        resourceTypes = new ArrayList<>();
        resourceOptions = new ArrayList<>();
        
        resWaiterList = new ArrayList<>();
        
        addPacketOption();
    }
    
    /**
     * Add Sharing Option
     */
    private void addPacketOption() {
        //add resource type
        DeviceType dt = DeviceType.getDeviceType(recvmsg.getResourceName());
        if(dt != null){
//            System.out.println("deviceType: "+dt.toString());
            resourceTypes.add(dt);
        }
        
        //add resource option
        ResourceOption resopt = ResourceOption.getResourceOptionbyOpt(recvmsg.getResourceData().getResourceString());
        if(resopt != null){
//            System.out.println("resOpt: "+resopt.toString());
            resourceOptions.add(resopt);
        }
    }
    
    public void preRun() {
//        System.out.println("set PreRun for "+rv_packet.getSource().getAddress());
        //check cached resource in resource storage
        checkCachedResource();
        
        //get Resource
        getNetworkResource();
        
        //Peripheral Resources are clear,
        if(completeWaiter()){
//            System.out.println("resource waiter clear");
            finishRun();
        }else
            sharing.sendNetwork(resWaiterList);
    }
    
    public void finishRun(){
//        System.out.println("-------------Finish Run");
        cancelScheduler();
        run();
    }

    @Override
    public void run() {
//        System.out.println("----------------------after Run for "+rv_packet.getSource().getAddress());

        
        //get localResource
        getLocalResource();
        
        //calculate resource
        Summary summary = calculateResource();
        
//        System.out.println("summary-------: "+summary.getSummary());
        
        //response message
        
        sendCalculatedResource(summary);
    }
    
    private void checkCachedResource(){
        
    }
    
    private Summary calculateResource(){
        Summary sum = new Summary();
//        PrintResources("Local", resources.get(RESOURCE_TYPE.LOCALRESOURCE));
        AnalysisResource(resources.get(RESOURCE_TYPE.LOCALRESOURCE), sum);
//        PrintResources("ChildNodes", resources.get(RESOURCE_TYPE.CHILDRESOURCE));
        AnalysisResource(resources.get(RESOURCE_TYPE.CHILDRESOURCE), sum);
//        PrintResources("HeaderNodes", resources.get(RESOURCE_TYPE.HEADERRESOURCE));
        AnalysisResource(resources.get(RESOURCE_TYPE.HEADERRESOURCE), sum);
        
        return sum;
    }
    
    /**
     * get Child node resources in same group
     */
    protected void getGroupResource(){
//        System.out.println("get Group Resource");
        List<Node> cnodes = routing.getChildNodes();
        ResponseWaiter waiter = new ResponseWaiter(this, RESOURCE_TYPE.CHILDRESOURCE);
        resWaiterList.add(waiter);
        
        //Request for Unicast 
        for(Node n : cnodes){
            if(n.isSameNode(rv_packet.getSource()))
                continue;
            for(ThingProperty p: n.getProperties().values()){
                if(p.getDeviceType().isSameDeivce(recvmsg.getResourceName())){
//                    System.out.println("SetupMSG: "+recvmsg.getResourceName()+", pdevice: "+p.getDeviceType());
                    waiter.putPacket(n, p, recvmsg);
                }
            }
        }
    }
    
    /**
     * Event Handler from network Resource
     * GroupResource가 끝나고 HeaderResource 초기화 전에 이게 끝나버림 안됨
     * 해결방법, getgroupresource 및 headerresource는 초기화 후에 전송하는 걸로 해야함
     */
    protected synchronized void networkResourceEventHandler(RESOURCE_TYPE src, ResData resdata){
//        System.out.println("S Res Hndl - "+resdata.getProperty().getSourceProfile().getAddress()
//                +", "+resdata.getResourceString()
//                +", "+resdata.getProperty().getResourcetoJSON().toJSONString());
        resources.get(src).add(resdata);
        if(completeWaiter()){
//            System.out.println("Finish Waiter");
            finishRun();
        }
//            System.out.println("--------gr: "+rd.getProperty().getID()+", "+rd.getResourceString());
    }
    
    protected boolean completeWaiter(){
        boolean iscom = true;
        for(ResponseWaiter wt : resWaiterList){
            iscom = wt.completeAllResponse();
            if(!iscom)
                break;
        }
        return iscom;
    }
    
    /**
     * 
     * @param res
     * @param _summary 
     */
    private void AnalysisResource(List<ResData> res, Summary _summary) {
        for(ResData rd: res)
            _summary.addSummary(rd.getResourceDouble());
    }
    
    private void PrintResources(String name, List<ResData> res){
        System.out.println("-- "+name+" resource data");
        for(ResData rd: res){
            System.out.print("--------d: ");
            if(rd.getProperty() != null)
                System.out.print(rd.getProperty().getID()+", ");
            System.out.println(rd.getResourceString());
        }
    }
    
    public abstract void getNetworkResource();
    
    private List<ResData> getLocalResource() {
//        System.out.println("get Local Datas by DeviceType: "+recvmsg.getResourceName());
        if(sharing == null)
            System.out.println("sharing null");
        if(resourceTypes.size() <= 0)
            return null;
        
        Request req = new Request(resourceTypes.get(0).getDeviceTypeString(), null, rv_packet.getSource());
        
        for(ResData rd: sharing.getLocalResource(req)){
//            System.out.println("List of local data: "+rd.getResourceString());
            resources.get(RESOURCE_TYPE.LOCALRESOURCE).add(rd);
            //LocalResources.add(rd);
        }

        return null;
    }
    
    protected void sendCalculatedResource(Summary summary){
        if(resourceOptions.size() <= 0)
            return;
        
        ResourceOption reso = resourceOptions.get(0);
        

        SendMessage sendmsg = new SendMessage(null,summary.getResponseData(reso));
        sendmsg.AddAttribute(Request.MSG_ATTR.Sharing, SharingMessage.HEADER_RESPONSE.getValue());
        
        //for Experiment
        Information svalue = recvmsg.getResourcebyName(Request.MSG_ATTR.Sharing_EX);
        int v = svalue != null ? svalue.getResourceInt() : -1;
        if(v != -1)
            sendmsg.AddAttribute(Request.MSG_ATTR.Sharing_EX, v);
        //Response MSG
        if(sendmsg != null){
//            System.out.println("SEND CALCULATED RESOURCE\n");
            networkmanager.SEND_RESPONSE(rv_packet, sendmsg, MinTMessageCode.CONTENT, false);
//            networkmanager.SEND(new SendMSG(CoAPPacket.HEADER_TYPE.NON, 0
//                        , CoAPPacket.HEADER_CODE.CONTENT, rv_packet.getSource(), sendmsg, rv_packet.getMSGID()));
        }
    }

    public void setScheduleHandler(ScheduledFuture<?> f) {
        currentScheduler = f;
    }
    
    public void cancelScheduler(){
        if(currentScheduler != null)
            currentScheduler.cancel(false);
    }
    
    class Summary{
        private List<Double> data;
        private double summary = 0;
        private double MAX = 0;
        private double MIN = Double.MAX_VALUE;
        public Summary(){
            data = new ArrayList<>();
        }
        
        public void addSummary(double _sum) {
            data.add(_sum);
            summary += _sum;
            setMax(_sum);
            setMin(_sum);
        }
        public double getAverage() {
            double count = getCount();
            return count <= 0 ? 0 : summary / count;
        }

        private void setMax(double _sum) {
            if(MAX < _sum)
                MAX = _sum;
        }

        private void setMin(double _sum) {
            if(MIN > _sum)
                MIN = _sum;
        }
        
        public double getCount() { return data.size();}
        
        public double getSummary(){
            return summary;
        }
        
        public double getMaximum(){
            return MAX;
        }
        
        public double getMinimum(){
            return MIN;
        }
        
        public double getResponseData(ResourceOption resopt){
            if(resopt.isLast()) return getSummary();
            else if(resopt.isAverage()) return getAverage();
            else if(resopt.isMaximum()) return getMaximum();
            else if(resopt.isMinimum()) return getMinimum();
            else return getSummary();
        }
    }
}
