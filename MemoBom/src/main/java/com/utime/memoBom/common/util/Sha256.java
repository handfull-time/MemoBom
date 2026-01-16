package com.utime.memoBom.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {
	
	public static String encrypt(String text)  {
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		
        md.update(text.getBytes());

        return Sha256.bytesToHex(md.digest());
    }

    private static String bytesToHex(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
