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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Scheduler {

    private static final int MAX_THREAD = 5;
    private AppRequest[] requestQueue;
    private int tail;
    private int head;
    private int count;
    private ScheduleWorkerThread[] threadPool;

    public Scheduler() {
        this.requestQueue = new AppRequest[MAX_THREAD];
        this.head = 0;
        this.tail = 0;
        this.count = 0;

        threadPool = new ScheduleWorkerThread[MAX_THREAD];
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = new ScheduleWorkerThread("Worker-" + i, this);
        }
    }

    public void SchedulerRunning() {
        for (ScheduleWorkerThread threadPool1 : threadPool) {
            threadPool1.start();
        }
    }

    public synchronized void putRequest(AppRequest request) {
        requestQueue[tail] = request;
        tail = (tail + 1) % requestQueue.length;
        count++;
        notifyAll();
    }

    public synchronized AppRequest takeRequest() {
        while (count <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        AppRequest request = requestQueue[head];
        head = (head + 1) % requestQueue.length;
        count--;
        notifyAll();
        return request;
    }
}
