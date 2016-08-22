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

import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Packet Protocol for MinT
 * MinT Protocol
 * {DIR|INS|ID}{source}{Previous Departure}{final destination}{msg data}
 * |-header---||---------------------------route-------------||--data--|
 *             |                         name|address        || 256KB  | should make maximum size
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
    private String data="";
    private byte[] packetdata = null;
    private String packetdataString = null;
    private final int Numberoftotalpacket = 5;
    private String Scheme = "mint:";
    private final String EMPTY_MSG = "-";
    private StringBuilder MakeData = new StringBuilder();
    private HEADER_DIRECTION h_direction;
    private HEADER_INSTRUCTION h_instruction;
    private int HEADER_MSGID = HEADER_MSGID_INITIALIZATION;

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
//            packetdataString = TypeCaster.unzipStringFromBytes(packet);
            packetdataString = new String(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        makeData(packetdataString);
    }
    
    /**
     * Make Byte Packet Data
     * @deprecated 
     * @param rlist
     * @param msgdata
     * @return 
     */
    private void makeByteData(TreeMap<ROUTE, NetworkProfile> rlist, String msgdata){
        byte[][] route = new byte[rlist.size()][];
        byte[] msg = getStringtoByte(msgdata);
        byte[] result;
        int routesize = 0;
        int msgsize = msg.length;
        int i=0;
        for(NetworkProfile pf : rlist.values()){
            if(pf != null)
                route[i] = getStringtoByte(pf.getProfile());
            else
                route[i] = getStringtoByte("");
            routesize += route[i].length;
            i++;
        }
        
        result = new byte[routesize+msgsize];
        
        //insert route to byte
        int prevlength = 0;
        for(i=0;i<route.length;i++){
            System.arraycopy(route[i], 0, result, prevlength, route[i].length);
            prevlength += route[i].length;
        }
        
        //insert msg
        System.arraycopy(msg, 0, result, prevlength, msgsize);
        
        packetdata = result;
        System.out.println("Before length : "+packetdata.length);
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
        MakeData.setLength(0);
        MakeData.append(Scheme);
        //String mdata = getProtocolData(DataAnalizer(msgdata));
        MakeData.append(getProtocolData(makeHeadertoString(h_dir, h_ins, MSG_ID)));
        MakeData.append(getProtocolbyROUTE(ROUTE.SOURCE));
        MakeData.append(getProtocolbyROUTE(ROUTE.PREV));
        MakeData.append(getProtocolbyROUTE(ROUTE.DESTINATION));
        MakeData.append(getProtocolData(DataAnalizer(msgdata)));
//        result += getProtocolData(makeHeadertoString(h_dir, h_ins, MSG_ID));
//        result += getProtocolbyROUTE(ROUTE.SOURCE);
//        result += getProtocolbyROUTE(ROUTE.PREV);
//        result += getProtocolbyROUTE(ROUTE.DESTINATION);
//        result += mdata;
        try {
//            this.packetdata = TypeCaster.zipStringToBytes(MakeData.toString());
            packetdataString = MakeData.toString();
            packetdata = packetdataString.getBytes();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Make Header
     * @param h_dir
     * @param h_ins
     * @return 
     */
    private String makeHeadertoString(HEADER_DIRECTION h_dir, HEADER_INSTRUCTION h_ins, int msg_id) {
        return h_dir.getBit()+"|"+h_ins.getBit()+"|"+msg_id;
    }
    
    private byte[] makeheader(HEADER_DIRECTION h_dir, HEADER_INSTRUCTION h_ins){
        byte theader = 0;
        theader = (byte) (h_dir.getBit() << 3);
        theader += (byte) h_ins.getBit();
        return theader;
    }
    
    private void makeStringtoHeader(String data){
        String[] temp = data.split("\\|");
        this.h_direction = HEADER_DIRECTION.getHeaderDirection(Integer.parseInt(temp[0]));
        this.h_instruction = HEADER_INSTRUCTION.getHeaderInstruction(Integer.parseInt(temp[1]));
        this.HEADER_MSGID = Integer.parseInt(temp[2]);
    }
    
    /**
     * return byte Packet Data
     * @return 
     */
    public byte[] getPacket(){
        return packetdata;
    }
    
    public String getPacketString(){
        return packetdataString;
    }
    
    /**
     * Translate String to byte
     * @param data
     * @return 
     */
    private byte[] getStringtoByte(String data){
        byte[] result;
        String prev = getProtocolData(data);
        result = prev.getBytes();
        return result;
    }
    
    /**
     * get Protocol by Route
     * @param route
     * @return 
     */
    private String getProtocolbyROUTE(ROUTE route){
        NetworkProfile pf = this.routelist.get(route);
        if(pf != null)
            return getProtocolData(pf.getProfile());
        else return getProtocolData("");
    }
    
    /**
     * inner protocol for MinT
     * @param origin
     * @return 
     */
    private String getProtocolData(String origin){
        return "{"+origin+"}";
//        Prodata.setLength(0);
//        return Prodata.append("{").append(origin).append("}").toString();
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
    private void makeData(String spacket) {
        spacket = spacket.trim();
        spacket = spacket.split(this.Scheme)[1];
        spacket = spacket.substring(1,spacket.length()-1);
        Pattern p = Pattern.compile("\\}\\{");
        String[] split = p.split(spacket);
        
//        System.out.println("Check Packet Data");
//        for(String s : split){
//            System.out.println("--"+s);
//        }
        
        if(split.length == Numberoftotalpacket){
            routelist.put(ROUTE.SOURCE, new NetworkProfile(split[1]));
            routelist.put(ROUTE.PREV, new NetworkProfile(split[2]));
            routelist.put(ROUTE.NEXT, null);
            routelist.put(ROUTE.DESTINATION, new NetworkProfile(split[3]));
            data = split[4];
            makeStringtoHeader(split[0]);
        }
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
