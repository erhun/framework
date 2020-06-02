package org.erhun.framework.basic.utils.regex;

import java.util.regex.Pattern;

/**
 * @author gorilla
 */
public interface RegularPatterns {

	/**
	 * 手机号(简单)
	 */
	Pattern PATTERN_MOBILE_SIMPLE = Pattern.compile(Regulars.REGEX_MOBILE_SIMPLE);

	/**
	 * 手机号(匹配各运营商号段)
	 */
	Pattern PATTERN_MOBILE = Pattern.compile(Regulars.REGEX_MOBILE);

	/**
	 * 固定电话
	 */
	Pattern PATTERN_TEL = Pattern.compile(Regulars.REGEX_TEL);

	/**
	 * 身份证号码15位
	 */
	Pattern PATTERN_ID_CARD_15 = Pattern.compile(Regulars.REGEX_ID_CARD_15);

	/**
	 * 身份证号码18位
	 */
	Pattern PATTERN_ID_CARD_18 = Pattern.compile(Regulars.REGEX_ID_CARD_18);

	/**
	 * 邮箱
	 */
	Pattern PATTERN_EMAIL = Pattern.compile(Regulars.REGEX_EMAIL);

	/**
	 * URL
	 */
	Pattern PATTERN_URL = Pattern.compile(Regulars.REGEX_URL);

	/**
	 * yyyy-MM-dd格式的日期校验
	 */
	Pattern PATTERN_DATE = Pattern.compile(Regulars.REGEX_DATE);

	/**
	 * IP地址
	 */
	Pattern PATTERN_IP = Pattern.compile(Regulars.REGEX_IP);

}

