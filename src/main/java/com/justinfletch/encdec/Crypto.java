package com.justinfletch.encdec;

import java.security.Key;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;

public class Crypto {

	public static String encrypt(Key key, String data) throws Exception {
		JsonWebEncryption jwe = new JsonWebEncryption();
		jwe.setPayload(data);
		jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
		jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
		jwe.setKey(key);
		String serializedJwe = jwe.getCompactSerialization();
		return serializedJwe;
	}
	
	
	public static String decrypt(Key key, String data) throws Exception {
		JsonWebEncryption jwe = new JsonWebEncryption();
		jwe = new JsonWebEncryption();
		jwe.setAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, 
		       KeyManagementAlgorithmIdentifiers.A128KW));
		jwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, 
		       ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
		jwe.setKey(key);
		jwe.setCompactSerialization(data);
		return jwe.getPayload();
	}
	
}
