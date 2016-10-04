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
package MinTFramework.SystemScheduler;

import MinTFramework.MinT;
import MinTFramework.MinTConfig;
import MinTFramework.Network.Protocol.UDP.UDP;
import MinTFramework.Util.Benchmarks.BenchAnalize;
import MinTFramework.Util.Benchmarks.MinTBenchmark;
import MinTFramework.Util.LimitedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread Pool Adjustment
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ThreadAdjustment implements Runnable{
    MinT frame = MinT.getInstance();
    MinTBenchmark bench;
    ConcurrentHashMap<String, BenchAnalize> pools;
    
    LimitedQueue<Double> THarray;
    LimitedQueue<Double> arrayRequestNn;
    private double accumReqeustNn = 0;
    private int Nt = 1; //current Number of Thread
    private int PrevN = 1; //prev Number of Thread
    private double THt = 0;
    private final int TRequestSize = 3;
    private ThreadPoolExecutor recvpool;
    private double AccumQW = 0; //Accumulated Queue Weight
    
    private int MAX_THREAD = 2;
    private double MAX_TH = 0;
    public ThreadAdjustment() {
        bench = frame.getBenchmark();
        pools = bench.getPoolsInfo();
        THarray = new LimitedQueue(TRequestSize);
        arrayRequestNn = new LimitedQueue(3);
        recvpool = frame.getSysteScheduler().getThreadPool(MinTthreadPools.NET_RECV_HANDLE);
    }
    
    /**
     * System Schedule - Thread Pool size of Receive Handle Adjustment
     * Adaptive thread control on real-time request handling from network receive listener (UDP receiver, BLE recv,..)
     * processing time period : 1 second
     * Necessary data to processing : from benchmark
     */
    @Override
    public void run() {
        try{
            System.out.println("Start Thread Adjustment");
            while(!Thread.currentThread().isInterrupted()){
                Thread.sleep(1000);
                AdjustRecvHandler();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
    }

    /**
     * Thread pool size of receive handle adjustment
     * Data info
     * R_t: Request per Sec (request/sec)
     * H_t: Handle per Sec (r/s)
     * TH_t: throughput per handle thread (r/s)
     * N_t: Current Number of Threads
     * 
     * algorithm
     * N_t+1 = evaluation(PN_t+1)
     * 
     * Evaluation
     *  - 1, PN_t+1 smaller than 1
     *  - M_t+1, PN_t+1 b bigger than M_t+1
     *  - PN_t+1, otherwise
     * 
     * PN_t+1 = round( (R_t - H_t) / TH_t ) + QW_t + N_t
     * TH_t = (R_t-2 / N_t- + R_t-1 / N_t-1 + R_t / N_t) / 3
     * 
     * QW_t = (int)C_t
     * Q_acc,t+1 = C_t - QW_t
     * C_t = QS_t / QS_max + Q_acc,t
     *   - QS_max : Max queue size
     *   - QS_t : Current Handle queue size
     */ 
    private void AdjustRecvHandler() {
        BenchAnalize Ht = pools.get(MinTthreadPools.NET_RECV_HANDLE.toString());
//        BenchAnalize St = pools.get(UDP.UDP_Thread_Pools.UDP_SENDER.toString());
        BenchAnalize Rt = pools.get(UDP.UDP_Thread_Pools.UDP_RECV_LISTENER.toString());

        if(Ht != null && Rt != null){
//            System.out.println("------------------------------------------------------------------------------------------");
            double Hdata = getData(Ht);
            double Rdata = getData(Rt);
            
            //store information
            THt = calibrateHTrend(THarray);
            AddHTtrend(Hdata);
            AccumRequestNn(Ht, Rt);
            
            //Queue를 이용한 처리
            double queueP = getQueueWegiht(recvpool);
            
            double result = THt == 0 ? 0 : (Rdata - Hdata) / THt;
            
            arrayRequestNn.add(result);
//            System.out.println("originR: "+result+", RL: "+arrayRequestNn);
            
            result = Math.round(result);
            
            double nextN = result + queueP;
            int nN =  (int)nextN + Nt;
            
            //not working, decrease number of Thread
            if(THt == 0)
                nN = nN - 1;
            
            //지속적으로 Number of Thread가 올라가는데도 성능이 증가하지 않으면 멈춤
            MAX_THREAD = setMAXThread(THarray, MAX_THREAD);
            
            if(nN < 1)
                Nt = 1;
            else if(nN > MAX_THREAD)
                Nt = MAX_THREAD;
            else
                Nt = nN;
            
            PrevN = Nt;
            
            // for Debug
//            System.out.printf("QeueP: %.2f",queueP);
//            System.out.println("");
//            System.out.println("THArray: "+THarray);
//            System.out.println("");
//            System.out.printf("AccHt: %.2f, RNn: %.2f, TH: %.2f, MTHt:%.2f, MT: %d, Next N: %.2f, nN: %d, NT: %d",accumReqeustNn,result,THt,MAX_TH,MAX_THREAD,nextN,nN, Nt);
//            System.out.println("");
//            System.out.println("UDP_RECV_LISTNER is not Null() - "+Rdata);
//            System.out.println("NET_RECV_HANDLE is not Null() - "+Hdata);
            
            frame.getSysteScheduler().setPoolsize(MinTthreadPools.NET_RECV_HANDLE, Nt);
            Rt.clearBuffer();
            Ht.clearBuffer();
//            St.clearBuffer();
        }
            
    }
    
    private double getData(BenchAnalize input){
        if(input == null)
            return -1;
        int size = input.ReqperSec.size() - 1;
        /**
         * 서로 다른 스레드에서 동시에 Pool 정보를 저장 및 불러옴
         * Pool 정보를 저장하는 시간보다 불러오는 시간이 더 빠를 때가 있음
         * 데이터가 저장될 때 까지 기다림
         */
        while(size < 0){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
            size = input.ReqperSec.size() - 1;
        }
//        if(size < 0)
//            return -1;
        return input.ReqperSec.get(size);
    }
    
    private double getTime(BenchAnalize input){
        if(input == null)
            return -1;        
        int size = input.totaltime.size() - 1;
        if(size < 0)
            return 0;
        return input.totaltime.get(size);
    }
    
    private double getTotalRequest(BenchAnalize input){
        if(input == null)
            return -1;
        double reqsum = 0;
        
        if(input.TRequest == null)
            return 0;
        
        for(double d : input.TRequest)
            reqsum += d;
        return reqsum;
    }
    
    /**
     * 
     * @param Ht 
     */
    private void AddHTtrend(double Ht) {
        THarray.add(Ht);
    }
    
    private void AccumRequestNn(BenchAnalize Ht, BenchAnalize Rt) {
        double Hdata = getTotalRequest(Ht);
        double Rdata = getTotalRequest(Rt);
        accumReqeustNn += Rdata - Hdata;
    }
    
    /**
     * get Trend Ht (T-2,T-1,T)
     * @param Ht
     * @return 
     */
    private double calibrateHTrend(LimitedQueue<Double> array) {
        double res = 0;
        double size = array.size();
        
        int cnt = 0;
        double ret = 0;
        for(int i=0;i<size;i++){
            ret = array.get(i);
            if(ret > 0)
                cnt ++;
            res += ret;
        }
        
        return res == 0 || cnt == 0 ? 0 : res/cnt;
    }

    private double getQueueWegiht(ThreadPoolExecutor recvpool) {
        double RecvHandleQueueSize = recvpool.getQueue().size();
        double RecvHandlemaxQueue = MinTConfig.NETWORK_RECEIVE_WAITING_QUEUE;
        double queueP = RecvHandlemaxQueue == 0 ? 0 : RecvHandleQueueSize / RecvHandlemaxQueue;
        double accdata = AccumQW + queueP;
        int N = (int)(accdata / 1);
        AccumQW = accdata - (double)N;
//        System.out.println("Qsize: "+RecvHandleQueueSize+", queueP: "+queueP+", accdata: "+accdata+", N: "+N+", AccumQW: "+AccumQW);
        return N;
    }

    private int setMAXThread(LimitedQueue<Double> THarr, int max_thread) {
        int arrsize = THarr.size() - 1;
        double thn = 0;
        double n = 0;
        double n_1 = 0;
        if(arrsize > 0){
            n = THarr.get(arrsize);
            n_1 = THarr.get(arrsize-1);
            thn = n - n_1;
        }
//        System.out.println("N: "+n+", N-1: "+n_1+", THN: "+thn+", MAX_TH: "+MAX_TH);
        if(thn < 0 && n > MAX_TH){
            MAX_TH = n;
            return max_thread + 1;
        }else
            return max_thread;
    }

    private boolean isDecreaseTrend(LimitedQueue<Double> list) {
        boolean isplus = false;
        for(double d : list){
            if(d >= 0)
                isplus = true;
        }
        return !isplus;
    }
}
