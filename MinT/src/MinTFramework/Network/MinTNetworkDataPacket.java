/*
 * Copyright (C) 2015 soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>, youngtak Han <gksdudxkr@gmail.com>
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
package MinTFramework.Network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTNetworkDataPacket implements Serializable {

    private String src;
    private String dst;
    private int srcport;
    private int dstport;
    private String data;

    public MinTNetworkDataPacket() {
        src = "";
        dst = "";
        data = "";
    }

    public MinTNetworkDataPacket(String data, String dst, int dstport) {
        this.dst = dst;
        this.data = data;
        this.dstport = dstport;

    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDestPort(int port) {
        this.dstport = port;
    }

    public void setSrcPort(int port) {
        this.srcport = port;
    }

    public String getDst() {
        return dst;
    }

    public String getSrc() {
        return src;
    }

    public String getData() {
        return data;
    }

    public int getDestPort() {
        return dstport;
    }

    public int getSrcPort() {
        return srcport;
    }

    public byte[] getBytes() {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray();
        } catch (IOException ex) {
        }
        return bytes;
    }


}
