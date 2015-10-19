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
package MinTFramework.Network.BLE;

import MinTFramework.ExternalDevice.DeviceBLE;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class BLEReceiver implements Runnable {

    /**
     * @param args the command line arguments
     */
    MessageReceiveImpl msgReceiveImpl;
    BLE ble;
    String tempbuf;
    byte[] inbuf;
    byte[] mintPacket;
    DeviceBLE deviceBLE;

    /***
     * BLE Receiver Thread Constructor
     * @param deviceBLE
     * @param ble
     */
    public BLEReceiver(DeviceBLE deviceBLE, BLE ble) {
        this.deviceBLE = deviceBLE;
        this.ble = ble;
    }

    /***
     * make new thread msg impl
     * @param msimpl 
     */
    public void setReceive(MessageReceiveImpl msimpl) {
        this.msgReceiveImpl = msimpl;
    }

    /***
     * Waiting until something received
     * when message received, make new Thread using msgReceivedImpl
     */
    @Override
    public void run() {
        
        tempbuf = deviceBLE.readUARTString();
        inbuf = tempbuf.getBytes();
        msgReceiveImpl.makeNewReceiver();
        
        ble.MatcherAndObservation(inbuf);
        
    }
}
