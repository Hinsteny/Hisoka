package org.hisoka.common.util.other;

import org.hisoka.common.exception.SystemException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class IPUtil {

    public static String localhost = "127.0.0.1";

    /**
     * 获取本机IP(非127.0.0.1)
     * @return
     */
    public static String getLocalIp() {
        String localIp = null;
        InetAddress inetAddress = null;
        Enumeration<NetworkInterface> allNetInterfaces = null;

        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new SystemException("GetIp error", e);
        }

        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                inetAddress = (InetAddress) addresses.nextElement();

                if (inetAddress != null && inetAddress instanceof Inet4Address) {
                    String host = inetAddress.getHostAddress();

                    if (localhost.equals(host)) {
                        continue;
                    }

                    localIp = host;
                }
            }
        }

        return localIp;
    }

    public static void main(String[] args) {
        String localIp= IPUtil.getLocalIp();
        System.out.println("本机的IP = " + localIp);
    }
}
