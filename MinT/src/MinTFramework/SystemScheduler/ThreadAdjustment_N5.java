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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread Pool Adjustment
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ThreadAdjustment_N5 implements Runnable{
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
    public ThreadAdjustment_N5() {
        bench = frame.getBenchmark();
        pools = bench.getPoolsInfo();
        THarray = new LimitedQueue(TRequestSize);
        arrayRequestNn = new LimitedQueue(3);
        recvpool = frame.getSysteScheduler().getThreadPool(MinTthreadPools.NET_RECV_HANDLE);
    }
    
    @Override
    public void run() {
        try{
            System.out.println("Start Thread Adjustment");
            while(!Thread.currentThread().isInterrupted()){
                Thread.sleep(500);
                
                AdjustRecvHandler();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
    }

    private void AdjustRecvHandler() {
        BenchAnalize Ht = pools.get(MinTthreadPools.NET_RECV_HANDLE.toString());
        BenchAnalize St = pools.get(UDP.UDP_Thread_Pools.UDP_SENDER.toString());
        BenchAnalize Rt = pools.get(UDP.UDP_Thread_Pools.UDP_RECV_LISTENER.toString());
        
        
        
        if(Ht != null){
            THt = calibrateHTrend(Ht,THarray);
        }
        if(Ht != null && St != null && Rt != null){
            double Hdata = getData(Ht);
            double Sdata = getData(St);
            double Rdata = getData(Rt);
            
            AddHTtrend(Hdata);
            AccumRequestNn(Hdata, Rdata);
            
            //Queue를 이용한 처리
            double queueP = getQueueWegiht(recvpool);
            double result = THt == 0 ? 0 : (Rdata - Hdata) / THt;
            arrayRequestNn.add(result);
//            result = result >= 0 ? Math.round(result) : result;
            System.out.println("originR: "+result+", RL: "+arrayRequestNn);
            if(result >= 0)
                result = Math.round(result);
            else if(isDecreaseTrend(arrayRequestNn)){
                result = result;
            }else
                result = 0;
            
            double nextN = result + queueP;
            int nN =  (int)nextN + PrevN;
            
            //not working, decrease number of Thread
            if(Hdata == 0)
                nN = nN - 1;
            
            MAX_THREAD = setMAXThread(THt, MAX_THREAD);
            
            if(nN < 1)
                Nt = 1;
            else if(nN > MAX_THREAD)
                Nt = MAX_THREAD;
            else
                Nt = nN;
            
            //지속적으로 Number of Thread가 올라가는데도 성능이 증가하지 않으면 멈춤
            
            PrevN = Nt;
            System.out.printf("queueP: %.2f",queueP);
            System.out.println("");
            System.out.printf("result: %.2f, TH: %.2f, MTHt:%.2f, MT: %d, Next N: %.2f, nN: %d, NT: %d",result,THt,MAX_TH,MAX_THREAD,nextN,nN, Nt);
            System.out.println("");
//            System.out.println("result: "+result+", TH : "+THt+" NExt N : "+nextN+", NT: "+ Nt);
            System.out.println("UDP_RECV_LISTNER is not Null() - "+Rdata);//+"("+Rt.ReqperSec.size()+"), Time: "+getTime(Rt)+", Time: "+getRequest(Rt));
            System.out.println("NET_RECV_HANDLE is not Null() - "+Hdata);//+"("+Ht.ReqperSec.size()+"), Time: "+getTime(Ht)+", Time: "+getRequest(Ht));
//            System.out.println("UDP_SEND is not Null() - "+Sdata);
            frame.getSysteScheduler().setPoolsize(MinTthreadPools.NET_RECV_HANDLE, Nt);
            Rt.clearBuffer();
            Ht.clearBuffer();
            St.clearBuffer();
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
    
    private double getRequest(BenchAnalize input){
        if(input == null)
            return -1;        
        int size = input.TRequest.size() - 1;
        if(size < 0)
            return 0;
        return input.TRequest.get(size);
    }
    
    /**
     * 
     * @param Ht 
     */
    private void AddHTtrend(double Ht) {
        THarray.add(Ht);
    }
    
    private void AccumRequestNn(double Ht, double Rt) {
        accumReqeustNn += Rt - Ht;
    }
    
    /**
     * get Current Ht
     * @param Ht
     * @return 
     */
    private double calibrateHTrend(BenchAnalize Ht, LimitedQueue<Double> array) {
        double cht = getData(Ht);
        
        if(cht > 0)
            return cht;
        else{
            double res = 0;
            double size = array.size();

            for(int i=0;i<size;i++){
                double t = array.get(i);
                if(t > 0)
                    res = t;
            }
            return res;
        }
    }
    
    /**
     * get Trend Ht (T-2,T-1,T)
     * @param Ht
     * @return 
     */
    
    private double calibrateHTrend_OLD(LimitedQueue<Double> array) {
        double res = 0;
        double size = array.size();
        
        for(int i=0;i<size;i++)
            res += array.get(i);
        
        if(size < TRequestSize)
            return 0;
        
        return res == 0 || size == 0 ? 0 : res/size;
    }

    private double getQueueWegiht(ThreadPoolExecutor recvpool) {
        double RecvHandleQueueSize = recvpool.getQueue().size();
        double RecvHandlemaxQueue = MinTConfig.NETWORK_RECEIVE_WAITING_QUEUE;
        double queueP = RecvHandlemaxQueue == 0 ? 0 : RecvHandleQueueSize / RecvHandlemaxQueue;
        double accdata = AccumQW + queueP;
        int N = (int)(accdata / 1);
        AccumQW = accdata - (double)N;
        System.out.println("queueP: "+queueP+", accdata: "+accdata+", N: "+N+", AccumQW: "+AccumQW);
        return N;
    }

    private int setMAXThread(double THt, int max_thread) {
        if(THt > MAX_TH){
            MAX_TH = THt;
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
