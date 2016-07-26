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
import MinTFramework.Util.TypeCaster;
import java.io.IOException;
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
        //지연 없어도 될 것 같음
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(BLEReceiver.class.getName()).log(Level.SEVERE, null, ex);
//        }
        System.out.println("Reading Start!!...");
        while(true)
        {
            tempbuf = "";
            tempbuf = deviceBLE.readUARTString("#");
            if(tempbuf.length() > 0){
                try {
//                    System.out.println("TEST : " + tempbuf);
                    inbuf = TypeCaster.zipStringToBytes(tempbuf);
                } catch (IOException ex) {
                    Logger.getLogger(BLEReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }

                ble.MatcherAndObservation(inbuf);
            }
        }
    }
}
