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
package MinTFramework.Network.Sharing.node;

import MinTFramework.Network.Sharing.node.SpecPower.POWER_CATE;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum Platforms {
    RASPBERRYPI3_WIRELESS_POWERED(0, SpecCPU.ARM_CORTEX_A53, SpecNetwork.WIRELESS, new SpecPower(POWER_CATE.POWER, 0), 1000),
    RASPBERRYPI3_WIRED_POWERED(1, SpecCPU.ARM_CORTEX_A53, SpecNetwork.WIRED, new SpecPower(POWER_CATE.POWER, 0), 1000),
    BEAGLEBONEBLACK_WIRELESS(2, SpecCPU.ARM_CORETEX_A8, SpecNetwork.WIRELESS, new SpecPower(POWER_CATE.POWER, 0), 500),
    BEAGLEBONEBLACK_WIRED(3, SpecCPU.ARM_CORETEX_A8, SpecNetwork.WIRED, new SpecPower(POWER_CATE.POWER, 0), 500),
    INTEL_EDISON(4, SpecCPU.INTEL_ATOM_2_CORE, SpecNetwork.WIRELESS, new SpecPower(POWER_CATE.POWER, 0), 1000),
    COMPUTER(4, SpecCPU.INTEL_I7_G7, SpecNetwork.WIRED, new SpecPower(POWER_CATE.POWER, 0), 16000),
    GETCONFIG(0, SpecCPU.NONE, SpecNetwork.WIRELESS, new SpecPower(POWER_CATE.BATTERY,0), 0),
    NONE(0, SpecCPU.NONE, SpecNetwork.WIRELESS, new SpecPower(POWER_CATE.BATTERY,0), 0);

    private SpecCPU cpu = null;
    private SpecNetwork network = null;
    private SpecPower power = null;
    private double Storage_Amount = 0;
    private int src;

    Platforms(int _src, SpecCPU _cpu, SpecNetwork _net, SpecPower _power, double storage) {
        cpu = _cpu;
        network = _net;
        power = _power;
        Storage_Amount = storage;
        src = _src;
    }
    public SpecCPU getCPU() {return cpu;}
    public SpecNetwork getNetwork() {return network;}
    public SpecPower getPower() {return power;}
    public double getStorageAmount(){return Storage_Amount;}
    public int getSrc() {return src;}

    public static Platforms getPowerbyValue(int src) {
        for (Platforms pc : Platforms.values()) {
            if (pc.src == src) {
                return pc;
            }
        }
        return Platforms.NONE;
    }

    void setStorageRemaining(double _samount) {
        Storage_Amount = _samount;
    }
}
