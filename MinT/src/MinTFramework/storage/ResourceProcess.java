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
package MinTFramework.storage;

import MinTFramework.Network.Resource.Request;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResourceProcess {
    protected Resource res;
    protected Request req;
    protected ResourceThreadHandle rth = null;
    protected boolean isRunning = true;
    
    public ResourceProcess(Resource res, Request req, ResourceThreadHandle rth) {
        this.res = res;
        this.req = req;
        this.rth = rth;
    }

    public boolean isRunning() {
        return isRunning;
    }
    
    public Resource getResource(){
        return res;
    }
    
    protected void processGet() {
        Object result = res.get(req);
        if(result != null){
            res.data.setResource(result);
        }
    }
}
