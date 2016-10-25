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
package MinTFramework.Network.Routing;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class DiscoverRole {
    private boolean isNewDiscover = false;
    private int timetable = 0;
    private int Default_Time_sec = 10; //sec
    
    public DiscoverRole(int dtime){
        timetable = 0;
        Default_Time_sec = dtime;
    }
    
    /**
     * added new Node
     */
    public void addedNewNode(){
        isNewDiscover = true;
    }
    
    /**
     * Check New Node Identifying
     * @return 
     */
    private boolean checkNewNode(){
        boolean nstatus = isNewDiscover;
        if(isNewDiscover){
            isNewDiscover = false;
            timetable = 0;
        }
        return nstatus;
    }
    
    public void interrupt(){
        timetable = Default_Time_sec;
    }
    
    /**
     * return discovery time role
     * if new node is added, timetable will be reset
     * @return 
     */
    public boolean doDiscoveryTimeRole(){
        int ctime = timetable;
        
        if(!checkNewNode())
            timetable ++;
        System.out.println("--------------------timeTable: "+timetable);
        if(ctime < Default_Time_sec)
            return true;
        else
            return false;
    }
}
