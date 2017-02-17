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
package MinTFramework.Network.MessageProtocol.coap;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import java.util.Random;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Retransmission {
    private int ack_timeout; //for timeout
    private float ack_random_factor; //for timeout
    private float ack_timeout_scale;
    private Random rand; //for random timeout
    
    private MinT mint = MinT.getInstance();
    private NetworkManager nmanager = null;
    
    public Retransmission(){
        nmanager = mint.getNetworkManager();
    }
    
    
}
