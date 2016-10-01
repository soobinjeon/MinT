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

import MinTFramework.MinTConfig;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class IPAddress {
    String ipaddress = "";
    int port = 0;
    
    /**
     * Insert IP Address
     * @param ipa 
     */
    public IPAddress(String ipa){
        this(ipa, MinTConfig.INTERNET_COAP_PORT);
    }
    
    /**
     * insert IP Address
     * @param ipa
     * @param port 
     */
    public IPAddress(String ipa, int port){
        ipaddress = ipa;
        port = port;
    }
    
    public IPAddress(IPAddress ip){
        this(ip.getAddress(), ip.getPort());
    }
    
    public int getPort(){
        return port;
    }
    
    public String getIPAddress(){
        return ipaddress;
    }
    
    public String getAddress(){
        return ipaddress+":"+port;
    }
    
    public static IPAddress getIPAddress(String addr){
        String[] p = addr.split(":");
        IPAddress nipaddr = null;
        if(p.length > 1 && p[1] != null)
            nipaddr = new IPAddress(p[0], Integer.parseInt(p[1]));
        else if(p.length > 0 && p[0] != null)
            nipaddr = new IPAddress(p[0]);
        
        return nipaddr;
    }
}
