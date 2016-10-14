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
import MinTFramework.Network.Routing.node.CurrentNode;
import MinTFramework.Network.Routing.node.Platforms;
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
    
    //Group Name
    protected String groupName = "group";
    
    //Current Node Specify
    protected CurrentNode currentNode = null;
    
    //is this header node?
    protected boolean isHeaderNode = false;
    
    public static String ROUTING_PHASE_POOL = "Routing PHase Pool";
    
    public RoutingProtocol(){
        frame = MinT.getInstance();
        resStorage = frame.getResStorage();
        sysSched = frame.getSysteScheduler();
        routingtable = new RoutingTable();
        phases = new ConcurrentHashMap<>();
        if(currentNode == null)
            currentNode = new CurrentNode(null, null, false, Platforms.NONE, groupName);
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
    
    public void setRoutingProtocol(String _name){
        groupName = _name;
    }
    
    public String getCurrentRoutingGroup(){
        return groupName;
    }
    
    public CurrentNode getCurrentNode(){
        return currentNode;
    }
    
    protected void setHeaderNode(boolean setheader){
        isHeaderNode = setheader;
    }
    
    protected boolean isHeaderNode(){
        return isHeaderNode;
    }
    
    public void setRoutingProtocol(String _groupName, Platforms platforms){
        setRoutingProtocol(_groupName);
        currentNode = new CurrentNode(platforms, groupName);
    }

    @Override
    public void run() {
        try {
            System.out.println("Running Router!");
            System.out.println("--- Current Node("+groupName+")-"+currentNode.toString());
            ExecutePhase();
        }catch(InterruptedException e){
            System.out.println("Routring Protocol- Thread Intrrupt Exception");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void routingHandle(PacketDatagram rv_packet) {
        rhandle.receiveHandle(rv_packet);
    }

    private void registPhaseScheduler() {
        if(sysSched != null)
        sysSched.registerThreadPool(ROUTING_PHASE_POOL
                , Executors.newSingleThreadExecutor());
    }

    private void TurnOnPhase(int i) throws InterruptedException {
        if(i>0)
            phases.get(i-1).inturrupt();
        sysSched.executeProcess(ROUTING_PHASE_POOL, phases.get(i));
    }

    private void ExecutePhase() throws InterruptedException {
        int i = 0;
        TurnOnPhase(i++);
        Thread.sleep(10000);
        TurnOnPhase(i++);
        Thread.sleep(10000);
        TurnOnPhase(i++);
    }

}
