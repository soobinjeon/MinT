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

import MinTFramework.MinT;
import MinTFramework.Network.Request;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class InstructionManager extends ResourceManager implements ResourceManagerHandle{
    public InstructionManager(MinT _frame, ResourceStorage rs){
        super(_frame,rs);
        initHandler(this);
    }
    
    @Override
    public void set(Request req, Resource res) {
        setDevice(req, res);
    }

    @Override
    public Object get(Request req, Resource res) {
        return null;
    }

    @Override
    public void put(Request req, Resource res) {
    }

    /**
     * Add Instruction to storage
     * @param is 
     */
    public void addInstruction(ThingInstruction is){
        RS.addResource(is);
    }

    /**
     * operate the thing from request code
     * @param req
     * @param res 
     */
    private void setDevice(Request req, Resource res) {
        ResourceThread rt = new ResourceThread(frame,res,req);
        frame.putService(rt);
    }
}
