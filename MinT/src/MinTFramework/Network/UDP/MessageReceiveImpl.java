/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MinTFramework.Network.UDP;
import java.net.DatagramPacket;


public interface MessageReceiveImpl{
    public void messageReceive(DatagramPacket packet);
}
