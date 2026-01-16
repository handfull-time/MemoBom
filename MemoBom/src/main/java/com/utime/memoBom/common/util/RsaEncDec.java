package com.utime.memoBom.common.util;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsaEncDec {
	
	private static final String KeyEncAlgorithm = "RSA";

	public static KeyPair generateRSAKeyPair() {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance(KeyEncAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			log.error("🔥", e);
			return null;
		}
		
        keyGen.initialize(2048, new SecureRandom());
        return keyGen.generateKeyPair();
	}
	
	public static String getPulicKey(KeyPair keyPair) {
		return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
	}
	
	public static String getPulicKeyScript(KeyPair keyPair) {
		return "-----BEGIN PUBLIC KEY-----\n" + getPulicKey(keyPair) + "\n-----END PUBLIC KEY-----";
	}

	public static String getPrivateKey(KeyPair keyPair) {
		return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
	}
	
	public static PublicKey convertPublicKey( String publicKeyBase64 ) {
		final byte [] decKey = Base64.getDecoder().decode(publicKeyBase64);
		
		KeyFactory factory;
		try {
			factory = KeyFactory.getInstance(KeyEncAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			log.error("🔥", e);
			return null;
		}
		
		try {
			return factory.generatePublic( new X509EncodedKeySpec(decKey) );
		} catch (InvalidKeySpecException e) {
			log.error("🔥", e);
			return null;
		}
	}
	
	public static PrivateKey convertPrivateKey( String privateKeyBase64 ) {
		final byte [] decKey = Base64.getDecoder().decode(privateKeyBase64);
		
		KeyFactory factory;
		try {
			factory = KeyFactory.getInstance(KeyEncAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			log.error("🔥", e);
			return null;
		}
		
		try {
			return factory.generatePrivate( new PKCS8EncodedKeySpec(decKey) );
		} catch (InvalidKeySpecException e) {
			log.error("🔥", e);
			return null;
		}
	}
	
	public static String rsaEncode( String plainText, String publicKey ) {
		
		return rsaEncode( plainText, RsaEncDec.convertPublicKey( publicKey ) );
	}
	
	public static String rsaEncode( String plainText, PublicKey publicKey ) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(KeyEncAlgorithm);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			log.error("🔥", e);
			return null;
		}
		
		byte[] encData;
		try {
			encData = cipher.doFinal(plainText.getBytes(Charset.defaultCharset()));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			log.error("🔥", e);
			return null;
		}
		
		return Base64.getEncoder().encodeToString(encData);
	}
	
	public static String rsaDecode( String encText, String privateKey ) {
		return rsaDecode( encText, RsaEncDec.convertPrivateKey(privateKey));
	}
	
	public static String rsaDecode( String encText, PrivateKey privateKey ) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(KeyEncAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			log.error("🔥", e);
			return null;
		}
		
		byte [] encBytes = Base64.getDecoder().decode(encText);
		
		byte[] encData;
		try {
			encData = cipher.doFinal(encBytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			log.error("🔥", e);
			return null;
		}
		
		return new String(encData, Charset.defaultCharset());
	}
}
