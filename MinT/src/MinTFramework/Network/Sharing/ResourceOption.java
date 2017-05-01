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
package MinTFramework.Network.sharing;

/**
 * Resource Option to get a resource from other nodes
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum ResourceOption {
    LIST("lst", 0),
    AVERAGE("avg", 1),
    MIN("min", 2),
    MAX("max", 3),
    DEFAULT("dt", 10);
    
    private String opt = null;
    private int optvalue = 10;
    ResourceOption(String n, int _optvalue){
        opt = n;
        optvalue = _optvalue;
    }
    
    public String toOption(){
        return opt;
    }
    
    public static ResourceOption getResourceOptionbyOpt(String n){
        for(ResourceOption ro : ResourceOption.values()){
            if(ro.toOption().equals(n))
                return ro;
        }
        return ResourceOption.DEFAULT;
    }

    public int getOptionValue() {
        return optvalue;
    }

    public boolean isLast() { return this == LIST; }
    public boolean isAverage() { return this == AVERAGE; }
    public boolean isMinimum() { return this == MIN; }
    public boolean isMaximum() { return this == MAX; }
}
