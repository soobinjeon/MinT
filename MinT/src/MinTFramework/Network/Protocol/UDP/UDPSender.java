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
package MinTFramework.Network.Protocol.UDP;
import MinTFramework.Network.PacketDatagram;

public class UDPSender implements Runnable {
    private PacketDatagram packet;
    
    public UDPSender(PacketDatagram _packet){
        packet = _packet;
    }
    
    @Override
    public void run() {
        try {
            UDPSendThread ust = (UDPSendThread)Thread.currentThread();
            ust.sendData(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
