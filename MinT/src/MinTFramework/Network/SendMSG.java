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

import MinTFramework.Util.Benchmarks.Performance;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SendMSG implements Runnable{
    private int head_version;
    private PacketDatagram_coap.HEADER_TYPE head_type;
    private int head_tokenLength;
    private PacketDatagram_coap.HEADER_CODE head_code;
    private NetworkProfile destination;
    private String msg;
    private ResponseHandler resHandle;
    private int resKey;
    private int SendHit = 0;
    
    public SendMSG(int hv, PacketDatagram_coap.HEADER_TYPE ht, int tkl, 
            PacketDatagram_coap.HEADER_CODE hc, NetworkProfile dst, Request msg,
            ResponseHandler resHandle, int resKey){
        head_version = hv;
        head_type = ht;
        head_tokenLength = tkl;
        head_code = hc;
        destination = dst;
        if(msg == null)
            this.msg = "";
        else
            this.msg = msg.getMessageString();
        this.resHandle = resHandle;
        this.resKey = resKey;
    }
    
    /**
     * Response
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (GET, SET, POST, PUT, DELETE, DISCOVERY)
     * @param dst Destination profile
     * @param msg Resource and request
     * @param resKey 
     */
    public SendMSG(int hv, PacketDatagram_coap.HEADER_TYPE ht, int tkl
            , PacketDatagram_coap.HEADER_CODE hc, NetworkProfile dst
            , Request msg, int resKey){
        this(hv, ht, tkl, hc, dst,msg,null,resKey);
    }
    
    /**
     * Request only
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (SET, POST, PUT, DELETE)
     * @param dst Destination profile
     * @param msg Resource and request
     */
    public SendMSG(int hv, PacketDatagram_coap.HEADER_TYPE ht, int tkl
            , PacketDatagram_coap.HEADER_CODE hc
            , NetworkProfile dst, Request msg){
        this(hv, ht, tkl, hc,dst,msg,null,PacketDatagram_coap.HEADER_MSGID_INITIALIZATION);
    }
    
    /**
     * send for response
     * @param hd Direction (Request, Response)
     * @param hi Instruction (GET, DISCOVERY)
     * @param dst Destination profile
     * @param msg Resource and request
     * @param resHandle response handler (need to GET, DISCOVERY)
     */
    public SendMSG(int hv, PacketDatagram_coap.HEADER_TYPE ht, int tkl
            , PacketDatagram_coap.HEADER_CODE hc
            , NetworkProfile dst, Request msg, ResponseHandler resHandle){   
        this(hv, ht, tkl, hc,dst,msg,resHandle,PacketDatagram_coap.HEADER_MSGID_INITIALIZATION);
    }
    
    @Override
    public void run() {
        SendAdapter sendA = (SendAdapter)Thread.currentThread();
        Transportation trans = sendA.getTransportation();
        Performance bench = sendA.getBench();
        //put the sendmsg to transportation
        if(bench != null)
            bench.startPerform();
        trans.EndPointSend(this);
        if(bench != null)
            bench.endPerform(0);
    }
    
    public int getVersion(){
        return head_version;
    }
    
    public PacketDatagram_coap.HEADER_TYPE getHeader_Type(){
        return head_type;
    }
    
    public int getTokenLength(){
        return head_tokenLength;
    }
    
    public PacketDatagram_coap.HEADER_CODE getHeader_Code(){
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
    
    public int getResponseKey(){
        return resKey;
    }
    
    public boolean isRequest(){
        //return head_code.isRequest() && !head_code.NeedResponse();
        return head_code.isRequest();
    }
    
    public boolean isResponse(){
        return head_code.isResponse();
    }
    
    public boolean isRequestGET(){
        return head_code.isRequest() && this.resHandle != null;
    }
    
    public int getSendHit(){
        return this.SendHit;
    }
    
    public void Sended(){
        SendHit ++;
    }

    void setResKey(int makePacketID) {
        resKey = makePacketID;
    }

}
