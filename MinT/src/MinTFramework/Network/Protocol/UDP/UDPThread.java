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

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Util.Benchmarks.Performance;
import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class UDPThread extends Thread {
    protected String name = null;
    protected UDP udp = null;
    protected NetworkManager networkmanager = null;
    protected UDP.UDP_Thread_Pools threadpool = null;
    protected DebugLog dl = null;
    protected Performance bench = null;
    protected MinT parent;
    protected boolean isBenchMode = false;
    
    public UDPThread(String _name, UDP _udp, UDP.UDP_Thread_Pools utp){
        super(_name);
        name = _name;
        udp = _udp;
        networkmanager = udp.getNetworkManager();
        parent = MinT.getInstance();
        dl = new DebugLog(name+"_Debug");
        threadpool = utp;
        checkBench();
//        setPriority(MAX_PRIORITY);
    }
    
    public void checkBench(){
        if(!isBenchMode && parent.getBenchmark() != null && parent.getBenchmark().isMakeBench()){
            bench = new Performance(name);
            parent.getBenchmark().addPerformance(threadpool.toString(), bench);
            isBenchMode = true;
            System.out.println("make.."+name+" Bench");
        }
    }
    
    public void startPerform(){
        if (bench != null) {
            bench.startPerform();
        }
    }
    
    public void endPerform(int size){
        if (bench != null) {
                bench.endPerform(size);
            }
    }
    
    public UDP getUDP(){
        return udp;
    }
    
    public NetworkManager getNetworkManager(){
        return networkmanager;
    }
}
