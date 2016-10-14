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
package MinTFramework.Network.Routing.node;

/**
 * Node's spec for elect header
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NodeSpecify {
    private SpecCPU cpu = null;
    private SpecNetwork network = null;
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
    public NodeSpecify(SpecCPU _cpu, SpecNetwork _netSRC, SpecPower _pSRC, double _samount){
        network = _netSRC;
        power = _pSRC;
        Storage_Amount = _samount;
        cpu = _cpu;
        CaculateWeight();
    }
    
    public NodeSpecify(Platforms _spec){
        this(_spec.getCPU(), _spec.getNetwork(), _spec.getPower(), _spec.getStorageAmount());
    }
    
    private void CaculateWeight(){
        Weight = ((cpu.getCORE() * cpu.getHZ()) / 1000) 
                + network.getValue()
                + (Storage_Amount * 0.5);
    }
    
    public void setPowerRemaining(double _pamount){
        power.setRemaining(_pamount);
        CaculateWeight();
    }
    
    public void setStorageRemaining(double _samount){
        Storage_Amount = _samount;
        CaculateWeight();
    }
    
    public SpecCPU getCPU(){
        return cpu;
    }
    
    public SpecNetwork getNetwork(){
        return network;
    }
    
    public SpecPower getPower(){
        return power;
    }
    
    public double getSpecWeight(){
        return Weight;
    }
}