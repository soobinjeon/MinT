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
package MinTFramework.Network.Sharing.node;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum SpecNetwork {
    WIRELESS(1, 1.0),
    WIRED(2, 2.0);
    private double value = 0;
    private int src = 0;

    SpecNetwork(int _src, double v) {
        value = v;
        src = _src;
    }
    public double getValue(){return value;}
    public int getSrc() {return src;}

    public static SpecNetwork getPowerbyValue(int src) {
        for (SpecNetwork pc : SpecNetwork.values()) {
            if (pc.src == src) {
                return pc;
            }
        }
        return SpecNetwork.WIRED;
    }
}
