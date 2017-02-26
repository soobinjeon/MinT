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

import MinTFramework.Network.SendMSG;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PacketIDManager {
    private final short DEFAULT_ID = 1;
    private short id = DEFAULT_ID;
    private short tkn = 22;
    private boolean idcycled = false;
    private boolean tkncycled = false;
    private ConcurrentHashMap<Short,SendMSG> idlist;
    private ConcurrentHashMap<Short,SendMSG> tknlist;
    public PacketIDManager(ConcurrentHashMap<Short,SendMSG> idlist, ConcurrentHashMap<Short,SendMSG> tknlist){
        this.idlist = idlist;
        this.tknlist = tknlist;
    }
    public synchronized short makeToken(){
        if(tkn == Short.MAX_VALUE){
            tkn = DEFAULT_ID;
            tkncycled = true;
        }
        if(!idcycled)
            return tkn++;
        else{
            while(true){
                if(tknlist.get(tkn) == null)
                    break;
                tkn++;
            }
            return tkn++;
        }
    }
    public synchronized short makeMessageID(){
        if(id == Short.MAX_VALUE){
            id = DEFAULT_ID;
            idcycled = true;
        }
        if(!tkncycled)
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
