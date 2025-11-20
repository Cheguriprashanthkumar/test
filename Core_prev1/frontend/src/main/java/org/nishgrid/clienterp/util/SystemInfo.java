package org.nishgrid.clienterp.util;

import java.net.InetAddress;

public class SystemInfo {

    public static String getSystemId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
