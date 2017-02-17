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
import MinTFramework.Network.SendMSG;
import MinTFramework.SystemScheduler.MinTthreadPools;
import MinTFramework.SystemScheduler.SystemScheduler;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

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
    private SystemScheduler sysSched = null;
    
    public Retransmission(){
        nmanager = mint.getNetworkManager();
        sysSched = mint.getSystemScheduler();
        
        rand = new Random(CoAPPacket.CoAPConfig.RANDOM_SEED);
        ack_timeout = CoAPPacket.CoAPConfig.ACK_TIMEOUT;
        ack_random_factor = CoAPPacket.CoAPConfig.ACK_RANDOM_FACTOR;
        ack_timeout_scale = CoAPPacket.CoAPConfig.ACK_TIMEOUT_SCALE;
    }
    
    /***
     * Register retransmission task to the MinT scheduler and SendMSG
     * @param msg CON msg
     */
    public void activeRetransmission(SendMSG msg){
        long timeout = 0;

        if (msg.getSendHit() == 0) {
            timeout = getRandomTimeout(ack_timeout, (int) (ack_timeout * ack_random_factor));
        } else {
            timeout = (int) ack_timeout_scale * msg.getCurrentTimeout();
        }
        
        msg.setCurrentTimeout(timeout);
        ScheduledFuture<?> f = sysSched.submitSchedule(MinTthreadPools.RETRANSMISSION_HANDLE, new RetransmissionTask(msg), timeout);
        msg.setRetransmissionHandle(f);
        
        msg.Sended();
    }
    
    /***
     * Get Random value between min and max
     *
     * @param min
     * @param max
     * @return Random value between min and max
     */
    private long getRandomTimeout(final int min, final int max) {
        if (min == max) {
            return min;
        }
        return min + rand.nextInt(max - min);                
    }
}
