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
package MinTFramework.Network.syspacket;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTApplicationPacket {

    private String src;
    private String dst;
    private String msg;
    
    public MinTApplicationPacket() {
        src = "";
        dst = "";
        msg = "";
    }

    public MinTApplicationPacket(String src, String dst, String msg) {
        this.src = src;
        this.dst = dst;
        this.msg = msg;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }
    public void setSrc(String src) {
        this.src = src;
    }

    public void setData(String data) {
        this.msg = data;
    }

    public String getDst() {
        return dst;
    }

    public String getSrc() {
        return src;
    }

    public String getMessage() {
        return msg;
    }

//
//    public byte[] getBytes() {
//        byte[] bytes = null;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(bos);
//            oos.writeObject(this);
//            oos.flush();
//            oos.close();
//            bos.close();
//            bytes = bos.toByteArray();
//        } catch (IOException ex) {
//        }
//        return bytes;
//    }
//

}
