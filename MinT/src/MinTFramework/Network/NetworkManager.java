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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NetworkManager {
    private Network currentNetwork;
    private String nodename;
    private ApplicationProtocol ap;
    /**
     * 
     * Default Network is Null
     */
    public NetworkManager() {
        currentNetwork = null;
        ap = new MinTApplicationPacketProtocol();
    }

    public void setApplicationProtocol(ApplicationProtocol ap){
        this.ap = ap;
    }
    /**
     * *
     * Network Set
     *
     * @param network
     */
    public void setNetwork(Network network) {
        this.currentNetwork = network;
        currentNetwork.setApplicationProtocol(ap);
    }
    /**
     * *
     *
     * @param dst
     * @param msg
     */
    public void sendMsg(String dst, String msg) {
        /**
         * 
         * Todo:최종 dst를 protocol에서 찾아 중간지점을 지정하는 루틴 필요
         */
        if (currentNetwork != null) {
            currentNetwork.send(dst, msg);
        }
    }

    public void setNodeName(String name) {
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

}
