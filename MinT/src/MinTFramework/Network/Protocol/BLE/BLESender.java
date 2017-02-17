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
package MinTFramework.Network.Protocol.BLE;
import MinTFramework.ExternalDevice.DeviceBLE;
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;
import java.net.*;

public class BLESender {

    InetAddress address;
    CoAPPacket msg;
    int seq;
    DeviceBLE deviceBLE;

    public BLESender(DeviceBLE deviceBLE) {
        this.deviceBLE = deviceBLE;
    }

    public void SendMsg(CoAPPacket msg, String dst) {
        this.msg = msg;
                
        new Thread(new SendMsg(deviceBLE, msg)).start();
        
    }
}
