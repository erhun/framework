package org.erhun.framework.basic.utils.conversion;

import org.erhun.framework.basic.utils.datetime.DateFormatUtils;
import org.erhun.framework.basic.utils.string.StringUtils;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author weichao <gorilla@aliyun.com>
 */
public final class ConvertUtils {

	private static final String COLOR_KEY = "0123456789ABCDEF";

	private static final String HEX_PREFIX = "0x";

	private static final int HEX_RADIX = 16;

	private static final Class<?>[] CONSTR_ARGS = { String.class };


	private ConvertUtils() {
	}

	public static Boolean toBoolean(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof String) {
			return  "y".equalsIgnoreCase((String)value)
				|| "t".equalsIgnoreCase((String)value)
				|| "true".equalsIgnoreCase((String)value)
				|| "yes".equalsIgnoreCase((String)value)
				|| "1".equalsIgnoreCase((String)value);
		}		
		if (value instanceof Number) {
			return ((Number)value).intValue() == 1;
		}		
		throw new RuntimeException("The value " + value+ " can't be converted to a Boolean object");
	}

	public static Byte toByte(Object value) {
		if (value == null) {
			return null;
		}
		Number n = toNumber(value, Byte.class);
		if (n instanceof Byte) {
			return (Byte) n;
		} else {
			return Byte.valueOf(n.byteValue());
		}
	}


	public static Number toNumber(Object value, Class<?> targetClazz) {		
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return (Number) value;
		}
		String str = value.toString();	
		if (str.startsWith(HEX_PREFIX)) {
			try {
				return new BigInteger(str.substring(HEX_PREFIX.length()), HEX_RADIX);
			} catch (NumberFormatException nex) {
				throw new RuntimeException("Could not convert " + str + " to " + targetClazz.getName()+ "! Invalid hex number.", nex);
			}
		}
		try {
			Constructor<?> constr = targetClazz.getConstructor(CONSTR_ARGS);
			return (Number) constr.newInstance(new Object[] {str});			
		} catch (InvocationTargetException itex) {
			throw new RuntimeException("Could not convert " + str + " to "+ targetClazz.getName(), itex.getTargetException());
		} catch (Exception ex) {
			throw new RuntimeException("Conversion error when trying to convert " + str+ " to " + targetClazz.getName(), ex);
		}
	}

	public static Short toShort(Object value) {
		if (value == null) {
			return null;
		}
		Number number = toNumber(value, Short.class);
		if (number instanceof Short) {
			return (Short) number;
		} else {
			return Short.valueOf(number.shortValue());
		}
	}

	public static Integer toInteger(Object value) {
		if (value == null) {
			return null;
		}
		Number number = toNumber(value, Integer.class);
		if (number instanceof Integer) {
			return (Integer) number;
		} else {
			return Integer.valueOf(number.intValue());
		}
	}

	public static Long toLong(Object value) {
		if (value == null) {
			return null;
		}
		Number number = toNumber(value, Long.class);
		if (number instanceof Long) {
			return (Long) number;
		} else {
			return Long.valueOf(number.longValue());
		}
	}

	public static Float toFloat(Object value) {
		if (value == null) {
			return null;
		}
		Number number = toNumber(value, Float.class);
		if (number instanceof Float) {
			return (Float) number;
		} else {
			return new Float(number.floatValue());
		}
	}

	public static Double toDouble(Object value) {		
		if (value == null) {
			return null;
		}
		Number number = toNumber(value, Double.class);
		if (number instanceof Double) {
			return (Double) number;
		} else {
			return new Double(number.doubleValue());
		}
	}

	public static BigInteger toBigInteger(Object value) {
		if (value == null) {
			return null;
		}
		Number number = toNumber(value, BigInteger.class);
		if (number instanceof BigInteger) {
			return (BigInteger) number;
		} else {
			return BigInteger.valueOf(number.longValue());
		}
	}

	public static BigDecimal toBigDecimal(Object value) {		
		if (value == null) {
			return null;
		}
		Number number = toNumber(value, BigDecimal.class);
		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		} else {
			return new BigDecimal(number.doubleValue());
		}
	}

	public static Date toDate(Object value, String format) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			try {
				return new SimpleDateFormat(format).parse((String) value);
			} catch (ParseException e) {
				throw new RuntimeException("The value " + value + "with format "+format+" can't be converted to a Date", e);
			}			
		} else if (value instanceof Long) {
			return new Date(((Long)value).longValue());
		} else if (value instanceof Date) { 
			return (Date) value;
		} else if (value instanceof Calendar) {
			return ((Calendar) value).getTime();
		}		
		throw new RuntimeException("The value " + value + " can't be converted to a Date");		
	} 
	
	public static Timestamp toTime(Object value) {
		if (value == null) 	return null;
		if (value instanceof Timestamp) return (Timestamp) value;
		if (value instanceof Long) return new Timestamp(((Long)value).longValue());		
		if (value instanceof Date) return new Timestamp(((Date)value).getTime());
		if (value instanceof String) {			
			String v = (String)value;			
			if (v.indexOf("-") < 0) {
				Date date = new Date();
				v = DateFormatUtils.format(date, "yyyy-MM-dd") +" "+v;
			}			
			if (v.indexOf(":") < 0) {
				v += " 00:00:00";
			}
			Date ts;			
			try {
				ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(v);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}			
			return new Timestamp(ts.getTime());
		}		
		throw new RuntimeException("The value " + value + " can't be converted to a Timestamp");		
	}

	public static Calendar toCalendar(Object value, String format) {
		if (value == null) {
			return null;
		}
		if (value instanceof Calendar) {
			return (Calendar) value;
		}
		if (value instanceof Date) {			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date) value);			
			return calendar;
		} else if (value instanceof String) {
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new SimpleDateFormat(format).parse((String) value));
				return calendar;
			} catch (ParseException e) {
				throw new RuntimeException("The value " + value
						+ " can't be converted to a Calendar", e);
			}
		} 
		throw new RuntimeException("The value " + value + " can't be converted to a Calendar");
	}
	
	public static String toString(Object value) {		
		if (value == null) {
			return "";
		}
		if (value instanceof String) {
			return (String) value;
		}
		if (value instanceof Boolean) { 
			return (((Boolean)value).booleanValue()) ? "true":"false";
		}
		if (value instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return sdf.format((Date)value);
		} 		
		if (value instanceof byte[]) {
			return value.toString();
		}
		return String.valueOf(value);
	}

	public static char toChar(Object value, char defaultValue) {		
		try {		
			return toChar(value);		
		} catch (RuntimeException e) {			
			return defaultValue;
		}
	}

	public static char toChar(Object value) {		
		if (null == value) {
			throw new RuntimeException("Impossible to convert "+value+" from "+(null == value ? "unknown" : value.getClass().getName())+" to "+char.class.getName());
		}
		if (value instanceof Character) {
			return ((Character) value).charValue();
		}
		if (value instanceof Number) {
			int int_value = ((Number) value).intValue();
			return (char) int_value;
		}
		if (value instanceof String && 1 == ((String) value).length()) {
			return ((String) value).charAt(0);
		}
		throw new RuntimeException("Impossible to convert "+value+" from "+(null == value ? "unknown" : value.getClass().getName())+" to "+char.class.getName());
	}

	public static boolean toBoolean(Object value, boolean defaultValue) {		
		try {			
			return toBoolean(value);			
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static byte toByte(Object value, byte defaultValue) {		
		try {
			return toByte(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static short toShort(Object value, short defaultValue) {		
		try {
			return toShort(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int toInt(Object value, int defaultValue) {		
		try {
			return toInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int toInt(Object value) throws RuntimeException {		
		if (null == value) {
			throw new RuntimeException("Impossible to convert "+value+" from "+(null == value ? "unknown" : value.getClass().getName())+" to "+int.class.getName());
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Impossible to convert "+value+" from "+(null == value ? "unknown" : value.getClass().getName())+" to "+int.class.getName());
			}
		}	
		throw new RuntimeException("Impossible to convert "+value+" from "+(null == value ? "unknown" : value.getClass().getName())+" to "+int.class.getName());
	}

	public static long toLong(Object value, long defaultValue) {
		try {
			return toLong(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float toFloat(Object value, float defaultValue) {
		try {
			return toFloat(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double toDouble(Object value, double defaultValue) {
		try {
			return toDouble(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Locale toLocale(Object value) {
		if (value == null)	return null;
		if (value instanceof Locale) return (Locale) value;
		if (value instanceof String) {
			String[] elements = ((String)value).split("_ ");
			String language = elements.length > 0 ? elements[0] : "";
			String country  = elements.length > 1 ? elements[1] : "";
			String variant  = elements.length > 2 ? elements[2] : "";
			if (language.length() > 0) {
				return new Locale(language, country, variant);
			} else {
				throw new RuntimeException("The value " + value+ " can't be converted to a Locale");
			}
		}
		throw new RuntimeException("The value " + value+ " can't be converted to a Locale");
	}

	public static Color toColor(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Color) {
			return (Color) value;
		}
		if (value instanceof String && !StringUtils.isBlank((String) value)) {
			String color = ((String) value).trim();
			int[] components = new int[3];
			// check the size of the string
			int minlength = components.length * 2;			
			if (color.length() < minlength) {
				throw new RuntimeException("The value " + value
						+ " can't be converted to a Color");
			}
			// remove the leading #
			if (color.startsWith("#")) {
				color = color.substring(1);
			}
			try {
				// parse the components
				for (int i = 0; i < components.length; i++) {
					components[i] = Integer.parseInt(color.substring(2 * i, 2 * i + 2), HEX_RADIX);
				}
				// parse the transparency
				int alpha;
				if (color.length() >= minlength + 2) {
					alpha = Integer.parseInt(color.substring(minlength,
							minlength + 2), HEX_RADIX);
				} else {
					alpha = Color.black.getAlpha();
				}
				return new Color(components[0], components[1], components[2], alpha);
			} catch (Exception e) {
				throw new RuntimeException("The value " + value + " can't be converted to a Color", e);
			}
		} 
		throw new RuntimeException("The value " + value + " can't be converted to a Color");
	}

	public static String toHEX(int[] rgb) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(COLOR_KEY.substring(
				(int)Math.floor(rgb[0] / 16),
				(int)Math.floor(rgb[0] / 16) + 1));

		sb.append(COLOR_KEY.substring(rgb[0] % 16, (rgb[0] % 16) + 1));
		sb.append(COLOR_KEY.substring(
				(int)Math.floor(rgb[1] / 16),
				(int)Math.floor(rgb[1] / 16) + 1));

		sb.append(COLOR_KEY.substring(rgb[1] % 16, (rgb[1] % 16) + 1));
		sb.append(COLOR_KEY.substring(
				(int)Math.floor(rgb[2] / 16),
				(int)Math.floor(rgb[2] / 16) + 1));

		sb.append(COLOR_KEY.substring(rgb[2] % 16, (rgb[2] % 16) + 1));
		return sb.toString();
	}
	/**
	 * HEX string to RGB array
	 * @param hex
	 * @return
	 */
	public static int[] toRGB(String hex) {		
		if (hex.startsWith("#")) 
			hex = hex.substring(1, hex.length()).toUpperCase();
		else 
			hex = hex.toUpperCase();		

		int[] hexArray = new int[6];
		char[] chars = hex.toCharArray();

		for (int i = 0; i < hex.length(); i++) {
			if (chars[i] == 'A') {
				hexArray[i] = 10;
			}
			else if (chars[i] == 'B') {
				hexArray[i] = 11;
			}
			else if (chars[i] == 'C') {
				hexArray[i] = 12;
			}
			else if (chars[i] == 'D') {
				hexArray[i] = 13;
			}
			else if (chars[i] == 'E') {
				hexArray[i] = 14;
			}
			else if (chars[i] == 'F') {
				hexArray[i] = 15;
			}
			else {
				hexArray[i] = Integer.parseInt(Character.valueOf(chars[i]).toString());
			}
		}

		int[] rgb = new int[3];
		
		rgb[0] = (hexArray[0] * 16) + hexArray[1];
		rgb[1] = (hexArray[2] * 16) + hexArray[3];
		rgb[2] = (hexArray[4] * 16) + hexArray[5];

		return rgb;
	}
}
