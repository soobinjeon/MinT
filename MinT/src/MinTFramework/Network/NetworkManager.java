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

import MinTFramework.*;
import MinTFramework.Network.*;
import MinTFramework.Network.UDP.UDP;
import MinTFramework.Network.syspacket.MinTApplicationPacketProtocol;
import MinTFramework.Util.DebugLog;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NetworkManager {
    private MinT frame = null;
    private ArrayList<NetworkType> networkList = new ArrayList<NetworkType>();
    private HashMap<NetworkType,Network> networks = new HashMap<NetworkType,Network>();
    private String nodename = null;
    
    private Handler networkHandler = null;
    private ApplicationProtocol ap;
    private DebugLog dl = new DebugLog("NetworkManager");
    
    /**
     * Auto Set Network Manager as possible
     * @param frame
     */
    public NetworkManager(MinT frame) {
        this.frame = frame;
        ap = new MinTApplicationPacketProtocol();
        networkHandler = new Handler(frame) {
            @Override
            public void userHandler(String src, String msg) {
            }
        };
        
        setNodeName();
    }

    /**
     * add network
     * @param ntype 
     */
    public void AddNetwork(NetworkType ntype){
        networkList.add(ntype);
    }
    
    /**
     * Turn on All Networks!
     */
    public void TurnOnNetwork(){
        for(NetworkType ty : networkList){
            setOnNetwork(ty);
        }
    }
    /**
     * *
     * Set Up the networks
     * Available Networks : UDP, BLE, COAP(asap)
     * @param ntype type of Network NetworkType
     * @param port Internet port for (UDP,TCP/IP,COAP), null for others
     */
    public void setOnNetwork(NetworkType ntype) {
        if(ntype == NetworkType.UDP){
            networks.put(ntype, new UDP(ntype.getPort(),ap,this.frame));
            dl.printMessage("Turned on UDP: "+ntype.getPort());
        }
        else if(ntype == NetworkType.BLE){
//            networks.put(ntype, new BLE())
            dl.printMessage("Turned on BLE");
        }
        else if(ntype == NetworkType.COAP){
            dl.printMessage("Turned on COAP");
        }
    }
    
    public void setApplicationProtocol(ApplicationProtocol ap){
        this.ap = ap;
        
        for(Network n:networks.values()){
            n.setApplicationProtocol(ap);
        }
    }
    
    /**
     * *
     *
     * @param dst
     * @param msg
     */
    public void sendMsg(String dst, String msg) {
        /**
         * UDP만 지윈
         * Todo:최종 dst를 protocol에서 찾아 중간지점을 지정하는 루틴 필요
         */
        Network cn = networks.get(NetworkType.UDP);
        if (cn != null) {
            cn.send(dst, msg);
        }else{
            dl.printMessage("Error : There are no Networks");
            System.out.println("Error : There are no Networks");
        }
    }

    /**
     * set Network Handler in network manager
     * @param nhandler 
     */
    public void setNetworkHandler(Handler nhandler){
        if(nhandler != null)
            this.networkHandler = nhandler;
    }
    
    /**
     * get Network Handler
     * @return 
     */
    public Handler getNetworkHandler(){
        return this.networkHandler;
    }
    
    /**
     * set Node Name
     * @param name 
     */
    public void setNodeName(String name) {
        if(name != null)
            this.nodename = name;
    }

    /**
     * Return node name
     *
     * @return
     */
    public String getNodeName() {
        return nodename;
    }

    /**
     * set automatically Node Name
     */
    private void setNodeName() {
        nodename = "node1";
    }

}
