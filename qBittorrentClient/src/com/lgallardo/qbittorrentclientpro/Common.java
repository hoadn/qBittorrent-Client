package com.lgallardo.qbittorrentclientpro;

/**
 * Created by lgallard on 1/30/15.
 */
public class Common {

    public static String calculateSize(String value){

        long bytes = Long.parseLong(value);

        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp-1) +  "i";

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

    }


}
