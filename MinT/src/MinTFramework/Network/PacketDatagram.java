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

import java.nio.ByteBuffer;
import java.util.TreeMap;

/**
 * Packet Protocol for MinT
 * MinT Protocol
 * {DIR|INS|ID}{source}{final destination}{msg data}
 * |-header---||----------route----------||--data--|
 *            ||         address         || 256KB  | should make maximum size
 *  - MESSAGE HEADER
 *     - DIR : REQUEST(0), RESPONSE(1) (1 bit)
 *     - INS : GET(0), SET(1), POST(2), DELETE(3), DISCOVERY(4) (3 bit)
 *     - ID  : 1~1024 (4byte)
 * 
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PacketDatagram {
    public static final int HEADER_MSGID_INITIALIZATION = 0;
    private TreeMap<ROUTE, NetworkProfile> routelist= new TreeMap<>();
    
    private byte[] packetdata = null;
    
    private final int Numberoftotalpacket = 5;
    private String data="";
    private String Scheme = "mint:";
    private final String EMPTY_MSG = "-";
    private StringBuilder MakeData = new StringBuilder();
    
    private HEADER_DIRECTION h_direction;
    private HEADER_INSTRUCTION h_instruction;
    private int HEADER_MSGID = HEADER_MSGID_INITIALIZATION;
    
    private final int MAIN_HEADER_SIZE = 5;
    private final int PACKET_HEADER_SIZE = MAIN_HEADER_SIZE 
            + (NetworkProfile.NETWORK_ADDRESS_BYTE_SIZE * 2);
    private enum ROUTE{
        SOURCE, PREV, NEXT, DESTINATION;
    }
    
    /**
     * Data -> MinT Protocol
     * @param src source departure = name|address
     * @param prev previous departure = name|address
     * @param next next destination = name|address
     * @param dest final destination = name|address
     * @param msg msg = service(0:null, other:service)|response() <- need to thinking
     * @return 
     */
    public PacketDatagram(int msgid, HEADER_DIRECTION h_dir, HEADER_INSTRUCTION h_ins, 
            NetworkProfile src, NetworkProfile prev, NetworkProfile next, NetworkProfile dest, String msg) {
        routelist.put(ROUTE.SOURCE,src);
        routelist.put(ROUTE.PREV,prev);
        routelist.put(ROUTE.NEXT,next);
        routelist.put(ROUTE.DESTINATION,dest);
        h_direction = h_dir;
        h_instruction = h_ins;
        data = msg;
        HEADER_MSGID = msgid;
//        makePacketData(HEADER_MSGID,h_direction, h_instruction,data);
    }
    
    public PacketDatagram(HEADER_DIRECTION h_dir, HEADER_INSTRUCTION h_ins, 
            NetworkProfile src, NetworkProfile prev, NetworkProfile next, NetworkProfile dest, String msg) {
        this(HEADER_MSGID_INITIALIZATION, h_dir, h_ins, src, prev, next, dest, msg);
    }
    
    /**
     * MinT Protocol -> Data
     * @param packet 
     */
    public PacketDatagram(byte[] packet){
        packetdata = packet;
        try {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        makeData(packetdata);
    }
    
    public void makeBytes() {
        makePacketData(this.HEADER_MSGID,this.h_direction, this.h_instruction, data);
    }
    
    /**
     * Make Byte Packet Data
     * @param rlist
     * @param msgdata 
     */
    private void makePacketData(int MSG_ID, HEADER_DIRECTION h_dir, HEADER_INSTRUCTION h_ins, String msgdata){
//        MakeData.setLength(0);
//        MakeData.append(Scheme);
//        MakeData.append(getProtocolData(makeHeadertoString(h_dir, h_ins, MSG_ID)));
//        MakeData.append(getProtocolbyROUTE(ROUTE.SOURCE));
//        MakeData.append(getProtocolbyROUTE(ROUTE.PREV));
//        MakeData.append(getProtocolbyROUTE(ROUTE.DESTINATION));
//        MakeData.append(getProtocolData(DataAnalizer(msgdata)));
        
        try {
            byte[] strarray = DataAnalizer(msgdata).getBytes();
            byte[] pack = new byte[PACKET_HEADER_SIZE + strarray.length];
            byte[] header = makeHeadertoBytes(h_dir, h_ins, MSG_ID);
            System.out.println("packet size : "+pack.length);
            System.out.println("MSGDIR : "+h_dir.toString()+", MSGINT : "+h_ins.toString()+", MID : "+MSG_ID);
            System.out.println("msgdat : "+msgdata);
            int pos = MergePacket(pack, header, 0);
            pos = MergePacket(pack, getProtocolbyROUTE(ROUTE.SOURCE), pos);
            pos = MergePacket(pack, getProtocolbyROUTE(ROUTE.DESTINATION), pos);
            pos = MergePacket(pack, strarray, pos);
            
            packetdata = pack;
//            System.out.println(packetdataString);
            for(int i=0;i<packetdata.length;i++){
                String s1 = String.format("%8s", Integer.toBinaryString(packetdata[i] & 0xFF)).replace(' ','0');
                System.out.print(s1+" ");
            }
            System.out.println("");
            System.out.println(packetdata.length);
        } catch (Exception ex) {
            ex.printStackTrace();
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
    private byte[] makeHeadertoBytes(HEADER_DIRECTION h_dir, HEADER_INSTRUCTION h_ins, int msg_id){
        byte[] theader = new byte[MAIN_HEADER_SIZE];
        byte[] msgbytes = ByteBuffer.allocate(Integer.SIZE/8).putInt(msg_id).array();
        
        theader[0] = (byte) (h_dir.getBit() << 3);
        theader[0] += (byte) h_ins.getBit();
        
        for(int i=1;i<theader.length;i++)
            theader[i] = msgbytes[i-1];
        return theader;
    }
    
    private void makeBytestoHeader(byte[] packet){
        for(int i=0;i<packet.length;i++){
            String s1 = String.format("%8s", Integer.toBinaryString(packet[i] & 0xFF)).replace(' ','0');
            System.out.print(s1+" ");
        }
        System.out.println("");
            
        byte header = packet[0];
        byte[] msg_id = new byte[Integer.BYTES];
        System.arraycopy(packet, 1, msg_id, 0, 4);
        
        System.out.println("test : "+header);
        this.h_direction = HEADER_DIRECTION.getHeaderDirection(header & 8);
        System.out.println("test : "+header);
        System.out.println("test : "+(header & 7));
        this.h_instruction = HEADER_INSTRUCTION.getHeaderInstruction(header & 7);
        this.HEADER_MSGID = ByteBuffer.wrap(msg_id).getInt();
        System.out.println("H dir : "+h_direction.toString()
                +" H Ins : "+h_instruction.toString()
                +" MSG ID : "+HEADER_MSGID);
    }
    
    /**
     * return byte Packet Data
     * @return 
     */
    public byte[] getPacket(){
        return packetdata;
    }
    
    public String getPacketString(){
        return String.valueOf(packetdata);
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
//            return getProtocolData(pf.getProfile());
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
     * Make Data from byte Packet(MinT Protocol)
     * @param packet 
     */
    private void makeData(byte[] spacket) {
        makeBytestoHeader(spacket);
//        spacket = spacket.trim();
//        spacket = spacket.split(this.Scheme)[1];
//        spacket = spacket.substring(1,spacket.length()-1);
//        Pattern p = Pattern.compile("\\}\\{");
//        String[] split = p.split(spacket);
//        
//        if(split.length == Numberoftotalpacket){
//            routelist.put(ROUTE.SOURCE, new NetworkProfile(split[1]));
//            routelist.put(ROUTE.PREV, new NetworkProfile(split[2]));
//            routelist.put(ROUTE.NEXT, null);
//            routelist.put(ROUTE.DESTINATION, new NetworkProfile(split[3]));
//            data = split[4];
//            makeStringtoHeader(split[0]);
//        }
    }
    
    public NetworkProfile getSource(){
        return routelist.get(ROUTE.SOURCE);
    }
    
    public NetworkProfile getPreviosNode(){
        return routelist.get(ROUTE.PREV);
    }
    
    public NetworkProfile getNextNode(){
        return routelist.get(ROUTE.NEXT);
    }
    
    public NetworkProfile getDestinationNode(){
        return routelist.get(ROUTE.DESTINATION);
    }
    
    public void setSource(NetworkProfile src){
        routelist.put(ROUTE.SOURCE, src);
//        makePacketData(this.HEADER_MSGID,this.h_direction, this.h_instruction, data);
    }
    
    public void setPrevNode(NetworkProfile prev){
        routelist.put(ROUTE.PREV, prev);
//        makePacketData(this.HEADER_MSGID, this.h_direction, this.h_instruction, data);
    }
    
    public String getMsgData(){
        return data;
    }
    
    public HEADER_DIRECTION getHeader_Direction(){
        return this.h_direction;
    }
    
    public HEADER_INSTRUCTION getHeader_Instruction(){
        return this.h_instruction;
    }
    
    public int getMSGID(){
        return HEADER_MSGID;
    }
    
//    public PacketDatagram getclone(){
//        byte[] nbyte = new byte[packetdata.length];
//        System.arraycopy(packetdata, 0, nbyte, 0, packetdata.length);
//        return new PacketDatagram(routelist.get(ROUTE.NEXT), nbyte);
//    }
    
    public static enum HEADER_DIRECTION {
        REQUEST(0), RESPONSE(1);
        private int num;
        HEADER_DIRECTION(int num){
            this.num = num;
        }
        public int getBit(){
            return num;
        }
        public static HEADER_DIRECTION getHeaderDirection(int i){
            for(HEADER_DIRECTION h : HEADER_DIRECTION.values()){
                if(h.getBit() == i)
                    return h;
            }
            return null;
        }

        boolean isRequest() {return this == REQUEST;}
        boolean isResponse() {return this == RESPONSE;}
    }
    public static enum HEADER_INSTRUCTION {
        GET(0), SET(1), POST(2), DELETE(3), DISCOVERY(4);
        private int num;
        HEADER_INSTRUCTION(int num){
            this.num = num;
        }
        public int getBit(){
            return num;
        }
        public static HEADER_INSTRUCTION getHeaderInstruction(int i){
            for(HEADER_INSTRUCTION h : HEADER_INSTRUCTION.values()){
                if(h.getBit() == i)
                    return h;
            }
            return null;
        }

        public boolean isGet() {return this == GET;}
        public boolean isSet() {return this == SET;}
        public boolean isPost() {return this == POST;}
        public boolean isDelete() {return this == DELETE;}
        public boolean isDiscovery() {return this == DISCOVERY;}
        public boolean NeedResponse() {return this == GET || this == DISCOVERY;}
    }
}
