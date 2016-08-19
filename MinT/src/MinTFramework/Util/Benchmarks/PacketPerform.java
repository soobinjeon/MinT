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
public class PacketPerform {
    private int totalbytes = 0;
    private int packets = 0;
    private long stime = 0;
    private long etime = 0;
    private long totaltime = 0;
    private double time = 0;
    public PacketPerform(){
        
    }
    
    public void startPerform(){
        totalbytes = 0;
        packets = 0;
        stime = System.currentTimeMillis();
    }
    
    public void endPerform(){
        etime = System.currentTimeMillis();
        time = (double)((etime-stime)/1000.0);
    }
    
    public synchronized void setPacketInfo(int bytesize){
        totalbytes += bytesize;
        packets ++;
    }
    
    public synchronized void setPacketInfo(int bytesize, long time){
        setPacketInfo(bytesize);
        this.totaltime += time;
        this.time = (double)(totaltime / 1000.0);
    }
    
    public double getTotalTime(){
        return totaltime / 1000.0;
    }
    
    public double getPacketperSec(){
        return packets / time;
    }
    
    public double getByteperSec(){
        return totalbytes / time;
    }
    
    public double getBytesPerPacket(){
        return totalbytes / packets;
    }
}
