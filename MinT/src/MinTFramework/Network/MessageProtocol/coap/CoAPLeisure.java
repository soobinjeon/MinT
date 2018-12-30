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
import MinTFramework.MinTConfig;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.SendMSG;
import MinTFramework.Network.Sharing.routingprotocol.RoutingProtocol;
import MinTFramework.SystemScheduler.MinTthreadPools;
import MinTFramework.SystemScheduler.SystemScheduler;
import java.util.Random;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class CoAPLeisure {
    private MinT mint = MinT.getInstance();
    private NetworkManager nmanager = mint.getNetworkManager();
    private SystemScheduler sysSched = mint.getSystemScheduler();
    private RoutingProtocol routing = nmanager.getRoutingProtocol();
    private float default_leisure = CoAPPacket.CoAPConfig.DEFAULT_LEISURE;
    private long leisure = 0;
    private Random rand;
    private int seed = 0;
    
    public CoAPLeisure(){    
        String[] n = MinTConfig.IP_ADDRESS.split("\\.");
        for(String tmp : n){
            seed+=Integer.parseInt(tmp);
        }
        rand = new Random(seed);
        leisure = (long)(default_leisure*1000);
//        setLeisure();
    }
    
    public void putLeisureScheduler(SendMSG smsg){
//        leisure = rand.nextInt((int)default_leisure);
        leisure = rand.nextInt((int)default_leisure*1000);
        sysSched.submitSchedule(MinTthreadPools.MULTICAST_LEISURE_HANDLE, new CoAPLeisureTask(smsg), leisure);
    }
    public void setLeisure(){
        long les;
        long G = 5;
        long S = 40;
        long R = 100000;
        
        /**
         * lb_Leisure = S * G / R
         * S : estimated response size
         * G : a group size
         * R : target data transfer rate
         */
        
        //leisure = S * G / R; default
        default_leisure = S * G / (float)R * 3 * 1000;
    }
}
