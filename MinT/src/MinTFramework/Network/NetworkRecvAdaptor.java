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
import MinTFramework.Schedule.Service;
import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin
 */
public class NetworkRecvAdaptor extends Service{
    Network parentNetwork;
    NetworkManager parentNetworkManager;
    SystemHandler systemHandle;
    byte[] recvPacket;
    Profile currentProfile;
    SystemHandler syshandle;
    DebugLog dl = new DebugLog("NetworkReceiver");
    
    public NetworkRecvAdaptor(byte[] recvPacket, Network pn){
        super(pn.frame);
        this.recvPacket = recvPacket;
        parentNetwork = pn;
        parentNetworkManager = parentNetwork.getNetworkManager();
        currentProfile = parentNetwork.getProfile();
        syshandle = new SystemHandler(frame);
    }
    
    @Override
    public void execute() {
        MatcherAndObservation(recvPacket);
    }
    
    /**
     * *
     * Shared Method for Matcher
     * should lock for mutex
     * Check Destination of packet if destination is here, call NetworkHandler
     * but, forwarding
     *
     * @param packet
     */
    private void MatcherAndObservation(byte[] packet) {
//        dl.printMessage(new String(packet));
        PacketProtocol matchedPacket = new PacketProtocol(currentProfile, packet);
        dl.printMessage(matchedPacket.getPacketString());
        if (isFinalDestiny(matchedPacket.getDestinationNode())) {
            dl.printMessage("is Final?");
            syshandle.startHandle(matchedPacket);
            this.parentNetworkManager.setHandlerCount();

        } else { //If stopover, through to stopover method in networkmanager
            stopOver(matchedPacket);
        }
    }
    
    /**
     * Check whether destination is here
     *
     * @param dst
     * @return
     */
    private boolean isFinalDestiny(Profile dst) {
        return currentProfile.equals(dst);
    }
    
    /**
     * Stop Over Processor
     *
     * @param packet
     */
    public void stopOver(PacketProtocol packet) {

    }
}
