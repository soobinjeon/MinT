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

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PacketIDManager {
    private final int DEFAULT_ID = 1;
    private int id = DEFAULT_ID;
    private boolean cycled = false;
    private ConcurrentHashMap<Integer,SendMSG> idlist;
    public PacketIDManager(ConcurrentHashMap<Integer,SendMSG> IDList){
        idlist = IDList;
    }
    
    public synchronized int makePacketID(){
        if(id == Integer.MAX_VALUE){
            id = DEFAULT_ID;
            cycled = true;
        }
        
        if(!cycled)
            return id++;
        else{
            while(true){
                if(idlist.get(id) == null)
                    break;
                id++;
            }
            return id++;
        }
    }
}
