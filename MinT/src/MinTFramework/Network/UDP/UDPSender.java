/*
 * Copyright (C) 2015 soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>, youngtak Han <gksdudxkr@gmail.com>
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
package MinTFramework.Network.UDP;
import java.io.IOException;
import java.net.*;

public class UDPSender {

    DatagramSocket socket;
    DatagramPacket inPacket;
    DatagramPacket outPacket;
    InetAddress address;
    int dstPort;
    String msg;
    int seq;

    public UDPSender(DatagramSocket socket) throws SocketException {
        this.socket = socket;
        this.dstPort = 0;
    }

    public void SendMsg(String msg, String dstIP, int dstPort) throws UnknownHostException, SocketException, IOException {
        this.msg = msg;
        this.address = InetAddress.getByName(dstIP);
        this.dstPort = dstPort;
        outPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, dstPort);
        
        new Thread(new SendMsg(socket, outPacket)).start();
    }
}
