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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Packet Protocol for MinT
 * MinT Protocol
 * {source}{previous departure}{next dstination}{final destination}{msg data}
 * |---------------------------route------------------------------||--data--|
 * |                         name|address                         ||  data  |
 * 
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PacketProtocol {
    private TreeMap<ROUTE, Profile> routelist= new TreeMap<>();
    private String data;
    private byte[] packetdata = null;
    private String packetdataString = null;
    private final int Numberoftotalpacket = 5;
    private enum ROUTE{
        SOURCE, PREV, NEXT, DESTINATION;
    }
    
    /**
     * Data -> MinT Protocol
     * @param src source departure = name|address
     * @param prev previous departure = name|address
     * @param next next destination = name|address
     * @param dest final destination = name|address
     * @param msg msg = request(0:null, other:request)|response() <- need to thinking
     * @return 
     */
    public PacketProtocol(Profile src, Profile prev, Profile next, Profile dest, String msg) {
        routelist.put(ROUTE.SOURCE,src);
        routelist.put(ROUTE.PREV,prev);
        routelist.put(ROUTE.NEXT,next);
        routelist.put(ROUTE.DESTINATION,dest);
        data = msg;
        makePacketData(routelist,data);
    }
    
    /**
     * MinT Protocol -> Data
     * @param packet 
     */
    public PacketProtocol(byte[] packet){
        packetdata = packet;
        try {
            packetdataString = unzipStringFromBytes(packet);
        } catch (IOException ex) {
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
    private void makeByteData(TreeMap<ROUTE, Profile> rlist, String msgdata){
        byte[][] route = new byte[rlist.size()][];
        byte[] msg = getStringtoByte(msgdata);
        byte[] result;
        int routesize = 0;
        int msgsize = msg.length;
        int i=0;
        for(Profile pf : rlist.values()){
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
    
    /**
     * Make Byte Packet Data
     * @param rlist
     * @param msgdata 
     */
    private void makePacketData(TreeMap<ROUTE, Profile> rlist, String msgdata){
        String result = "";
        String mdata = getProtocolData(msgdata);
        for(Profile pf : rlist.values()){
            if(pf != null)
                result += getProtocolData(pf.getProfile());
            else
                result += getProtocolData("");
        }
        
        result += mdata;
        try {
            this.packetdata = zipStringToBytes(result);
            packetdataString = result;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
     * inner protocol for MinT
     * @param origin
     * @return 
     */
    private String getProtocolData(String origin){
        return "{"+origin+"}";
    }

    /**
     * Make Data from byte Packet(MinT Protocol)
     * @param packet 
     */
    private void makeData(String spacket) {
        spacket = spacket.trim();
        spacket = spacket.substring(1,spacket.length()-1);
        Pattern p = Pattern.compile("\\}\\{");
        String[] split = p.split(spacket);
        
        if(split.length == Numberoftotalpacket){
            routelist.put(ROUTE.SOURCE, new Profile(split[0]));
            routelist.put(ROUTE.PREV, new Profile(split[1]));
            routelist.put(ROUTE.NEXT, new Profile(split[2]));
            routelist.put(ROUTE.DESTINATION, new Profile(split[3]));
            data = split[4];
        }
    }
    
    public Profile getSource(){
        return routelist.get(ROUTE.SOURCE);
    }
    
    public Profile getPreviosNode(){
        return routelist.get(ROUTE.PREV);
    }
    
    public Profile getNextNode(){
        return routelist.get(ROUTE.NEXT);
    }
    
    public Profile getDestinationNode(){
        return routelist.get(ROUTE.DESTINATION);
    }
    
    public void setSource(Profile src){
        routelist.put(ROUTE.SOURCE, src);
        makePacketData(routelist, data);
    }
    
    public void setPrevNode(Profile prev){
        routelist.put(ROUTE.PREV, prev);
        makePacketData(routelist, data);
    }
    
    public String getMsgData(){
        return data;
    }
    
    //GZIPOutputStream을 이용하여 문자열 압축하기
    public byte[] zipStringToBytes(String input) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
        bufferedOutputStream.write(input.getBytes());

        bufferedOutputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    //GZIPInputStream을 이용하여 byte배열 압축해제하기
    public String unzipStringFromBytes(byte[] bytes) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(gzipInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[100];

        int length;
        while ((length = bufferedInputStream.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        bufferedInputStream.close();
        gzipInputStream.close();
        byteArrayInputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toString();
    }
}
