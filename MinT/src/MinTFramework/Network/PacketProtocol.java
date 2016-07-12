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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

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
    private TreeMap<ROUTE, Profile> routelist= new TreeMap<ROUTE, Profile>();
    private String data;
    private byte[] packetdata;
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
        packetdata = makeByteData(routelist, data);
    }
    
    /**
     * MinT Protocol -> Data
     * @param packet 
     */
    public PacketProtocol(byte[] packet){
        makeData(packet);
    }
    
    /**
     * Make Byte Packet Data
     * @param rlist
     * @param msgdata
     * @return 
     */
    private byte[] makeByteData(TreeMap<ROUTE, Profile> rlist, String msgdata){
        byte[][] route = new byte[rlist.size()][];
        byte[] msg = getStringtoByte(msgdata);
        byte[] result;
        int routesize = 0;
        int msgsize = msg.length;
        int i=0;
        for(Profile pf : rlist.values()){
            route[i] = getStringtoByte(pf.getProfile());
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
        
        return result;
    }
    
    /**
     * return byte Packet Data
     * @return 
     */
    public byte[] getPacket(){
        return packetdata;
    }
    
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
    private void makeData(byte[] packet) {
        String spacket = new String(packet);
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
    
    public String getMsgData(){
        return data;
    }
}
