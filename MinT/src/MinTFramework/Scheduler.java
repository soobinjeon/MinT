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
package MinTFramework;

import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Scheduler {

    //for Service Queue
    private Service[] serviceQueue;
    private int tail;
    private int head;
    private int count;
    private ScheduleWorkerThread[] threadPool;
    private DebugLog log = new DebugLog("Scheduler");

    public Scheduler(int serviceQueueLength, int numOfThread) {
        this.serviceQueue = new Service[serviceQueueLength];
        this.head = 0;
        this.tail = 0;
        this.count = 0;

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
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            threadPool1.start();
        }
    }

    /**
     * *
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

    /**
     * **
    Print all Service ID in thread pool
     */
    public synchronized void showWorkingThreads() {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            log.printMessage(threadPool1.getName() + " " + threadPool1.getServiceId());
        }
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
     * Generate unique service ID
     * @return unique service ID
     */
    private synchronized int makeID() {
        int length = threadPool.length+serviceQueue.length;
        boolean rid[] = new boolean[length];
        int newid = 0;
        boolean idflag = true;
        
        for(int i=0;i<length;i++){
            rid[i] = false;
        }
        
        /**
         * *
         * check ThreadPool
         */
        for (int i = 0; i < threadPool.length; i++) {
            if (threadPool[i].getServiceId() != -1) {
                rid[threadPool[i].getServiceId()] = true;
            }
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

        while (count >= serviceQueue.length) {
            try {
                wait();
                break;
            } catch (InterruptedException e) {
            }
        }

        service.setID(makeID());

        serviceQueue[tail] = service;

        tail = (tail + 1) % serviceQueue.length;
        count++;
        notifyAll();

    }

    /***
     * !!!For workerthread use only!!!
     * !!!Do not use at Application!!!
     * 
     * take service in service queue
     * @return service 
     */
    public synchronized Service takeService() {
        while (count <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        Service service = serviceQueue[head];
        head = (head + 1) % serviceQueue.length;
        count--;
        notifyAll();
        return service;
    }
}
