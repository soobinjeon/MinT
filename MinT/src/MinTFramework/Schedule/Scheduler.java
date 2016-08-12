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
package MinTFramework.Schedule;

import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Scheduler {
    //for Service Queue
    private String name;
    private Service[] serviceQueue;
    private int tail;
    private int head;
    private int queuecount;
    private int totalQueueSize = 0;
    private int totalThreadPoolSize = 0;
    private boolean isStopAllService = false;
    private ScheduleWorkerThread[] threadPool;
    private DebugLog log = new DebugLog("Scheduler");
    
    /**
     * Scheduler for MinT
     * @param serviceQueueLength
     * @param numOfThread 
     */
    public Scheduler(String name, int serviceQueueLength, int numOfThread) {
        this.name = name;
        this.totalQueueSize = serviceQueueLength;
        this.totalThreadPoolSize = numOfThread;
        this.serviceQueue = new Service[serviceQueueLength];
        this.head = 0;
        this.tail = 0;
        this.queuecount = 0;
        
        threadPool = new ScheduleWorkerThread[numOfThread];
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = new ScheduleWorkerThread("ScheduleWorker-" + i, this);
        }
    }
    
    /**
     * *
     * Run all thread in threadpool.
     */
    public void SchedulerRunning() {
        isStopAllService = false;
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            threadPool1.start();
        }
        System.out.println("Scheduler-"+name+" [QueueSIZE-"+getQueueTotalLength()+"|ThreadSIZE-"+totalThreadPoolSize+"] was started.");
    }

    /**
     * @deprecated 
     * Stop service.
     *
     * @param service Service to stop.
     */
    public void stopService(Service service) {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            if (threadPool1.getServiceId() == service.getID()) {
                threadPool1.interrupt();
            }
        }
    }
    
    public void stopAllService(){
        isStopAllService = true;
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            threadPool1.Threadinterrupt();
        }
        this.putService(new Service() {
            @Override
            public void execute() {
            }
        });
    }
    
    /**
     * **
    Print all Service ID in thread pool
     */
    public synchronized void showWorkingThreads() {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            log.printMessage(threadPool1.getName() + " " + threadPool1.getServiceId());
        }
    }
    
    public int getQueueWaitingLength(){
        return queuecount;
    }
    
    public int getQueueTotalLength(){
        return this.totalQueueSize;
    }

    /**
     * get Number of Walking Threads
     *
     * @return
     */
    public int getNumberofWorkingThreads() {
        int num = 0;
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            if (threadPool1.isWorking()) {
                num++;
            }
        }
        return num;
    }


    /***
     * @deprecated 
     * Generate unique service ID
     * @return unique service ID
     */
    private synchronized int makeID() {
        int length = serviceQueue.length+1;
        boolean rid[] = new boolean[length];
        int newid = 0;
        boolean idflag = true;
        
        for(int i=0;i<length;i++){
            rid[i] = false;
        }
        
        /**
         * *
         * check serviceQueue
         */
        for (int i = 0; i < serviceQueue.length; i++) {
            if (serviceQueue[i] != null) {
                rid[serviceQueue[i].getID()] = true;
            }
        }
        /**
         * *
         * Make New ID
         */
        newid = 0;
        while(true){
            if(rid[newid] == false){
                break;
            }
            else{
                newid++;
            }
        }

        return newid;
    }
    
    /***
     * Insert service into serviceQueue
     * @param service service to be inserted in the queue
     */
    public synchronized void putService(Service service) {

        while (queuecount >= serviceQueue.length) {
            try {
                wait();
                break;
            } catch (InterruptedException e) {
            }
        }

        //make ID
        service.setID(tail);

        serviceQueue[tail] = service;

        tail = (tail + 1) % serviceQueue.length;
        queuecount++;
        notifyAll();
//        log.printMessage("putService["+service.getID()
//                +"] : ["+getQueueLength()+"/"+serviceQueue.length+"]");
    }

    /***
     * !!!For workerthread use only!!!
     * !!!Do not use at Application!!!
     * 
     * take service in service queue
     * @param cthread
     * @return service 
     */
    protected synchronized Service takeService(ScheduleWorkerThread cthread) {
        while (!isStopAllService && queuecount <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        
        if(isStopAllService)
            cthread.Threadinterrupt();
        
        Service service = serviceQueue[head];
        serviceQueue[head] = null;
        head = (head + 1) % serviceQueue.length;
        queuecount--;
        notifyAll();
//        log.printMessage("takeService["+service.getID()
//                +"] : ["+getQueueLength()+"/"+serviceQueue.length+"]");
        return service;
    }
}
