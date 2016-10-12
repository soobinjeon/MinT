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
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.storage.ResourceStorage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RoutingProtocol implements Runnable{
    protected MinT frame;
    protected NetworkManager networkManager;
    protected ResourceStorage resStorage;
    protected RoutHandler rhandle;
    protected RoutingTable routingtable;
    protected ConcurrentHashMap<Integer, Phase> phases;
    protected SystemScheduler sysSched;
    protected String groupName = "group";
    
    public static String ROUTING_PHASE_POOL = "Routing PHase Pool";
    
    public RoutingProtocol(){
        frame = MinT.getInstance();
        resStorage = frame.getResStorage();
        sysSched = frame.getSysteScheduler();
        routingtable = new RoutingTable();
        phases = new ConcurrentHashMap<>();
        registPhaseScheduler();
        if(resStorage == null)
            System.out.println("res Storage null");
    }
    
    
    /**
     * init Routing Protocol after starting a MinT
     * @param aThis 
     */
    public void init(NetworkManager aThis) {
        networkManager = aThis;
        initPhase();
        rhandle = new RoutHandler(this);
    }
    
    /**
     * set Phase
     */
    private void initPhase() {
        phases.put(0, new PhaseDiscover(this, null));
        phases.put(1, new PhaseHeaderElection(this, phases.get(0)));
        phases.put(2, new ExecuteRouting(this, phases.get(1)));
    }
    
    public void setCurrentRoutingGroup(String _name){
        groupName = _name;
    }
    
    public String getCurrentRoutingGroup(){
        return groupName;
    }

    @Override
    public void run() {
        System.out.println("Running Router!");
        int i = 0;
        sysSched.executeProcess(ROUTING_PHASE_POOL, phases.get(i));
    }

    public void routingHandle(PacketDatagram rv_packet) {
        rhandle.receiveHandle(rv_packet);
    }

    private void registPhaseScheduler() {
        if(sysSched != null)
        sysSched.registerThreadPool(ROUTING_PHASE_POOL
                , Executors.newSingleThreadExecutor());
    }

}
