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
package MinTFramework.SystemScheduler;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * System Process Scheduler
 * Managed Process Pool list
 *  - Service Pool (Cached Thread Pool)
 *  - Network Receive Pool (Fixed Queue size)
 *  - Network Send Pool (Fixed Queue size)
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Scheduler {
    protected HashMap<String, ExecutorService> threadPools = new HashMap<>();
    
    /**
     * Register Thread Pool
     * @param name
     * @param pool 
     */
    public void registThreadPool(String name, ExecutorService pool){
        threadPools.put(name, pool);
    }
    
    /**
     * Execute Process
     * @param target
     * @param run 
     */
    public void executeProcess(String target, Runnable run){
        ExecutorService exe = threadPools.get(target);
        if(exe != null)
            exe.execute(run);
    }
    
    /**
     * @param target
     * @param run 
     */
    public Future<Object> submitProcess(String target, Callable run){
        ExecutorService exe = threadPools.get(target);
        Future<Object> retvalue = null;
        if(exe != null)
            retvalue = exe.submit(run);
        return retvalue;
    }
    
    /**
     * Stop All Thread Pool
     */
    public void shutdownNowAllPools(){
        for(ExecutorService exe : threadPools.values()){
            exe.shutdownNow();
        }
    }
}
