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
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class BenchAnalize {
//    private enum INSTANCE {PERFORM, PACKETPERFORM;}
    private int psize = 0;
    private int numofPerform = 0;
    private long totalTime = 0;
    private long totalRequest = 0;
    private long totalbytes = 0;
    private long packets = 0;
    
//    private INSTANCE instance;
    private String pm;
    ConcurrentHashMap<Integer, Performance> pflist;
//    protected ArrayList<Performance> pflist;
    protected ArrayList<PerformData> datas;
    
    public ArrayList<Long> totaltime = new ArrayList<>();
    public ArrayList<Double> avgtime = new ArrayList<>();
    public ArrayList<Long> TRequest = new ArrayList<>();
    public ArrayList<Long> Tpackets = new ArrayList<>();
    public ArrayList<Long> Tbytes = new ArrayList<>();
    public ArrayList<Double> ReqperSec = new ArrayList<>();
    public ArrayList<Double> PckperSec = new ArrayList<>();
    public ArrayList<Integer> NofPerform = new ArrayList<>();
    
    private boolean isInserting = false;
    public BenchAnalize(String pm){
        this.pm = pm;
        pflist = new ConcurrentHashMap<>();
//        pflist = new ArrayList<>();
        datas = new ArrayList<>();
//        Initialize();
    }
    
    /**
     * add Performance
     * @param p 
     */
    public void addPerformance(Performance p){
        pflist.put(psize++, p);
    }
    public void analize() {
        analize(null);
    }
    public void analize(BenchAnalize others) {
        if(pflist.size() == 0)
            datas.add(new PerformData(datas.size()+1, pm, 0, 0, 0, 0, 0));
        else{
            resetParam();
            for(Performance pf : pflist.values()){
                Performance nf = pf.getPerformance();
                if(nf.getRequest() > 0){
                    numofPerform ++;
                    totalTime += nf.getTotalTime();
                    totalRequest += nf.getRequest();
                    totalbytes += nf.getTotalBytes();
                    packets += nf.getTotalPackets();
                }
            }
            datas.add(new PerformData(datas.size()+1, pm, totalRequest, totalTime, totalbytes, packets, numofPerform));
        }
        
        insertAnalysisData(false,datas);
        if(others != null)
            others.insertAnalysisData(true, datas);
    }
    
    public synchronized void insertAnalysisData(boolean isother, ArrayList<PerformData> datas){
        isInserting = true;
        int idx = datas.size()-1;
        int inidx = 0;
//        try{
            inidx = datas.size()-1;
            if(idx >= 0){
//                if(!isother){
////                    System.err.println("Origin-size: "+idx+", inidx: "+inidx);
//                }else{
//                    System.err.println("Other-size: "+idx+", inidx: "+inidx);
//                }
                totaltime.add(datas.get(idx).getTotalTime());
                avgtime.add(datas.get(idx).getAvgTime());
                TRequest.add(datas.get(idx).getRequest());
                Tpackets.add(datas.get(idx).getTotalPackets());
                Tbytes.add(datas.get(idx).getTotalBytes());
                NofPerform.add(datas.get(idx).getNumofPerform());
                ReqperSec.add(datas.get(idx).getRequestperSec());
                PckperSec.add(datas.get(idx).getPacketperSec());
            }else{
                System.err.printf("IDX is Zero");
            }
        isInserting = false;
        notifyAll();
//        }catch(Exception e){
//            System.err.printf("InsertAnalysis Out of Bound Error");
//            System.err.printf("IDX: "+idx);
//            e.printStackTrace();
//        }
    }
    
    private void resetParam(){
        numofPerform = 0;
        totalTime = 0;
        totalRequest = 0;
        totalbytes = 0;
        packets = 0;
    }
    
    public void printAllBenches(){
        for(Performance pf : pflist.values()){
            pf.print();
        }
    }
    
    public void printAllBenchList(){
        int cnt = 0;
        for(Performance pf: pflist.values()){
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
    
    public synchronized void clearBuffer(){
        while(isInserting){
            try {
                System.out.println("------------------------------------insert waiting!!!");
                wait();
            } catch (InterruptedException ex) {
                System.out.println("------------------------------------exit waiting!!!");
            }
        }
//        System.out.println("------------------------------------exit waiting!!!");
        this.resetParam();
        datas.clear();
        totaltime.clear();
        avgtime.clear();
        TRequest.clear();
        Tpackets.clear();
        Tbytes.clear();
        ReqperSec.clear();
        PckperSec.clear();
        NofPerform.clear();
    }
}
