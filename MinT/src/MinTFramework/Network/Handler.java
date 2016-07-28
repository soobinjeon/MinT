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
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.ResourceStorage;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Handler extends Service{
    protected MinT frame;
    protected PacketProtocol recv_packet;
    protected ResourceStorage resStorage;
    DebugLog dl = new DebugLog("Handler");
    public Handler(MinT _frame){
        super(_frame);
        this.frame = _frame;
        resStorage = this.frame.getResStorage();
    }
    /***
     * Call Packet Handler: Do not supprot upper v2.03
     * @param src packet source
     * @param msg pakcet message
     * @param frame MinTFramework
     */
    @Deprecated
    public void callPacketHandleService(String src, String msg, MinT frame){};
    
    /***
     * Call Packet Handler:: Do not support upper v2.03
     * You should use UserHandler(String src, String msg);
     * @param src packet source
     * @param msg pakcet message
     */
    @Deprecated
    public void callPacketHandleService(String src, String msg){};
    
    /**
     * Handler for User service
     * @param src
     * @param msg 
     */
    abstract public void userHandler(Profile src, String msg);
    
    /**
     * call Handler
     * @param packet
     */
    protected void callhadler(PacketProtocol packet){
        recv_packet = packet;
    }
    
    /**
     * System Handler can handle navigator for 
     * discovering searched sensor nodes (?) <- would need to routing protocol,
     * information searching <- need to storages,
     * ,and so on
     * @param src
     * @param msg 
     */
    private void SystemHandler(Profile src, String msg){
        /**
         * get, post using resource storage
         */
        dl.printMessage("Processing SystemHandler");
        Request req = new Request("Device2", 0, src);
        
        //if msg = get
        if(msg.equals("get")){
            dl.printMessage("set get");
            resStorage.getProperty(req);
        }
        else if(msg.equals("observe")){
            dl.printMessage("set Observe");
            dl.printMessage(resStorage.OberveLocalResource().toJSONString());
            frame.sendDirectMessage(src, resStorage.OberveLocalResource().toJSONString());
        }
        //if msg = set
        
        //if observing
        
//        Object obj = resStorage.getProperty(new Request("Device2", 0));
//        dl.printMessage("result : "+(double)obj);
    }
    
    @Override
    public void execute(){
        SystemHandler(recv_packet.getSource(), recv_packet.getMsgData());
        userHandler(recv_packet.getSource(),recv_packet.getMsgData());
    }
}