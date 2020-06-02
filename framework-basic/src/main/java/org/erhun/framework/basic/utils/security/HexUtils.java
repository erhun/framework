package org.erhun.framework.basic.utils.security;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author weichao <gorilla@aliyun.com>
 */
public class HexUtils {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final String DEFAULT_CHARSET_NAME = "UTF-8";

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte[] decodeHex(final char[] data) throws Exception {

        final int len = data.length;

        if ((len & 0x01) != 0) {
            throw new Exception("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    protected static int toDigit(final char ch, final int index) throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    private final Charset charset;

    public HexUtils() {
        this.charset = DEFAULT_CHARSET;
    }

    public HexUtils(final Charset charset) {
        this.charset = charset;
    }

    public HexUtils(final String charsetName) {
        this(Charset.forName(charsetName));
    }

    public byte[] decode(final byte[] array) throws Exception {
        return decodeHex(new String(array, getCharset()).toCharArray());
    }

    public Object decode(final Object object) throws Exception {
        try {
            final char[] charArray = object instanceof String ? ((String) object).toCharArray() : (char[]) object;
            return decodeHex(charArray);
        } catch (final ClassCastException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public byte[] encode(final byte[] array) {
        return encodeHexString(array).getBytes(this.getCharset());
    }

    public Object encode(final Object object) throws Exception {
        try {
            final byte[] byteArray = object instanceof String ?
                                   ((String) object).getBytes(this.getCharset()) : (byte[]) object;
            return encodeHex(byteArray);
        } catch (final ClassCastException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharsetName() {
        return this.charset.name();
    }

    @Override
    public String toString() {
        return super.toString() + "[charsetName=" + this.charset + "]";
    }
}
