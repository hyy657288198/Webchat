package org.yiyang.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    /**
     * Get MD5 encryption
     */
    public static String getPwd(String pwd) {
        try {
            // Create encrypted object
            MessageDigest digest = MessageDigest.getInstance("md5");

            // Call the method of the encrypted object, and the encryption action has been completed
            byte[] bs = digest.digest(pwd.getBytes());
            // Next, we need to optimize the encrypted results and follow the optimization idea of MySQL
            // 1.Convert all data to positive numbers
            String hexString = "";
            for (byte b : bs) {
                int temp = b & 255;
                // 2.Convert all data into hexadecimal form
                if (temp < 16 && temp >= 0) {
                    hexString = hexString + "0" + Integer.toHexString(temp);
                } else {
                    hexString = hexString + Integer.toHexString(temp);
                }
            }
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

}
