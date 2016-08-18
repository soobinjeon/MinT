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
public class NetworkProfile {
    private String id;
    private String name="";
    private String address="";
    private String ipaddr="";
    private int Port = 0;
    private NetworkType ntype;
    private String Split = "|";
    
    /**
     * new Network Profile for current, next or final destination nodes
     * @param name Name of Node
     * @param address Address of Node (ex : Internet based:ip address, BLE:bluetooth address and so on)
     * @param ntype type of Network
     */
    public NetworkProfile(String name, String address, NetworkType ntype){
        this.name = name;
        this.address = address;
        this.ntype = ntype;
        makeID();
        setIPMode();
    }
    
    public NetworkProfile(String bytearray){
        String[] temp = bytearray.split("\\"+Split);
        if(temp.length > 1){
            name = temp[0];
            address = temp[1];
            ntype = null;
            makeID();
        }
        
        if(temp.length > 2){
            ntype = NetworkType.getNetworkType(Integer.parseInt(temp[2]));
        }
        
        setIPMode();
    }
    
    public String getName(){
        return name;
    }
    
    public String getAddress(){
        return address;
    }
    
    public String getId(){
        return id;
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
        makeID();
        setIPMode();
    }
    
    public void setName(String name){
        this.name = name;
        makeID();
    }
    
    public String getProfile(){
        String str;
        str = name+Split+address;
        if(ntype!=null)
            str += Split+ntype.getID();
        return str;
    }
    
    public boolean isNameProfile(){
        return !name.equals("")&&(address == null || address.equals(""));
    }
    
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
}
