/*
 * Copyright (C) 2015 HanYoungTak
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
package MinTFramework.Network.Protocol.BLE;

import MinTFramework.ExternalDevice.DeviceBLE;
import MinTFramework.Network.MessageProtocol.PacketDatagram;
import static java.lang.Thread.sleep;

/**
 *
 * @author HanYoungTak
 */
public class SendMsg implements Runnable {

    PacketDatagram outPacket;
    DeviceBLE deviceBLE;
    MessageReceiveImpl msgReceiveImpl;

    /***
     * send datagram packet to datagram socket
     * @param deviceBLE 
     * @param outPacket 
     */
    public SendMsg(DeviceBLE deviceBLE, PacketDatagram outPacket) {
        this.deviceBLE = deviceBLE;
        this.outPacket = outPacket;
    }

    @Override
    public void run() {
        //Convert Byte to String
        //System.out.println("Write Packet");
        deviceBLE.writeUART(outPacket.getPacketString() + "#");
        
        //disconnect 지연 필수 200 정도 여기가 문제가 되면 BLE 연결이 끊어지지 않음
        try {
            sleep(200);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //System.out.println("Disconnect");
        deviceBLE.disconnect();
        deviceBLE.setRole(0);
    }

}

