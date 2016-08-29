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

import MinTFramework.ThreadsPool.MinTthreadPools;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SystemScheduler extends Scheduler{
    
    public SystemScheduler(){
        //register System Pool
        registMinTSystemthreadPool(MinTthreadPools.SYSTEM);
        //register Resource Pool
        registMinTSystemthreadPool(MinTthreadPools.RESOURCE);
        //register Receive Pool
        registMinTSystemthreadPool(MinTthreadPools.NET_RECV_HANDLE);
        //register sender Pool
    }
    
    private void registMinTSystemthreadPool(MinTthreadPools mtp){
        registThreadPool(mtp.toString(), mtp.getServiceThread());
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
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) threadPools.get(tp.toString());
        return tpe.getQueue().size();
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
     * submit process to target thread pool
     * 
     * @param target
     * @param run Callable type returned process
     */
    public Future<Object> submitProcess(MinTthreadPools target, Callable run){
        return submitProcess(target.toString(), run);
    }
}
