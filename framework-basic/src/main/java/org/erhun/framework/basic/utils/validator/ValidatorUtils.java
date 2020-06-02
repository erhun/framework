package org.erhun.framework.basic.utils.validator;

import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.basic.utils.regex.RegularPatterns;

import java.util.regex.Pattern;

/**
 * 通用验证工具类
 * @Author weichao <gorilla@aliyun.com>
 */
public class ValidatorUtils {

    /**
     * 验证手机号（简单）
     */
    public static boolean isMobileSimple(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_MOBILE_SIMPLE, text);
    }

    /**
     * 验证手机号（精确）
     */
    public static boolean isMobile(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_MOBILE, text);
    }

    /**
     * 验证固定电话号码
     */
    public static boolean isTel(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_TEL, text);
    }

    /**
     * 验证15或18位身份证号码
     */
    public static boolean isIdCard(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_ID_CARD_15, text) || isMatch(RegularPatterns.PATTERN_ID_CARD_18, text);
    }

    /**
     * 验证邮箱
     */
    public static boolean isEmail(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_EMAIL, text);
    }

    /**
     * 验证URL
     */
    public static boolean isUrl(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_URL, text);
    }

    /**
     * 验证yyyy-MM-dd格式的日期校验
     */
    public static boolean isDate(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_DATE, text);
    }

    /**
     * 验证IP地址
     */
    public static boolean isIp(CharSequence text) {
        return isMatch(RegularPatterns.PATTERN_IP, text);
    }

    public static boolean isMatch(Pattern pattern, CharSequence text) {
        return StringUtils.isNotEmpty(text) && pattern.matcher(text).matches();
    }
}
