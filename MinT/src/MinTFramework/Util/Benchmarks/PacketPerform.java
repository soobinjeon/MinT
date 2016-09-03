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
 * @author soobin
 */
public class PacketPerform extends Performance{
    private int totalbytes = 0;
    private int packets = 0;
    
    public PacketPerform(String Name){
        this(Name, true);
    }
    public PacketPerform(String Name, boolean isdebug){
        super(Name, isdebug, BENCHMARK_TYPE.PACKET);
    }
    
    /**
     * for Performance copy
     * @param name
     * @param request
     * @param totaltime 
     */
    public PacketPerform(String name, double request, double time
            , int totalbytes, int packets){
        super(name, request, time);
        this.totalbytes = totalbytes;
        this.packets = packets;
    }
    
    @Override
    public void reset(){
        super.reset();
        totalbytes = 0;
        packets = 0;
    }
    
    public void endPerform(int bytesize){
        setPacketInfo(bytesize);
        endPerform();
    }
    
    private void setPacketInfo(int bytesize){
        totalbytes += bytesize;
        packets ++;
    }
    
    public double getPacketperSec(){
        return getTotalTime() == 0 ? 0 : packets / getTotalTime();
    }
    
    public double getByteperSec(){
        return getTotalTime() == 0 ? 0 : totalbytes / getTotalTime();
    }
    
    public double getBytesPerPacket(){
        return totalbytes / packets;
    }
    
    public double getTotalBytes(){
        return totalbytes;
    }
    
    public double getTotalPackets(){
        return packets;
    }
    
    /**
     * return current performance
     * @return 
     */
    @Override
    public PacketPerform getPerformance(){
        while(iscalibrating){
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        PacketPerform tp = null;
        synchronized(this){
            tp = new PacketPerform(Name, getRequest(), getTotalTime()
                    ,totalbytes, packets);
            reset();
        }
        return tp;
    }
}
