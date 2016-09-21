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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class OSUtil {

    /**
     * Linux Shell Command example) OSUtil.linuxShellCommand("ls -al");
     *
     * @param cmd
     */
    public static void linuxShellCommand(String cmd) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(OSUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getLinuxIPAddress() {
        String hostAddr = "";

        try {
            Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();
            while (nienum.hasMoreElements()) {
                NetworkInterface ni = nienum.nextElement();
                Enumeration<InetAddress> kk = ni.getInetAddresses();
                while (kk.hasMoreElements()) {
                    InetAddress inetAddress = kk.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {
                        hostAddr = inetAddress.getHostAddress();
                    }
                }
            }
            
            if(hostAddr == null || hostAddr.equals("")){
                hostAddr = "127.0.0.1";
            }
            
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostAddr;
    }

    private static String getWindowsIPAddress() {
        InetAddress ip = null;
        /**
         * * Real 로 시작하는 어댑터의 IP할당 try { for (Enumeration<NetworkInterface> en =
         * NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
         * NetworkInterface intf = en.nextElement(); if
         * (intf.getDisplayName().startsWith("Real")) { for
         * (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
         * enumIpAddr.hasMoreElements();) { InetAddress inetAddress =
         * enumIpAddr.nextElement(); return
         * inetAddress.getHostAddress().toString(); } } } } catch
         * (SocketException ex) { } System.err.println("There is no REALTEK
         * adapter");
         */
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return ip != null ? ip.getHostAddress() : null;
    }

    public static String getIPAddress() {
        if (OSValidator.isWindows()) {
            return getWindowsIPAddress();
        } else if (OSValidator.isMac()) {
            return getLinuxIPAddress();
        } else if (OSValidator.isUnix()) {
            return getLinuxIPAddress();
        } else if (OSValidator.isSolaris()) {
            return getLinuxIPAddress();
        } else {
            return getLinuxIPAddress();
        }
    }
    
    public static void busySleep(long nanos) {
        long elapsed;
        final long startTime = System.nanoTime();
        do {
            elapsed = System.nanoTime() - startTime;
        } while (elapsed < nanos);
    }
}
