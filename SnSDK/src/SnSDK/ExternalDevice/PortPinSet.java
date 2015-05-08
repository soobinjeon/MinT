/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnSDK.ExternalDevice;

/**
 *
 * @author HanYoungTak
 */
public class PortPinSet {
    private final int port;
    private final int pin;
    
    public PortPinSet(int port, int pin)
    {
        this.port = port;
        this.pin = pin;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public int getPin()
    {
        return pin;
    }
}
