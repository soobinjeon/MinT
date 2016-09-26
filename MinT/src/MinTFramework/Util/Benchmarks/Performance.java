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
    protected int period = 0;
    protected long request = 0;
    protected long stime = 0;
    protected long etime = 0;
    protected long totaltime = 0;
    protected long totalbytes = 0;
    protected long packets = 0;
    
    
    protected String Name;
    protected boolean iscalibrating = false;
    protected boolean isgettering = false;
    
    public Performance(String name){
        Name = name;
    }
    
    /**
     * for Performance copy
     * @param name
     * @param request
     * @param totaltime 
     */
    public Performance(String name, long request, long time, long totalbytes, long packets){
        this(name);
        this.request = request;
        this.totaltime = time;
        this.totalbytes = totalbytes;
        this.packets = packets;
    }
    
    public void reset(){
        request = 0;
        totaltime = 0;
        totalbytes = 0;
        packets = 0;
    }
    
    public synchronized void startPerform(){
//        while(isgettering){
//            try {
////                System.out.println("------------------------------------insert start waiting!!!");
//                wait();
//            } catch (InterruptedException ex) {
////                System.out.println("------------------------------------exit start waiting!!!");
//            }
//        }
        iscalibrating = true;
//        stime = System.currentTimeMillis();
        stime = System.nanoTime();
    }
    
    public synchronized void endPerform(int bytesize){
//        etime = System.currentTimeMillis();
        etime = System.nanoTime();
        setPacketInfo(stime, etime);
        setPacketInfo(bytesize);
        iscalibrating = false;
        notifyAll();
    }
    
    private void setInfo(){
        request ++;
    }
    
    private void setPacketInfo(int bytesize){
        totalbytes += bytesize;
        packets ++;
    }
    
    public void setPacketInfo(long st, long et){
        setInfo();
        totaltime += et - st;
//        System.out.println("total time : st-"+st+", et-"+et+", ttime-" + totaltime);
    }
    
    public long getTotalTime(){
        return totaltime;
    }
    
    public double getTime(){
        double sec = 1000000000.0;
        return totaltime == 0 ? 0 : (double)(totaltime / sec);
    }
    
    public long getRequest(){
        return request;
    }
    
    public double getRequestperSec(){
        return getTime() == 0 ? 0 : request / getTime();
    }
    
    public double getPacketperSec(){
        return getTime() == 0 ? 0 : packets / getTime();
    }
    
    public double getByteperSec(){
        return getTime() == 0 ? 0 : totalbytes / getTime();
    }
    
    public double getBytesPerPacket(){
        return totalbytes / packets;
    }
    
    public long getTotalBytes(){
        return totalbytes;
    }
    
    public long getTotalPackets(){
        return packets;
    }
      
    public void print(){
        System.out.format(Name+": Time:%.3f | Req:%.2f | Req/Sec:%.2f%n"
                , getTime(), request, getRequestperSec());
    }
    
    /**
     * return current performance
     * @return 
     */
    public synchronized Performance getPerformance(){
        while(iscalibrating){
            try {
//                System.out.println("------------------------------------insert waiting!!!");
                wait();
            } catch (InterruptedException ex) {
//                System.out.println("------------------------------------exit waiting!!!");
            }
        }
        isgettering = true;
        Performance tp = null;
        tp = new Performance(Name, request, totaltime, totalbytes, packets);
//        System.out.println("tp tst before: "+tp.getTotalTime() + ", "+tp.getRequest());
        reset();
//        System.out.println("tp tst after: "+tp.getTotalTime() + ", "+tp.getRequest());
        isgettering = false;
//        notifyAll();
        return tp;
    }
}