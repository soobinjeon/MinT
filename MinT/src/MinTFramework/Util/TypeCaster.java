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
package MinTFramework.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author soobin
 */
public class TypeCaster {
    public static final Charset MinTCharset = Charset.forName("utf-8");
    //GZIPOutputStream을 이용하여 문자열 압축하기
    public static byte[] zipStringToBytes(String input) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
        bufferedOutputStream.write(input.getBytes());

        bufferedOutputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    //GZIPInputStream을 이용하여 byte배열 압축해제하기
    public static String unzipStringFromBytes(byte[] bytes) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(gzipInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[100];

        int length;
        while ((length = bufferedInputStream.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        bufferedInputStream.close();
        gzipInputStream.close();
        byteArrayInputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toString();
    }
    
    public static byte[] ObjectTobyte(Object value, ByteOrder order) {
        ByteBuffer buff = null;
        if(value instanceof Integer){
            buff = ByteBuffer.allocate(Integer.SIZE/8);
            buff.order(order);
            buff.putInt((Integer)value);
        }
        else if(value instanceof Double){
            buff = ByteBuffer.allocate(Double.SIZE/8);
            buff.order(order);
            buff.putDouble((Double)value);
        }
        else if(value instanceof Long){
            buff = ByteBuffer.allocate(Long.SIZE/8);
            buff.order(order);
            buff.putLong((Long)value);
        }
        else if(value instanceof Float){
            buff = ByteBuffer.allocate(Float.SIZE/8);
            buff.order(order);
            buff.putFloat((Float)value);
        }
        else if(value instanceof Short){
            buff = ByteBuffer.allocate(Short.SIZE/8);
            buff.order(order);
            buff.putShort((Short)value);
        }
        else if(value instanceof Character){
            buff = ByteBuffer.allocate(Character.SIZE/8);
            buff.order(order);
            buff.putChar((Character)value);
        }
        else if(value instanceof String){
            String strv = (String)value;
//            buff = ByteBuffer.wrap(MinTCharset.encode(strv).array());
            buff = ByteBuffer.wrap(strv.getBytes());
            buff.order(order);
        }else
            return null;

//        System.out.println("intTobyte : " + buff);
        return buff.array();
    }
    
    
    public static double byteToDouble(byte[] bytes, ByteOrder order) {

        ByteBuffer buff = ByteBuffer.allocate(bytes.length);
        buff.order(order);

        // buff사이즈는 4인 상태임
        // bytes를 put하면 position과 limit는 같은 위치가 됨.
        buff.put(bytes);
        // flip()가 실행 되면 position은 0에 위치 하게 됨.
        buff.flip();

//        System.out.println("byteToInt : " + buff);
        double result = 0;
        if(bytes.length == 2)
            result = (double)buff.getShort();
        else if(bytes.length == 4)
            result = (double)buff.getInt();
        else
            result = buff.getDouble();
        return result; // position위치(0)에서 부터 4바이트를 int로 변경하여 반환
    }
    
    public static String bytesToString(byte[] input) {
//        StringBuilder s = new StringBuilder(input.length);
//
//        for (int i = 0; i < input.length; i++) {
//            s.append((char) input[i]);
//        }
        String ret = null;
        ret = new String(input);
//        ret = MinTCharset.decode(ByteBuffer.wrap(input)).toString();
//        return s.toString();
        return ret;
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
}
