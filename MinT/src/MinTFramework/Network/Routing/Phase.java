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
package MinTFramework.Network.Routing;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.Request;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Phase implements Runnable{
    private Phase prevPhase = null;
    private Phase nextphase = null;
    
    protected MinT frame;
    protected NetworkManager networkmanager;
    protected RoutingProtocol routing;
    protected RoutingTable rtable;
    
    private boolean workingPhase = false;
    
    public Phase(RoutingProtocol _rp, Phase _prevPhase){
        routing = _rp;
        prevPhase = _prevPhase;
        if(prevPhase != null)
            prevPhase.setnextPhase(this);
        
        frame = MinT.getInstance();
        networkmanager = frame.getNetworkManager();
        rtable = routing.routingtable;
    }
    
    public void setnextPhase(Phase nphase){
        nextphase = nphase;
    }
    
    public Phase getPrevPhase(){
        return prevPhase;
    }
    
    public Phase getNextPhase(){
        return nextphase;
    }
    
    /**
     * set phase to working mode
     * @param wp 
     */
    protected void setWorkingPhase(boolean wp){
        workingPhase = wp;
    }
    
    public boolean isWorkingPhase(){
        return workingPhase;
    }
    
    public abstract boolean hasMessage(int msg);
    public abstract void requestHandle(PacketDatagram rv_packet, Request req);
    public abstract void responseHandle(PacketDatagram rv_packet, Request req);
}
