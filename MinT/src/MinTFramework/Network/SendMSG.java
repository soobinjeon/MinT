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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SendMSG implements Runnable{
    private PacketDatagram.HEADER_DIRECTION head_dir;
    private PacketDatagram.HEADER_INSTRUCTION head_inst;
    private NetworkProfile destination;
    private String msg;
    private ResponseHandler resHandle;
    private int resKey;
    private int SendHit = 0;
    
    public SendMSG(PacketDatagram.HEADER_DIRECTION hd, PacketDatagram.HEADER_INSTRUCTION hi, NetworkProfile dst, String msg,
            ResponseHandler resHandle, int resKey){
        head_dir = hd;
        head_inst = hi;
        destination = dst;
        this.msg = msg;
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
    public SendMSG(PacketDatagram.HEADER_DIRECTION hd
            , PacketDatagram.HEADER_INSTRUCTION hi, NetworkProfile dst
            , String msg, int resKey){
        this(hd,hi,dst,msg,null,resKey);
    }
    
    /**
     * Request only
     * @param hd Direction (Request, Response)
     * @param hi for Instruction (SET, POST, PUT, DELETE)
     * @param dst Destination profile
     * @param msg Resource and request
     */
    public SendMSG(PacketDatagram.HEADER_DIRECTION hd, PacketDatagram.HEADER_INSTRUCTION hi
            , NetworkProfile dst, String msg){
        this(hd,hi,dst,msg,null,PacketDatagram.HEADER_MSGID_INITIALIZATION);
    }
    
    /**
     * send for response
     * @param hd Direction (Request, Response)
     * @param hi Instruction (GET, DISCOVERY)
     * @param dst Destination profile
     * @param msg Resource and request
     * @param resHandle response handler (need to GET, DISCOVERY)
     */
    public SendMSG(PacketDatagram.HEADER_DIRECTION hd, PacketDatagram.HEADER_INSTRUCTION hi
            , NetworkProfile dst, String msg, ResponseHandler resHandle){   
        this(hd,hi,dst,msg,resHandle,PacketDatagram.HEADER_MSGID_INITIALIZATION);
    }
    
    @Override
    public void run() {
        SendAdapter sendA = (SendAdapter)Thread.currentThread();
        Transportation trans = sendA.getTransportation();
        //put the sendmsg to transportation
        trans.EndPointSend(this);
    }
    
    public PacketDatagram.HEADER_DIRECTION getHeader_Direction(){
        return head_dir;
    }
    
    public PacketDatagram.HEADER_INSTRUCTION getHeader_Instruction(){
        return head_inst;
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
        return head_dir.isRequest() && !head_inst.NeedResponse();
    }
    
    public boolean isResponse(){
        return head_dir.isResponse();
    }
    
    public boolean isRequestGET(){
        return head_dir.isRequest() && head_inst.NeedResponse() && this.resHandle != null;
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
