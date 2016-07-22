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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class BLEReceiver implements Runnable {

    /**
     * @param args the command line arguments
     */
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
     * Waiting until something received
     * when message received, make new Thread using msgReceivedImpl
     */
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(BLEReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true)
        {
            System.out.println("Reading Start!!...");
//            while(true)
            tempbuf = deviceBLE.readUARTString("#");
            //Byte temp = new Byte(tempbuf);
            System.out.println("TEST : " + tempbuf);
//            tempbuf = tempbuf.substring(tempbuf.indexOf("{src"));
            inbuf = tempbuf.getBytes();
        
            //ble.MatcherAndObservation(inbuf);
        }
    }
}
