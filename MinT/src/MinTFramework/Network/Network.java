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

import MinTFramework.MinT;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Network {

    protected MinT frame;
    private ApplicationProtocol ap;
    private MinTApplicationPacket matchedPacket;

    abstract public void setDestination(String dst);
    abstract protected void send(byte[] packet);
    

    public Network(MinT frame) {
        this.frame = frame;
        ap = null;
    }
    public void setApplicationProtocol(ApplicationProtocol ap){
        this.ap = ap;
    }
    public void MatcherAndObservation(byte[] packet) {
        matchedPacket = ap.getApplicationPacket(packet);
        if (isFinalDestinyHere(matchedPacket.getDst())) {
            frame.getNetworkHandler().callPacketHandleRequest(matchedPacket.getSrc(), matchedPacket.getData(), frame);  
        } else {
            /**
             * Todo: 
             * 라우팅 테이블에서 다음 목표를 받아와야함
             */
            frame.sendMessage(matchedPacket.getDst(), matchedPacket.getData());
        }
    }
    public void send(String dst, String msg){
        /***
         * Todo : 
         * String dst를 라우팅 테이블에서 실제 주소로 변환할 루틴 필요
         */
        this.setDestination(dst);
        this.send(ap.makeApplicationPacket(frame.getNodeName(), dst, msg));
    }

    private boolean isFinalDestinyHere(String dst) {
        return frame.getNodeName().equals(dst);
    }
}
