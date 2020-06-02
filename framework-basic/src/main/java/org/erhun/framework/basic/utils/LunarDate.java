package org.erhun.framework.basic.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * 农历日期类
 * @author weichao
 *
 */
public class LunarDate {

	private static final int YEAR_1900 = 1900;

	private int year;

	private int month;

	private int day;

	private boolean leap;

	private final static String chineseNumber[] = { "正", "二", "三", "四", "五", "六", "七", "八", "九","十", "十一", "腊" };

	private final static String chineseNumber1[] = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };

	/**
	 * 生肖
	 */
	private final static String[] animals = new String[] { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

	/**
	 * 农历年
	 */
	private final static String[] gan = new String[] { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };

	/**
	 * 农历时辰
	 */
	private final static String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };

	private final static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260,
			0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250,
			0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5,
			0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0,
			0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0,
			0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
			0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
			0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950,
			0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558,
			0x0b540, 0x0b5a0, 0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50,
			0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,
			0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954, 0x0d4a0, 0x0da50,
			0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4,
			0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
			0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, 0x05aa0,
			0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
			0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20,
			0x0ada0 };

	/**
	 *  农历 n年的总天数
	 * @param n
	 * @return
	 */
	private int yearDays(final int n) {
		int i, sum = 348;
		for (i = 0x8000; i > 0x8; i >>= 1) {
			if ((lunarInfo[n - 1900] & i) != 0) {
				sum += 1;
			}
		}
		return (sum + leapDays(n));
	}

	/**
	 * 农历 n年闰月的天数
	 * @param n
	 * @return
	 */
	private int leapDays(final int n) {
		if (leapMonth(n) != 0) {
			if ((lunarInfo[n - 1900] & 0x10000) != 0) {
				return 30;
			} else {
				return 29;
			}
		} else {
			return 0;
		}
	}

	/**
	 *  农历 y年闰哪个月 1-12 , 没闰传回 0
	 * @param y
	 * @return
	 */
	private int leapMonth(final int y) {
		return (int) (lunarInfo[y - 1900] & 0xf);
	}

	/**
	 *  农历 y年m月的总天数
	 * @param x
	 * @param m
	 * @return
	 */
	private int monthDays(final int x, final int m) {
		if ((lunarInfo[x - YEAR_1900] & (0x10000 >> m)) == 0) {
			return 29;
		} else {
			return 30;
		}
	}

	/**
	 * 农历 y年的生肖
	 * @return
	 */
	public String animalsYear() {
		return animals[(year - 4) % 12];
	}

	/**
	 * 月日的offset 干支, 0=甲子
	 * @param num
	 * @return
	 */
	private String cyclicalm(final int num) {
		return (gan[num % 10] + Zhi[num % 12]);
	}

	/**
	 * offset 干支, 0=甲子
	 * @return
	 */
	public String cyclical() {
		final int num = year - 1900 + 36;
		return (cyclicalm(num));
	}

	public String getChinaDayString(final int day) {
		final String chineseTen[] = { "初", "十", "廿", "卅" };
		final int n = day % 10 == 0 ? 9 : day % 10 - 1;
		if (day > 30) {
			return "";
		}
		if (day == 10) {
			return "初十";
		} else {
			return chineseTen[day / 10] + chineseNumber1[n];
		}
	}

	public LunarDate() {
		this(new Date());
	}

	public LunarDate(final Date date) {
		int leapMonth = 0;
		Date baseDate = null;
		try {
			baseDate = new SimpleDateFormat("yyyy-MM-dd").parse("1900-1-31");
		} catch (final ParseException e) {
		}

		// 求出和1900年1月31日相差的天数
		int offset = (int) ((date.getTime() - baseDate.getTime()) / 86400000L);

		// 用offset减去每农历年的天数
		// 计算当天是农历第几天
		// i最终结果是农历的年份
		// offset是当年的第几天
		int iYear, daysOfYear = 0;
		for (iYear = 1900; (iYear < 2050) && (offset > 0); iYear++) {
			daysOfYear = yearDays(iYear);
			offset -= daysOfYear;
		}
		if (offset < 0) {
			offset += daysOfYear;
			iYear--;
		}
		// 农历年份
		year = iYear;

		// yearCyl = iYear - 1864;
		leapMonth = leapMonth(iYear); // 闰哪个月,1-12
		leap = false;

		// 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
		int iMonth, daysOfMonth = 0;
		for (iMonth = 1; (iMonth < 13) && (offset > 0); iMonth++) {
			// 闰月
			if ((leapMonth > 0) && (iMonth == (leapMonth + 1)) && !leap) {
				--iMonth;
				leap = true;
				daysOfMonth = leapDays(year);
			} else {
				daysOfMonth = monthDays(year, iMonth);
			}

			offset -= daysOfMonth;
			// 解除闰月
			if (leap && (iMonth == (leapMonth + 1))) {
				leap = false;
			}
		}
		// offset为0时，并且刚才计算的月份是闰月，要校正
		if ((offset == 0) && (leapMonth > 0) && (iMonth == leapMonth + 1)) {
			if (leap) {
				leap = false;
			} else {
				leap = true;
				--iMonth;
			}
		}
		// offset小于0时，也要校正
		if (offset < 0) {
			offset += daysOfMonth;
			--iMonth;
		}
		month = iMonth;
		day = offset + 1;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(cyclical()).append("(").append(animalsYear()).append(")").append("年");
		sb.append(leap ? "闰" : "").append(chineseNumber[month - 1]).append("月");
		sb.append(getChinaDayString(day));
		return sb.toString();
	}
}
