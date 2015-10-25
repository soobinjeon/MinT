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
package MinTFramework.Network.BLE;

import MinTFramework.ExternalDevice.DeviceBLE;

/**
 *
 * @author HanYoungTak
 */
public class SendMsg implements Runnable {

    byte[] outPacket;
    DeviceBLE deviceBLE;
    MessageReceiveImpl msgReceiveImpl;

    /***
     * send datagram packet to datagram socket
     * @param deviceBLE 
     * @param outPacket 
     */
    public SendMsg(DeviceBLE deviceBLE, byte[] outPacket) {
        this.deviceBLE = deviceBLE;
        this.outPacket = outPacket;
    }

    @Override
    public void run() {
        //Convert Byte to String
        deviceBLE.writeUART(new String(outPacket), "#");
        deviceBLE.disconnect();
        deviceBLE.setRole(0);
    }

}

