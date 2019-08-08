package EncryptionHandler;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Aes {

	public static void decrypt(byte[] cipherText, byte[] secretKey, FileOutputStream fos)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
		for (int i = 0; i < cipherText.length; i += 16) {
			try {
				fos.write(cipher.doFinal(Arrays.copyOfRange(cipherText, i, i + 16)));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public static byte[] decryptToByteArray(byte[] cipherText, byte[] secretKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
		for (int i = 0; i < cipherText.length; i += 16) {
			try {
				baos.write(cipher.doFinal(Arrays.copyOfRange(cipherText, i, i + 16)));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return baos.toByteArray();
	}
	public static byte[] encryptToByteArray(byte[] data, byte[] secretKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
		for (int i = 0; i < data.length; i += 16) {
			try {
				baos.write(cipher.doFinal(Arrays.copyOfRange(data, i, i + 16)));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return baos.toByteArray();
	}

}
