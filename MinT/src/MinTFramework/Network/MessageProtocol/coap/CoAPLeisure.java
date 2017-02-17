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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class CoAPLeisure {
    private MinT mint = MinT.getInstance();
    private NetworkManager nmanager = mint.getNetworkManager();
    private SystemScheduler sysSched = mint.getSystemScheduler();
    private float default_leisure = CoAPPacket.CoAPConfig.DEFAULT_LEISURE;
    private long leisure = 0;
    
    public CoAPLeisure(){
        leisure = (long)(default_leisure*1000);
    }
    
    public void putLeisureScheduler(SendMSG smsg){
        System.out.println("input leisure");
        sysSched.submitSchedule(MinTthreadPools.MULTICAST_LEISURE_HANDLE, new CoAPLeisureTask(smsg), leisure);
    }
    
    public void setLeisure(int Gsize){
        long les;
        long G = Gsize;
        long S = 40;
        long R = 1000;
        
        /**
         * lb_Leisure = S * G / R
         * S : estimated response size
         * G : a group size
         * R : target data transfer rate
         */
        
        leisure = S * G / R;
    }
}
