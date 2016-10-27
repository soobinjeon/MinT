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
package MinTFramework.Network.sharing.routingprotocol;

import MinTFramework.MinT;
import MinTFramework.Network.NetworkManager;
import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.PacketDatagram;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Network.Resource.SendMessage;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.sharing.node.CurrentNode;
import MinTFramework.Network.sharing.node.Node;
import MinTFramework.Network.sharing.node.Platforms;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.storage.ResourceStorage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    protected ConcurrentHashMap<ROUTING_PHASE, Phase> phases;
    protected SystemScheduler sysSched;
    
    //Group Name
    protected String groupName = "group";
    
    //Current Node Specify
    protected CurrentNode currentNode = null;
    
    //is this header node?
    protected boolean isHeaderNode = false;
    
    public static String ROUTING_PHASE_POOL = "Routing PHase Pool";
    
    private boolean isActiveRouting = false;
    
    public RoutingProtocol(){
        frame = MinT.getInstance();
        resStorage = frame.getResStorage();
        sysSched = frame.getSystemScheduler();
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
        phases.put(ROUTING_PHASE.DISCOVER, new PhaseDiscover(this, null));
        phases.put(ROUTING_PHASE.HEADERELECTION, new PhaseHeaderElection(this, phases.get(0)));
        phases.put(ROUTING_PHASE.EXECUTEROUTING, new ExecuteRouting(this, phases.get(1)));
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
    
    public boolean isHeaderNode(){
        return isHeaderNode;
    }
    
    public void setRoutingProtocol(String _groupName, Platforms platforms){
        setRoutingProtocol(_groupName);
        currentNode = new CurrentNode(platforms, groupName);
        isActiveRouting = true;
    }
    
    public Node getHeaderNode(){
        return routingtable.getHeaderNode();
    }

    @Override
    public void run() {
        try {
            if(isActiveRouting){
                System.out.println("Running Router!");
                System.out.println("--- Current Node("+groupName+")-"+currentNode.toString());
                ExecutePhase();
            }
        }catch(InterruptedException e){
            System.out.println("Routring Protocol- Thread Intrrupt Exception");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void routingHandle(PacketDatagram rv_packet, ReceiveMessage recvmsg) {
        rhandle.receiveHandle(rv_packet, recvmsg);
    }

    private void registPhaseScheduler() {
        if(sysSched != null)
        sysSched.registerThreadPool(ROUTING_PHASE_POOL
                , Executors.newSingleThreadExecutor());
    }

    private void TurnOnPhase(ROUTING_PHASE rp) throws InterruptedException {
        if(rp.getPhaseNum()>0)
            phases.get(ROUTING_PHASE.getPHASEbyNum(rp.getPhaseNum()-1)).inturrupt();
        
//waiting for completing a previous phase
        Future<Object> getable = sysSched.submitProcess(ROUTING_PHASE_POOL, phases.get(rp));
        
        try {
            //return data
            boolean recvData = (Boolean)getable.get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }
    
    protected Phase getPhasebyName(ROUTING_PHASE rp){
        return phases.get(rp);
    }

    /**
     * 
     * @throws InterruptedException 
     */
    private void ExecutePhase() throws InterruptedException {
        for(int i=0;i<phases.size();i++){
            //새로운 노드가 추가되면 시간 계속 연장 해야함
            //노드가 추가되지 않을 시 디스커버리 모드 계속 작동
            //기본 시간 증가해야함
            TurnOnPhase(ROUTING_PHASE.getPHASEbyNum(i));
        }
    }
    
    public void getAllClientsResourceInfo(){
        for(Node n : routingtable.getRoutingTable().values()){
            getClientResourceInfo(n);
        }
    }
    
    public void getClientResourceInfo(String addr){
        Node n = routingtable.getNodebyAddress(addr);
        if(n != null)
            getClientResourceInfo(n);
    }
    
    public void getClientResourceInfo(final Node n){
        //Response
        ResponseHandler nres = new ResponseHandler() {
            @Override
            public void Response(ResponseData resdata) {
                n.setResources();
            }
        };
        //send Each Nodes
        frame.REQUEST_GET(n.gettoAddr(), new SendMessage().AddAttribute(Request.MSG_ATTR.WellKnown, null), nres);
    }

    /**
     * has Child Node
     * @param source
     * @return 
     */
    public boolean hasChildNode(NetworkProfile source) {
        Node n = routingtable.getChildNodebyAddress(source.getAddress());
        if(n == null)
            return false;
        else
            return true;
    }
    
    /**
     * Routing Phase ENUM
     */
    public static enum ROUTING_PHASE {
        DISCOVER (0), HEADERELECTION (1), EXECUTEROUTING (2);
        
        private int phasenum = 0;
        ROUTING_PHASE(int num){
            phasenum = num;
        }
        
        public static ROUTING_PHASE getPHASEbyNum(int num){
            for(ROUTING_PHASE rp : ROUTING_PHASE.values()){
                if(rp.phasenum == num)
                    return rp;
            }
            
            return null;
        }
        
        public int getPhaseNum(){
            return phasenum;
        }
    }
}
