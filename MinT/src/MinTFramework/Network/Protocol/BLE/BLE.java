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
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkType;
import MinTFramework.Network.MessageProtocol.CoAPPacket;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class BLE extends Network {
    

    BLEReceiver receiver;
    BLESender sender;
    MessageReceiveImpl msgimpl;
    Thread receiverThread;
    String cmd;
    DeviceBLE deviceBLE = null;
    DebugLog dl = new DebugLog("BLE Network");
    
    /**
     * BLE Communication Structure
     * Do not support under v2.03
     * @param _ap
     * @param frame 
     * @param nm 
     */
    public BLE(String NodeName){
        super(new NetworkProfile(NodeName,null,NetworkType.BLE));
        if(!setBLEDevice()){
            String str = "BLE devices are not detected in the MinT: Please check it out";
            System.err.println(str);
//            dl.printMessage(str);
            isWorking(false);
            return;
        }
        //set Address
        profile.setAddress(deviceBLE.getAddress());
        System.out.println("BLE addr : "+profile.getAddress());
        receiver = new BLEReceiver(deviceBLE, this);
        sender = new BLESender(deviceBLE);
//        this.startReceiveThread();
    }
    
    /**
     * set BLE Device, if there are no BLE devices, return false
     * @return BLE existence
     */
    private boolean setBLEDevice() {
        deviceBLE = frame.getBLEDevice();
        if(deviceBLE == null)
            return false;
        else
            return true;
    }

    /***
     * Make and start Receiver thread
     */
    private void startReceiveThread() {
        receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    /**
     * Setting Destination and Connection
     *
     * @param _dst
     * @param dst destination for msg {MAC} / example ":78A5043F7EC6"
     */
    public String setDestination(NetworkProfile _dst) {
        String dst = _dst.getAddress();
        deviceBLE.setRole(1);
        //지연 필수******************짧을 시 통신불가, 1초보다 짧을 시 되었다 안되었다함
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //
        if(deviceBLE.connect(dst))
        {
//            System.out.println("Success : Connect");
            //deviceBLE.writeUART("AT");
            //return true;
        }
        else
        {
//            System.out.println("Fail : Connect");
            //deviceBLE.writeUART("AT");
            //return false;
        }
        return dst;
    }
    
    /***
     * Sending Message
     * @param packet 
     */
    @Override
    protected void sendProtocol(CoAPPacket packet) {
        //System.out.println(packet);
        String dst = setDestination(packet.getNextNode());
        sender.SendMsg(packet, dst);     //send, disconnect, setrole(0)
    }

    /**
     * @see 
     */
    @Override
    protected void interrupt() {
    }

    @Override
    protected void sendMulticast(CoAPPacket packet) {
        //Do Something
    }
}
