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
public enum RT_MSG {
    DIS_BROADCAST(0,0),
    HE_HEADERBROADCAST(1,0),
    RT_RTOPTION(2,0);
    
    private int phase;
    private int num;
    private final int identifier = 10;
    RT_MSG(int _phase, int _num){
        phase = _phase;
        num = _num;
    }
    
    public boolean isEqual(int _num){
        int rphase = _num / identifier;
        int rnum = _num - rphase;
        
        return rphase == phase && num == rnum;
    }

    public int getValue() {
        return phase * identifier + num;
    }
    
    private int PhaseIdentifier(int _n){
        return (_n / identifier);
    }
    
    /**
     * is Same Phase of this
     * @param msg
     * @return 
     */
    boolean isSamePhase(int msg) {
        return phase == PhaseIdentifier(msg);
    }
}
