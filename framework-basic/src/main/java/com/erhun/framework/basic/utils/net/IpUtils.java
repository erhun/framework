package com.erhun.framework.basic.utils.net;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class IpUtils {

	/**
	 * IP转数组
	 * @param ipAddr
	 * @return byte[]
	 */
	public static byte[] convertIpToBytes(String ipAddr) {
		try {
			return InetAddress.getByName(ipAddr).getAddress();
		} catch (Exception e) {
			throw new IllegalArgumentException(ipAddr + " is invalid IP");
		}
	}

	/**
	 * 字节转IP
	 * @param bytes
	 * @return int
	 */
	public static String convertByteToIp(byte[] bytes) {
		return new StringBuilder().append(bytes[0] & 0xFF).append('.').append(
				bytes[1] & 0xFF).append('.').append(bytes[2] & 0xFF)
				.append('.').append(bytes[3] & 0xFF).toString();
	}
	/**
	 * 字节转数组
	 * @param bytes
	 * @return int
	 */
	public static int convertByteToInt(byte[] bytes) {
		int addr = bytes[3] & 0xFF;
		addr |= ((bytes[2] << 8) & 0xFF00);
		addr |= ((bytes[1] << 16) & 0xFF0000);
		addr |= ((bytes[0] << 24) & 0xFF000000);
		return addr;
	}
	/**
	 * IP转int
	 * @param ipAddr
	 * @return int
	 */
	public static int convertIpToInt(String ipAddr) {
		try {
			return convertByteToInt(convertIpToBytes(ipAddr));
		} catch (Exception e) {
			throw new IllegalArgumentException(ipAddr + " is invalid IP");
		}
	}

	/**
	 * 整数转IP
	 * @param ipInt
	 * @return String
	 */
	public static String convertTntToIp(int ipInt) {
		return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.')
				.append((ipInt >> 16) & 0xff).append('.').append(
						(ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff))
				.toString();
	}


	public static String getIp(HttpServletRequest req) {
	    if(req == null){
	        return null;
	    }
	    try {
			String ip = req.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = req.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = req.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = req.getRemoteAddr();
			}
			return ip;
		}catch (Exception ex){
	    	return null;
		}
	}
}
