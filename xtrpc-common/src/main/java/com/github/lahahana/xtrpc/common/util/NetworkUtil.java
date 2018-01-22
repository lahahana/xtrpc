package com.github.lahahana.xtrpc.common.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkUtil {

    public static InetAddress getLocalHostInetAddress() {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String assembleAddress(String host, int port) {
        return host + ":" + port;
    }

    public static String assembleAddress(InetSocketAddress inetSocketAddress) {
        return inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort();
    }

    public static Tuple<String, Integer> assembleHostPortPair(String address) {
        String[] hostPortPair = address.split(":");
        if (hostPortPair.length == 2) {
            return new Tuple<>(hostPortPair[0], Integer.parseInt(hostPortPair[1]));
        } else {
            throw new IllegalArgumentException("invalid socket address format");
        }
    }
}
