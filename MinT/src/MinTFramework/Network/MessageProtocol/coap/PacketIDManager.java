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
package MinTFramework.Network.MessageProtocol.coap;

import MinTFramework.MinT;
import MinTFramework.Network.SendMSG;
import MinTFramework.SystemScheduler.MinTthreadPools;
import MinTFramework.SystemScheduler.SystemScheduler;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PacketIDManager {
    private final short DEFAULT_ID = 0;
    private short id = DEFAULT_ID;
    private short tkn = DEFAULT_ID;
    private boolean idcycled = false;
    private boolean tkncycled = false;
    private final ConcurrentHashMap<String,SendMSG> idlist;
    private final ConcurrentHashMap<String,SendMSG> tknlist;
    private final ConcurrentHashMap<String,Short> idlength;
    private Random rand;
    SystemScheduler scheduler;
    
    public PacketIDManager(ConcurrentHashMap<String,SendMSG> nodeidlist, ConcurrentHashMap<String,SendMSG> tknlist){
        this.idlist = nodeidlist;
        this.tknlist = tknlist;
        this.scheduler = MinT.getInstance().getSystemScheduler();
        this.idlength = new ConcurrentHashMap<>();
        rand = new Random(CoAPPacket.CoAPConfig.RANDOM_SEED);
    }
    
    /***
     * Get randomized token
     * @param dst
     * @return 
     */
//    public synchronized short makeToken(String dst, boolean isMulticast) {
    public synchronized short makeToken(SendMSG sendmsg) {
        String key;
        
        do {
            tkn = (short) rand.nextInt(Short.MAX_VALUE);
//            key = dst+"#"+tkn;
            key = CoAPManager.getListKey(tkn, sendmsg);
        } while (tknlist.containsKey(key));
        if(sendmsg.isUDPMulticastMode()){
//            System.out.println("MulticastToken Generated!: " + dst + " Make Token : " + tkn);
            scheduler.submitSchedule(MinTthreadPools.PACKETLIFETIME_HANDLE, new TokenRemoveTask(key), CoAPPacket.CoAPConfig.NON_LIFETIME * 1000);
        } else {
//            System.out.println("Get Destination " + dst + " Make Token : " + tkn);
            scheduler.submitSchedule(MinTthreadPools.PACKETLIFETIME_HANDLE, new TokenRemoveTask(key), CoAPPacket.CoAPConfig.MAX_TRANSMIT_WAIT * 1000);
        }
        
        return tkn;
    }
    
    public synchronized short makeMessageID(SendMSG sendmsg) {
        idcycled = false;
        String key;
        String dst = CoAPManager.getDestinationKey(sendmsg);
        if(idlength.containsKey(dst)){
            id = idlength.get(dst);
        } else {
            id = DEFAULT_ID;
        }
        
        if(id == Short.MAX_VALUE){
            id = DEFAULT_ID;
            idcycled = true;
        }

        id++;
        key = CoAPManager.getListKey(id, sendmsg);
        
        if(!idcycled){ 
            //return id;
        } else {
            while(true){
                if(idlist.get(key) == null)
                    break;
                id++;
//                key = dst+"#"+id;
                key = CoAPManager.getListKey(id, sendmsg);
            }
            //return id++;
        }
        idlength.put(dst, id);
        
        if (sendmsg.getHeader_Type().isCON()) {
//            System.out.println("CON Message generated: Destination: " + dst + " / message ID: " + id);
            scheduler.submitSchedule(MinTthreadPools.PACKETLIFETIME_HANDLE, new MessageIDRemoveTask(key), CoAPPacket.CoAPConfig.EXCHANGE_LIFETIME * 1000);
        } else if(sendmsg.getHeader_Type().isNON()){
//            System.out.println("NON Message generated: Destination: " + dst + " / message ID: " + id);
            scheduler.submitSchedule(MinTthreadPools.PACKETLIFETIME_HANDLE, new MessageIDRemoveTask(key), CoAPPacket.CoAPConfig.NON_LIFETIME * 1000);
        }
        
        return id;
    }
    /***
     * Runnable task for remove message id
     */
    public class MessageIDRemoveTask implements Runnable{
        private String msgid;
        
        public MessageIDRemoveTask(String msgid){
            this.msgid = msgid;
        }
        
        @Override
        public void run() {
            if(idlist.containsKey(msgid)){
//                System.out.println("Remove message ID : "+msgid);
                idlist.remove(msgid);
            }
        }
    }
    
    public class TokenRemoveTask implements Runnable{
        private String tkn;
        
        public TokenRemoveTask(String tkn){
            this.tkn = tkn;
        }
        
        @Override
        public void run() {
            if(tknlist.containsKey(tkn)){
//                System.out.println("Remove token :"+tkn);
                tknlist.remove(tkn);
            }
        }
    }
}
