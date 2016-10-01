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

import MinTFramework.MinT;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResData;
import MinTFramework.storage.ResourceStorage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author soobisooba
 */
public class SystemHandler{
    protected MinT frame;
    protected ResourceStorage resStorage;
    protected NetworkManager nmanager;
    DebugLog dl = new DebugLog("SystemHandler");
    
    public SystemHandler(){
        this.frame = MinT.getInstance();
        resStorage = this.frame.getResStorage();
        nmanager = frame.getNetworkManager();
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
        if(recv_packet.getHeader_Direction().isRequest()){
            SystemHandleRequest(recv_packet);
        }else if(recv_packet.getHeader_Direction().isResponse()){
            SystemHandleResponse(recv_packet);
        }
    }
    
    /**
     * Request handle by requesting from other node
     * @param rv_packet 
     */
    private void SystemHandleRequest(PacketDatagram rv_packet){
        if(rv_packet.getHeader_Instruction().isGet()){
//            dl.printMessage("set get");
//            System.out.println("Catched (GET) by System Handler, " + rv_packet.getSource().getProfile()+", "+rv_packet.getMSGID());
            System.out.println("Catched (GET) by System Handler, " + rv_packet.getMsgData());
            
            Request req = new RequestHandle(rv_packet.getMsgData(), rv_packet.getSource());
            ResData res = resStorage.getProperty(req);
            
            if(res != null)
                req = new Request(null, res.getResourceString());
            else
                req = null;
            nmanager.SEND(new SendMSG(PacketDatagram.HEADER_DIRECTION.RESPONSE, PacketDatagram.HEADER_INSTRUCTION.GET
                    , rv_packet.getSource(), req, rv_packet.getMSGID()));
//            System.out.println("Sended Data to "+rv_packet.getSource().getProfile()+", "+rv_packet.getMSGID());
//            System.out.println("Thread Status ["+frame.getNumberofWorkingThreads()+"/"+MinTConfig.DEFAULT_THREAD_NUM+"]");
        }else if(rv_packet.getHeader_Instruction().isSet()){
            Request req = new RequestHandle(rv_packet.getMsgData(), rv_packet.getSource());
            resStorage.setInstruction(req);
        }else if(rv_packet.getHeader_Instruction().isPost()){
//            resStorage.setInstruction(req);
        }else if(rv_packet.getHeader_Instruction().isDelete()){
            
        }else if(rv_packet.getHeader_Instruction().isDiscovery()){
            Request ret = new Request(null, resStorage.DiscoverLocalResource(rv_packet.getDestinationNode()).toJSONString());
            nmanager.SEND(new SendMSG(PacketDatagram.HEADER_DIRECTION.RESPONSE, PacketDatagram.HEADER_INSTRUCTION.DISCOVERY
                    , rv_packet.getSource(), ret, rv_packet.getMSGID()));
        }
    }
    
    /**
     * Response handler when this node receives a response message from other node what is requested by this node.
     * @param rv_packet 
     */
    private void SystemHandleResponse(PacketDatagram rv_packet){
        Request senderRequest = new RequestHandle(rv_packet.getMsgData(), rv_packet.getSource());
        if(rv_packet.getHeader_Instruction().isGet()){
            ResponseHandler reshandle = nmanager.getResponseDataMatchbyID(rv_packet.getMSGID());
            if(reshandle != null)
                reshandle.Response(new ResponseData(rv_packet, senderRequest.getResource()));
        }else if(rv_packet.getHeader_Instruction().isSet()){
            
        }else if(rv_packet.getHeader_Instruction().isPost()){
            
        }else if(rv_packet.getHeader_Instruction().isDelete()){
            
        }else if(rv_packet.getHeader_Instruction().isDiscovery()){
            ResponseHandler reshandle = nmanager.getResponseDataMatchbyID(rv_packet.getMSGID());
            if(reshandle != null){
//                dl.printMessage("Response DISCOVERY");
                ResponseData resdata = new ResponseData(rv_packet,senderRequest.getResource());
                UpdateDiscoverData(resdata);
                reshandle.Response(resdata);
            }
//            dl.printMessage(rv_packet.getMsgData());
        }
    }
    
    /**
     * update Discovered Data in Storage
     * @param resdata 
     */
    private void UpdateDiscoverData(ResponseData resdata){
        JSONObject discovery = resStorage.getDiscoveryResource(resdata.getResourceString());
        JSONArray jpr = (JSONArray)discovery.get(ResourceStorage.RESOURCE_TYPE.property.toString());
        for(int i=0;i<jpr.size();i++){
            resStorage.addNetworkResource(ResourceStorage.RESOURCE_TYPE.property, (JSONObject)jpr.get(i), resdata);
        }
        
        JSONArray jis = (JSONArray)discovery.get(ResourceStorage.RESOURCE_TYPE.instruction.toString());
        for(int i=0;i<jis.size();i++){
            resStorage.addNetworkResource(ResourceStorage.RESOURCE_TYPE.instruction, (JSONObject)jis.get(i), resdata);
        }
    }

}
