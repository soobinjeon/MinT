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
package MinTFramework.Network.Routing.MinTSharing;

import MinTFramework.Network.Profile;
import MinTFramework.Network.Request;
import MinTFramework.Network.ResponseData;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.RoutingProtocol;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.Resource;
import MinTFramework.storage.ResourceStorage;
import MinTFramework.storage.ThingProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTRoutingProtocol extends RoutingProtocol{
    DebugLog dl = new DebugLog("MinTRoutingProtocol");
    public MinTRoutingProtocol(){
        
    }
    
    public void getOberveData(Profile dts){
        frame.REQUEST_OBSERVE(dts, new ResponseHandler() {
            @Override
            public void Response(ResponseData resdata) {
                dl.printMessage(resdata.getResourceString());
                JSONObject observe = resStorage.getOberveResource(resdata.getResourceString());
                
                JSONArray jpr = (JSONArray)observe.get(ResourceStorage.RESOURCE_TYPE.property);
                for(int i=0;i<jpr.size();i++){
                    ThingProperty np = new ThingProperty(((JSONObject)jpr.get(i)).toJSONString(), Resource.StoreCategory.Network, resdata.getSourceInfo()) {
                        @Override
                        public void set(Request req) {}
                        @Override
                        public Object get(Request req) {return null;}
                    };
                    resStorage.addResource(np);
                }
                
                for(String pl :resStorage.getPropertyList()){
                    dl.printMessage("Property List : "+pl);
                }
                
                /*add instruction*/
//                JSONArray jis = (JSONArray)observe.get(ResourceStorage.RESOURCE_TYPE.instruction);
                
                
            }
        });
    }
}
