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
import MinTFramework.Schedule.Scheduler;
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
    protected Scheduler networkScheduler;
    protected Profile profile;
    private RoutingProtocol routing;
    private boolean isworking = true;
    
    private DebugLog dl = new DebugLog("Network",false);
    /***
     * set destination of packet
     * @param dst 
     */
    abstract protected void setDestination(Profile dst);
    /***
     * send packet
     * @param packet 
     */
    abstract protected void sendProtocol(PacketProtocol packet);

    /**
     * *
     * Constructor
     *
     * @param frame MinT Framework Object
     */
    public Network(MinT frame, NetworkManager nm, Profile npro, RoutingProtocol _routing) {
        this.frame = frame;
        this.networkmanager = nm;
        networkScheduler = networkmanager.getNetworkScheduler();
        routing = _routing;
        profile = npro;
        if(!MinTConfig.IP_ADDRESS.equals("")){
            profile.setAddress(MinTConfig.IP_ADDRESS);
        }
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
    public void ReceiveHandler(byte[] packet){
//        dl.printMessage("Call NetworkReceiver");
        this.networkScheduler.putService(new NetworkReceiver(packet, this));
    }

    /**
     * *
     * send message to dst
     *
     * @param packet
     * @throws MinTFramework.Exception.NetworkException
     */
    public synchronized void send(PacketProtocol packet) throws NetworkException{
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
    
    public Profile getProfile(){
        return profile;
    }
    
    public NetworkType getNetworkType(){
        return profile.getNetworkType();
    }
    
    public NetworkManager getNetworkManager(){
        return networkmanager;
    }
}
