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
public class SpecPower {
        private POWER_CATE power = null;
        private double remaining = 0;
        
        /**
         * 
         * @param cate_src Source Code of Power Category
         * @param remaining Power remaining amount
         */
        public SpecPower(int cate_src, double _remaining){
            power = POWER_CATE.getPowerbyValue(cate_src);
            remaining = _remaining;
        }
        
        public SpecPower(int cate_src){
            this(cate_src, 0);
        }
        
        public SpecPower(POWER_CATE pc, double _remaining){
            this(pc.getSrc(), _remaining);
        }
        
        public POWER_CATE getPowerCategory(){
            return power;
        }
        
        public void setRemaining(double rem){
            remaining = rem;
        }
        
        public double getRemaining(){
            return remaining;
        }
        
        public enum POWER_CATE {
            POWER(0, 0.0),
            BATTERY(1, 0.0);

            private double value = 0;
            private int src = 0;

            POWER_CATE(int _src, double v) {
                value = v;
                src = _src;
            }

            public double getValue() {
                return value;
            }

            public int getSrc() {
                return src;
            }
            
            public static POWER_CATE getPowerbyValue(int src){
                for(POWER_CATE pc : POWER_CATE.values()){
                    if(pc.src == src)
                        return pc;
                }
                return POWER_CATE.POWER;
            }
        }
    }
