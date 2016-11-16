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
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.SystemScheduler.MinTthreadPools;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResourceManager {
    final protected ResourceStorage RS;
    final protected MinT frame;
    final private SystemScheduler sysSched;
//    private ResourceManagerHandle rhandle;
    /**
     * 
     * @param _frame
     * @param rs 
     */
    public ResourceManager(){
        frame = MinT.getInstance();
        sysSched = frame.getSystemScheduler();
        RS = frame.getResStorage();
    }

    protected void initHandler(ResourceManagerHandle handle) {
        if(this instanceof PropertyManager)
            RS.setPropertyHandler(handle);
        else if(this instanceof InstructionManager)
            RS.setInstructionHandler(handle);
    }
    
    /**
     * Execute Resource Thread
     * @param res 
     */
    protected void executeResource(Runnable res){
        sysSched.executeProcess(MinTthreadPools.RESOURCE, res);
    }
    
    /**
     * Callback Resource Thread
     * @param res
     * @return 
     */
    protected Future<Object> submitResource(Callable res){
        return sysSched.submitProcess(MinTthreadPools.RESOURCE, res);
    }
}
