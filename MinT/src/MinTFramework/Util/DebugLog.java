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
package MinTFramework.Util;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class DebugLog {

    private final String Logname;

    public DebugLog() {
        Logname = "NONAME";
    }

    /**
     * DebugLoger name
     *
     * @param name DebugLog name
     */
    public DebugLog(String name) {
        Logname = name;
    }

    /**
     * 콘솔에 메시지 출력
     *
     * @param str 출력할 메시지
     */
    public void printMessage(String str) {
        System.out.println(Logname + ": " + str);
    }
}
