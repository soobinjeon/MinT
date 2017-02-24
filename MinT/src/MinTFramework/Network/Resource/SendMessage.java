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

import MinTFramework.Network.MessageProtocol.MinTMessageCode;
import MinTFramework.storage.datamap.Information;
import org.json.simple.JSONObject;

/**
 *
 * @author soobin
 */
public class SendMessage extends Request{
    protected MinTMessageCode messagecode = null;

    /**
     * set Request for Sender
     * @param res Resource Name for requesting
     * @param _getResource Request Method
     */
    public SendMessage(String resName, Object _getResource){
        super(resName, _getResource, null);
        setMessagetoJSON();
    }
    
    public SendMessage(){
        super(null);
    }
    
    private void setMessagetoJSON(){
        resObject = new JSONObject();
        for(MSG_ATTR key : this.resources.keySet()){
            resObject.put(key.getName(), resources.get(key).getResource());
        }
        messageString = resObject.toJSONString();
    }
    
    public SendMessage AddAttribute(MSG_ATTR attr, Object value){
        resources.put(attr, new Information(value));
        setMessagetoJSON();
        return this;
    }
    
    public SendMessage addResponseCode(MinTMessageCode minTMessageCode) {
        messagecode = minTMessageCode;
        return this;
    }
    
    public MinTMessageCode getMessageCode(){
        return messagecode;
    }
}
