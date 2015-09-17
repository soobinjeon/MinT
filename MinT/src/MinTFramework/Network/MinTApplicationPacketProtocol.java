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
public class MinTApplicationPacketProtocol extends ApplicationProtocol {

    @Override
    public byte[] makeApplicationPacket(String src, String fdst, String msg) {
        byte[] bsrc;
        byte[] bfdst;
        byte[] bmsg;
        
        byte[] data;
        
        String nsrc;
        String nfdst;
        String nmsg;

        nsrc = "{src:" + src + "}";
        nfdst = "{fdst:"+ fdst +"}";
        nmsg = "{msg:" + msg + "}";

        bsrc = nsrc.getBytes();
        bfdst = nfdst.getBytes();
        bmsg = nmsg.getBytes();

        data = new byte[bsrc.length + bfdst.length + bmsg.length];

        System.arraycopy(bsrc, 0, data, 0, bsrc.length);
        System.arraycopy(bfdst, 0, data, bsrc.length, bfdst.length);
        System.arraycopy(bmsg, 0, data, bsrc.length+bfdst.length, bmsg.length);

        return data;
    }

    @Override
    public MinTApplicationPacket getApplicationPacket(byte[] packet) {
        /***
         * Todo: Packet Separation
         * byte[] packet = {src:$(ADDRESS)}{msg:$(MESSAGE)} 
         * to
         * MinTApplicationPacket.address = $(ADDRESS)
         * MinTAPplicationPacket.data = $(MESSAGE)
         */
        
        return null;
    }
}
