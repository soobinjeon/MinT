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
package MinTFramework.Network;

import MinTFramework.MinTConfig;

/**
 *
 * @author HanYoungTak
 */
public enum NetworkType {
    BLE("BLE",null),
    UDP("UDP",MinTConfig.INTERNET_TCPUDP_PORT),
    TCPIP("TCP/IP",MinTConfig.INTERNET_TCPUDP_PORT),
    COAP("COAP",MinTConfig.INTERNET_COAP_PORT),
    NFC("NFC",null), 
    BLUETOOTH("BLUETOOTH",null);
    
    private String networkType;
    private Integer port;
    
    private NetworkType(String networkType, Integer port)
    {
        this.networkType = networkType;
        this.port = port;
    }
    
    public String getDeviceType()
    {
        return networkType;
    }
    
    /**
     * Caution!!! Port number is set up to constant value for Network.
     * UDP사용 시 포트가 여러개면, 민티 초기 설정 떄 연결 가능 포트 리스트를 저장해주고 해당 포트를 모두 검색하는 것도 좋은 방법인거 같음? (쓸데없는 것일수도)
     * @param port 
     */
    public void setPort(Integer port){
        this.port = port;
    }
    
    /**
     * get Port
     * @return 
     */
    public Integer getPort(){
        return port;
    }
}
