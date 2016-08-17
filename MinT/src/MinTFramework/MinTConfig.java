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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTConfig {
    static public final int DEFAULT_THREAD_NUM = 100;
    static public final int DEFAULT_REQEUSTQUEUE_LENGTH = 100000;
    
    static public final int NETWORK_WAITING_QUEUE = 100000;
    static public final int NETWORK_THREADPOOL_NUM = 10;
    
    //UDP
    static public final int UDP_NUM_OF_LISTENER_THREADS = 1;
    
    static public boolean DebugMode = false;
    static public final int NOT_WORKING_THREAD_SERVICE_ID = -1;
    
    //for Network
    static public final int RESPONSE_ID_MAX = 120000;
    static public final int INTERNET_TCPUDP_PORT = 6513;
    static public final int INTERNET_COAP_PORT = 6514;
    static public String IP_ADDRESS = "";
}
