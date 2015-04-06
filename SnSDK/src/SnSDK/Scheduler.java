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
package SnSDK;

import SnSDK.Util.DebugLog;
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Scheduler {

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

    public void SchedulerRunning() {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            threadPool1.start();
        }
    }

    public void stopRequest(int requestId) {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            if (threadPool1.getRequestId() == requestId) {
                threadPool1.interrupt();
                //System.out.println(threadPool1.getName() + " FIND!");
            }
        }
    }

    public synchronized void showWorkingThreads() {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            log.printMessage(threadPool1.getName() + " " + threadPool1.getRequestId());
        }
    }

    public synchronized void putRequest(Request request) {
        while (count >= requestQueue.length) {
            try {
                wait();
                break;
            } catch (InterruptedException e) {
            }
        }
        requestQueue[tail] = request;

        tail = (tail + 1) % requestQueue.length;
        count++;
        notifyAll();
    }

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
