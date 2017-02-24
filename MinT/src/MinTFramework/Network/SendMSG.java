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

import MinTFramework.Network.MessageProtocol.ApplicationProtocol;
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Util.Benchmarks.Performance;
import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SendMSG implements Runnable{
    private ApplicationProtocol appprotocolType = null;
    
    //for CoAP
    private int head_version;
    private CoAPPacket.HEADER_TYPE head_type;
    private int head_tokenLength;
    private CoAPPacket.HEADER_CODE head_code;
    private short messageId;
    private NetworkProfile destination;
    private String msg;
    private ResponseHandler resHandle;
    private ScheduledFuture<?> retransmissionHandle;
    private long currentTimeout = 0;
    private short resKey;
    private int SendHit = 0;
    
    //transmit
    private boolean isUDPMulticast = false;
    private NetworkProfile FinalDestination;
    private NetworkProfile nextNode;
    
    //for Response
    private boolean res_for_multicast = false;
    
    public SendMSG(CoAPPacket.HEADER_TYPE ht, int tkl, 
            CoAPPacket.HEADER_CODE hc, NetworkProfile dst, Request msg,
            ResponseHandler resHandle, short resKey){
        head_version = CoAPPacket.CoAPConfig.COAP_VERSION;
        head_type = ht;
        head_tokenLength = tkl;
        head_code = hc;
        messageId = 0;
        destination = dst;
        if(msg == null)
            this.msg = "";
        else
            this.msg = msg.getMessageString();
        this.resHandle = resHandle; //token number
        this.resKey = resKey;
        this.retransmissionHandle = null;
        
        //fixme - set Only CoAP message protocol
        appprotocolType = ApplicationProtocol.COAP;
    }
    
    /***
     * Send Message for Response on CoAP
     * @param rv_packet
     * @param ht
     * @param hc
     * @param msg 
     */
    public SendMSG(CoAPPacket rv_packet, CoAPPacket.HEADER_TYPE ht, CoAPPacket.HEADER_CODE hc, Request msg){
        this(ht, rv_packet.getHeader_TokenLength(), hc, rv_packet.getSource(), msg, null, rv_packet.getToken());
        this.messageId = rv_packet.getMSGID();
        this.res_for_multicast = rv_packet.hasMulticast();
        
    }
    
    /**
     * Request only on Multi-cast
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (SET, POST, PUT, DELETE)
     * @param dst Destination profile
     * @param msg Resource and request
     */
    public SendMSG(CoAPPacket.HEADER_TYPE ht, int tkl
            , CoAPPacket.HEADER_CODE hc
            , NetworkProfile dst, Request msg, ResponseHandler resHandle, boolean _isMulticast){
        this(ht, tkl, hc,dst,msg,resHandle,CoAPPacket.HEADER_MSGID_INITIALIZATION);
        this.isUDPMulticast = _isMulticast;
    }
    
    /**
     * send for response
     * @param hd Direction (Request, Response)
     * @param hi Instruction (GET, DISCOVERY)
     * @param dst Destination profile
     * @param msg Resource and request
     * @param resHandle response handler (need to GET, DISCOVERY)
     */
    public SendMSG(CoAPPacket.HEADER_TYPE ht, int tkl
            , CoAPPacket.HEADER_CODE hc
            , NetworkProfile dst, Request msg, ResponseHandler resHandle){
        this(ht, tkl, hc,dst,msg,resHandle,CoAPPacket.HEADER_MSGID_INITIALIZATION);
    }
    
    @Override
    public void run() {
        SendAdapter sendA = (SendAdapter)Thread.currentThread();
        Transportation trans = sendA.getTransportation();
        Performance bench = sendA.getBench();
        //put the sendmsg to transportation
        if(bench != null)
            bench.startPerform();
        try{
            trans.EndPointSend(this);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(bench != null)
            bench.endPerform(0);
    }
    
    public int getVersion(){
        return head_version;
    }
    
    public short getMessageID(){
        return messageId;
    }
    
    public void setMessageID(short mid){
        messageId = mid;
    }
    
    public CoAPPacket.HEADER_TYPE getHeader_Type(){
        return head_type;
    }
    
    public int getTokenLength(){
        return head_tokenLength;
    }
    
    public CoAPPacket.HEADER_CODE getHeader_Code(){
        return head_code;
    }
        
    public NetworkProfile getDestination(){
        return destination;
    }
    
    public String Message(){
        return msg;
    }
    
    public ResponseHandler getResponseHandler(){
        return resHandle;
    }
    
    public short getResponseKey(){
        return resKey;
    }
    
    public boolean isRequestGET(){
        return resHandle != null;
    }
    
    public int getSendHit(){
        return this.SendHit;
    }
    
    public void Sended(){
        SendHit ++;
    }

    public void setResKey(short makePacketID) {
        resKey = makePacketID;
    }

    /**
     * set FinalDestination from Transportation Module for send
     * @param fdst 
     */
    public void setFinalDestination(NetworkProfile fdst) {
        FinalDestination = fdst;
    }
    
    public NetworkProfile getFinalDestination(){
        return FinalDestination;
    }

    /**
     * set NextNode from Transportation Module for send
     * @param _nextNode 
     */
    public void setNextNode(NetworkProfile _nextNode) {
        nextNode = _nextNode;
    }
    
    public NetworkProfile getNextNode(){
        return nextNode;
    }
    
    public boolean isUDPMulticastMode(){
        return isUDPMulticast;
    }
    
    /**
     * this send message is response message for Multicast.
     * send message type is unicast.
     * @return 
     */
    public boolean isResponseforMulticast(){
        return res_for_multicast;
    }
    
    public void setCurrentTimeout(long timeout){
        this.currentTimeout = timeout;
    }
    
    public long getCurrentTimeout(){
        return currentTimeout;
    }
    
    public synchronized void setRetransmissionHandle(ScheduledFuture<?> retransmissionHandle){
        if(this.retransmissionHandle != null){
            this.retransmissionHandle.cancel(false);
//            System.out.println("SendMSG.java "+this.getMessageID()+" message : Retransmission Handle is canceled!");
        }
        this.retransmissionHandle = retransmissionHandle;
    }
    
    public ScheduledFuture<?> getRetransmissionHandle(){
        return this.retransmissionHandle;
    }

    public ApplicationProtocol getApplicationProtocol() {
        return appprotocolType;
    }
    
}
