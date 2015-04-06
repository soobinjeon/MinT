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
package SnSDK;

import SnSDK.ExternalDevice.Device;
import SnSDK.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Request implements RequestImpl {

    protected final Device device;
    private final int id;
    private final int prior;

    public Request() {
        device = null;
        id = 0;
        prior = 0;

    }

    public Request(Device device, int id) {
        this.device = device;
        this.id = id;
        this.prior = 0;
    }

    public Request(Device device, int id, int prior) {
        this.device = device;
        this.id = id;
        this.prior = prior;
    }

    public int getPrior() {
        return this.prior;
    }

    public int getID() {
        return this.id;
    }

    @Override
    public void execute() {
        System.out.println(Thread.currentThread().getName() + " executes " + this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
}
