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
import MinTFramework.ThreadsPool.PoolWorkerThread;
import MinTFramework.ThreadsPool.ResourcePool;
import MinTFramework.Util.Benchmarks.Performance;

/**
 *
 * @author soobin
 */
public class ReceiveAdaptor extends PoolWorkerThread<RecvMSG>{
    private MatcherAndSerialization matcher;
    
    private Performance bench = null;
    private MinT parent;
    
    public ReceiveAdaptor(String name, int ID, ResourcePool pool) {
        super(name, ID, pool);
        matcher = new MatcherAndSerialization(NetworkLayers.LAYER_DIRECTION.RECEIVE);
        parent = MinT.getInstance();
        if(parent.isBenchMode()){
            bench = new Performance(this.getName());
            parent.addPerformance(MinT.PERFORM_METHOD.RECV_LAYER, bench);
        }
    }

    @Override
    protected void HandleResoure(RecvMSG resource) {
        if(bench != null)
            bench.startPerform();
        matcher.EndPointReceive(resource);
        if(bench != null)
            bench.endPerform();
    }
}
