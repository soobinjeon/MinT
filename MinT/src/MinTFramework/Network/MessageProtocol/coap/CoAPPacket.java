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

import MinTFramework.Network.MessageProtocol.ApplicationProtocol;
import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import MinTFramework.Network.MessageProtocol.ReceiveAttribute;
import MinTFramework.Network.MessageProtocol.SendAttribute;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.RecvMSG;
import MinTFramework.Network.SendMSG;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.TreeMap;

/**
 * Packet Protocol for MinT
 * MinT Protocol
 * {DIR|INS|ID}{source}{final destination}{msg data}
 * |-header---||----------route----------||--data--|
 *            || address(ip:port, ble)   ||        | should make maximum size
 * 
 * CoAP Header (4 bytes)
 * 0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |Ver| T |  TKL  |      Code     |          Message ID           |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |   Token (if any, TKL bytes) ..,
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |   Options (if any) ...,
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |1 1 1 1 1 1 1 1|    Payload (if any)...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * 
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class CoAPPacket extends PacketDatagram {
    public static final short HEADER_MSGID_INITIALIZATION = 0;
        
    private final String EMPTY_MSG = "-";
    
    //CoAP Header
    private int h_ver = 0x01;                                   //Ver
    private HEADER_TYPE h_type;                                 //T
    private int h_tkl;                                          //TKL
    private HEADER_CODE h_code;                                 //Code
    private short HEADER_MSGID;     //Message ID
    private short tkn;
    private final int MAIN_HEADER_SIZE = 4;
    private final int PACKET_HEADER_SIZE = MAIN_HEADER_SIZE 
            + (NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE * 2) + 2;// + h_tkl;
    
    /**
     * Data -> MinT Protocol
     * @param src source departure = name|address
     * @param prev previous departure = name|address
     * @param next next destination = name|address
     * @param dest final destination = name|address
     * @param msg msg = service(0:null, other:service)|response() <- need to thinking
     * @return 
     */
    public CoAPPacket(short msgid, int h_ver, HEADER_TYPE h_type, int h_tkl, HEADER_CODE h_code,
            NetworkProfile src, NetworkProfile prev, NetworkProfile next, NetworkProfile dest, String msg
            , boolean isMulti) {
        super(src,prev,next,dest,isMulti,msg, ApplicationProtocol.COAP);
        
        this.h_ver = h_ver;
        this.h_type = h_type;
        this.h_tkl = h_tkl;
        this.h_code = h_code;
        this.tkn = 0;
        HEADER_MSGID = msgid;
    }
    
    /**
     * Data -> MinT Protocol for Sender
     * @param _smsg  
     */
    public CoAPPacket(SendMSG _smsg){
        this(_smsg.getMessageID(), _smsg.getVersion(), _smsg.getHeader_Type(), _smsg.getTokenLength()
                ,_smsg.getHeader_Code(), null, null, _smsg.getNextNode(), _smsg.getFinalDestination(), _smsg.Message()
                ,_smsg.isUDPMulticastMode());
        sendMsg = _smsg; // fix me
        if(this.h_tkl != 0){
            tkn = _smsg.getResponseKey();
        }
    }
    
//    /**
//     * @deprecated 
//     * @param h_ver
//     * @param h_type
//     * @param h_tkl
//     * @param h_code
//     * @param src
//     * @param prev
//     * @param next
//     * @param dest
//     * @param msg 
//     */
//    public CoAPPacket(int h_ver, HEADER_TYPE h_type, int h_tkl, HEADER_CODE h_code,
//            NetworkProfile src, NetworkProfile prev, NetworkProfile next, NetworkProfile dest, String msg) {
//        this(HEADER_MSGID_INITIALIZATION, h_ver, h_type, h_tkl, h_code, src, prev, next, dest, msg);
//    }
    
    /**
     * MinT Protocol -> Data
     * @param packet 
     */
    public CoAPPacket(RecvMSG packet){
        super(packet,ApplicationProtocol.COAP);
    }
    
    @Override
    protected SendAttribute makeSendedPacket() {
        return new SendAttribute(makePacketData(this.HEADER_MSGID,this.h_ver, this.h_type, this.h_tkl, this.h_code, this.tkn, message));
    }
    
    /**
     * Make Byte Packet Data
     * @param rlist
     * @param msgdata 
     */
    private byte[] makePacketData(short MSG_ID, int h_ver, HEADER_TYPE h_type, int h_tkl, HEADER_CODE h_code, short tkn, String msgdata){
        try {
            byte[] strarray = DataAnalizer(msgdata).getBytes();
            byte[] pack = new byte[PACKET_HEADER_SIZE + strarray.length];
            byte[] header = makeHeadertoBytes(h_ver, h_type, h_tkl, h_code, MSG_ID);
//            System.out.println("packet size : "+pack.length);
//            System.out.println("");
//            System.out.println("MSGDIR : "+h_dir.toString()+", MSGINT : "+h_ins.toString()+", MID : "+MSG_ID);
//            System.out.println("next node : "+routelist.get(ROUTE.NEXT).getProfile());
//            System.out.println("dest node : "+routelist.get(ROUTE.DESTINATION).getProfile());
            int pos = MergePacket(pack, header, 0);
            pos = MergePacket(pack, makeTokenToBytes(tkn), pos);
            pos = MergePacket(pack, getProtocolbyROUTE(ROUTE.SOURCE), pos);
            pos = MergePacket(pack, getProtocolbyROUTE(ROUTE.DESTINATION), pos);
            pos = MergePacket(pack, strarray, pos);
            
            return pack;
//            for(int i=0;i<packetdata.length;i++){
//                String s1 = String.format("%8s", Integer.toBinaryString(packetdata[i] & 0xFF)).replace(' ','0');
//                System.out.print(s1+" ");
//            }
//            System.out.println("");
//            System.out.println(packetdata.length);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private int MergePacket(byte[] parent, byte[] inserter, int pos){
        int i = 0;
//        if(pos + inserter.length > parent.length)
        for(i=pos;i<inserter.length+pos;i++){
            parent[i] = inserter[i-pos];
        }
        return i;
    }
    
    /**
     * Make Header
     * @param h_dir
     * @param h_ins
     * @return 
     */
    private byte[] makeHeadertoBytes(int h_ver, HEADER_TYPE h_type, int h_tkl, HEADER_CODE h_code, short msg_id){
        byte[] theader = new byte[MAIN_HEADER_SIZE];
        byte[] msgbytes = ByteBuffer.allocate(Short.SIZE/8).putShort(msg_id).array();
        
        theader[0] = (byte) (h_ver << 6);
        theader[0] |= (byte) (h_type.getType() << 4);
        theader[0] |= (byte) h_tkl;
        theader[1] |= (byte) h_code.getCode();
        
        for(int i=2;i<theader.length;i++)
            theader[i] = msgbytes[i-2];
        return theader;
    }
    
    private void makeBytestoHeader(byte[] packet){
        byte headerFirstByte = packet[0];
        byte headerSecondByte = packet[1];
        byte[] msg_id = new byte[Integer.SIZE / 16];
        System.arraycopy(packet, 2, msg_id, 0, 2);
        this.h_ver = ((headerFirstByte & 0xC0) >> 6);   //0xC0 = 1100 0000
        this.h_type = HEADER_TYPE.getHeaderType((headerFirstByte & 0x30) >> 4);  //0x30 = 0011 0000
        this.h_tkl = headerFirstByte & 0x0F;         //0x0F = 0000 1111
        this.h_code = HEADER_CODE.getHeaderCode(headerSecondByte);
        this.HEADER_MSGID = ByteBuffer.wrap(msg_id).getShort();
//        System.out.println("VER:"+h_ver);
//        System.out.println("TYPE:"+h_type);
//        System.out.println("TKL:"+h_tkl);
//        System.out.println("CODE:"+h_code);
//        System.out.println("Received MSGID:"+HEADER_MSGID);
    }
    
    private byte[] makeTokenToBytes(short tkn){
        byte[] btkn = new byte[2];
        btkn[0] = (byte)(tkn & 0xff);
        btkn[1] = (byte)((tkn >> 8) & 0xff);
        return btkn;
    }
    
    private short makeBytesToToken(byte[] btkn){
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(btkn[0]);
        bb.put(btkn[1]);
        short ret = bb.getShort(0);
        return ret;
    }
    
    /**
     * get Protocol by Route
     * @param route
     * @return 
     */
    private byte[] getProtocolbyROUTE(ROUTE route){
        NetworkProfile pf = this.routelist.get(route);
        if(pf != null){
            return pf.getbyteAddress();
        }
        else return new byte[NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE];
    }
    
    /**
     * Empty Data Converter
     * @param data
     * @return 
     */
    private String DataAnalizer(String data){
        if(data.equals(""))
            return this.EMPTY_MSG;
        else if(data.equals(EMPTY_MSG))
            return "";
        else
            return data;
    }
    
    /**
     * make packet from received packet
     * @param recvmsg
     * @return 
     */
    @Override
    protected ReceiveAttribute makeReceivedPacket(RecvMSG recvmsg) {
        byte[] nprofile = new byte[NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE];
        int pos = MAIN_HEADER_SIZE;
        byte[] spacket = recvmsg.getRecvBytes();
        byte[] msg;
        TreeMap<PacketDatagram.ROUTE, NetworkProfile> routelist = new TreeMap<>();
        makeBytestoHeader(spacket);
        
        byte[] btkn = new byte[2]; //h_tkn
        
        System.arraycopy(spacket, pos, btkn, 0, 2);
        pos+=2;
        this.tkn = this.makeBytesToToken(btkn);
//        System.out.println("makeReceivedPacket: TOKEN:"+tkn);
        System.arraycopy(spacket, pos, nprofile, 0, NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE);
//        for(int i=0;i<nprofile.length;i++){
//            String s1 = String.format("%8s", Integer.toBinaryString(nprofile[i] & 0xFF)).replace(' ','0');
//            System.out.print(s1+" ");
//        }
//        System.out.println("");
        routelist.put(ROUTE.SOURCE, new NetworkProfile(nprofile));
        pos += NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE;
        System.arraycopy(spacket, pos, nprofile, 0, NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE);
        routelist.put(ROUTE.DESTINATION, new NetworkProfile(nprofile));
        routelist.put(ROUTE.PREV, new NetworkProfile(recvmsg));
        
        pos += NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE;
        int msglen = spacket.length - pos;
        msg = new byte[msglen];
        System.arraycopy(spacket, pos, msg, 0, msglen);
        String data = DataAnalizer(new String(msg));
//        System.out.println("makeReceivedPacket: recvid : "+HEADER_MSGID);
//        System.out.println("SOURCE : "+routelist.get(ROUTE.SOURCE).getProfile());
//        System.out.println("PREV : "+routelist.get(ROUTE.PREV).getProfile());
//        System.out.println("DEST : "+routelist.get(ROUTE.DESTINATION).getProfile());
//        System.out.println("msg : "+data);
        return new ReceiveAttribute(routelist, data);
    }
    
    @Override
    protected ROLE_DIRECTION setRoleDirection() {
        if(getHeader_Code().isRequest())
            return ROLE_DIRECTION.REQUEST;
        else
            return ROLE_DIRECTION.RESPONSE;
    }

    @Override
    protected MinTMessageCode setRoleClass() {
        return MinTMessageCode.getHeaderCode(h_code.getCode());
//        switch(h_code){
//            case GET:
//                return MinTMessageCode.GET;
//            case POST:
//                return MinTMessageCode.POST;
//            case PUT:
//                return MinTMessageCode.PUT;
//            case DELETE:
//                return MinTMessageCode.DELETE;
//            case CONTENT:
//                return MinTMessageCode.CONTENT;
//            case CHANGED:
//                return MinTMessageCode.CHANGED;
//            default :
//                return MinTMessageCode.EMPTY;
//        }
    }
    
    public int getHeader_Version(){
        return this.h_ver;
    }
    
    public HEADER_TYPE getHeader_Type(){
        return this.h_type;
    }
    
    public int getHeader_TokenLength(){
        return this.h_tkl;
    }
    
    public HEADER_CODE getHeader_Code(){
        return this.h_code;
    }
    
    public short getMSGID(){
        return HEADER_MSGID;
    }
    
    public short getToken(){
        return tkn;
    }

//    public CoAPPacket getclone(){
//        byte[] nbyte = new byte[packetdata.length];
//        System.arraycopy(packetdata, 0, nbyte, 0, packetdata.length);
//        return new CoAPPacket(routelist.get(ROUTE.NEXT), nbyte);
//    }
    /*
    CON : Confirmable
    NON : Non-Confirmable
    ACK : Acknowledgement
    RST : Reset
    */
    public static enum HEADER_TYPE {
        CON(1), NON(2), ACK(3), RST(4);
        private int type;
        HEADER_TYPE(int type){
            this.type = type;
        }
        public int getType(){
            return type;
        }
        public static HEADER_TYPE getHeaderType(int type){
            for(HEADER_TYPE h : HEADER_TYPE.values()){
                if(h.getType() == type)
                    return h;
            }
            return null;
        }
        public boolean isCON() {return this == CON;}
        public boolean isNON() {return this == NON;}
        public boolean isACK() {return this == ACK;}
        public boolean isRST() {return this == RST;}
    }
    
    /*
    3-bit : class code
    5-bit : detail code
    "c.dd"
    c : 0-7 (class code)
    dd : 00-31 (detail code)
    */
    public static enum HEADER_CODE {
        EMPTY(0, 0),
        //Request
        GET(0, 1),
        POST(0, 2),
        PUT(0, 3),
        DELETE(0, 4),
        
        DISCOVERY(0, 5),    //DISCOVERY??
        
        //Response
            //Success
        CREATED(2, 1),
        DELETED(2, 2),
        VALID(2, 3),
        CHANGED(2, 4),
        CONTENT(2, 5),
        CONTINUE(2, 31),

            //Client Error
        BAD_REQUEST(4, 0),
        UNAUTHORIZED(4, 1),
        BAD_OPTION(4, 2),
        FORBIDDEN(4, 3),
        NOT_FOUND(4, 4),
        METHOD_NOT_ALLOWED(4, 5),
        NOT_ACCEPTABLE(4, 6),
        REQUEST_ENTITY_INCOMPLETE(4, 8),
        PRECONDITION_FAILED(4, 12),
        REQUEST_ENTITY_TOO_LARGE(4, 13),
        UNSUPPORTED_CONTENT_FORMAT(4, 15),

            //Server Error
        INTERNAL_SERVER_ERROR(5, 0),
        NOT_IMPLEMENTED(5, 1),
        BAD_GATEWAY(5, 2),
        SERVICE_UNAVAILABLE(5, 3),
        GATEWAY_TIMEOUT(5, 4),
        PROXY_NOT_SUPPORTED(5, 5);
                
        int code;
        int classCode;
        int detailCode;
        
        HEADER_CODE(int classCode, int detailCode){
            this.classCode = classCode;
            this.detailCode = detailCode;
            code = classCode << 5 | detailCode;
        }
        
        public int getCode(){
            return code;
        }
        
        public int getClassCode(){
            return classCode;
        }
        
        public int getDetailCode(){
            return detailCode;
        }
         
        public static HEADER_CODE getHeaderCode(int code){
            for(HEADER_CODE h : HEADER_CODE.values()){
                if(h.getCode() == code)
                    return h;
            }
            return null;
        }
        
        public static HEADER_CODE getHeaderCode(int classCode, int detailCode){
            for(HEADER_CODE h : HEADER_CODE.values()){
                if((h.getClassCode() == classCode) && (h.getDetailCode() == detailCode))
                    return h;
            }
            return null;
        }
                
        public boolean isRequest() {return classCode == 0;}
        public boolean isResponse() {return classCode != 0;}
        
        public boolean isGet() {return this == GET;}
        public boolean isPost() {return this == POST;}
        public boolean isPut() {return this == PUT;}
        public boolean isDelete() {return this == DELETE;}

        public boolean isCreated() {return this == CREATED;}
        public boolean isDeleted() {return this == DELETED;}
        public boolean isValid() {return this == VALID;}
        public boolean isChanged() {return this == CHANGED;}
        public boolean isContent() {return this == CONTENT;}
        public boolean isContinue() {return this == CONTINUE;}
        
        public boolean NeedResponse() {return (this == GET) || (this == PUT) || (this == DELETE);}
        
        public boolean isSuccessResponse() {return classCode == 2;}
        public boolean isFailResponse() {return (classCode != 4) || (classCode != 5);}
        public boolean isClientError() {return classCode == 4;}
        public boolean isServerError() {return classCode == 5;}
    }
    
    public static class CoAPConfig {
        static public final int CoAP_MULTICAST_TTL = 5;
        static public final int COAP_VERSION = 0x01;
        static public final int RANDOM_SEED = 10;
        static public final int ACK_TIMEOUT = 2000;
        static public final float ACK_RANDOM_FACTOR = 1.5f;
        static public final float ACK_TIMEOUT_SCALE = 2.0f;
        static public final int MAX_RETRANSMIT = 4;
        static public final float DEFAULT_LEISURE = 5;
        static public final int MAX_TRANSMIT_SPAN = 45;
        static public final int MAX_TRANSMIT_WAIT = 93;
        static public final int MAX_LATENCY = 100;
        static public final int PROCESSING_DELAY = 2000;
        static public final int EXCHANGE_LIFETIME = 247;
        static public final int NON_LIFETIME = 145;
    }
}
