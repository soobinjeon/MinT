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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ScheduleWorkerThread extends Thread {

    private final Scheduler scheduler;
    private Request request;
    private int requestId;
    
    public ScheduleWorkerThread(String name, Scheduler scheduler) {
        super(name);
        this.request = null;
        this.scheduler = scheduler;
        this.requestId = 0;
    }

    public synchronized int getRequestId() {
        return requestId;
    }
    /**
     * Stop request in this thread
     */
    public synchronized void stopRequest() {
        this.request = null;
        this.requestId = 0;
    }

    @Override
    public void run() {
        while (true) {
            this.requestId = 0;
            this.request = scheduler.takeRequest();
            this.requestId = request.getID();
            System.out.println(Thread.currentThread().getName() + " Thread catched Request id : ");
            request.execute();
        }
    }
}