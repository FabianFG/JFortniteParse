package EncryptionHandler;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Aes {
	private static String salt;
	private static int iterations = 65536;
	private static int keySize = 256;
	private static byte[] ivBytes;

	public static void main(String[] args) throws IOException {

		FileInputStream fin = new FileInputStream("D:\\Fabian\\Documents\\PakTest\\test.uasset");
		byte[] bytes = new byte[fin.available()];
		fin.read(bytes);
		try {
			FileOutputStream fos = new FileOutputStream("D:\\Fabian\\Documents\\PakTest\\testOut.uasset");
			final byte[] key = DatatypeConverter
					.parseHexBinary("be2db196eb94c3ea458ffb6aa9fbe1edc4bd427afc8103c4197d081f28d9569e");
			decrypt(bytes, key, fos);

			fos.close();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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
