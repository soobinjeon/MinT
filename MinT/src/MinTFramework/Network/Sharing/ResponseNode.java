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
package MinTFramework.Network.sharing;

import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Network.ResponseHandler;
import MinTFramework.storage.ResData;
import MinTFramework.storage.ThingProperty;

/**
 * Response Handler Class for waiting a Resource Response
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ResponseNode implements ResponseHandler{
    private ResponseWaiter resWaiter;
    private ThingProperty SendedResource;
    public ResponseNode(ResponseWaiter cw, ThingProperty _sendedResource){
        resWaiter = cw;
        SendedResource = _sendedResource;
    }

    @Override
    public void Response(ResponseData resdata) {
        String recvAddr = resdata.getSourceInfo().getAddress();
        String curAddr = SendedResource.getSourceProfile().getAddress();
        ThingProperty tp = SendedResource;
//        System.out.println("---ResponseData: RevAddr"+recvAddr+", "+resdata.getPacketProtocol().getMsgData());
//        System.out.println("------ResponseData: CurAddr"+curAddr);
        /**
         * 멀티캐스트 일 때!! 같은 핸들러로 모든 리스폰스를 받기 때문에
         * 각 핸들러에 데이터를 매칭 시켜줘야함
         */ 
        if(recvAddr != null && curAddr != null && !recvAddr.equals(curAddr)){
            for(SharingPacket sp: resWaiter.getPackets()){
                if(sp.getProperty().getSourceProfile().getAddress()
                        .equals(recvAddr)){
//                    System.out.println("----Find recvAddr");
                    tp = sp.getProperty();
                }
            }
        }
        ResData resd = new ResData(resdata.getResource(), tp);
        //update current resource
        tp.put(resdata);
        //activating response event
        resWaiter.responsed(resd);
    }
    
}
