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

import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class ResourcePool {
    //for Service Queue
    private String name;
    private Object[] ObjectQueue;
    private int tail;
    private int head;
    private int queuecount;
    private int totalQueueSize = 0;
    private int totalThreadPoolSize = 0;
    private boolean isStopAllService = false;
    private PoolWorkerThread[] threadPool;
    private DebugLog log = new DebugLog("Resource Pool");
    
    /**
     * Scheduler for MinT
     * @param ObjectQueueLength
     * @param numOfThread 
     */
    public ResourcePool(String name, int ObjectQueueLength, int numOfThread) {
        this.name = name;
        this.totalQueueSize = ObjectQueueLength;
        this.totalThreadPoolSize = numOfThread;
        this.ObjectQueue = new Object[ObjectQueueLength];
        this.head = 0;
        this.tail = 0;
        this.queuecount = 0;
    }
    
    protected abstract PoolWorkerThread makeWorkerThread(int numofThread, ResourcePool parentPool);
    
    /**
     * *
     * Run all thread in thread pool.
     */
    public void StartPool() {
        isStopAllService = false;
        
        threadPool = new PoolWorkerThread[totalThreadPoolSize];
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = makeWorkerThread(i, this);
        }
        
        for (PoolWorkerThread threadPool1 : threadPool) {
            threadPool1.start();
        }
        System.out.println("Scheduler-"+name+" [QueueSIZE-"+getQueueTotalLength()+"|ThreadSIZE-"+totalThreadPoolSize+"] was started.");
    }
    
    public void stopAllThreads(){
        for(PoolWorkerThread threads : threadPool){
            threads.interrupt();
        }
    }
    
    public int getQueueWaitingLength(){
        return queuecount;
    }
    
    public int getQueueTotalLength(){
        return this.totalQueueSize;
    }

//    /**
//     * get Number of Walking Threads
//     *
//     * @return
//     */
//    public int getNumberofWorkingThreads() {
//        int num = 0;
//        for (PoolWorkerThread threadPool1 : threadPool) {
//            if (threadPool1.isWorking()) {
//                num++;
//            }
//        }
//        return num;
//    }
    
    /**
     * get Number of Threads
     * @return 
     */
    public int getNumberofThreads(){
        return threadPool.length;
    }

    
    
    /***
     * Insert service into ObjectQueue
     * @param service service to be inserted in the queue
     */
    public synchronized void putResource(Object res) {
        boolean isinterrupted = false;
        while (queuecount >= ObjectQueue.length) {
            try {
                wait();
                break;
            } catch (InterruptedException e) {
                System.out.println("puter Interrupted");
                isinterrupted = true;
                break;
            }
        }

//        //make ID
//        service.setID(tail);
        if(!isinterrupted){
            ObjectQueue[tail] = res;

            tail = (tail + 1) % ObjectQueue.length;
            queuecount++;
            notifyAll();
        }
//        log.printMessage("putService["+service.getID()
//                +"] : ["+getQueueLength()+"/"+ObjectQueue.length+"]");
    }

    /***
     * !!!For workerthread use only!!!
     * !!!Do not use at Application!!!
     * 
     * take service in service queue
     * @param cthread
     * @return service 
     */
    protected synchronized Object takeResource(PoolWorkerThread taker) {
        boolean isinterrupted = false;
        while (queuecount <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                isinterrupted = true;
                taker.interrupt();
                break;
            }
        }
        
        if(!isinterrupted){
            Object res = ObjectQueue[head];
            ObjectQueue[head] = null;
            head = (head + 1) % ObjectQueue.length;
            queuecount--;
            notifyAll();
    //        log.printMessage("takeService["+service.getID()
    //                +"] : ["+getQueueLength()+"/"+ObjectQueue.length+"]");
            return res;
        }else
            return null;
    }
}
