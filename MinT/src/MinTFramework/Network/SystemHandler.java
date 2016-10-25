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
package MinTFramework.Network;

import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Network.Resource.Request;
import MinTFramework.MinT;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.Routing.RoutingProtocol;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResData;
import MinTFramework.storage.Resource;
import MinTFramework.storage.ResourceStorage;

/**
 *
 * @author soobisooba
 */
public class SystemHandler{
    protected MinT frame;
    protected ResourceStorage resStorage;
    protected NetworkManager nmanager;
    protected RoutingProtocol rout;
    DebugLog dl = new DebugLog("SystemHandler");
    
    public SystemHandler(){
        this.frame = MinT.getInstance();
        resStorage = this.frame.getResStorage();
        nmanager = frame.getNetworkManager();
        rout = nmanager.getRoutingProtocol();
    }
    
    public void startHandle(PacketDatagram recv_pk){
        SystemHandler(recv_pk);
    }
    
    /**
     * System Handler can handle navigator for 
     * discovering searched sensor nodes (?) <- would need to routing protocol,
     * information searching <- need to storages,
     * ,and so on
     * @param src
     * @param msg 
     */
    private void SystemHandler(PacketDatagram recv_packet){
        /**
         * get, post using resource storage
         */
        if(recv_packet.getHeader_Code().isRequest()){
            SystemHandleRequest(recv_packet);
        }else if(recv_packet.getHeader_Code().isResponse()){
            SystemHandleResponse(recv_packet);
        }
    }
    
    /**
     * Request handle by requesting from other node
     * @param rv_packet 
     */
    private void SystemHandleRequest(PacketDatagram rv_packet){
        Request req = new ReceiveMessage(rv_packet.getMsgData(), rv_packet.getSource());
        if (isRouting(req)){
            rout.routingHandle(rv_packet);
        }else if(rv_packet.getHeader_Code().isGet()){
//            dl.printMessage("set get");
//            System.out.println("Catched (GET) by System Handler, " + rv_packet.getSource().getProfile()+", "+rv_packet.getMSGID());
//            System.out.println("Catched (GET) by System Handler, " + rv_packet.getMsgData());
            
//            System.out.println("rname: " + req.getResourceName() + ", rd: " + req.getResourceData().getResourceString());
            Request ret = null;
            //Temporary Routing Discover Mode
            if (isDiscover(req)) {
                Network cnet = frame.getNetworkManager().getNetwork(rv_packet.getSource().getNetworkType());
                String redata = resStorage.DiscoverLocalResource(cnet.getProfile()).toJSONString();
                ret = new SendMessage(null, redata)
                        .AddAttribute(Request.MSG_ATTR.WellKnown, null);
            } else {
                //Directly
                ResData res = resStorage.getProperty(req, Resource.StoreCategory.Local);
                if (res != null) {
                    ret = new SendMessage(null, res.getResourceString());
                }
            }
            nmanager.SEND(new SendMSG(PacketDatagram.HEADER_TYPE.NON, 0
                    , PacketDatagram.HEADER_CODE.CONTENT, rv_packet.getSource(), ret, rv_packet.getMSGID()));
        }else if(rv_packet.getHeader_Code().isPut()){
            resStorage.setInstruction(req);
        }else if(rv_packet.getHeader_Code().isPost()){
            resStorage.setInstruction(req);
        }else if(rv_packet.getHeader_Code().isDelete()){
        }
    }
    
    /**
     * Response handler when this node receives a response message from other node what is requested by this node.
     * @param rv_packet 
     */
    private void SystemHandleResponse(PacketDatagram rv_packet){
        Request sendedRequest = new ReceiveMessage(rv_packet.getMsgData(), rv_packet.getSource());
        ResponseHandler reshandle = nmanager.getResponseDataMatchbyID(rv_packet.getMSGID());
        if (isRouting(sendedRequest)){
            rout.routingHandle(rv_packet);
        }else if(rv_packet.getHeader_Code().isContent()){
            if(isDiscover(sendedRequest)){
                try {
                    ResponseData resdata = new ResponseData(rv_packet, sendedRequest.getResourceData().getResource());
                    resStorage.updateDiscoverData(resdata);
                    if (reshandle != null) {
                        reshandle.Response(resdata);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(rv_packet.getHeader_Code().isDeleted()){
        }else if(rv_packet.getHeader_Code().isValid()){
        }else if(rv_packet.getHeader_Code().isChanged()){
        }else if(rv_packet.getHeader_Code().isCreated()){
        }else if(rv_packet.getHeader_Code().isContinue()){
        }
        
        if(!isDiscover(sendedRequest) && reshandle != null)
            reshandle.Response(new ResponseData(rv_packet, sendedRequest.getResourceData().getResource()));
    }
    
    /**
     * update Discovered Data in Storage
     * @param resdata 
     */
//    private void UpdateDiscoverData(ResponseData resdata){
//        JSONObject discovery = resStorage.getDiscoveryResource(resdata.getResourceString());
//        JSONArray jpr = (JSONArray)discovery.get(ResourceStorage.RESOURCE_TYPE.property.toString());
//        for(int i=0;i<jpr.size();i++){
//            resStorage.addNetworkResource(ResourceStorage.RESOURCE_TYPE.property, (JSONObject)jpr.get(i), resdata);
//        }
//        
//        JSONArray jis = (JSONArray)discovery.get(ResourceStorage.RESOURCE_TYPE.instruction.toString());
//        for(int i=0;i<jis.size();i++){
//            resStorage.addNetworkResource(ResourceStorage.RESOURCE_TYPE.instruction, (JSONObject)jis.get(i), resdata);
//        }
//    }

    /**
     * is Discovery Mode
     * @param req
     * @return 
     */
    private boolean isDiscover(Request req) {
        if(req.getResourcebyName(Request.MSG_ATTR.WellKnown) != null)
            return true;
        else
            return false;
    }

    private boolean isRouting(Request req) {
        if(req.getResourcebyName(Request.MSG_ATTR.Routing) != null)
            return true;
        else
            return false;
    }

}
