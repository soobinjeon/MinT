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
package MinTFramework.Util.Benchmarks;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Performance {
    private double request = 0;
    private long stime = 0;
    private long etime = 0;
    private long totaltime = 0;
    private double time = 0;
    private boolean isdebug = false;
    private BENCHMARK_TYPE benchtype = BENCHMARK_TYPE.DEFAULT;
    private String Name;
    
    public static enum BENCHMARK_TYPE {DEFAULT, PACKET;}
    
    public Performance(String name){
        this(name, true, BENCHMARK_TYPE.DEFAULT);
    }
    public Performance(String name, BENCHMARK_TYPE bt){
        this(name, true, bt);
    }
    public Performance(String name, boolean isdebug, BENCHMARK_TYPE bt){
        isdebug = isdebug;
        benchtype = bt;
        Name = name;
    }
    
    public void reset(){
        request = 0;
        stime = 0;
        etime = 0;
        totaltime = 0;
        time = 0;
    }
    
    public void startPerform(){
        stime = System.currentTimeMillis();
    }
    
    public void endPerform(){
        etime = System.currentTimeMillis();
        setPacketInfo(stime, etime);
    }
    
    private void setInfo(){
        request ++;
    }
    
    public synchronized void setPacketInfo(long st, long et){
        setInfo();
        this.totaltime += et - st;
        this.time = (double)(totaltime / 1000.0);
    }
    
    public double getTotalTime(){
        return time;
    }
    
    public double getRequest(){
        return request;
    }
    
    public double getRequestperSec(){
        return time == 0 ? 0 : request / time;
    }
    
    public BENCHMARK_TYPE getBenchType(){
        return benchtype;
    }
    
    public void print(){
        System.out.format("Time:%.3f | Request:%d | Req/Sec:%.2f%n"
                , time, request, getRequestperSec());
    }
}
