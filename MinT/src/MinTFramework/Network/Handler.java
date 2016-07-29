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

import MinTFramework.*;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResourceStorage;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Handler extends Service{
    protected MinT frame;
    protected PacketProtocol recv_packet;
    protected ResourceStorage resStorage;
    protected NetworkManager nmanager;
    DebugLog dl = new DebugLog("Handler");
    public Handler(MinT _frame){
        super(_frame);
        this.frame = _frame;
        resStorage = this.frame.getResStorage();
        nmanager = this.frame.getNetworkManager();
    }
    /***
     * Call Packet Handler: Do not supprot upper v2.03
     * @param src packet source
     * @param msg pakcet message
     * @param frame MinTFramework
     */
    @Deprecated
    public void callPacketHandleService(String src, String msg, MinT frame){};
    
    /***
     * Call Packet Handler:: Do not support upper v2.03
     * You should use UserHandler(String src, String msg);
     * @param src packet source
     * @param msg pakcet message
     */
    @Deprecated
    public void callPacketHandleService(String src, String msg){};
    
    /**
     * Handler for User service
     * @param src
     * @param msg 
     */
    abstract public void userHandler(PacketProtocol rev_packet);
    
    /**
     * call Handler
     * @param packet
     */
    protected void callhadler(PacketProtocol packet){
        recv_packet = packet;
    }
    
    /**
     * System Handler can handle navigator for 
     * discovering searched sensor nodes (?) <- would need to routing protocol,
     * information searching <- need to storages,
     * ,and so on
     * @param src
     * @param msg 
     */
    private void SystemHandler(){
        /**
         * get, post using resource storage
         */
        dl.printMessage("Processing SystemHandler");
        dl.printMessage(recv_packet.getPacketString());
        dl.printMessage("pk_length : "+recv_packet.getPacket().length);
        //Request req = new Request("Device2", 0, src);
        if(recv_packet.getHeader_Direction().isRequest()){
            SystemHandleRequest(recv_packet);
        }else if(recv_packet.getHeader_Direction().isResponse()){
            SystemHandleResponse(recv_packet);
        }
    }
    
    private void SystemHandleRequest(PacketProtocol rv_packet){
        if(rv_packet.getHeader_Instruction().isGet()){
            dl.printMessage("set get");
            Request req = new Request(rv_packet.getMsgData(), 0, rv_packet.getSource());
            String resmsg = String.valueOf(resStorage.getProperty(req));
            nmanager.RESPONSE(PacketProtocol.HEADER_DIRECTION.RESPONSE, PacketProtocol.HEADER_INSTRUCTION.GET
                    , rv_packet.getSource(), resmsg, rv_packet.getMSGID());
        }else if(rv_packet.getHeader_Instruction().isSet()){
            
        }else if(rv_packet.getHeader_Instruction().isPost()){
            
        }else if(rv_packet.getHeader_Instruction().isDelete()){
            
        }else if(rv_packet.getHeader_Instruction().isObserve()){
            dl.printMessage("set Observe");
            String ret = resStorage.OberveLocalResource().toJSONString();
            nmanager.RESPONSE(PacketProtocol.HEADER_DIRECTION.RESPONSE, PacketProtocol.HEADER_INSTRUCTION.OBSERVE
                    , rv_packet.getSource(), ret, rv_packet.getMSGID());
        }
    }
    
    private void SystemHandleResponse(PacketProtocol rv_packet){
        if(rv_packet.getHeader_Instruction().isGet()){
            dl.printMessage("Response get");
            ResponseHandler reshandle = nmanager.getResponseDataMatchbyID(rv_packet.getMSGID());
            if(reshandle != null)
                reshandle.Response(new ResponseData(rv_packet.getSource(),rv_packet.getMsgData()));
        }else if(rv_packet.getHeader_Instruction().isSet()){
            
        }else if(rv_packet.getHeader_Instruction().isPost()){
            
        }else if(rv_packet.getHeader_Instruction().isDelete()){
            
        }else if(rv_packet.getHeader_Instruction().isObserve()){
            ResponseHandler reshandle = nmanager.getResponseDataMatchbyID(rv_packet.getMSGID());
            if(reshandle != null){
                dl.printMessage("Response Observe");
                reshandle.Response(new ResponseData(rv_packet.getSource(),rv_packet.getMsgData()));
            }
//            dl.printMessage(rv_packet.getMsgData());
        }
    }
    
    @Override
    public void execute(){
        SystemHandler();
        userHandler(recv_packet);
    }
}