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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class UDPReceiver implements Runnable {
    /**
     * @param args the command line arguments
     */
    DatagramSocket socket;
    DatagramPacket inPacket;
    MessageReceiveImpl msgReceiveImpl;
    byte[] inbuf;

    public UDPReceiver(DatagramSocket socket) throws SocketException {
        this.socket = socket;
    }

    public void setReceive(MessageReceiveImpl msimpl) {
        this.msgReceiveImpl = msimpl;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] inbuf = new byte[256];
                inPacket = new DatagramPacket(inbuf, inbuf.length);
                socket.receive(inPacket);
                new Thread(new RecvMsg(inPacket, msgReceiveImpl)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
