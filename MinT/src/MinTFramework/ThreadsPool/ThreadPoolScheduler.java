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
package MinTFramework.ThreadsPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * System Process Thread Pool Scheduler
 *  - 
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ThreadPoolScheduler {
    protected HashMap<String, ExecutorService> threadPools = new HashMap<>();
    
    /**
     * Register Thread Pool
     * @param name
     * @param pool 
     */
    public void registerThreadPool(String name, ExecutorService pool){
        threadPools.put(name, pool);
        System.out.println("Scheduler: Registered ThreadPool-"+name);
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
    
    public Future<Object> submitProcess(String target, Runnable run){
        ExecutorService exe = threadPools.get(target);
        Future<Object> retvalue = null;
        if(exe != null)
            retvalue = (Future<Object>) exe.submit(run);
        return retvalue;
    }
    
    /**
     * submit Schedule to singleScheduledExecutor
     * @param target
     * @param task
     * @param timeout
     * @param timeunit
     * @return 
     */
    public ScheduledFuture<Object> submitSchedule(String target,Runnable task, long timeout,TimeUnit timeunit){
        ScheduledExecutorService exe = (ScheduledExecutorService) threadPools.get(target);
        ScheduledFuture<Object> retvalue = null;
        if(exe != null)
            retvalue = (ScheduledFuture<Object>) exe.schedule(task, timeout, timeunit);
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
    
    /**
     * Stop Selected Thread Pool
     * @param name 
     */
    public void shutdownNowSelectedPool(String name){
        ExecutorService exe = threadPools.get(name);
        if(exe != null)
            exe.shutdownNow();
    }
    
    /**
     * get Registered Thread Pools
     * @return 
     */
    public Collection<ExecutorService> getRegisteredThreadPools(){
        return threadPools.values();
    }
    
    /**
     * get Registered Thread
     * @param target
     * @return 
     */
    public ExecutorService getRegisteredThread(String target){
        return threadPools.get(target);
    }
    
    /**
     * get Registered Thread Pool Size
     * @return 
     */
    public int getRegisteredPoolSize(){
        return threadPools.size();
    }
    
    /**
     * get Number of Total Working Threads
     * @return 
     */
    public int getTotalActiveThreads(){
        int totalThreads = 0;
        for(ExecutorService es : threadPools.values()){
            ThreadPoolExecutor tpe = (ThreadPoolExecutor)es;
            totalThreads += tpe.getActiveCount();
        }
        return totalThreads;
    }
    
    /**
     * get Registered Pool list key
     * String Type
     * @return 
     */
    public Iterator getRegisteredPoolList(){
        return threadPools.keySet().iterator();
    }
    
    public int getRegisteredPoolQueueSize(String target){
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) threadPools.get(target);
        return tpe.getQueue().size();
    }
}
