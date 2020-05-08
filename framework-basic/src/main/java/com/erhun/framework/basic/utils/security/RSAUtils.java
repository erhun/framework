package com.erhun.framework.basic.utils.security;

import com.erhun.framework.basic.utils.string.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 * @date 2016-1-22
 */
public final class RSAUtils {

	private static final Provider BC_PROVIDER = new BouncyCastleProvider();

	private static final int KEY_SIZE = 1024;

	public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
	
	private static final int MAX_ENCRYPT_BLOCK = 128;

	/**
	 * 加密算法RSA
	 */
	public static final String ALGORITHM = "RSA";

	/**
	 * 获取公钥的key
	 */
	public static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 获取私钥的key
	 */
	public static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;
	
	static{
		Security.addProvider(BC_PROVIDER);
	}
	
	/**
	 * 生成密钥对
	 * 
	 * @return 密钥对
	 */
	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC_PROVIDER);
			keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] encryptPrivateKey(String privateKey, String data) {
		return encryptPrivateKey(privateKey, null, data);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param transformation
	 * @param data
	 * @return
	 */
	public static byte[] encryptPrivateKey(String privateKey, String transformation, String data) {
		return encryptPrivateKey(privateKey, transformation, data, MAX_ENCRYPT_BLOCK);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] encryptPrivateKey(String privateKey, String transformation, String data, int blockSize) {
		return encryptPrivateKey(privateKey, transformation, data.getBytes(), MAX_ENCRYPT_BLOCK);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] encryptPrivateKey(String privateKey, String provider, String transformation, String data, int blockSize) {
		return encryptPrivateKey(getPrivateKey(privateKey, provider), provider, transformation, data.getBytes(), blockSize);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] encryptPrivateKey(String privateKey, String transformation, byte[] data) {
		return encryptPrivateKey(getPrivateKey(privateKey), null, transformation, data, MAX_ENCRYPT_BLOCK);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] encryptPrivateKey(String privateKey, String transformation, byte[] data, int blockSize) {
		return encryptPrivateKey(getPrivateKey(privateKey), null, transformation, data, blockSize);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] encryptPrivateKey(PrivateKey privateKey, String provider, String transformation, byte[] data, int blockSize) {
		
		try {
			
			Cipher cipher;
			
			if("BC".equals(provider)){
				cipher = Cipher.getInstance(privateKey.getAlgorithm(), provider);
			}else{
				cipher = Cipher.getInstance(privateKey.getAlgorithm());
			}
			
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			
			if(blockSize < 1){
				blockSize = MAX_ENCRYPT_BLOCK;
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			int inputLen = data.length, offset = 0, i = 0, len;
			byte[] cache;
			
			while ((len = inputLen - offset) > 0) {
				if (len > blockSize) {
					cache = cipher.doFinal(data, offset, blockSize);
				} else {
					cache = cipher.doFinal(data, offset, inputLen - offset);
				}
				out.write(cache, 0, cache.length);
				i++;
				offset = i * blockSize;
			}
			
			return out.toByteArray();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param transformation
	 * @param data
	 * @return
	 */
	public static byte[] encrypt(String publicKey, String transformation, String data) {
		return encrypt(publicKey, transformation, data, 0);
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param transformation
	 * @param data
	 * @param blockSize
	 * @return
	 */
	public static byte[] encrypt(String publicKey, String transformation, String data, int blockSize) {
		return encrypt(getPublicKey(publicKey), transformation, data.getBytes(), blockSize);
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param transformation
	 * @param data
	 * @param blockSize
	 * @return
	 */
	public static byte[] encrypt(String publicKey, String provider, String transformation, String data, int blockSize) {
		return encrypt(getPublicKey(publicKey, provider), transformation, data.getBytes(), blockSize);
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param transformation
	 * @param data
	 * @return
	 */
	public static byte[] encrypt(String publicKey, String transformation, byte [] data) {
		return encrypt(getPublicKey(publicKey), transformation, data, MAX_ENCRYPT_BLOCK);
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param transformation
	 * @param data
	 * @param blockSize
	 * @return
	 */
	public static byte[] encrypt(String publicKey, String transformation, byte [] data, int blockSize) {
		return encrypt(getPublicKey(publicKey), transformation, data, blockSize);
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param transformation
	 * @param data
	 * @return
	 */
	public static byte[] encrypt(PublicKey publicKey, String transformation, byte[] data, int blockSize) {
		
		try {
			
			Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			if(blockSize < 1){
				blockSize = MAX_ENCRYPT_BLOCK;
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int inputLen = data.length, offset = 0, i = 0, len;
			byte[] cache;
			
			while ((len = inputLen - offset) > 0) {
				if (len > blockSize) {
					cache = cipher.doFinal(data, offset, blockSize);
				} else {
					cache = cipher.doFinal(data, offset, inputLen - offset);
				}
				out.write(cache, 0, cache.length);
				i++;
				offset = i * blockSize;
			}
			
			return out.toByteArray();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param data
	 * @return
	 */
	public static String encryptToBase64(PublicKey publicKey, String data) {
		byte[] d = encrypt(publicKey, null, data.getBytes(), 0);
		return d != null ? Base64.encodeToString(d, false) : null;
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] decryptByPrivateKey(String privateKey, String transformation, byte[] data) {
		
		Cipher cipher = null;
		
		try {
			cipher = Cipher.getInstance(StringUtils.defaultIfEmpty(transformation, "RSA/ECB/PKCS1Padding"));
			cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
			byte[] output = cipher.doFinal(data);
			return output;
		} catch (Exception e) {
		}
		
		return null;
		
	}

	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] decryptByPrivateKey(PrivateKey privateKey, String transformation, byte[] data) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(StringUtils.defaultIfEmpty(transformation, "RSA/ECB/PKCS1Padding"));
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(data);
			return output;
		} catch (Exception e) {
		}
		return null;
		
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] decryptByPrivateKey(PrivateKey privateKey, byte[] data) {
		
		return decryptByPrivateKey(privateKey, null, data);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static byte[] decrypt(PrivateKey privateKey, String transformation, byte[] data) {
		try {
			Cipher cipher = Cipher.getInstance(transformation, BC_PROVIDER);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static String decrypt(PrivateKey privateKey, String data) {
		byte[] d = decryptByPrivateKey(privateKey, Base64.decode(data));
		return d != null ? new String(data) : null;
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param data
	 * @return
	 */
	public static String decrypt(String privateKey, String data) {
		byte[] d = decryptByPrivateKey(getPrivateKey(privateKey), Base64.decode(data));
		return d != null ? new String(data) : null;
	}
	
	/**
	 * 
	 * @param privateKey
	 * @param transformation "RSA/ECB/PKCS1Padding"|
	 * @param data
	 * @return
	 */
	public static String decrypt(String privateKey, String transformation, String data) {
		byte[] d = decrypt(getPrivateKey(privateKey), transformation, Base64.decode(data));
		return d != null ? new String(d) : null;
	}

	/**
	 *
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Hex.decode(key));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, new BouncyCastleProvider());
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		return decrypt(data, privateKey);
	}

	/**
	 *
	 * @param data
	 * @param modulus
	 * @param exponent
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String modulus, String exponent) throws Exception {
		RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(new BigInteger(modulus, 16), new BigInteger(exponent, 16));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		return decrypt(data, privateKey);
	}

	/**
	 *
	 * @param data
	 * @param modulus
	 * @param exponent
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String modulus, String exponent) throws Exception {

		RSAPublicKeySpec spec =  new RSAPublicKeySpec(new BigInteger(modulus, 16), new BigInteger(exponent, 16));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(spec);

		return decrypt(data, publicKey);
	}

	/**
	 *
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Hex.decode(key));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
		return decrypt(data, publicKey);
	}

	public static byte [] decrypt(byte [] data, RSAPrivateKey privateKey) throws Exception {

		Cipher cipher = Cipher.getInstance(ALGORITHM, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return decryptChunk(cipher, data);

	}

	/**
	 *
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, RSAPublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM, new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return decryptChunk(cipher, data);
	}

	/**
	 *
	 * @param cipher
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static byte [] decryptChunk(Cipher cipher, byte [] data) throws Exception{

		int blockSize = cipher.getBlockSize();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
		int j = 0;
		while (data.length - j * blockSize > 0) {
			bout.write(cipher.doFinal(data, j * blockSize, blockSize));
			j++;
		}

		return bout.toByteArray();
	}
	
	/**
	 * 
	 * @param privateKeyFile
	 * @return
	 */
	public static PrivateKey fileToPrivateKey(String privateKeyFile) {
		File key = new File(privateKeyFile);
		try {
			FileInputStream is = new FileInputStream(key);
			FileChannel channel = is.getChannel();
			byte[] buffer = new byte[is.available()];
			channel.read(ByteBuffer.wrap(buffer));
			return getPrivateKey(new String(buffer));
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param privateKey
	 * @return
	 */
	public static PrivateKey getPrivateKey(String privateKey) {
		return getPrivateKey(privateKey, null);
	}
	
	/**
	 * 
	 * @param privateKey
	 * @return
	 */
	public static PrivateKey getPrivateKey(String privateKey, String provider) {
		RSAPrivateKey pk = null;
		try {
			byte[] buffer = Base64.decode(privateKey);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory;
			if("BC".equals(provider)){
				keyFactory = KeyFactory.getInstance("RSA", BC_PROVIDER);
			}else{
				keyFactory = KeyFactory.getInstance("RSA");
			}
			pk = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pk;
	}
	
	/**
	 * 
	 * @param publicKeyString
	 * @return
	 */
	public static PublicKey getPublicKey(String publicKeyString) {
		return getPublicKey(publicKeyString, null);
	}
	
	/**
	 * 
	 * @param publicKeyString
	 * @return
	 */
	public static PublicKey getPublicKey(String publicKeyString, String provider) {
		try {
			byte[] keyBytes = Base64.decode(publicKeyString);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory;
			if("BC".equals(provider)){
				keyFactory = KeyFactory.getInstance(ALGORITHM, BC_PROVIDER);
			}else{
				keyFactory = KeyFactory.getInstance(ALGORITHM);
			}
			return keyFactory.generatePublic(keySpec);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		return sign(data, keyFactory.generatePrivate(pkcs8KeySpec));
	}

	/**
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, PrivateKey privateKey)
			throws Exception {
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateKey);
		signature.update(data);
		return Base64.encodeToString(signature.sign(), false);
	}
}