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
package MinTFramework;

import MinTFramework.Network.Sharing.node.SpecNetwork;
import MinTFramework.Network.Sharing.node.SpecPower;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTConfig {
    static public final int DEFAULT_THREAD_NUM = 10;
    static public final int DEFAULT_REQEUSTQUEUE_LENGTH = 10000;
    
    //Network Adaptor
    static public final int NETWORK_RECEIVE_WAITING_QUEUE = 100000;
    static public final int NETWORK_SEND_WAITING_QUEUE = 100000;
    static public final int NETWORK_RECEIVE_POOLSIZE = 1;
    static public final int NETWORK_SEND_POOLSIZE = 1;
    
    static public boolean DebugMode = false;
    static public final int NOT_WORKING_THREAD_SERVICE_ID = -1;
    
    //for Network
    static public final int RESPONSE_ID_MAX = 120000;
    static public final int INTERNET_COAP_PORT = 5683;
    
    //for CoAP
    static public String IP_ADDRESS = "";
    static public int USER_PORT = 0;
    static public final String CoAP_MULTICAST_ADDRESS = "224.0.1.187";
    
    //for Sharing
    private String GROUP_NAME = "";
    private int TOTAL_BATTERY = 1000;
    private SpecPower nodepower = new SpecPower(SpecPower.POWER_CATE.POWER, 0);
    private SpecNetwork network = SpecNetwork.WIRED;
    
    //for Android
    static public String ANDROID_FILE_PATH = "";

    static public final String MinTConfigFilePath = "MinTConfig.cfg";
    private File configName = null;

    public MinTConfig () {
        loadConfigFile();
    }

    private void loadConfigFile() {
        try {
            File configName = new File(MinTConfigFilePath);

            if(configName != null)
                System.out.println("Load MinT Config File..."+configName.getName());
            
            BufferedReader in = new BufferedReader(new FileReader(configName));
            String s;
            
            while ((s = in.readLine()) != null) {
                String[] sp = s.split("=");
                
                //for String debug
//                System.out.print("String=["+sp.length+"] -> ( ");
//                System.out.print(s+" ),:");
//                for(String spc : sp){
//                    System.out.print(spc.trim());
//                    System.out.print(",");
//                }
//                System.out.println("");
                
                if(sp.length > 1)
                    setConfig(sp);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Config File not found");
            System.err.println(e); // 에러가 있다면 메시지 출력
        }
        
        //for Data debug
        System.out.println("Config values are set up...");
        System.out.println("IP_ADDR : "+IP_ADDRESS);
        System.out.println("USER_PORT : "+USER_PORT);
        System.out.println("GROUP_NAME : "+GROUP_NAME);
        System.out.println("TOTAL_BATTERY : "+TOTAL_BATTERY);
        System.out.println("POWER STATUS : "+nodepower.getPowerCategory()+", "+nodepower.getRemaining());
    }

    private void setConfig(String[] value) {
        ConfigFile cCfg;
        try {
            cCfg = ConfigFile.valueOf(value[0].trim());
        } catch (Exception e) {
            System.out.println("unable connect config name to '"+value[0].trim()+"'");
            cCfg = null;
        }
        
        int cnum = -1;
        if(cCfg != null)
            cnum = cCfg.num;
        try {
            switch (cnum) {
                case 0:
                    IP_ADDRESS = value[1].trim();
                    break;
                case 1:
                    USER_PORT = Integer.parseInt(value[1].trim());
                    break;
                case 2:
                    GROUP_NAME = value[1].trim();
                    break;
                case 3:
                    TOTAL_BATTERY = Integer.parseInt(value[1].trim());
                    nodepower.setRemaining(TOTAL_BATTERY);
                    break;
                case 4:
                    String p = value[1].trim();
                    if(p != null)
                        nodepower.setPowerCategory(p);
                default:
                    break;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Not read Attribute in Config File -> "+ cnum);
            if(cnum == 1)
                USER_PORT = MinTConfig.INTERNET_COAP_PORT;
        }
    }
    
    public String getIP_ADDRESS(){
        return IP_ADDRESS;
    }
    
    public String getGroupName(){
        return GROUP_NAME;
    }
    
    public int getUserPort(){
        return USER_PORT;
    }
    
    /**
     * set Virtual Battery Size
     * @return 
     */
    public int getTotalBattery(){
        return TOTAL_BATTERY;
    }
    
    /**
     * get Virtual Power Data
     * @return 
     */
    public SpecPower getSpecPower(){
        return nodepower;
    }
    
    public enum ConfigFile {
        IP_ADDR(0, "IP_ADDR"),
        PORT(1, "PORT"),
        GROUP_NAME(2, "GROUP_NAME"),
        TOTAL_BATTERY(3, "TOTAL_BATTERY"),
        POWER(4, "POWER"),
        NETWORK(5, "NETWORK_STATUS");
        
        private String name;
        private int num;
        ConfigFile(int _num,String _name){
            name = _name;
            num = _num;
        }
        
        public String toString(){
            return name;
        }
        
    }
}
