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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author soobin
 */
public class TypeCaster {
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
}
