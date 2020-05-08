package com.erhun.framework.basic.utils.datetime;

import com.erhun.framework.basic.utils.string.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 */
public class DateFormatUtils {

	/**
	 *
	 */
	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 *
	 */
	public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 日期
	 * YYYY-MM-DD
	 */
	public static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

	/**
	 * 日期时间
	 * YYYY-MM-DD HH:MM:SS
	 */
	public static final ThreadLocal<DateFormat> DATETIME_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATETIME_PATTERN));
	
	/**
	 * 日期时间
	 * YYYY-MM-DD HH:MM:SS
	 */
	public static final ThreadLocal<DateFormat> TIMESTAMP_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat(TIMESTAMP_PATTERN));
	
	/**
	 * 日期
	 * YYYYMMDD
	 */
	public static final ThreadLocal<DateFormat> DATE_NO_SEPARATOR_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));
	
	/**
	 * 日期时间
	 * YYYYMMDDhhmmss
	 */
	public static final ThreadLocal<DateFormat> DATETIME_NO_SEPARATOR_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));
	
	/**
	 * 时间截
	 * YYYYMMDDhhmmssSSS
	 */
	public static final ThreadLocal<DateFormat> TIMESTAM_NO_SEPARATOR_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmssSSS"));

    public DateFormatUtils() {
        super();
    }
    
    public static String format(long millis, String pattern) {
        return format(new Date(millis), pattern, null);
    }
   
    public static String format(Date date, String pattern) {
        return format(date, pattern, null);
    }

    public static String format(long millis, String pattern, Locale locale) {
        return format(new Date(millis), pattern, locale);
    }

    public static String format(Date date, String pattern, Locale locale) {
    	if (locale == null) {
			locale = Locale.getDefault();
		}
        SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
        return df.format(date);
    }
    
    public static Date parseSQLDate( String date ){
      	if( StringUtils.isBlank(date) ){
      		return null;
      	}
 		return java.sql.Date.valueOf(date);
    }
     
    /**
     * 将时间格式的字符串转化为Date
     * @param date
     * @param format
     * @return
     */
	public static Date parse(String date, String format) {
		Date dt = null;
		try {
			dt = new SimpleDateFormat(date).parse(format);
		} catch (ParseException e) {
			return dt;
		}
		return dt;
	}
	
	/**
	 * 
	 * @param date
	 * @return YYYY-MM-DD
	 */
	public static String toDateString(Date date){
		if(date == null){
			return null;
		}
		return DATE_FORMAT.get().format(date);
	}

	/**
	 *
	 * @param date
	 * @return YYYY-MM-DD
	 */
	public static String toDateString(Temporal date){
		if(date == null){
			return null;
		}
		return DATE_FORMAT.get().format(date);
	}
	
	/**
	 * 
	 * @param date
	 * @return YYYYMMDD
	 */
	public static String toDateString2(Date date){
		if(date == null){
			return null;
		}
		return DATE_NO_SEPARATOR_FORMAT.get().format(date);
	}

	/**
	 *
	 * @param date
	 * @return YYYYMMDD
	 */
	public static String toDateString2(Temporal date){
		if(date == null){
			return null;
		}
		return DATE_NO_SEPARATOR_FORMAT.get().format(date);
	}
	
	/**
	 * 
	 * @param date
	 * @return YYYY-MM-DD HH:MM:SS
	 */
	public static String toDateTimeString(Date date){
		if(date == null){
			return null;
		}
		return DATETIME_FORMAT.get().format(date);
	}

	/**
	 *
	 * @param date
	 * @return YYYY-MM-DD HH:MM:SS
	 */
	public static String toDateTimeString(Temporal date){
		if(date == null){
			return null;
		}
		return DATETIME_FORMAT.get().format(date);
	}
	
	/**
	 * 
	 * @param date
	 * @return yyyyMMddHHmmss
	 */
	public static String toDateTimeString2(Date date){
		if(date == null){
			return null;
		}
		return DATETIME_NO_SEPARATOR_FORMAT.get().format(date);
	}

	/**
	 *
	 * @param date
	 * @return yyyyMMddHHmmss
	 */
	public static String toDateTimeString2(Temporal date){
		if(date == null){
			return null;
		}
		return DATETIME_NO_SEPARATOR_FORMAT.get().format(date);
	}
	
	/**
	 * 
	 * @param date
	 * @return YYYY-MM-DD HH:MM:SS.SSS
	 */
	public static String toTimestampString(Date date){
		if(date == null){
			return null;
		}
		return TIMESTAMP_FORMAT.get().format(date);
	}

	/**
	 *
	 * @param date
	 * @return YYYY-MM-DD HH:MM:SS.SSS
	 */
	public static String toTimestampString(Temporal date){
		if(date == null){
			return null;
		}
		return TIMESTAMP_FORMAT.get().format(date);
	}
	
	/**
	 * 
	 * @param date
	 * @return yyyyMMddHHmmss
	 */
	public static String toTimestampString2(Date date){
		if(date == null){
			return null;
		}
		return TIMESTAM_NO_SEPARATOR_FORMAT.get().format(date);
	}

	/**
	 *
	 * @param date
	 * @return yyyyMMddHHmmss
	 */
	public static String toTimestampString2(Temporal date){
		if(date == null){
			return null;
		}
		return TIMESTAM_NO_SEPARATOR_FORMAT.get().format(date);
	}
	
	/**
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String toString(Date date, DateFormat dateFormat){
		return dateFormat.format(date);
	}
	
	/**
	 * 
	 * @param dateString: yyyy-MM-dd | yyyy-MM-dd HH:MM:SS
	 * @return
	 */
	public static Date parse(String dateString){
		
		if(dateString.indexOf(" ") > -1){
			try {
				return DATETIME_FORMAT.get().parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			try {
				return DATE_FORMAT.get().parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return null;
			
	}
    
}
