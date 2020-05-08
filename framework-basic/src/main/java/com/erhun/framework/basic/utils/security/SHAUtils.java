package com.erhun.framework.basic.utils.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 * @date 2016-3-17
 */
public class SHAUtils {
	
	/**
	 * 
	 * @param src
	 * @param secretCode
	 * @return
	 */
	public static byte [] hmacSha1(String src, String secretCode){
		
		try{
	        Mac mac = Mac.getInstance("HmacSHA1");  
	        SecretKeySpec secret = new SecretKeySpec(secretCode.getBytes("UTF-8"), mac.getAlgorithm());  
	        mac.init(secret);  
	        return mac.doFinal(src.getBytes("UTF-8"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
    }
	
	/**
	 * 
	 * @param src
	 * @param key
	 * @return
	 */
	public static byte [] hmacSha256(String src, String key){
		
		try{
	        Mac mac = Mac.getInstance("HmacSHA256");  
	        SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), mac.getAlgorithm());  
	        mac.init(secret);  
	        return mac.doFinal(src.getBytes("UTF-8"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
    }
	
	/**
	 * 
	 * @param src
	 * @param key
	 * @return
	 */
	public static byte [] hmacSha256(String src, byte [] key){
		
		try{
	        Mac mac = Mac.getInstance("HmacSHA256");  
	        SecretKeySpec secret = new SecretKeySpec(key, mac.getAlgorithm());  
	        mac.init(secret);  
	        return mac.doFinal(src.getBytes("UTF-8"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
    }
	
	/**
	 * 
	 * @param src
	 * @param secretCode
	 * @return
	 */
	public static byte [] hmacSha512(String src, String secretCode){
		
		try{
	        Mac mac = Mac.getInstance("HmacSHA512");  
	        SecretKeySpec secret = new SecretKeySpec(secretCode.getBytes("UTF-8"), mac.getAlgorithm());  
	        mac.init(secret);  
	        return mac.doFinal(src.getBytes("UTF-8"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
    }
	
	/**
	 * 
	 * @param src
	 * @param secretCode
	 * @return
	 */
	public static byte [] hmacMd5(String src, String secretCode){
		try{
	        Mac mac = Mac.getInstance("HmacMD5");  
	        SecretKeySpec secret = new SecretKeySpec(secretCode.getBytes("UTF-8"), mac.getAlgorithm());  
	        mac.init(secret);  
	        return mac.doFinal(src.getBytes("UTF-8"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
    }
	
	/**
	 * 
	 * @param src
	 * @param secretCode
	 * @return
	 */
	public static String hmacSha1Hex(String src, String secretCode){
		
		try{
			byte source [] = hmacSha1(src, secretCode);
			if(source != null){
				return HexUtils.encodeHexString(source);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return src;
    } 
}
