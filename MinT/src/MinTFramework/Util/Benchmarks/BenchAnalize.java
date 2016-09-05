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

import java.util.ArrayList;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class BenchAnalize {
//    private enum INSTANCE {PERFORM, PACKETPERFORM;}
    
    private int numofPerform = 0;
    private long totalTime = 0;
    private long totalRequest = 0;
    private long totalbytes = 0;
    private long packets = 0;
    
//    private INSTANCE instance;
    private String pm;
    
    protected ArrayList<Performance> pflist;
    protected ArrayList<PerformData> datas;
    
    public BenchAnalize(String pm){
        this.pm = pm;
        pflist = new ArrayList<>();
        datas = new ArrayList<>();
//        Initialize();
    }
    
    /**
     * add Performance
     * @param p 
     */
    public void addPerformance(Performance p){
        pflist.add(p);
    }
    
    public void analize() {
        if(pflist.size() == 0)
            datas.add(new PerformData(datas.size()+1, pm, 0, 0, 0, 0, 0));
        else{
            resetParam();
            for(Performance pf : pflist){
                Performance nf = pf.getPerformance();
                if(nf.getRequest() > 0){
                    numofPerform ++;
                    totalTime += nf.getTotalTime();
                    totalRequest += nf.getRequest();
                    totalbytes += nf.getTotalBytes();
                    packets += nf.getTotalPackets();
                }
            }
            System.out.println("insert - "+numofPerform+", "+totalTime+", "+totalRequest+", "+totalbytes+", "+packets);
            datas.add(new PerformData(datas.size()+1, pm, totalRequest, totalTime, totalbytes, packets, numofPerform));
        }
    }
    
    private void resetParam(){
        numofPerform = 0;
        totalTime = 0;
        totalRequest = 0;
        totalbytes = 0;
        packets = 0;
    }
    
    public void printAllBenches(){
        for(Performance pf : pflist){
            pf.print();
        }
    }
    
    public void printAllBenchList(){
        int cnt = 0;
        for(Performance pf: pflist){
            System.out.println(cnt+": "+pf.Name);
            cnt++;
        }
    }
    
    public ArrayList<PerformData> getDatas(){
        return this.datas;
    }
    
    public void print(){
//        System.out.print(pm+"-Total : ");
//        if(tp instanceof Performance){
//            System.out.format("NoP:%d | T:%.3f | T/R:%.8f | Req:%.0f | Req/s:%.2f | Bytes/P:%.2f | Bytes/s:%.2fK | Pk/s:%.2fK%n"
//                    , getNumofPerform(), getAvgTime(), getSECperRequest(), totalRequest, getRequestperSec()
//                    ,getBytesPerPacket(), getByteperSec()/1000, getPacketperSec()/1000);
//        }else{
//            System.out.println("");
//        }
    }

    String getName() {
        return this.pm;
    }
}
