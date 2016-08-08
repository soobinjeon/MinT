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
public class ScheduleWorkerThread extends Thread {

    private final Scheduler scheduler;
    private Service service;
    private int serviceId;
    private DebugLog dl;
    public static final int NOT_WORKING_THREAD_SERVICE_ID = -1;
    private boolean isInterrupted = false;
    
    /**
     * init Schedule Worker Thread
     * @param name
     * @param scheduler 
     */
    public ScheduleWorkerThread(String name, Scheduler scheduler) {
        super(name);
        this.service = null;
        this.scheduler = scheduler;
        this.serviceId = NOT_WORKING_THREAD_SERVICE_ID;
        isInterrupted = false;
        dl = new DebugLog(name);
    }

    public synchronized int getServiceId() {
        return serviceId;
    }
    
//    /**
//     * Stop service in this thread
//     */
//
//    public synchronized void stopService() {
//        this.service = null;
//        this.serviceId = NOT_WORKING_THREAD_SERVICE_ID;
//    }
    
    /**
     * Interrupt Thread
     */
    public void Threadinterrupt(){
        isInterrupted = true;
        if(service != null){
            service.serviceInterrupt();
        }
    }
    
    /**
     * Is Thread Working For Service?
     * @return 
     */
    public synchronized boolean isWorking(){
        return serviceId != NOT_WORKING_THREAD_SERVICE_ID;
    }
    
    
    @Override
    public void run() {
        try{
            while (!isInterrupted) {
                this.serviceId = NOT_WORKING_THREAD_SERVICE_ID;
                this.service = scheduler.takeService(this);
                if(service != null){
                    this.serviceId = service.getID();
        //                dl.printMessage("Service("+service.getClass().getName()+") Start ID("+serviceId+"), ["+scheduler.getNumberofWorkingThreads()+"/]");
                    service.execute();
        //            dl.printMessage("Service("+service.getClass().getName()+") End ID("+serviceId+"), ["+(scheduler.getNumberofWorkingThreads()-1)+"/"+DEFAULT_THREAD_NUM+"]");
                }
            }
            if(isInterrupted){
                if(service != null){
                    service.serviceInterrupt();
                }
//                System.out.println(this.getName()+" status : "+Thread.interrupted());
            }
        }catch(Exception e){
            
        }
    }
}
