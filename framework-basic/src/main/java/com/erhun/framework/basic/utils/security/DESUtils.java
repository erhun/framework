package com.erhun.framework.basic.utils.security;


import com.erhun.framework.basic.utils.string.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 * @date 2016-4-15
 */
public class DESUtils {
	
    public static final String KEY_ALGORITHM = "DES";
    
    public static final String CIPHER_ALGORITHM_ECB = "DES/ECB/PKCS5Padding";
    
    public static final String CIPHER_ALGORITHM_CBC = "DES/CBC/PKCS5Padding";  
      
    public static void main(String[] args) throws Exception {  
       
       /* byte[] encrypt = decrypt(org.bouncycastle.util.encoders.Base64.decode("g53AcDs5XSGCNP6itOSVCo2JStUgFn8pA5e2MpKaziQ74I8M7aGlB2qCYKP0eA3N7vL/kcf6dLki6WAwcWAdrr0sBainNjgdT7/tntSkriBCCcW6VK1TDnQxtb1hEiBVO4XGyB5I0RZNykpObvs4F1sK5VlAK5JVHQbBHGb7+MiaUnuaReSYAgZ17O9l1Y8pcdaWD5IKH0GjvtgWy0l33SskRech6VUGEWW6SjmjZT4="), Base64.decode("evg4C8vLAfI=".getBytes()), null, Base64.decode("EcfJHms2pUs=".getBytes()));  
        System.out.println(new String(encrypt));*/
          
    }  
      
    /**
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String key) throws Exception {  
      return encrypt(data, key, null, null, false);
    }
    
    /**
     * 
     * @param data
     * @param padding
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String padding, String key) throws Exception {  
      return encrypt(data, key, null, null, false);
    }
    
    /**
     * 
     * @param data
     * @param key
     * @param algorithm
     * @param iv
     * @return
     */
    public static byte [] encrypt(byte[] data, Object key, String algorithm, Object iv, boolean secureRandom){
    
		try {
			Key k = toKey(toBytes(key));
			byte [] ivb = toBytes(iv);
			Cipher cipher;
	        if(ivb != null){
	        	cipher = Cipher.getInstance(StringUtils.defaultIfEmpty(algorithm, CIPHER_ALGORITHM_CBC));
	        	if(secureRandom){
	        		cipher.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(ivb), new SecureRandom());
	        	}else{
	        		cipher.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(ivb));
	        	}
	        }else{
	        	cipher = Cipher.getInstance(StringUtils.defaultIfEmpty(algorithm, CIPHER_ALGORITHM_ECB));
	        	if(secureRandom){
	        		cipher.init(Cipher.ENCRYPT_MODE, k, new SecureRandom());
	        	}else{
	        		cipher.init(Cipher.ENCRYPT_MODE, k);
	        	}
	        }
	        return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
        
    }
    
    /**
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {  
       return decrypt(data, key, null, null);
    }
    
    /**
     * 
     * @param data
     * @param algorithm
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String algorithm, String key) throws Exception {  
       return decrypt(data, key, algorithm, null);
    }
    
    /**
     * 
     * @param data
     * @param key
     * @param algorithm
     * @param iv
     * @return
     */
    public static byte[] decrypt(byte [] data, Object key, String algorithm, Object iv){
    	
    	try{
	        Key k = toKey(toBytes(key));  
	        Cipher cipher;
	        byte  ivb [] = toBytes(iv);
	        if(ivb != null){
	        	cipher = Cipher.getInstance(StringUtils.defaultIfEmpty(algorithm, CIPHER_ALGORITHM_CBC));
	        	cipher.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(ivb));
	        }else{
	        	cipher = Cipher.getInstance(StringUtils.defaultIfEmpty(algorithm, CIPHER_ALGORITHM_ECB));
	        	cipher.init(Cipher.DECRYPT_MODE, k);
	        }
	        return cipher.doFinal(data);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    /**
     * 
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {  
        DESKeySpec des = new DESKeySpec(key);  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);  
        SecretKey secretKey = keyFactory.generateSecret(des);  
        return secretKey;  
    }
    
    private static SecretKeySpec generateSecureRandomKey(String password) throws NoSuchAlgorithmException {
		
		KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);
		SecureRandom random = new SecureRandom();
		random.setSeed(password.getBytes());
		kgen.init(128, random);

		return new SecretKeySpec(kgen.generateKey().getEncoded(), KEY_ALGORITHM);
		
	}
    
    private static byte [] toBytes(Object o) throws UnsupportedEncodingException{
    	
    	if(o == null){
    		return null;
    	}
    	
    	if(o instanceof String){
    		return ((String)o).getBytes("UTF-8");
    	}
    	
    	return (byte[])o;
    }
    
}
