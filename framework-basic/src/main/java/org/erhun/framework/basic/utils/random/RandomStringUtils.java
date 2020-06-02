package org.erhun.framework.basic.utils.random;

import java.security.SecureRandom;

/**
 * @author weichao
 */
public class RandomStringUtils {


	public final static String KEY1 = "0123456789";

	public final static String KEY2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public final static String KEY3 = "abcdefghijklmnopqrstuvwxyz";

	public final static String random() {
		return random(12);
	}

	public final static String random(int length) {
		return random(KEY1 + KEY2 + KEY3, length);
	}

	public final static String randomNumeric(int length) {
		return random(KEY1, length);
	}

	public final static String random(String key, int length) {

		SecureRandom secureRandom = new SecureRandom();
		secureRandom.setSeed(System.nanoTime());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(key.charAt(secureRandom.nextInt(key.length())));
        }
        return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(randomNumeric(6));
	}
}
