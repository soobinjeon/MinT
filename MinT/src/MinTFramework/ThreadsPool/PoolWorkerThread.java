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
public abstract class PoolWorkerThread<T> extends Thread {
    private int WorkerThreadID = -1;
    private final ResourcePool pool;
    private T resource;
    private DebugLog dl;
    public static final int NOT_WORKING_THREAD_SERVICE_ID = -1;
    
    abstract protected void HandleResoure(T resource);
    /**
     * init Schedule Worker Thread
     * @param name
     * @param scheduler 
     */
    public PoolWorkerThread(String name, int ID, ResourcePool pool) {
        super(name+"-"+ID);
        WorkerThreadID = ID;
        this.resource = null;
        this.pool = pool;
        dl = new DebugLog(name);
    }
    
    /**
     * get WorkerThread ID
     * @return 
     */
    public int getThreadID(){
        return WorkerThreadID;
    }

    @Override
    public void run() {
        try{
            while (!Thread.currentThread().isInterrupted()) {
                this.resource = (T) pool.takeResource(this);
                if(resource != null){
                    HandleResoure(resource);
                }
            }
        }catch(Exception e){
            System.out.println(this.getName()+" Inturrupted!, status : "+Thread.interrupted());
        }finally{
            System.out.println(this.getName()+" Inturrupted!, status : "+Thread.interrupted());
        }
    }
}
