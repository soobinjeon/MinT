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
package MinTFramework.Network.MessageProtocol;

import MinTFramework.Network.MessageProtocol.coap.CoAPManager;
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum ApplicationProtocol {
    COAP(new CoAPManager()),
    MQTT(null);
    
    private MessageTransfer msg;
    
    ApplicationProtocol(MessageTransfer _msg){
        msg = _msg;
    }
    
    public MessageTransfer getMessageManager(){
        return msg;
    }
    
    public boolean isCOAP(){ return this == COAP;}
    public boolean isMQTT(){ return this == MQTT;}

    /**
     * 아 sendmsg recvmsg를 고치지 않는 이상 해결되지 않겠다...
     * get Datagram from Send Message
     * @param sendmsg
     * @return 
     */
    public PacketDatagram newPacketDatagram(APImpl ap_protocol) {
        PacketDatagram packet = null;
        switch(ap_protocol.getApplicationProtocol()){
            case COAP:
                if (ap_protocol.getSendMSG() != null)
                    packet = new CoAPPacket(ap_protocol.getSendMSG());
                else
                    packet = new CoAPPacket(ap_protocol.getRecvMSG());
                break;
            case MQTT:
                packet = null;
                break;
            default:
                packet = null;
                break;
        }
        return packet;
    }
}
