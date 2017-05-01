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
import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResData;
import MinTFramework.storage.Resource;

/**
 *
 * @author soobisooba
 */
public class SystemHandler extends Handler{
    protected RoutingProtocol rout;
    DebugLog dl = new DebugLog("SystemHandler");
    
    public SystemHandler(){
        rout = nmanager.getRoutingProtocol();
    }
    
    /**
     * Request handle by requesting from other node
     * @param receivemsg
     * @param rv_packet 
     */
    @Override
    public void HandleRequest(PacketDatagram rv_packet, ReceiveMessage receivemsg){
        MinTMessageCode responsecode = null;
        SendMessage ret = null;
        if(rv_packet.getMessageCode().isGet()){
//            dl.printMessage("set get");
//            System.out.print("Catched (GET) by System Handler, " + rv_packet.getSource().getProfile()+", "+rv_packet.getMSGID());
//            System.out.println(", sender IP : "+rv_packet.getSource().getAddress());
//            System.out.println("Catched (GET) by System Handler, " + rv_packet.getMsgData());
//            System.out.println("rname: " + req.getResourceName() + ", rd: " + req.getResourceData().getResourceString());
            
            //Temporary Routing Discover Mode
            if (isDiscover(receivemsg)) {
                Network cnet = frame.getNetworkManager().getNetwork(rv_packet.getSource().getNetworkType());
                String redata = resStorage.DiscoverLocalResource(cnet.getProfile()).toJSONString();
                ret = new SendMessage(null, redata)
                        .AddAttribute(Request.MSG_ATTR.WellKnown, null);
            } else {
                //Directly
                ResData res = resStorage.getProperty(receivemsg, Resource.StoreCategory.Local);
                if (res != null) {
                    ret = new SendMessage(null, res.getResourceString());
                }
            }
            if(ret != null)
                responsecode = MinTMessageCode.CONTENT;
            else
                responsecode = MinTMessageCode.CONTENT;
            
        //FIX it! : 아래 명령어에 대한 명령들이 Set Instruction에 제대로 구현되어 있지 않음!!!!    
        }else if(rv_packet.getMessageCode().isPut()){
            resStorage.setInstruction(receivemsg);
            responsecode = MinTMessageCode.CREATED;
//            responsecode = MinTMessageCode.CHANGED;
            ret = new SendMessage(null, responsecode.toString());
        }else if(rv_packet.getMessageCode().isPost()){
            resStorage.setInstruction(receivemsg);
            responsecode = MinTMessageCode.CREATED;
//            responsecode = MinTMessageCode.CHANGED;
//            responsecode = MinTMessageCode.DELETED;
            ret = new SendMessage(null, responsecode.toString());
        }else if(rv_packet.getMessageCode().isDelete()){
            resStorage.setInstruction(receivemsg);
            responsecode = MinTMessageCode.DELETED;
            ret = new SendMessage(null, responsecode.toString());
        }
        
        nmanager.SEND_RESPONSE(rv_packet, ret, responsecode);
    }
    
    /**
     * Response handler when this node receives a response message from other node what is requested by this node.
     * @param packet
     * @param receivemsg
     */
    @Override
    public void HandleResponse(PacketDatagram rv_packet, ReceiveMessage receivemsg){
        //Handle for Discovery action
        if (isDiscover(receivemsg)) {
            ResponseData resdata = new ResponseData(rv_packet, receivemsg.getResourceData().getResource(),receivemsg);
            try {
                resStorage.updateDiscoverData(resdata);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
}
