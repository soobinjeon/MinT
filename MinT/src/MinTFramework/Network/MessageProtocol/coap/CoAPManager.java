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
import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket.HEADER_TYPE;
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
    private final ConcurrentHashMap<String,SendMSG> idlist = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,SendMSG> tknlist = new ConcurrentHashMap<>();
    private static final String MULTICAST_NODE_NAME = "MULTICASTNODE";
    private PacketIDManager idmaker;

    //CoAP Protocol
    private CoAPLeisure coapleisure = null;
    private Retransmission coapretransmit = null;
    
    public CoAPManager(){
        idmaker = new PacketIDManager(idlist, tknlist);
        
        coapleisure = new CoAPLeisure();
        coapretransmit = new Retransmission();
    }
    
    @Override
    public SendMSG sendResponse(PacketDatagram rv_pkt, SendMessage ret, MinTMessageCode responseCode, boolean isACK) {
        SendMSG res_msg = null;
        CoAPPacket rvpacket = (CoAPPacket)rv_pkt;
        CoAPPacket.HEADER_CODE hcode = CoAPPacket.HEADER_CODE.getHeaderCode(responseCode.getCode());
        if (rvpacket.getHeader_Type().isCON()) {
            res_msg = SEND_PIGGYBACK_ACK(rvpacket, (SendMessage) ret, hcode, isACK);
        } else {
            res_msg = SEND_NON_RESPONSE(rvpacket, (SendMessage) ret, hcode);
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
    private SendMSG SEND_PIGGYBACK_ACK(CoAPPacket rv_packet, SendMessage ret
            , CoAPPacket.HEADER_CODE hcode, boolean isACK) {
        // CoAP Piggyback procedure
        CoAPPacket cp = (CoAPPacket)rv_packet;
        HEADER_TYPE ht = HEADER_TYPE.ACK;
        if(!isACK)
            ht = HEADER_TYPE.CON;
//            SEND(new SendMSG(CoAPPacket.HEADER_TYPE.ACK, 0,
//                    CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getMSGID()));
//            SEND(new SendMSG(cp.getMSGID(), CoAPPacket.HEADER_TYPE.ACK, cp.getHeader_TokenLength(),
//                    CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getToken()));
        return new SendMSG(cp, ht, hcode, ret);
    }

    /**
     * @TODO implemantation
     * For separate ack for when piggyback is not possible
     * @param rv_packet received packet
     */
    public void SEND_ACK(CoAPPacket rv_packet) {

    }
    
    private SendMSG SEND_NON_RESPONSE(CoAPPacket rv_packet, SendMessage ret, CoAPPacket.HEADER_CODE hcode){
//            SEND(new SendMSG(idmaker.makeMessageID(), CoAPPacket.HEADER_TYPE.NON, cp.getHeader_TokenLength(),
//                                CoAPPacket.HEADER_CODE.CONTENT, cp.getSource(), ret, cp.getToken()));
        return new SendMSG(rv_packet, CoAPPacket.HEADER_TYPE.NON, hcode, ret);
    }
    
    @Override
    public void send(SendMSG sendmsg) {
        /**
         * Both Unicast and Multicast are accepted with
         * register message id and tokenid with response handler
         */
        if (sendmsg.isRequestGET() && sendmsg.getSendHit() == 0) {
            //check resend information
//            sendmsg.setResKey(getIDMaker().makeToken(dstName, sendmsg.isUDPMulticastMode()));
            sendmsg.setResKey(getIDMaker().makeToken(sendmsg));
            
            //message id
//            sendmsg.setMessageID(getIDMaker().makeMessageID(dstName, sendmsg.getHeader_Type()));
            sendmsg.setMessageID(getIDMaker().makeMessageID(sendmsg));
            
            putResponse(sendmsg.getResponseKey(), sendmsg);
        }else if(sendmsg.isUDPMulticastMode()){
            sendmsg.setMessageID(getIDMaker().makeMessageID(sendmsg));
        }else if(sendmsg.getHeader_Type().isCON() && sendmsg.getHeader_Code().isContent()
                && sendmsg.getSendHit() == 0){
            sendmsg.setMessageID(getIDMaker().makeMessageID(sendmsg));
//            System.out.println("CON message and CONTENT response! "+sendmsg.Message());
        }

        /**
         * Message Retransmit control
         */
        if (sendmsg.getHeader_Type().isCON()) {

            if (sendmsg.getSendHit() == 0) {
                putMessageID(sendmsg.getMessageID(), sendmsg);
            }

            //Start retransmit procedure
            getCoAPRetransmit().activeRetransmission(sendmsg);
        }
        else if(sendmsg.getHeader_Type().isNON()){
            putMessageID(sendmsg.getMessageID(), sendmsg);
        }
    }

    @Override
    public ResponseHandler receive(RecvMSG recvmsg) {
        CoAPPacket packet = (CoAPPacket)recvmsg.getPacketDatagram();
        if (packet.getHeader_Type().isACK()) {
//            System.out.println(packet.getMSGID()+"- ACK CHECK");
            //checkAck(packet.getMSGID());
            checkAck(packet);
        }

        ResponseHandler reshandle = null;
        //Run Response Handler for Response Mode
        if (packet.getHeader_Code().isResponse() && !packet.getHeader_Code().isEmpty()){
//            System.out.println("Response Check");
            reshandle = getResponseDataMatchbyID(packet.getSource().getAddress(), packet.getToken());
        }
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
//                System.out.println("Token removed : "+tkn);
                tknlist.remove(src+"#"+tkn);
            }
            return resd;
            
        } else if (tknlist.containsKey(MULTICAST_NODE_NAME+"#"+tkn)) {
//            System.out.println("Multicast response received!");
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
        tknlist.put(getListKey(responseKey,sendmsg), sendmsg);
    }
    public void putMessageID(short msgID, SendMSG sendmsg){
        idlist.put(getListKey(msgID,sendmsg), sendmsg);
    }

    public Retransmission getCoAPRetransmit(){
        return coapretransmit;
    }
    
    public static String getListKey(short key, SendMSG sendmsg){
        if(sendmsg.isUDPMulticastMode())
            return MULTICAST_NODE_NAME + "#" + key;
        else
            return sendmsg.getDestination().getAddress()+"#"+key;
    }
    
    public static String getDestinationKey(SendMSG sendmsg){
        if(sendmsg.isUDPMulticastMode())
            return MULTICAST_NODE_NAME;
        else
            return sendmsg.getDestination().getAddress();
    }
}
