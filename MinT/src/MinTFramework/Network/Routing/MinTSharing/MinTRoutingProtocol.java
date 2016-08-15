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
import MinTFramework.Network.ResponseData;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.Network.Routing.RoutingProtocol;
import MinTFramework.Util.DebugLog;
import MinTFramework.storage.Resource;
import MinTFramework.storage.ResourceStorage;
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
    
    public void getDISCOVERY(Profile dts){
        frame.DISCOVERY(dts, new ResponseHandler() {
            @Override
            public void Response(ResponseData resdata) {
                JSONObject discovery = resStorage.getDiscoveryResource(resdata.getResourceString());
                JSONArray jpr = (JSONArray)discovery.get(ResourceStorage.RESOURCE_TYPE.property.toString());
                for(int i=0;i<jpr.size();i++){
                    resStorage.addNetworkResource(ResourceStorage.RESOURCE_TYPE.property, (JSONObject)jpr.get(i), resdata);
                }
                
                for(Resource pl :resStorage.getProperties()){
                    dl.printMessage("PL : "+pl.getName()+", "+pl.getStorageDirectory().getSourceLocation());
                }
                
                /*add instruction*/
//                JSONArray jis = (JSONArray)discovery.get(ResourceStorage.RESOURCE_TYPE.instruction);
                
                
            }
        });
    }
}
