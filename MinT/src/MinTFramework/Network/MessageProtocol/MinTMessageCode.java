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
package MinTFramework.Network.MessageProtocol;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum MinTMessageCode {
        EMPTY(0, 0),
        //Request
        GET(0, 1),
        POST(0, 2),
        PUT(0, 3),
        DELETE(0, 4),
        
        DISCOVERY(0, 5),    //DISCOVERY??
        
        //Response
            //Success
        CREATED(2, 1),
        DELETED(2, 2),
        VALID(2, 3),
        CHANGED(2, 4),
        CONTENT(2, 5),
        CONTINUE(2, 31);

        int code;
        int classCode;
        int detailCode;
        
        MinTMessageCode(int classCode, int detailCode){
            this.classCode = classCode;
            this.detailCode = detailCode;
            code = classCode << 5 | detailCode;
        }
        
        public int getCode(){
            return code;
        }
        
        public int getClassCode(){
            return classCode;
        }
        
        public int getDetailCode(){
            return detailCode;
        }
         
        public static MinTMessageCode getHeaderCode(int code){
            for(MinTMessageCode h : MinTMessageCode.values()){
                if(h.getCode() == code)
                    return h;
            }
            return null;
        }
        
        public static MinTMessageCode getHeaderCode(int classCode, int detailCode){
            for(MinTMessageCode h : MinTMessageCode.values()){
                if((h.getClassCode() == classCode) && (h.getDetailCode() == detailCode))
                    return h;
            }
            return null;
        }
                
        public boolean isRequest() {return classCode == 0;}
        public boolean isResponse() {return classCode != 0;}
        
        public boolean isGet() {return this == GET;}
        public boolean isPost() {return this == POST;}
        public boolean isPut() {return this == PUT;}
        public boolean isDelete() {return this == DELETE;}

        public boolean isCreated() {return this == CREATED;}
        public boolean isDeleted() {return this == DELETED;}
        public boolean isValid() {return this == VALID;}
        public boolean isChanged() {return this == CHANGED;}
        public boolean isContent() {return this == CONTENT;}
        public boolean isContinue() {return this == CONTINUE;}
    }
