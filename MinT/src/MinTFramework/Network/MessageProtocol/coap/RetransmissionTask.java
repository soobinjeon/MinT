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
import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;
import MinTFramework.Network.SendMSG;
import java.sql.Time;
import java.time.LocalTime;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RetransmissionTask implements Runnable{
    private SendMSG msg;
    int transmissionCount;
    private MinT mint = MinT.getInstance();
    
    public RetransmissionTask(){}
    public RetransmissionTask(SendMSG msg){
        this.msg = msg;
        this.transmissionCount = 0;
    }
    
    @Override
    public void run() {
//        System.out.println("####################  Retransmission  #######################");
        transmissionCount = msg.getSendHit();
        System.err.println("#  "+Time.valueOf(LocalTime.now())+" / ID: "+msg.getMessageID()+" transmission failed! ("+transmissionCount+")");
        if(transmissionCount <= CoAPPacket.CoAPConfig.MAX_RETRANSMIT){
//            System.out.println("#   Message ID : "+ msg.getMessageID());
//            System.out.println("#   Reransmission Count : "+msg.getSendHit());
//            System.out.println("#   Current Timeout : "+msg.getCurrentTimeout()/1000.0f);
            
            
            mint.getNetworkManager().SEND(msg);
            
        } else{
            System.out.println("# "+msg.getMessageID()+" message is abandonced");
        }
//        System.out.println("#############################################################");        
//        System.out.println(".");
    }
    
}
