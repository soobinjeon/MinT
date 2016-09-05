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
    private long period = 1000;
    private ThreadPoolScheduler scheduler;
    private ConcurrentHashMap<String, BenchAnalize> benchmarks;
    
    private boolean isBenchMode = false;
    
    public MinTBenchmark(ThreadPoolScheduler scheduler) {
        benchmarks = new ConcurrentHashMap();
        this.scheduler = scheduler;
    }
    
    public void startBench() {
        if(!isBenchMode)
            return;
        
        //do Something
        String bname = "MinT Benchmark";
        scheduler.registerThreadPool(bname, Executors.newCachedThreadPool());
        scheduler.executeProcess(bname, new Runnable() {
            @Override
            public void run() {
                try {
                    while(!Thread.currentThread().isInterrupted()){
                        if(!isBenchMode){
                            System.out.println("bench End");
                            break;
                        }
                        for(BenchAnalize ba : benchmarks.values()){
//                            System.out.println(ba.getName()+"-Analizing..");
                            ba.analize();
                        }
                        Thread.sleep(period);
                    }
                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
                    System.out.println("Bench End!");
                }
            }
        });
    }
    
    public void endBench(){
        isBenchMode = false;
        makeExcelData();
    }
    
    public void setBenchMode(boolean bm, int period){
        this.period = period;
        isBenchMode = bm;
    }
    
    public boolean isBenchMode(){
        return isBenchMode;
    }
    
    private BenchAnalize setupBenchMark(String pm){
        BenchAnalize na = new BenchAnalize(pm);
        benchmarks.put(pm, na);
        return na;
    }
    
    public BenchAnalize addBenchMark(String pm){
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
    
//    public BenchAnalize getBenchAnalize(PERFORM_METHOD pm){
//        ArrayList<Performance> pl = benchmarks.get(pm);
//        if(pl == null)
//            return null;
//        else
//            return new BenchAnalize(pm, pl);
//    }

    private void makeExcelData() {
        MinTExporter ex = new MinTExporter("test.xls", (int)period);
        ex.makeExcel(getBenchmarks());
    }
}
