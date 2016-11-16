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
package MinTFramework.Network.Resource;

import MinTFramework.Network.NetworkProfile;
import MinTFramework.Network.RecvMSG;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author soobin
 */
public class ReceiveMessage extends Request{
    RecvMSG receivemsg;
     public ReceiveMessage(String JSONString, NetworkProfile tn, RecvMSG recvMSG) {
        super(tn);
        receivemsg = recvMSG;
        setJSONtoData(JSONString);
    }
    
    private void setJSONtoData(String JSONString) {
        try{
            messageString = JSONString;
            if(!JSONString.equals("")){
                JSONParser jsonParser = receivemsg.getJSONParser();
                resObject = (JSONObject)jsonParser.parse(messageString);
                for(Object obj : resObject.keySet()){
                    String resname = (String)obj;
                    Object res = resObject.get(obj);
                    if(res == null)
                        continue;
                    addResource(MSG_ATTR.get(resname), res);
                }
////                targetRes = (String)jsonObject.get(RESOURCENAME);
//                super.setResource((String)jsonObject.get(REQUESTMETHOD));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
