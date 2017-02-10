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

import MinTFramework.ThreadsPool.ThreadPoolScheduler;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * MinT Thread ThreadPoolScheduler
 Managed Process Pool list
  - Service Pool (Cached Thread Pool)
  - Network Receive Pool (Fixed Queue size)
  - Network Send Pool (Fixed Queue size)
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SystemScheduler extends ThreadPoolScheduler{
    private ArrayList<Service> serviceList;
    public SystemScheduler(){
        serviceList = new ArrayList<>();
        //System Scheduler
        registerMinTSystemthreadPool(MinTthreadPools.THREAD_ADJUST);
        //register System Pool
        registerMinTSystemthreadPool(MinTthreadPools.SYSTEM);
        //register Resource Pool
        registerMinTSystemthreadPool(MinTthreadPools.RESOURCE);
        //register Routing Pool
        registerMinTSystemthreadPool(MinTthreadPools.ROUTING_PROTOCOL);
        //register Receive Pool
        registerMinTSystemthreadPool(MinTthreadPools.NET_RECV_HANDLE);
        //register sender Pool
        registerMinTSystemthreadPool(MinTthreadPools.NET_SEND);
        //register retransmission pool
        registerMinTSystemthreadPool(MinTthreadPools.RETRANSMISSION_HANDLE);
    }
    
    /**
     * register System Thread Pool
     * @param mtp 
     */
    public void registerMinTSystemthreadPool(MinTthreadPools mtp){
        registerThreadPool(mtp.toString(), mtp.getServiceThread());
    }
    
    /**
     * Add Service
     * @param s 
     */
    public void addService(Service s){
        serviceList.add(s);
    }
    
    /**
     * 실행 중 서비스 실행할 수 있도록 수정 필요함.
     * @param s 
     */
    public void addExecuteService(Service s){
        executeProcess(MinTthreadPools.SYSTEM, s);
    }
    
    /**
     * start MinT Service
     */
    private void startService(){
        for(Service ts : serviceList)
            executeProcess(MinTthreadPools.SYSTEM, ts);
    }
    
    private void startThreadAdjust(){
        executeProcess(MinTthreadPools.THREAD_ADJUST, new ThreadAdjustment());
    }
    
    public void StartScheduler(){
        startService();
        startThreadAdjust();
    }
    
    public void setPoolsize(MinTthreadPools tp, int i){
        ThreadPoolExecutor exe = getThreadPool(tp);
        exe.setCorePoolSize(i);
        exe.setMaximumPoolSize(i);
    }
    
    /**
     * get ThreadPool
     * @param tp
     * @return 
     */
    public ThreadPoolExecutor getThreadPool(MinTthreadPools tp){
        return (ThreadPoolExecutor) threadPools.get(tp.toString());
    }
    
    /**
     * get Pool Queue Size
     * @param tp
     * @return 
     */
    public int getQueueSize(MinTthreadPools tp){
        return getRegisteredPoolQueueSize(tp.toString());
    }
    
    /**
     * execute Process to target thread pool
     * @param tp
     * @param run 
     */
    public void executeProcess(MinTthreadPools target, Runnable run){
        executeProcess(target.toString(), run);
    }
    
    /**
     * submit process to target thread pool with callback
     * 
     * @param target
     * @param run Callable type returned process
     */
    public Future<Object> submitProcess(MinTthreadPools target, Callable run){
        return submitProcess(target.toString(), run);
    }
    
    /**
     * sub process for non-callback
     * @param target
     * @param run
     * @return 
     */
    public Future<Object> submitProcess(MinTthreadPools target, Runnable run){
        return submitProcess(target.toString(), run);
    }
}
