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

import MinTFramework.ThreadsPool.ThreadPoolScheduler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTBenchmark {
    private final String bname = "MinT Benchmark";
    private long period = 1000;
    private ThreadPoolScheduler scheduler;
    private ConcurrentHashMap<String, BenchAnalize> benchmarks;
    private ConcurrentHashMap<String, BenchAnalize> poolsinfo;
    
    private boolean isBenchMode = false;
    
    private String Filename = "result";
    
    public MinTBenchmark(ThreadPoolScheduler scheduler, long pd) {
        benchmarks = new ConcurrentHashMap();
        poolsinfo = new ConcurrentHashMap();
        this.scheduler = scheduler;
        this.period = pd;
    }
    
    public void makeBenchMark() {
//        if(!isBenchMode)
//            return;
        //do Something
        System.out.println("Make BenchMark!");
        scheduler.registerThreadPool(bname, Executors.newSingleThreadExecutor());
        scheduler.executeProcess(bname, new Runnable() {
            @Override
            public void run() {
                try {
                    while(!Thread.currentThread().isInterrupted()){
                        for(BenchAnalize ba : benchmarks.values()){
//                            System.out.println(ba.getName()+"-Analizing..");
                            BenchAnalize pba = poolsinfo.get(ba.getName());
                            ba.analize(pba);
                        }
                        
                        if(!isBenchMode)
                            ClearBuffer();
                        
                        Thread.sleep(period);
                    }
                } catch (InterruptedException ex) {
                    System.out.println("Bench End!");
                }
            }
        });
    }
    
    private void ClearBuffer(){
        //clear buffer
        for(BenchAnalize ba : benchmarks.values())
            ba.clearBuffer();
    }
    
    public void startBench(String filename){
        isBenchMode = true;
        System.out.println("Start BenchMarks... - "+filename);
        Filename = filename;
        ClearBuffer();
    }
    public void endBench(){
        if(isBenchMode){
            System.out.println("end BenchMode!");
//            scheduler.shutdownNowSelectedPool(bname);
            makeExcelData(Filename);
            isBenchMode = false;
            ClearBuffer();
        }
    }
    
    public boolean isBenchMode(){
        return isBenchMode;
    }
    
    private BenchAnalize setupBenchMark(String pm){
        //insert BA to benchmarks
        BenchAnalize na = new BenchAnalize(pm);
        benchmarks.put(pm, na);
        
        //insert Pool Info
        BenchAnalize pool = new BenchAnalize(pm);
        poolsinfo.put(pm, pool);
        return na;
    }
    
    public BenchAnalize addBenchMark(String pm){
//        System.out.println("Add BenchMark: "+pm);
        BenchAnalize pl = benchmarks.get(pm);
        if(pl == null)
            pl = setupBenchMark(pm);
        return pl;
    }
    
    public synchronized void addPerformance(String pm, Performance p){
        BenchAnalize pl = addBenchMark(pm);
        pl.addPerformance(p);
    }
    
    public BenchAnalize getBenchmark(String pm){
        return benchmarks.get(pm);
    }
    
    public Collection<BenchAnalize> getBenchmarks(){
        return benchmarks.values();
    }
    
    public ArrayList<String> getBenchmarkList(){
        return new ArrayList<>(benchmarks.keySet());
    }
    
    public ConcurrentHashMap<String, BenchAnalize> getPoolsInfo() {
        return poolsinfo;
    }
    
//    public BenchAnalize getBenchAnalize(PERFORM_METHOD pm){
//        ArrayList<Performance> pl = benchmarks.get(pm);
//        if(pl == null)
//            return null;
//        else
//            return new BenchAnalize(pm, pl);
//    }

    private void makeExcelData(String filename) {
        MinTExporter ex = new MinTExporter(filename, (int)period);
        ex.makeExcel(getBenchmarks());
    }
}
