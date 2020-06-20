package org.erhun.framework.basic.utils.security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 
 * @author weichao
 *
 */
public class RSAUtils {
	
	
	/**
     * 加密算法RSA
     */
    public static final String ALGORITHM = "RSA";
    
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    public static final String PUBLIC_KEY = "RSAPublicKey";
    
    /**
     * 获取私钥的key
     */
    public static final String PRIVATE_KEY = "RSAPrivateKey";
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    
    
    public static void main(String[] args) throws Exception {
        
        System.out.println("58b7a170f24c7f8bee9258f26f049ec0262bab0bef0cf42eeaba7f09ad443fb308cc18b0cfced25adfc439c1aaa4674825325dc5c732bfe8c3181d3f0271f46913606704f81c6bea21bc376617a3ae4774a6d0764b67b39eb5f704caf810ef41d16a9abed0604b8101ecc1a1ce0001a04deae2f8a92219458f34a4cb820a24d3".length());
		
    	KeyPair pair = RSAUtils.generateKeyPair();
    	
    	RSAPrivateKey prikey = (RSAPrivateKey) pair.getPrivate();
		
    	
    	System.out.println("login.rsa.private_exponent " + prikey.getPrivateExponent().toString(16));
    	System.out.println("login.rsa.private_modulus " + prikey.getModulus().toString(16));
    	
    	System.out.println("login.rsa.privateKey " + new String( Hex.encode(prikey.getEncoded())));
    	
    	
    	System.out.println(new String(Hex.encode(prikey.getEncoded())).length());
    	
    	
    	RSAPublicKey pubkey = (RSAPublicKey) pair.getPublic();
    	
    	
    	System.out.println("login.rsa.public_exponent " + pubkey.getPublicExponent().toString(16));
    	System.out.println("login.rsa.public_modulus " + pubkey.getModulus().toString(16));
    	
    	System.out.println("login.rsa.publicKey " + new String(Hex.encode(pubkey.getEncoded())));

		
		
	}
    
    
    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     * 
     * @return
     * @throws Exception
     */
    public static KeyPair generateKeyPair() throws Exception {
    	
         //SecureRandom random = new SecureRandom();
         KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
         keyPairGen.initialize(1024);
         
        return keyPairGen.generateKeyPair();
    }
    
    public static byte [] decrypt(byte [] data, RSAPrivateKey privateKey) throws Exception {
		
      Cipher cipher = Cipher.getInstance(ALGORITHM, new BouncyCastleProvider());
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      return decrypt_chunkFinal(cipher, data);
	      
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
		return decrypt_chunkFinal(cipher, data);
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
    
    
    /**
     * 
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, RSAPublicKey publicKey) throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(ALGORITHM, new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = encrypt_chunkFinal(cipher, data);
        return encryptedData;
    }
    
    /**
     * 
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Hex.decode(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, new BouncyCastleProvider());
        return encrypt(data, (RSAPublicKey) keyFactory.generatePublic(x509KeySpec));
    }
    
    /**
     * 
     * @param data
     * @param modulus
     * @param exponent
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String modulus, String exponent) throws Exception {
    	
    	RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(modulus, 16), new BigInteger(exponent, 16));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, new BouncyCastleProvider());
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        
        return encrypt(data, publicKey);
    }
    

    /**
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, RSAPrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM, new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return encrypt_chunkFinal(cipher, data);
    }
    
    /**
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(new BigInteger(key, 16).toByteArray());
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, new BouncyCastleProvider());
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return encrypt(data, privateKey);
    }

    /**
     * 
     * @param data
     * @param modulus
     * @param exponent
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String modulus, String exponent) throws Exception {
    	
    	RSAPrivateKeySpec publicKey = new RSAPrivateKeySpec(new BigInteger(modulus, 16), new BigInteger(exponent, 16));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, new BouncyCastleProvider());
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(publicKey);
        
        return encrypt(data, privateKey);
    }
    

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * 
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encodeToString(signature.sign(), false);
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     * 
     * @return
     * @throws Exception
     * 
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }
    
    /**
     * 
     * @param cipher
     * @param data
     * @return
     * @throws Exception
     */
    private static byte[] encrypt_chunkFinal(Cipher cipher, byte[] data) throws Exception {
    	int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 127) {
                cache = cipher.doFinal(data, offSet, 127);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 127;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
		
		/*for(;;i++){
			int size = data.length - i * blockSize;
			if(size > 0 ){
				if (size > blockSize) {
					cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
				} else {
					cipher.doFinal(data, i * blockSize, size, raw, i * outputSize);
				}
			}else{
				break;
			}
		}*/
		
		
		//return raw;
	}
	
    /**
     * 
     * @param cipher
     * @param data
     * @return
     * @throws Exception
     */
	private static byte [] decrypt_chunkFinal(Cipher cipher, byte [] data) throws Exception{
		
		int blockSize = cipher.getBlockSize();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
		int j = 0;
		while (data.length - j * blockSize > 0) {
			bout.write(cipher.doFinal(data, j * blockSize, blockSize));
			j++;
		}
		
		return bout.toByteArray();
	}

}
