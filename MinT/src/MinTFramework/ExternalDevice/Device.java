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
package MinTFramework.ExternalDevice;

import java.util.ArrayList;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Device {

    protected ArrayList<PortPinSet> ppSet = new ArrayList<>();
    protected DeviceType dtype = null;
    private String Library_Name = null;
    
    /**
     * Initialize pin number, etc;
     */
    abstract protected void initDevice();
    abstract protected void freeDevice();
    
    /**
     * initialize device
     * @param _LibName
     * @param dtype 
     */
    public Device(String _LibName, DeviceType dtype){
        Library_Name = _LibName;
    }
    
    /**
     * Do not support in v2.02 and later
     * @deprecated 
     * @param _LibName 
     */
    public Device(String _LibName) {
        this(_LibName, DeviceType.NONE);
    }
    
    
    /**
     * Initiallize Sensor
     */
    public void initialize() {
        LoadLibrary();
        initDevice();
    }
    
    /**
     * Load Library
     *
     * @return true, Library Load Success
     */
    protected boolean LoadLibrary() {
        try {
            System.loadLibrary(Library_Name);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    protected String getLibraryName() {
        return this.Library_Name;
    }
    
    /**
     * Regist using Port, Pin number
     * - add port, pin set
    */
    public void registPortPin(int port, int pin)
    {
        ppSet.add(new PortPinSet(port, pin));
    }
    
    public void registUARTPortPin(int uartNumber)
    {
        PortPinSet uartTx = null;
        PortPinSet uartRx = null;
        switch(uartNumber)
        {
            case 1:
                uartTx = new PortPinSet(9, 24);
                uartRx = new PortPinSet(9, 26);
                break;
            case 2:
                uartTx = new PortPinSet(9, 21);
                uartRx = new PortPinSet(9, 22);
                break;
            case 4:
                uartTx = new PortPinSet(9, 13);
                uartRx = new PortPinSet(9, 11);
                break;
            case 5:
                uartTx = new PortPinSet(8, 37);
                uartRx = new PortPinSet(8, 38);
                break;
        }
        ppSet.add(uartTx);
        ppSet.add(uartRx);
    }
    public void registI2CPortPin(int i2cNumber)
    {
        PortPinSet i2c_sclA = null;
        PortPinSet i2c_sdaA = null;
        PortPinSet i2c_sclB = null;
        PortPinSet i2c_sdaB = null;
        switch(i2cNumber)
        {
            case 1:
                i2c_sclA = new PortPinSet(9, 17);
                i2c_sdaA = new PortPinSet(9, 18);
                i2c_sclB = new PortPinSet(9, 24);
                i2c_sdaB = new PortPinSet(9, 26);
                break;
            case 2:
                i2c_sclA = new PortPinSet(9, 19);
                i2c_sdaA = new PortPinSet(9, 20);
                i2c_sclB = new PortPinSet(9, 21);
                i2c_sdaB = new PortPinSet(9, 22);
                break;
        }
        ppSet.add(i2c_sclA);
        ppSet.add(i2c_sdaA);
        ppSet.add(i2c_sclB);
        ppSet.add(i2c_sdaB);
    }
    
    public void registADCPortPin(int adcNumber)
    {
        PortPinSet adcPPS = null;
        switch(adcNumber)
        {
            case 0:
                adcPPS = new PortPinSet(9, 39);
                break;
            case 1:
                adcPPS = new PortPinSet(9, 40);
                break;
            case 2:
                adcPPS = new PortPinSet(9, 37);
                break;
            case 3:
                adcPPS = new PortPinSet(9, 38);
                break;
            case 4:
                adcPPS = new PortPinSet(9, 33);
                break;
            case 5:
                adcPPS = new PortPinSet(9, 36);
                break;
            case 6:
                adcPPS = new PortPinSet(9, 35);
                break;
        }
    
        ppSet.add(adcPPS);
    }

    public ArrayList<PortPinSet> getPortPinList()
    {
        return ppSet;
    }
    
    /**
     * set Device Type
     * @param dt 
     */
    public void setDeviceType(DeviceType dt){
        dtype = dt;
    }
    
    /**
     * get Device Type
     * @return 
     */
    public DeviceType getDeviceType(){
        return dtype;
    }
}
