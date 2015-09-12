/*
 * Copyright (C) 2015 HanYoungTak
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
package MinTFramework;

/**
 *
 * @author HanYoungTak
 */
public enum DeviceType {
    TEMPERATURE("TEMPERATURE"), 
    HUMIDITY("HUMIDITY"), 
    TEMPHUMI("TEMPHUMI"), 
    ULTRASONIC("ULTRASONIC"), 
    MAGNETIC("MAGNETIC"), 
    GYRO("GYRO"), 
    SERVMOTOR("SERVMOTOR"), 
    DUST("DUST"),
    CO2("CO2"),
    
    BLE("BLE"),
    WIFI("WIFI"), 
    NFC("NFC"), 
    BLUETOOTH("BLUETOOTH");
    
    private String deviceType;
    
    private DeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public String getDeviceType()
    {
        return deviceType;
    }
}
