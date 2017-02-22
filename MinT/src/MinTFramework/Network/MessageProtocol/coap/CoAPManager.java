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
package MinTFramework.Network.MessageProtocol.coap;

import MinTFramework.Network.MessageProtocol.MessageTransfer;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.PacketIDManager;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.SendMSG;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class CoAPManager implements MessageTransfer{
    //Message Response List
    private final ConcurrentHashMap<Short, SendMSG> ResponseList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Short, SendMSG> IDList = new ConcurrentHashMap<>();
    private PacketIDManager idmaker;

    //CoAP Protocol
    private CoAPLeisure coapleisure = null;
    private Retransmission coapretransmit = null;
    
    public CoAPManager(){
        idmaker = new PacketIDManager(IDList, ResponseList);
        
        coapleisure = new CoAPLeisure();
        coapretransmit = new Retransmission();
    }
    
    @Override
    public SendMSG sendREsponse(CoAPPacket rv_packet, SendMessage ret, CoAPPacket.HEADER_CODE hcode) {
        SendMSG res_msg = null;
        
        if (rv_packet.getHeader_Type().isCON()) {
            res_msg = SEND_PIGGYBACK_ACK(rv_packet, (SendMessage) ret, hcode);
        } else {
            res_msg = SEND_SEPERATED_RESPONSE(rv_packet, (SendMessage) ret, hcode);
        }
        
        if(res_msg != null){
            if(res_msg.isResponseforMulticast()){
                coapleisure.putLeisureScheduler(res_msg);
                return null;
            }
            else{
                return res_msg;
            }
        }else
            return null;
    }
    
    /**
     * *
     * For piggyback acknowledge
     *
     * @param rv_packet Receved packet
     * @param ret
     */
    private SendMSG SEND_PIGGYBACK_ACK(PacketDatagram rv_packet, SendMessage ret, CoAPPacket.HEADER_CODE hcode) {
        // CoAP Piggyback procedure
        if (rv_packet.getMessageProtocolType() == PacketDatagram.MessageProtocol.COAP) {
            CoAPPacket cp = (CoAPPacket)rv_packet;
//            SEND(new SendMSG(CoAPPacket.HEADER_TYPE.ACK, 0,
//                    CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getMSGID()));
//            SEND(new SendMSG(cp.getMSGID(), CoAPPacket.HEADER_TYPE.ACK, cp.getHeader_TokenLength(),
//                    CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getToken()));
            return new SendMSG(cp, CoAPPacket.HEADER_TYPE.ACK, hcode, ret);
        } else {
            //non-CoAP Piggyback procedure
            return null;
        }
    }

    /**
     * @TODO implemantation
     * For separate ack for when piggyback is not possible
     * @param rv_packet received packet
     */
    public void SEND_ACK(CoAPPacket rv_packet) {

    }
    
    private SendMSG SEND_SEPERATED_RESPONSE(PacketDatagram rv_packet, SendMessage ret, CoAPPacket.HEADER_CODE hcode){
        if (rv_packet.getMessageProtocolType() == PacketDatagram.MessageProtocol.COAP) {
            CoAPPacket cp = (CoAPPacket)rv_packet;
//            SEND(new SendMSG(idmaker.makeMessageID(), CoAPPacket.HEADER_TYPE.NON, cp.getHeader_TokenLength(),
//                                CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getToken()));
            return new SendMSG(cp, CoAPPacket.HEADER_TYPE.NON, hcode, ret);
        } else {
            //For Non-CoAP Procedure
            return null;
        }
    }
    
    /**
     * get PacketID
     *
     * @return
     */
    public PacketIDManager getIDMaker() {
        return idmaker;
    }
    
    /**
     * get Response Data matched by Response ID
     *
     * @param num
     * @return
     */
    public ResponseHandler getResponseDataMatchbyID(short num) {
        SendMSG smsg = ResponseList.get(num);
        if (smsg == null) {
            return null;
        }
        ResponseHandler resd = smsg.getResponseHandler();
        if (resd != null) {
            ResponseList.remove(num);
        }
        return resd;
    }

    public void checkAck(short id) {
        SendMSG smsg = IDList.get(id);
        if (smsg == null) {
            System.out.println("There is No message ID: "+id);
            return;
        }
        smsg.setRetransmissionHandle(null);
        IDList.remove(id);
    }
    
    /**
     * get Response Msg List
     *
     * @return
     */
    public ConcurrentHashMap<Short, SendMSG> getResponseList() {
        synchronized (ResponseList) {
            return ResponseList;
        }
    }
    
    public void putResponse(short responseKey, SendMSG sendmsg) {
        ResponseList.put(responseKey, sendmsg);
//        System.out.println("size : "+ResponseList.size());
    }
    public void putCONMessage(short msgID, SendMSG sendmsg){
        IDList.put(msgID, sendmsg);
    }

    public int getResponseSize() {
        return ResponseList.size();
    }
    
    public Retransmission getCoAPRetransmit(){
        return coapretransmit;
    }
}
