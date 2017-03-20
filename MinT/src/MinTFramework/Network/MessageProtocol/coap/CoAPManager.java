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

import MinTFramework.MinT;
import MinTFramework.Network.MessageProtocol.MessageTransfer;
import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.RecvMSG;
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
    //private final ConcurrentHashMap<Short, SendMSG> ResponseList = new ConcurrentHashMap<>();
    //private final ConcurrentHashMap<Short, SendMSG> IDList = new ConcurrentHashMap<>();
    //private final ConcurrentHashMap<String,ConcurrentHashMap<Short,SendMSG>> nodeidlist = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,SendMSG> idlist = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,SendMSG> tknlist = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String,ConcurrentHashMap<Short,SendMSG>> tknlist = new ConcurrentHashMap<>();
    private final String MULTICAST_NODE_NAME = "MULTICASTNODE";
    private PacketIDManager idmaker;

    //CoAP Protocol
    private CoAPLeisure coapleisure = null;
    private Retransmission coapretransmit = null;
    
    public CoAPManager(){
        //idmaker = new PacketIDManager(IDList, ResponseList);
        idmaker = new PacketIDManager(idlist, tknlist);
        
        coapleisure = new CoAPLeisure();
        coapretransmit = new Retransmission();
    }
    
    @Override
    public SendMSG sendResponse(PacketDatagram rv_pkt, SendMessage ret, MinTMessageCode responseCode) {
        SendMSG res_msg = null;
        CoAPPacket rvpacket = (CoAPPacket)rv_pkt;
        System.out.println("CODE : "+CoAPPacket.HEADER_CODE.getHeaderCode(responseCode.getCode()));
        CoAPPacket.HEADER_CODE hcode = CoAPPacket.HEADER_CODE.getHeaderCode(responseCode.getCode());
        
        if (rvpacket.getHeader_Type().isCON()) {
            res_msg = SEND_PIGGYBACK_ACK(rvpacket, (SendMessage) ret, hcode);
        } else {
            res_msg = SEND_SEPERATED_RESPONSE(rvpacket, (SendMessage) ret, hcode);
        }
        System.out.println("CoAPManager / sendresponse : "+rv_pkt.getSource().getAddress()+"//"+(MinT.getInstance().getNodeName()));
        if(res_msg != null && !rv_pkt.getSource().getAddress().equals(MinT.getInstance().getNodeName())){
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
    private SendMSG SEND_PIGGYBACK_ACK(CoAPPacket rv_packet, SendMessage ret, CoAPPacket.HEADER_CODE hcode) {
        // CoAP Piggyback procedure
        CoAPPacket cp = (CoAPPacket)rv_packet;
//            SEND(new SendMSG(CoAPPacket.HEADER_TYPE.ACK, 0,
//                    CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getMSGID()));
//            SEND(new SendMSG(cp.getMSGID(), CoAPPacket.HEADER_TYPE.ACK, cp.getHeader_TokenLength(),
//                    CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getToken()));
        return new SendMSG(cp, CoAPPacket.HEADER_TYPE.ACK, hcode, ret);
    }

    /**
     * @TODO implemantation
     * For separate ack for when piggyback is not possible
     * @param rv_packet received packet
     */
    public void SEND_ACK(CoAPPacket rv_packet) {

    }
    
    private SendMSG SEND_SEPERATED_RESPONSE(CoAPPacket rv_packet, SendMessage ret, CoAPPacket.HEADER_CODE hcode){
//            SEND(new SendMSG(idmaker.makeMessageID(), CoAPPacket.HEADER_TYPE.NON, cp.getHeader_TokenLength(),
//                                CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getToken()));
        return new SendMSG(rv_packet, CoAPPacket.HEADER_TYPE.NON, hcode, ret);
    }
    
    @Override
    public void send(SendMSG sendmsg) {
        if (sendmsg.isUDPMulticastMode()){
            sendmsg.setResKey(getIDMaker().makeToken(MULTICAST_NODE_NAME, sendmsg.isUDPMulticastMode()));
            
            //message id
            //sendmsg.setMessageID(getIDMaker().makeMessageID());
            sendmsg.setMessageID(getIDMaker().makeMessageID(MULTICAST_NODE_NAME, sendmsg.getHeader_Type()));
            
            putResponse(sendmsg.getResponseKey(), sendmsg);
        }
        else if (sendmsg.isRequestGET() && sendmsg.getSendHit() == 0) {
            //check resend information
            sendmsg.setResKey(getIDMaker().makeToken(sendmsg.getDestination().getAddress(), sendmsg.isUDPMulticastMode()));
            
            //message id
            //sendmsg.setMessageID(getIDMaker().makeMessageID());
            sendmsg.setMessageID(getIDMaker().makeMessageID(sendmsg.getDestination().getAddress(), sendmsg.getHeader_Type()));
            
            putResponse(sendmsg.getResponseKey(), sendmsg);
            
        }

        /**
         * Message Retransmit control
         */
        if (sendmsg.getHeader_Type().isCON()) {

            if (sendmsg.getSendHit() == 0) {
                putCONMessage(sendmsg.getMessageID(), sendmsg);
            }

            //Start retransmit procedure
            getCoAPRetransmit().activeRetransmission(sendmsg);
        }
        else if(sendmsg.getHeader_Type().isNON()){
            putCONMessage(sendmsg.getMessageID(), sendmsg);
        }
    }

    @Override
    public ResponseHandler receive(RecvMSG recvmsg) {
        CoAPPacket packet = (CoAPPacket)recvmsg.getPacketDatagram();
        if (packet.getHeader_Type().isACK()) {
            //checkAck(packet.getMSGID());
            checkAck(packet);
        }

        ResponseHandler reshandle = null;
        //Run Response Handler for Response Mode
        if (packet.getHeader_Code().isResponse())
            reshandle = getResponseDataMatchbyID(packet.getSource().getAddress(), packet.getToken());
        return reshandle;
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
     * @param src
     * @param tkn
     * @return
     */
    public ResponseHandler getResponseDataMatchbyID(String src, short tkn) {
        if (tknlist.containsKey(src+"#"+tkn)) {
            SendMSG smsg = tknlist.get(src+"#"+tkn);
            if (smsg == null) {
                return null;
            }
            ResponseHandler resd = smsg.getResponseHandler();
            if (resd != null) {
                System.out.println("Token removed : "+tkn);
                tknlist.remove(src+"#"+tkn);
            }
            return resd;
            
        } else if (tknlist.containsKey(MULTICAST_NODE_NAME+"#"+tkn)) {
            System.out.println("Multicast response received!");
            SendMSG smsg = tknlist.get(MULTICAST_NODE_NAME+"#"+tkn);
            if (smsg == null) {
                return null;
            }
            ResponseHandler resd = smsg.getResponseHandler();
            return resd;
        } else {
            return null;
        }
    }

    public void checkAck(CoAPPacket recvpkt){
        SendMSG smsg;
        String key = recvpkt.getSource().getAddress()+"#"+recvpkt.getMSGID();
        
        if (idlist.containsKey(key)) {

            smsg = idlist.get(key);
            if (smsg == null) {
                System.out.println("There is No Key value : "+ key);
                return;
            }
            smsg.setRetransmissionHandle(null);
            //@FIXME ACK나 RESPONSE를 받으면 지워지도록 수정되어야 하나?
            //tmpidlist.remove(id);
        } else {
            System.out.println("There is No Key value : "+ key);
        }
    }

    /**
     * get Response Msg List
     *
     * @return
     */
    public ConcurrentHashMap<String, SendMSG> getResponseList() {
        synchronized (tknlist) {
            return tknlist;
        }
    }
    
    public void putResponse(short responseKey, SendMSG sendmsg) {
        if (sendmsg.isUDPMulticastMode()) { //멀티케스트일경우 노드의 이름을 멀티케스트 노드로 고정
            tknlist.put(MULTICAST_NODE_NAME + "#" + responseKey, sendmsg);
        } else {
            tknlist.put(sendmsg.getDestination().getAddress() + "#" + responseKey, sendmsg);
        }

//        System.out.println("size : "+ResponseList.size());
    }
    public void putCONMessage(short msgID, SendMSG sendmsg){
        if(sendmsg.isUDPMulticastMode()){
            idlist.put(MULTICAST_NODE_NAME+"#"+msgID, sendmsg);
        } else {
            idlist.put(sendmsg.getDestination().getAddress()+"#"+msgID, sendmsg);
        }
    }

    public Retransmission getCoAPRetransmit(){
        return coapretransmit;
    }
}
