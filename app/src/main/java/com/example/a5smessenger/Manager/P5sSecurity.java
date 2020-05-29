package com.example.a5smessenger.Manager;


import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//hàm mã hóa ko giải thích code
public class P5sSecurity {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final static String characterEncoding = "UTF-8";
	private final static String cipherTransformation = "AES/CBC/PKCS5Padding";
	private final static String aesEncryptionAlgorithm = "AES";

	private static String[] SeparatedString(String data) {

	
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		data = data.substring(0, data.length() - 24);
		String[] ArrStr = new String[2];

		char[] ArrStr2 = data.toCharArray();

		List<Character> arrCharData = new ArrayList<Character>();
		for (char c : ArrStr2) {
			arrCharData.add(c);
		}

		String strLenData = "", DataResult = "";
		String strLenKey = "", KeyResult = "";
		int DataLength;

		for (int i = 22; i > 0; i--) {
			try {

				Integer.parseInt(arrCharData.get(i).toString());
				strLenData = arrCharData.get(i).toString() + strLenData;
				arrCharData.remove(i);
			} catch (Exception ex) {
				arrCharData.remove(i);
				break;
			}
		}

		for (int i = arrCharData.size() - 1; i > 0; i--) {
			try {
				Integer.parseInt(arrCharData.get(i).toString());

				strLenKey = arrCharData.get(i).toString() + strLenKey;
				arrCharData.remove(i);
			} catch (Exception ex) {
				arrCharData.remove(i);
				break;
			}
		}

		int len = arrCharData.size() - 1;
		DataLength = Integer.parseInt(strLenData);
		for (int i = len; i > len - 7; i--) {
			arrCharData.remove(i);
		}
		for (int i = 0; i < 7; i++) // add 0 -> 6
		{
			DataResult += arrCharData.get(i).toString();
		}
		for (int i = 7; i < 11; i++) // 4
		{
			KeyResult += arrCharData.get(i).toString();
		}
		for (int i = 11; i < 19; i++)// add 7 -> 14
		{
			DataResult += arrCharData.get(i).toString();
		}
		for (int i = 19; i < 22; i++) // 3
		{
			KeyResult += arrCharData.get(i).toString();
		}
		for (int i = 22; i < DataLength + 15; i++) // 22 - 7 = 15
		{
			KeyResult += arrCharData.get(i).toString();
		}
		for (int i = (DataLength + 15); i < arrCharData.size(); i++) {
			DataResult += arrCharData.get(i).toString();
		}
		ArrStr[0] = DataResult;
		ArrStr[1] = KeyResult;
		return ArrStr;
	}

	@SuppressWarnings("rawtypes")
	private static String CombineString(String data, String key) {
		
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		ArrayList arr = new ArrayList();
		Random random = new Random();
		String lenData, lenKey;

		char[] arrCharKey = key.toCharArray();
		char[] arrCharData = data.toCharArray();
		lenData = randomUUIDString.substring(0, 7)
				+ (char) ('a' + random.nextInt(26 - 0) + 0)
				+ arrCharData.length + randomUUIDString.substring(0, 24);
		;
		lenKey = (char) ('a' + random.nextInt(26 - 0) + 0) + ""
				+ arrCharKey.length;
		char[] arrCharKeyLen = lenKey.toCharArray();
		char[] arrCharDataLen = lenData.toCharArray();

		for (int i = 0; i < 7; i++) // add 0 -> 6
		{
			arr.add(arrCharData[i]);
		}
		for (int i = 0; i < 4; i++) // add 0 -> 3
		{
			arr.add(arrCharKey[i]);
		}
		for (int i = 7; i < 15; i++)// add 7 -> 14
		{
			arr.add(arrCharData[i]);
		}

		for (char c : arrCharKeyLen) {
			arr.add(c);
		}

		for (int i = 4; i < 6; i++) // add 4 -> 5
		{
			arr.add(arrCharKey[i]);
		}
		for (int i = 6; i < arrCharKey.length; i++) {
			arr.add(arrCharKey[i]);
		}
		for (int i = 15; i < arrCharData.length; i++) {
			arr.add(arrCharData[i]);
		}

		for (char c : arrCharDataLen) {
			arr.add(c);
		}

		StringBuilder sb = new StringBuilder();
		for (Object s : arr) {
			sb.append(s);
		}

		return sb.toString();
	}

	private static byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
		
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpecy = new SecretKeySpec(key,
				aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
		cipherText = cipher.doFinal(cipherText);
		return cipherText;
	}

	private static byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
		
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key,
				aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);
		return plainText;
	}

	private static byte[] getKeyBytes(String key) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		byte[] keyBytes = new byte[16];

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] parameterKeyBytes = md.digest(key.getBytes(characterEncoding));

		System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
				Math.min(parameterKeyBytes.length, keyBytes.length));
		return keyBytes;
	}

	
	public static String encrypt(String plainText, String key)
			throws UnsupportedEncodingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
		
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		
		byte[] plainTextbytes = plainText.getBytes(characterEncoding);
		byte[] keyBytes = getKeyBytes(key);
		return P5sSecurity.CombineString(Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), Base64.DEFAULT),key);
	}

	
	public static String encrypt(String plainText)
			throws UnsupportedEncodingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
		
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		
		byte[] plainTextbytes = plainText.getBytes(characterEncoding);
		byte[] keyBytes = getKeyBytes(plainText);
		return Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), Base64.DEFAULT);
	}

	

	public static String decrypt(String encryptedText)
			throws KeyException, GeneralSecurityException,
            GeneralSecurityException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException {
		
		// đoạn code crypt data
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			bis = new BufferedInputStream(new FileInputStream("abc"));
			bos = new BufferedOutputStream(new FileOutputStream("abc", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		}
		
		String[] standard = P5sSecurity.SeparatedString(encryptedText);
		byte[] cipheredBytes = Base64.decode(standard[0], Base64.DEFAULT);
		byte[] keyBytes = getKeyBytes(standard[1]);
		return new String(decrypt(cipheredBytes, keyBytes, keyBytes),characterEncoding);
	}
}