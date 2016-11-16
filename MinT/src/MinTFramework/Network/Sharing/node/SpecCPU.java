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
package MinTFramework.Network.sharing.node;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum SpecCPU {
    ARM_CORTEX_A53(0, 1200, 4),
    ARM_CORETEX_A8(1, 1000, 1),
    INTEL_ATOM_2_CORE(2, 500, 2),
    INTEL_I7_G7(3, 3200, 4),
    NONE(100,0,0);
    
    private int hz = 0;
    private int core = 0;
    private int src = 0;
    SpecCPU(int _src, int _hz, int _core){
        hz = _hz;
        core = _core;
        src = _src;
    }
    
    public int getHZ(){return hz;}
    public int getCORE(){return core;}
    
    public int getSrc() {return src;}

    public static SpecCPU getPowerbyValue(int src) {
        for (SpecCPU pc : SpecCPU.values()) {
            if (pc.src == src) {
                return pc;
            }
        }
        return SpecCPU.NONE;
    }
}
