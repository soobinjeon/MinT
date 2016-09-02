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
    private double totalTime = 0;
    private double totalRequest = 0;
    protected ArrayList<Performance> pflist;
    
    private int totalbytes = 0;
    private int packets = 0;
    
//    private INSTANCE instance;
    private Performance tp;
    private String pm;
    public BenchAnalize(String pm){
        this.pm = pm;
        pflist = new ArrayList<>();
//        Initialize();
    }
    
    /**
     * add Performance
     * @param p 
     */
    public void addPerformance(Performance p){
        pflist.add(p);
    }

    private void Initialize() {
        for(Performance pf : pflist){
            numofPerform ++;
            totalTime += pf.getTotalTime();
            totalRequest += pf.getRequest();
            if(pf instanceof PacketPerform){
                PacketPerform pkf = (PacketPerform)pf;
                totalbytes += pkf.getTotalBytes();
                packets += pkf.getTotalPackets();
            }
            tp = pf;
        }
    }
    
    public void printAllBenches(){
        for(Performance pf : pflist){
            pf.print();
        }
    }
    
    public double getTotalTime(){
        return totalTime;
    }
    
    public int getNumofPerform(){
        return numofPerform;
    }
    
    public double getAvgTime(){
        return numofPerform == 0 ? 0 : totalTime / numofPerform;
    }
    
    public double getRequest(){
        return totalRequest;
    }
    
    public double getRequestperSec(){
        return totalTime == 0 ? 0 : totalRequest / getAvgTime();
    }
    
    public double getPacketperSec(){
        return totalTime == 0 ? 0 : packets / getAvgTime();
    }
    
    public double getByteperSec(){
        return totalTime == 0 ? 0 : totalbytes / getAvgTime();
    }
    
    public double getBytesPerPacket(){
        return packets == 0 ? 0 : totalbytes / getAvgTime();
    }
    
    public double getTotalBytes(){
        return totalbytes;
    }
    
    public double getTotalPackets(){
        return packets;
    }
    
    public double getSECperRequest(){
        return totalRequest == 0 ? 0 : getAvgTime() / totalRequest;
    }
    
    public void print(){
        System.out.print(pm+"-Total : ");
        if(tp instanceof PacketPerform){
            System.out.format("NoP:%d | T:%.3f | T/R:%.8f | Req:%.0f | Req/s:%.2f | Bytes/P:%.2f | Bytes/s:%.2fK | Pk/s:%.2fK%n"
                    , getNumofPerform(), getAvgTime(), getSECperRequest(), totalRequest, getRequestperSec()
                    ,getBytesPerPacket(), getByteperSec()/1000, getPacketperSec()/1000);
        }else if(tp instanceof Performance){
            System.out.format("NoP:%d | Time:%.3f | T/R:%.8f | Req:%.0f | Req/Sec:%.2f%n"
                    , getNumofPerform(), getAvgTime(), getSECperRequest(), totalRequest, getRequestperSec());
        }else{
            System.out.println("");
        }
    }

    public void analize() {
    }
}
