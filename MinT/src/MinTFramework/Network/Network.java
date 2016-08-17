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
package MinTFramework.Network;

import MinTFramework.Exception.*;
import MinTFramework.MinT;
import MinTFramework.MinTConfig;
import MinTFramework.Network.Routing.RoutingProtocol;
import MinTFramework.SystemScheduler.Scheduler;
import MinTFramework.SystemScheduler.Service;
import MinTFramework.Util.ByteBufferPool;
import MinTFramework.Util.DebugLog;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Network {
    protected MinT frame;
    protected NetworkManager networkmanager;
    protected NetworkProfile profile;
    protected ByteBufferPool byteBufferPool;
    private RoutingProtocol routing;
    
    //Network Pools
    private Scheduler networkAdaptorPool;
    
    private boolean isworking = true;
    
    private DebugLog ndl = new DebugLog("Network");
    /***
     * set destination of packet
     * @param dst 
     */
    abstract protected void setDestination(NetworkProfile dst);
    /***
     * send packet
     * @param packet 
     */
    abstract protected void sendProtocol(PacketDatagram packet);

    abstract protected void interrupt();
    /**
     * *
     * Constructor
     *
     * @param frame MinT Framework Object
     */
    public Network(MinT frame, NetworkManager nm, NetworkProfile npro, RoutingProtocol _routing) {
        this.frame = frame;
        this.networkmanager = nm;
        
        networkAdaptorPool = networkmanager.getNetworkAdaptorPool();
        byteBufferPool = networkmanager.getByteBufferPool();
        
        routing = _routing;
        profile = npro;
        
        ndl.printMessage("Set Network listener");
    }
    
    /**
     * use in NetworkManager
     * Turn On Network
     */
    public void TurnOnNetwork(){
        ndl.printMessage(this.getClass().getName()+" - started");
    }
    /***
     * 
     * Setting RoutingProtocol 
 !!! Not Network Procotol !!! 
 Default: "MinTApplicationProtocol"
     *
     * @param ap RoutingProtocol
     */
    public void setApplicationProtocol(RoutingProtocol routing) {
        this.routing = routing;
    }
    
    /**
     * call Receive Handler after Receiving data 
     * @param packet 
     */
    public synchronized void putReceiveHandler(byte[] packet){
        this.networkAdaptorPool.putService(new NetworkRecvAdaptor_OLD(packet, this));
    }

    /**
     * *
     * send message to dst
     *
     * @param packet
     * @throws MinTFramework.Exception.NetworkException
     */
    public synchronized void send(PacketDatagram packet) throws NetworkException{
        if(!isWorking())
            throw new NetworkException(NetworkException.NE.NetworkNotWorking);
        else{
            this.setDestination(packet.getNextNode());
            this.sendProtocol(packet);
        }
    }
    
    protected void isWorking(boolean is){
        this.isworking = is;
    }
    
    protected boolean isWorking(){
        return isworking;
    }
    
    public NetworkProfile getProfile(){
        return profile;
    }
    
    public NetworkType getNetworkType(){
        return profile.getNetworkType();
    }
    
    public NetworkManager getNetworkManager(){
        return networkmanager;
    }
}
