/*
 * Copyright (C) 2015 soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>, youngtak Han <gksdudxkr@gmail.com>
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

import MinTFramework.MinT;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Observation {
    private final Handler handler;
    private final MinT frame;
    
    
    public Observation(MinT frame){
        this.frame = frame;
        this.handler = frame.getNetworkHandler();
    }
    
    public void receive(String src, String fdst, String msg){
        if(isFinalDestinyHere(fdst)){
            handler.callPacketHandleRequest(src, msg, frame);
        }
        else {
            /**
             * Todo:
             * 라우팅 테이블에서 다음 목표를 받아와야함
             */
            frame.sendMessage(fdst, msg);
        }
    }
    
    public void callHandler(String src, String msg){
        handler.callPacketHandleRequest(src, msg, frame);
    }
    
    public boolean isFinalDestinyHere(String dst){
        return frame.getNodeName().equals(dst);
    }
}

