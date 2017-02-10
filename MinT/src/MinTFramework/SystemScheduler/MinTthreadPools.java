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

import MinTFramework.MinTConfig;
import MinTFramework.Network.ReceiveAdaptorFactory;
import MinTFramework.Network.SendAdapterFactory;
import MinTFramework.SystemScheduler.ServiceFactory;
import MinTFramework.ThreadsPool.RejectedExecutionHandlerImpl;
import MinTFramework.storage.ResourceProcFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MinT System Thread Pool Constructor
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum MinTthreadPools {
    THREAD_ADJUST(Executors.newSingleThreadExecutor()),
    SYSTEM(Executors.newCachedThreadPool(new ServiceFactory())),
    RESOURCE(Executors.newCachedThreadPool(new ResourceProcFactory())),
    NET_SEND(new ThreadPoolExecutor(MinTConfig.NETWORK_SEND_POOLSIZE
            , MinTConfig.NETWORK_SEND_POOLSIZE, 0
            , TimeUnit.SECONDS
            , new ArrayBlockingQueue<Runnable>(MinTConfig.NETWORK_SEND_WAITING_QUEUE)
            , new SendAdapterFactory()
            , new RejectedExecutionHandlerImpl())),
    NET_RECV_HANDLE(new ThreadPoolExecutor(MinTConfig.NETWORK_RECEIVE_POOLSIZE
            ,MinTConfig.NETWORK_RECEIVE_POOLSIZE, 0
            , TimeUnit.SECONDS
            , new ArrayBlockingQueue<Runnable>(MinTConfig.NETWORK_RECEIVE_WAITING_QUEUE)
            , new ReceiveAdaptorFactory()
            , new RejectedExecutionHandlerImpl())),
    ROUTING_PROTOCOL(Executors.newSingleThreadExecutor()), 
    RETRANSMISSION_HANDLE(Executors.newSingleThreadScheduledExecutor());

    ExecutorService es;

    MinTthreadPools(ExecutorService es) {
        this.es = es;
    }
    
    public ExecutorService getServiceThread(){
        return es;
    }
}
