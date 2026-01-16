package com.utime.memoBom.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * SEED-CBC 128bit 암호화 유틸리티.
 * <p>
 * BouncyCastle Provider를 사용하여 JCE 표준 방식으로 구현됨.
 * </p>
 */
public class SeedCipherUtil {

	public static byte [] key;
	
	public static byte [] iv;
	
    /**
     * 암호화 (Encrypt)
     * @return Base64 Encoded String
     */
    public static String encrypt(String plainText) {
    	
        return SeedCipherUtil.encrypt(plainText, SeedCipherUtil.key, SeedCipherUtil.iv);
    }

    /**
     * 암호화 (Encrypt)
     * @return Base64 Encoded String
     */
    public static String encrypt(String plainText, byte [] key, byte [] iv) {
    	
    	if( plainText == null || plainText.isEmpty() ) {
    		return "";
    	}
    	
    	final byte [] message = plainText.getBytes(StandardCharsets.UTF_8);
		
		final byte [] encData = KISA_SEED_CBC.SEED_CBC_Encrypt(key, iv, message, 0, message.length);
    	
        return Base64.getEncoder().encodeToString(encData);
    }

    /**
     * 복호화 (Decrypt)
     * @param encryptedBase64Str Base64 Encoded String
     */
    public static String decrypt(String encryptedBase64Str) {
    	return SeedCipherUtil.decrypt(encryptedBase64Str, SeedCipherUtil.key, SeedCipherUtil.iv);
    }

    /**
     * 복호화 (Decrypt)
     * @param encryptedBase64Str Base64 Encoded String
     */
    public static String decrypt(String encryptedBase64Str, byte [] key, byte [] iv) {
    	final byte [] message = Base64.getDecoder().decode(encryptedBase64Str);
    	
    	final byte [] decData = KISA_SEED_CBC.SEED_CBC_Decrypt(key, iv, message, 0, message.length);
    	
        return new String(decData, StandardCharsets.UTF_8);
    }
}