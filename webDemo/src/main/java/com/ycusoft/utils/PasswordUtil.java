package com.ycusoft.utils;

import java.security.MessageDigest;

public class PasswordUtil {
    public static String encrypt(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(pwd.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte value : b) {
                sb.append(String.format("%02x", value));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}