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

    //for Request Queue
    private Request[] requestQueue;
    private int tail;
    private int head;
    private int count;
    private ScheduleWorkerThread[] threadPool;
    private DebugLog log = new DebugLog("Scheduler");

    public Scheduler(int requestQueueLength, int numOfThread) {
        this.requestQueue = new Request[requestQueueLength];
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
     * Stop request.
     *
     * @param request Request to stop.
     */
    public void stopRequest(Request request) {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            if (threadPool1.getRequestId() == request.getID()) {
                threadPool1.interrupt();
            }
        }
    }

    /**
     * *
     * Print all Request ID in thread pool
     */
    public synchronized void showWorkingThreads() {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            log.printMessage(threadPool1.getName() + " " + threadPool1.getRequestId());
        }
    }
    
    /**
     * get Number of Walking Threads
     * @return 
     */
    public int getNumberofWorkingThreads(){
        int num = 0;
        for(ScheduleWorkerThread threadPool1 : threadPool){
            if(threadPool1.isWorking())
                num ++;
        }
        return num;
    }

    /***
     * Generate unique request ID
     * @return unique request ID
     */
    private synchronized int makeID() {
        int rid[] = new int[threadPool.length];
        int newid = 0;
        boolean idflag = true;

        /**
         * *
         * check ThreadPool
         */
        for (int i = 0; i < threadPool.length; i++) {
            rid[i] = threadPool[i].getRequestId();
            //    System.out.println(rid[i] + "     " + i);
        }
        while (idflag) {
            idflag = false;
            for (int i = 0; i < rid.length; i++) {
                if (rid[i] == newid && idflag == false) {
                    newid++;
                    idflag = true;
                }
            }
        }

        /**
         * *
         * check requestQueue
         */
        int rid2[] = new int[requestQueue.length];

        for (int i = 0; i < requestQueue.length; i++) {
            if (requestQueue[i] != null) {
                rid2[i] = requestQueue[i].getID();
            } else {
                rid2[i] = 0;
            }
        }
        idflag = true;
        while (idflag) {
            idflag = false;
            for (int i = 0; i < rid.length; i++) {
                if (rid2[i] == newid && idflag == false) {
                    newid++;
                    idflag = true;
                }
            }
        }
//        System.out.println("newid : " + newid);
        return newid;
    }

    /***
     * Insert request into requestQueue
     * @param request request to be inserted in the queue
     */
    public synchronized void putRequest(Request request) {

        while (count >= requestQueue.length) {
            try {
                wait();
                break;
            } catch (InterruptedException e) {
            }
        }

        request.setID(makeID());

        requestQueue[tail] = request;

        tail = (tail + 1) % requestQueue.length;
        count++;
        notifyAll();

    }

    /***
     * !!!For workerthread use only!!!
     * !!!Do not use at Application!!!
     * 
     * take request in request queue
     * @return request 
     */
    public synchronized Request takeRequest() {
        while (count <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        Request request = requestQueue[head];
        head = (head + 1) % requestQueue.length;
        count--;
        notifyAll();
        return request;
    }
}
