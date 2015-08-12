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
public abstract class Network {
    
    protected Observation observation;
    protected MinT frame;
    
    abstract public void send(String dst, String fdst, String msg);
    
    public Network(MinT frame){
        this.frame = frame;
        observation = new Observation(frame);   
    }
    public void MatcherSerializerAndObservation(byte[] packet){
        System.out.println(new String(packet));
        /**
         * Todo
         * 수신한 바이트 배열을 송신측, 메시지로 나누어 Observation 호출하기
         */
        
    }
}

