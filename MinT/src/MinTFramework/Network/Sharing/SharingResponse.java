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
package MinTFramework.Network.sharing;

import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.MinT;
import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.Network.sharing.Sharing;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.storage.ResData;
import MinTFramework.storage.ThingProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
    protected CoAPPacket rv_packet = null;
    
    protected List<ResData> LocalResources = null;
    protected List<ResData> ChildResources = null;
    protected List<ResData> HeaderResources = null;
    
    protected List<DeviceType> resourceTypes = null;
    protected List<ResourceOption> resourceOptions = null;
    
    public SharingResponse(CoAPPacket _rv_packet, ReceiveMessage _recvmsg){
        frame = MinT.getInstance();
        networkmanager = frame.getNetworkManager();
        sharing = networkmanager.getSharing();
        routing = networkmanager.getRoutingProtocol();
        recvmsg = _recvmsg;
        rv_packet = _rv_packet;
        LocalResources = new ArrayList<>();
        ChildResources = new ArrayList<>();
        HeaderResources = new ArrayList<>();
        
        resourceTypes = new ArrayList<>();
        resourceOptions = new ArrayList<>();
        
        addPacketOption();
    }
    
    /**
     * Add Sharing Option
     */
    private void addPacketOption() {
        //add resource type
        DeviceType dt = DeviceType.getDeviceType(recvmsg.getResourceName());
        if(dt != null){
            System.out.println("deviceType: "+dt.toString());
            resourceTypes.add(dt);
        }
        
        //add resource option
        ResourceOption resopt = ResourceOption.getResourceOptionbyOpt(recvmsg.getResourceData().getResourceString());
        if(resopt != null){
            System.out.println("resOpt: "+resopt.toString());
            resourceOptions.add(resopt);
        }
    }

    @Override
    public void run() {
        //check cached resource in resource storage
        checkCachedResource();
        
        //get localResource
        getLocalResource();
        
        //get Resource
        getResource();
        
        //calculate resource
        Summary summary = calculateResource();
        
        System.out.println("summary-------: "+summary.getSummary());
        
        //response message
        
        sendCalculatedResource(summary);
    }
    
    private void checkCachedResource(){
        
    }
    
    private Summary calculateResource(){
        Summary sum = new Summary();
        PrintResources("Local", LocalResources);
        AnalysisResource(LocalResources, sum);
        PrintResources("ChildNodes", ChildResources);
        AnalysisResource(ChildResources, sum);
        PrintResources("HeaderNodes", HeaderResources);
        AnalysisResource(HeaderResources, sum);
        
        return sum;
    }
    
    /**
     * get Child node resources in same group
     */
    protected void getGroupResource(){
        List<Node> cnodes = routing.getChildNodes();
        ResponseWaiter waiter = new ResponseWaiter();
        
        for(Node n : cnodes){
            if(n.isSameNode(rv_packet.getSource()))
                continue;
            for(ThingProperty p: n.getProperties().values()){
                System.out.println("RecvMsg: "+recvmsg.getResourceName()+", pdevice: "+p.getDeviceType());
                if(p.getDeviceType().isSameDeivce(recvmsg.getResourceName()))
                    frame.REQUEST_GET(n.gettoAddr().setCON(true), new SendMessage(p.getName(), null), waiter.putResponseHandler(p));
            }
        }
        
        System.out.println("wait for Group Resources");
        //waiting and get resources
        Queue<ResData> gr = waiter.get();
        System.out.println("success gr: "+gr.size());
        for(ResData rd : gr){
            ChildResources.add(rd);
            System.out.println("--------gr: "+rd.getProperty().getID()+", "+rd.getResourceString());
        }
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
    
    public abstract void getResource();
    
    private List<ResData> getLocalResource() {
        System.out.println("get Loal Datas by DeviceType: "+recvmsg.getResourceName());
        if(sharing == null)
            System.out.println("sharing null");
        if(resourceTypes.size() <= 0)
            return null;
        
        Request req = new Request(resourceTypes.get(0).getDeviceTypeString(), null, rv_packet.getSource());
        
        for(ResData rd: sharing.getLocalResource(req)){
            System.out.println("local data: "+rd.getResourceString());
            LocalResources.add(rd);
        }

        return null;
    }
    
    protected void sendCalculatedResource(Summary summary){
        if(resourceOptions.size() <= 0)
            return;
        
        ResourceOption reso = resourceOptions.get(0);
        
        SendMessage sendmsg = new SendMessage(null,summary.getResponseData(reso));
        
        //Response MSG
        if(sendmsg != null){
            networkmanager.SEND_RESPONSE(rv_packet, sendmsg.addResponseCode(MinTMessageCode.CONTENT));
//            networkmanager.SEND(new SendMSG(CoAPPacket.HEADER_TYPE.NON, 0
//                        , CoAPPacket.HEADER_CODE.CONTENT, rv_packet.getSource(), sendmsg, rv_packet.getMSGID()));
        }
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
