/*
 * Copyright (C) 2015 soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>, youngtak Han <gksdudxkr@gmail.com>
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

import MinTFramework.Network.ApplicationProtocol;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTPacket extends ApplicationProtocol {
    
    public static byte[] makeMinTPacket(String src, String msg){
        byte[] bsrc;
        byte[] bmsg;
        byte[] data;
        String nsrc;
        String nmsg;
        
        nsrc = "{src:"+src+"}";
        nmsg = "{msg:"+msg+"}";
        
        bsrc = nsrc.getBytes();
        bmsg = nmsg.getBytes();
        
        data = new byte[bsrc.length+bmsg.length];
        
        System.arraycopy(bsrc, 0, data, 0, bsrc.length);
        System.arraycopy(bmsg, 0, data, bsrc.length, bmsg.length);
        
        return data;
    }
    
    public static byte[] makeMinTPacket(String src, byte[] msg){
        byte[] bsrc;
        byte[] bmsg;
        byte[] mintdata;
        
        
        String nsrc;
        String nmsg;
        String tmsg;
        String tmp = new String(msg);
        //String tmsg = new String(msg);
        
        tmsg = tmp.replaceAll("//+s", "");
        
        nsrc = "{src:"+src+"}";
        nmsg = "{msg:"+tmsg+"}";
        
        bsrc = nsrc.getBytes();
        bmsg = nmsg.getBytes();
        
        mintdata = new byte[bsrc.length+bmsg.length];
        
        System.arraycopy(bsrc, 0, mintdata, 0, bsrc.length);
        System.arraycopy(bmsg, 0, mintdata, bsrc.length, bmsg.length);
        
        return mintdata;
    }

}
