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

import MinTFramework.Util.PlatformInfo;

/**
 * Node's spec for elect header
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NodeSpecify {
//    private SpecCPU cpu = null;
//    private SpecNetwork network = null;
    private SpecPower power = null;
    private double Storage_Amount = 0;
    private double Weight = 0;
    
    /**
     * Node Specify
     * @param _netSRC SpecNetwork type, Network Spec
     * @param _pSRC SpecPower type, Battery Power Spec
     * @param _pamount Battery Power Amount
     * @param _samount Storage Amount
     */
    public NodeSpecify(SpecPower _pSRC, double _samount){
        if(_pSRC != null)
            power = _pSRC;
        else
            power = new SpecPower(PlatformInfo.getPowerCategory(), PlatformInfo.getRemainingBaterry());
                    
        if(_samount == 0)
            Storage_Amount = PlatformInfo.getCurrentDiskUsableSpace();
        else
            Storage_Amount = _samount;
        
        Storage_Amount = Storage_Amount == 0 ? 0 : Storage_Amount / 1000; //convert to GB
        CaculateWeight();
    }
    
    public NodeSpecify(Platforms _spec){
        this(_spec.getPower(), _spec.getStorageAmount());
    }
    
    /**
     * set only Weight
     * @param _weight 
     */
    public NodeSpecify(double _weight){
        power = new SpecPower(SpecPower.POWER_CATE.BATTERY, 0);
        Weight = _weight;
    }
    
    /**
     * Calculate Header Election Weight
     */
    private void CaculateWeight(){
        
        double vi = (power.getInitialPower() - power.getRemaining()) / 1;
        double pi = (power.getPowerCategory().getValue() + vi) / Storage_Amount;
        System.out.println("powerv: "+power.getPowerCategory().getValue());
        System.out.println("curMem: "+Storage_Amount+", vi: "+vi+", pi: "+pi);
//        Weight = ((cpu.getCORE() * cpu.getHZ()) / 1000) 
//                + network.getValue()
//                + (Storage_Amount * 0.5);
        Weight = pi;
    }
    
    public void setPowerRemaining(double _pamount){
        power.setRemaining(_pamount);
        CaculateWeight();
    }
    
    public void setStorageRemaining(double _samount){
        Storage_Amount = _samount;
        CaculateWeight();
    }
    
    public double getPowerRemaining(){
        return power.getRemaining();
    }
    
    /*public SpecCPU getCPU(){
        return cpu;
    }
    
    public SpecNetwork getNetwork(){
        return network;
    }*/
    
    public SpecPower getPower(){
        return power;
    }
    
    public double getSpecWeight(){
        return Weight;
    }
}