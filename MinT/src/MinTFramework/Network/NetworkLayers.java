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
package MinTFramework.Network;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public interface NetworkLayers {
    public static enum LAYER_DIRECTION {
        RECEIVE, SEND;
    }
    /**
     * Just Implement to EndPoint Layer
     * @param packet 
     */
    public void EndPointReceive(byte[] packet);
    
    /**
     * Layer Implements
     * @param packet 
     */
    public void Receive(PacketDatagram packet);
    
    /**
     * Layer Implements
     * @param packet 
     */
    public void Send(PacketDatagram packet);
    
    public void EndPointSend(SendMSG sendmsg);
}
