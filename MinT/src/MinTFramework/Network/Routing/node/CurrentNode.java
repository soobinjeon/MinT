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

import MinTFramework.Network.NetworkProfile;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class CurrentNode extends Node{
    private Platforms platforms;
    private NodeSpecify nodeSpec;
    public CurrentNode(NetworkProfile _toAddr, NetworkProfile _nextAddr, boolean Hd, Platforms ns, String _gn) {
        super(_toAddr, _nextAddr, Hd, new NodeSpecify(ns).getSpecWeight(), _gn);
        platforms = ns;
        nodeSpec = new NodeSpecify(ns);
    }
    
    public CurrentNode(Platforms ns, String _gn){
        super(null, null, false, new NodeSpecify(ns).getSpecWeight(), _gn);
        platforms = ns;
        nodeSpec = new NodeSpecify(ns);
    }
    
    public NodeSpecify getNodeSpecify(){
        return nodeSpec;
    }
    
    public Platforms getNodePlatforms(){
        return platforms;
    }
    
    @Override
    public String toString(){
        return platforms.getCPU().toString()+", "+platforms.getNetwork().toString()
                +", "+platforms.getPower().getPowerCategory().toString()+"("+platforms.getPower().getRemaining()+")"
                +", wt: "+super.getSpecWeight();
    }
}
