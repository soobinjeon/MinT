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

import MinTFramework.Util.DebugLog;
import java.nio.ByteBuffer;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NetworkProfile {
    public static final int NETWORK_ADDRESS_BYTE_SIZE = 8;
    private String id;
    private String address="";
    private String ipaddr="";
    private int Port = 0;
    private NetworkType ntype;
    private String Split = "|";
    private byte[] addrbyte = new byte[NETWORK_ADDRESS_BYTE_SIZE];
    private DebugLog dl = new DebugLog("NETWORK Profile");
    /**
     * new Network Profile for current, next or final destination nodes
     * @param name Name of Node
     * @param address Address of Node (ex : Internet based:ip address, BLE:bluetooth address and so on)
     * @param ntype type of Network
     */
    public NetworkProfile(String name, String address, NetworkType ntype){
//        this.name = name;
        this.address = address;
        this.ntype = ntype;
        initialize();
    }
    
    public NetworkProfile(String bytearray){
        String[] temp = bytearray.split("\\"+Split);
//        name = temp[0];
        address = temp[1];
        if(temp.length > 2)
            ntype = NetworkType.getNetworkType(Integer.parseInt(temp[2]));
        initialize();
    }
    
    /**
     * Initialize Network Profile
     */
    private void initialize(){
        makeID();
        if(ntype != null){
            setIPMode();
            makebyte();
        }
    }
    
//    public String getName(){
//        return name;
//    }
    
    public String getAddress(){
        return address;
    }
    
    public String getId(){
        return id;
    }
    
    public byte[] getbyteAddress(){
        return addrbyte;
    }
    
    /**
     * get Port, if UDP, TCP/IP, CoAP
     * @return 
     */
    public int getPort(){
        return Port;
    }
    public String getIPAddr(){
        return this.ipaddr;
    }
    
    public NetworkType getNetworkType(){
        return ntype;
    }
    
    public void setAddress(String add){
        address = add+":"+Port;
        initialize();
    }
    
//    public void setName(String name){
//        this.name = name;
//        makeID();
//    }
    
    public String getProfile(){
        String str;
//        str = name+Split+address;
        str = address;
        if(ntype!=null)
            str += Split+ntype.getID();
        return str;
    }
    
//    public boolean isNameProfile(){
//        return !name.equals("")&&(address == null || address.equals(""));
//    }
    
    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj){
        NetworkProfile ep = (NetworkProfile)obj;
//        System.out.println("cid : "+this.getId()+", eid : "+ep.getId());
        if(this.id.equals(ep.getId()))
            return true;
        else
            return false;
    }

    private void makeID() {
        if(address==null || address.equals(""))
            id = "";
//            id = name + "";
        else
            id = address;
//            id = name+address;
    }
    
    private void setIPMode(){
        if(ntype == NetworkType.TCPIP 
                || ntype == NetworkType.UDP 
                || ntype == NetworkType.COAP){
            String[] p = this.address.split(":");
            if(p.length > 1 && p[1] != null){
                this.ipaddr = p[0];
                this.Port = Integer.parseInt(p[1]);
            }
        }
    }

    private void makebyte() {
        if(ntype == NetworkType.BLE){
            for(int i=0;i<addrbyte.length;i++)
                addrbyte[i] = 0;
        }else if(ntype.isIPbased()){
            long tadd = 0;
            int ts = 0;
            //insert ip address
            String[] split = ipaddr.split("\\.");
            for(String s : split){
                ts = Integer.parseInt(s);
                tadd = tadd << 12;
                tadd += ts;
            }
            //insert port
            tadd = tadd << 16;
            tadd += Port;
            
            addrbyte = ByteBuffer.allocate(NETWORK_ADDRESS_BYTE_SIZE).putLong(tadd).array();
            for (int i = 0; i < addrbyte.length; i++) {
                String s1 = String.format("%8s", Integer.toBinaryString(addrbyte[i] & 0xFF)).replace(' ', '0');
                System.out.print(s1 + " ");
            }
            System.out.println("");
        }
    }
}
