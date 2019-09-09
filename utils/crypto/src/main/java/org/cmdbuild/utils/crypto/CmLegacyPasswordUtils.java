package org.cmdbuild.utils.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


/**
 * This is the legacy password encrypter/decrypter that has always been used by
 * CMDBuild. The author is not known. One day we hope that it will be changed,
 * at least to support a variable salt.
 *
 */
public class CmLegacyPasswordUtils {

	public static String encrypt(String value) {
		return new Aux().encrypt(value);
	}

	public static String decrypt(String value) {
		return new Aux().decrypt(value);
	}

	private static class Aux {

		private static final byte[] salt = {(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x34, (byte) 0xE3, (byte) 0x03};
		private static final int uznig = 0x19d15ea;
		private static final int iterationCount = 19;
		private final Cipher ecipher;
		private final Cipher dcipher;

		private Aux() {
			try {
				String pPh = Integer.toString(uznig);
				KeySpec keySpec = new PBEKeySpec(pPh.toCharArray(), salt, iterationCount);
				SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
				AlgorithmParameterSpec cypherParameters = new PBEParameterSpec(salt, iterationCount);

				ecipher = Cipher.getInstance(key.getAlgorithm());
				ecipher.init(Cipher.ENCRYPT_MODE, key, cypherParameters);

				dcipher = Cipher.getInstance(key.getAlgorithm());
				dcipher.init(Cipher.DECRYPT_MODE, key, cypherParameters);
			} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeySpecException ex) {
				throw new RuntimeException(ex);
			}
		}

		private byte[] encrypt(byte[] val) throws IllegalBlockSizeException, BadPaddingException {
			return ecipher.doFinal(val);
		}

		private byte[] decrypt(byte[] val) throws IllegalBlockSizeException, BadPaddingException {
			return dcipher.doFinal(val);
		}

		private String encrypt(String val) {
			try {
				byte[] passwordBytesAsUTF8Encoding = val.getBytes("UTF8");
				byte[] encryptedPasswordBytes = encrypt(passwordBytesAsUTF8Encoding);
				return java.util.Base64.getEncoder().encodeToString(encryptedPasswordBytes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private String decrypt(String val) {
			try {
				byte[] encryptedPasswordBytes = java.util.Base64.getDecoder().decode(val);
				byte[] passwordBytesAsUTF8Encoding = decrypt(encryptedPasswordBytes);
				return new String(passwordBytesAsUTF8Encoding, "UTF8");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
