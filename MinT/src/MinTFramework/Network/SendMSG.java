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
import MinTFramework.Network.MessageProtocol.CoAPPacket;
import MinTFramework.MinTConfig;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Util.Benchmarks.Performance;
import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SendMSG implements Runnable{
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
    private boolean isUDPMulticast = false;
    private NetworkProfile FinalDestination;
    private NetworkProfile nextNode;
    
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
        this.resHandle = resHandle;
        this.resKey = resKey;
        this.retransmissionHandle = null;
    }
    
    /**
     * Response
     * @param ht
     * @param tkl Token length
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (GET, SET, POST, PUT, DELETE, DISCOVERY)
     * @param dst Destination profile
     * @param msg Resource and request
     * @param resKey 
     */
    public SendMSG(CoAPPacket.HEADER_TYPE ht, int tkl
            , CoAPPacket.HEADER_CODE hc, NetworkProfile dst
            , Request msg, short token){
        this(ht, tkl, hc, dst,msg,null,token);
    }
    
    /**
     * Response
     * @param msgid
     * @param ht
     * @param tkl
     * @param hc
     * @param token
     * @param dst
     * @param msg 
     */
    public SendMSG(short msgid, CoAPPacket.HEADER_TYPE ht, int tkl, 
            CoAPPacket.HEADER_CODE hc, NetworkProfile dst, 
            Request msg, short token){
        this(ht, tkl, hc, dst, msg, null, token);
        this.messageId = msgid;
    }
    
    /**
     * Request only
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (SET, POST, PUT, DELETE)
     * @param dst Destination profile
     * @param msg Resource and request
     */
    public SendMSG(CoAPPacket.HEADER_TYPE ht, int tkl
            , CoAPPacket.HEADER_CODE hc
            , NetworkProfile dst, Request msg, boolean _isMulticast){
        this(ht, tkl, hc,dst,msg,null,CoAPPacket.HEADER_MSGID_INITIALIZATION);
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
        messageId = MinT.getInstance().getNetworkManager().getIDMaker().makeMessageID();
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
    public void setMessageID(short msgid){
        this.messageId = msgid;
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
    
    public boolean isRequest(){
        //return head_code.isRequest() && !head_code.NeedResponse();
        return head_code.isPost();
    }
    
    public boolean isResponse(){
        return head_code.isResponse();
    }
    
    public boolean isRequestGET(){
        return (head_code.isGet() || head_code.isPut() || head_code.isDelete()) && this.resHandle != null;
    }
    
    public int getSendHit(){
        return this.SendHit;
    }
    
    public void Sended(){
        SendHit ++;
    }

    void setResKey(short makePacketID) {
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
    
    public void setCurrentTimeout(long timeout){
        this.currentTimeout = timeout;
    }
    public long getCurrentTimeout(){
        return currentTimeout;
    }
    
    public void setRetransmissionHandle(ScheduledFuture<?> retransmissionHandle){
        if(this.retransmissionHandle != null){
            this.retransmissionHandle.cancel(false);
            System.out.println("SendMSG.java "+this.getMessageID()+" message : Retransmission Handle is canceled!");
        }
        this.retransmissionHandle = retransmissionHandle;
    }
    public ScheduledFuture<?> getRetransmissionHandle(){
        return this.retransmissionHandle;
    }
    
}
