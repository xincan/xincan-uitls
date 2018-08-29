package com.xincan.utils.md5;

import java.security.MessageDigest;

/**
 * md5加密工具类
 */
public class MD5Util {

    /**
     * md5加密（大写）
     * @param mac
     * @return
     */
    public static String md5(String mac){
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(mac.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            mac = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mac;
    }

    /**
     * md5加密（大写）
     * @param mac
     * @return
     */
    public static String md5toUpperCase32(String mac){
        return md5(mac).toUpperCase();
    }

    /**
     * md5加密（小写）
     * @param mac
     * @return
     */
    public static String md5toLowerCase32(String mac){
        return md5(mac).toLowerCase();
    }
}
