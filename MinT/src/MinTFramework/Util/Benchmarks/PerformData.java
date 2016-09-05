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
public class PerformData extends Performance{
    int datacnt = 0;
    int numofthread = 1;
    
    public PerformData(int cnt, String name, long request, long time, long totalbytes, long packets, int numOfThread) {
        super(name, request, time, totalbytes, packets);
        numofthread = numOfThread;
        datacnt = cnt;
    }
    
    public int getNumofPerform(){
        return numofthread;
    }
    
    public double getAvgTime(){
        return super.getTime() == 0 ? 0 : super.getTime() / numofthread;
    }
    
    @Override
    public double getRequestperSec(){
        return getTime() == 0 ? 0 : request  / getAvgTime();
    }
    
    @Override
    public double getPacketperSec(){
        return getTime() == 0 ? 0 : packets  / getAvgTime();
    }
    
    @Override
    public double getByteperSec(){
        return getTime() == 0 ? 0 : totalbytes  / getAvgTime();
    }
    
//    @Override
//    public double getBytesPerPacket(){
//        return getTotalTime() == 0 ? 0 : totalbytes / ;
//    }
    
    public double getSECperRequest(){
        return request == 0 ? 0 : getAvgTime() / request;
    }
    
    @Override
    public void print(){
        System.out.format(Name+": NofT:%d Time:%.3f | Req:%d | Req/Sec:%.2f%n"
                ,numofthread, getAvgTime(), request, getRequestperSec());
    }
}