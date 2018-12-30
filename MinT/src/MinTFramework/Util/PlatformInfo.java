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
package MinTFramework.Util;

import MinTFramework.Network.Sharing.node.SpecPower;
import java.io.File;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PlatformInfo {
    /**
     * get Usage of Heap Mem
     * @return SIZE (MB)
     */
    public static double getUsageMemory(){
        long total = Runtime.getRuntime().totalMemory();
        return (double) total / (1024 * 1024);
    }
    
    /**
     * get Free of Heap Mem
     * @return SIZE (MB)
     */
    public static double getFreeMemory(){
        long free = Runtime.getRuntime().freeMemory();
        return (double) free / (1024 * 1024);
    }
    
    /**
     * get Max of Heap Mem
     * @return SIZE (MB)
     */
    public static double getMaxMemory(){
        long max = Runtime.getRuntime().maxMemory();
        return (double) max / (1024 * 1024);
    }
    
    /**
     * get Disk Lists
     * @return 
     */
    public static File[] getDisks(){
        File[] roots = File.listRoots();
        return roots;
    }
    
    /**
     * get Current Disk Size
     * @return MB
     */
    public static double getCurrentDiskTotalSpace(){
        File cdisk = new File(".");
        double size = cdisk.getTotalSpace() / Math.pow(1024, 2);
        return size;
    }
    
    public static double getCurrentDiskUsableSpace(){
        File cdisk = new File(".");
        double size = cdisk.getUsableSpace()/ Math.pow(1024, 2);
        return size;
    }
    
    public static double getCurrentDiskFreeSpace(){
        File cdisk = new File(".");
        double size = cdisk.getFreeSpace()/ Math.pow(1024, 2);
        return size;
    }
    
    /**
     * Temporary Option
     * @return 
     */
    public static double getRemainingBaterry(){
        return 0;
    }
    
    /**
     * Temporary Option
     * @return 
     */
    public static SpecPower.POWER_CATE getPowerCategory(){
        return SpecPower.POWER_CATE.POWER;
    }
}
