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
import MinTFramework.Exception.NetworkException;
import MinTFramework.Network.*;
import MinTFramework.Network.BLE.BLE;
import MinTFramework.Network.UDP.UDP;
import MinTFramework.Util.DebugLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class NetworkManager {

    private MinT frame = null;
    private ArrayList<NetworkType> networkList = new ArrayList<NetworkType>();
    private HashMap<NetworkType, Network> networks = new HashMap<NetworkType, Network>();
    private String NodeName = null;

    private Handler networkHandler = null;
    private RoutingProtocol routing;
    private DebugLog dl = new DebugLog("NetworkManager", false);

    /**
     * Auto Set Network Manager as possible
     *
     * @param frame
     */
    public NetworkManager(MinT frame) {
        this.frame = frame;
        routing = new RoutingProtocol();
        networkHandler = new Handler(frame) {
            @Override
            public void userHandler(Profile src, String msg) {
            }
        };

        setNodeName();
    }

    /**
     * add network
     *
     * @param ntype
     */
    public void AddNetwork(NetworkType ntype) {
        networkList.add(ntype);
    }

    /**
     * Turn on All Networks!
     */
    public void TurnOnNetwork() {
        for (NetworkType ty : networkList) {
            setOnNetwork(ty);
        }
    }

    /**
     * *
     * Set Up the networks Available Networks : UDP, BLE, COAP(asap)
     *
     * @param ntype type of Network NetworkType
     * @param port Internet port for (UDP,TCP/IP,COAP), null for others
     */
    public void setOnNetwork(NetworkType ntype) {
        if (ntype == NetworkType.UDP) {
            networks.put(ntype, new UDP(ntype.getPort(), routing, this.frame, this));
            dl.printMessage("Turned on UDP: " + ntype.getPort());
        } else if (ntype == NetworkType.BLE) {
            networks.put(ntype, new BLE(routing, frame, this));
            dl.printMessage("Turned on BLE");
        } else if (ntype == NetworkType.COAP) { // for CoAP, need to add
            dl.printMessage("Turned on COAP");
        }
    }

    public void setRoutingProtocol(RoutingProtocol ap) {
        this.routing = ap;

        for (Network n : networks.values()) {
            n.setApplicationProtocol(ap);
        }
    }

    /**
     * send Direct Message
     *
     * @param dst
     * @param msg
     */
    public void sendDirectMessage(Profile dst, String msg) {
        Profile fdst = null;
        if (dst.isNameProfile()) {
            //라우팅 스토어에서 검색
            fdst = dst;
        } else {
            fdst = dst;
        }
        sendMsg(new PacketProtocol(null, null, getNextNode(fdst), fdst, msg));
    }

    /**
     * Stop Over Processor
     *
     * @param packet
     */
    public void stopOver(PacketProtocol packet) {

    }

    /**
     * Routing Protocol
     *
     * @param fdst
     * @return
     */
    private Profile getNextNode(Profile fdst) {
        //Serch Routing Protocol
        return fdst;
    }

    /**
     * *
     * 얘는 프로토콜에서 목적지의 프로토콜에 따라 보내는 네트워크를 선택
     *
     * @param dst
     * @param msg
     */
    private void sendMsg(PacketProtocol packet) {
        NetworkType nnodetype = packet.getNextNode().getNetworkType();
        Network sendNetwork = networks.get(nnodetype);

        //set Source Node
        if (packet.getSource() == null) {
            packet.setSource(sendNetwork.getProfile());
        }

        //set Previous Node
        if (packet.getPreviosNode() == null) {
            packet.setPrevNode(sendNetwork.getProfile());
        }

        //Send Message
        if (sendNetwork != null) {
            try {
                dl.printMessage("Send Network" + sendNetwork.getNetworkType());
                dl.printMessage("Packet :" + packet.getPacketString());
                sendNetwork.send(packet);
            } catch (NetworkException ex) {
                ex.printStackTrace();
            }
        } else {
            dl.printMessage("Error : There are no Networks");
            System.out.println("Error : There are no Networks");
        }
    }

    /**
     * set Network Handler in network manager
     *
     * @param nhandler
     */
    public void setNetworkHandler(Handler nhandler) {
        if (nhandler != null) {
            this.networkHandler = nhandler;
        }
    }

    /**
     * get Network Handler
     *
     * @return
     */
    public Handler getNetworkHandler() {
        return this.networkHandler;
    }

    /**
     * set Node Name
     *
     * @param name
     */
    public void setNodeName(String name) {
        if (name != null) {
            this.NodeName = name;
        }
    }

    /**
     * Return node name
     *
     * @return
     */
    public String getNodeName() {
        return NodeName;
    }

    /**
     * set automatically Node Name
     */
    private void setNodeName() {
        NodeName = "temporary Node";
    }
}
