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

import MinTFramework.MinT;
import MinTFramework.Util.Benchmarks.Performance;

/**
 * Receive Adapter Thread Pool
 *  - A worker thread for Receiving thread pool
 *  - intercept receiving data(RecvMSG) from endpoint networks
 *  - send to Matcher
 *  - 
 * @author soobin
 */
public class ReceiveAdapter extends Thread{
    private MatcherAndSerialization matcher;
    
    private Performance bench = null;
    private MinT parent;
    
    public ReceiveAdapter(Runnable r, String name) {
        super(r,name);
        matcher = new MatcherAndSerialization(NetworkLayers.LAYER_DIRECTION.RECEIVE);
        parent = MinT.getInstance();
//        if(parent.isBenchMode()){
//            bench = new Performance("ReceiveAdaptor");
//            parent.addPerformance(MinT.PERFORM_METHOD.RECV_LAYER, bench);
//        }
    }
    
    /**
     * get Current Thread's Matcher
     * @return 
     */
    public MatcherAndSerialization getMatcher(){
        return matcher;
    }

//    @Override
//    public void run() {
//        if (bench != null) {
//            bench.startPerform();
//        }
        
//        if (bench != null) {
//            bench.endPerform();
//        }
//    }
}
