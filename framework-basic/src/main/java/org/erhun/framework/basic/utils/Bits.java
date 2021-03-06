package org.erhun.framework.basic.utils;

public class Bits {

	public static boolean getBoolean(byte[] b, int off) {
		return b[off] != 0;
	}

	public static char getChar(byte[] b, int off) {
		return (char) (((b[off + 1] & 0xFF)) +
				((b[off]) << 8));
	}

	public static short getShort(byte[] b, int off) {
		return (short) (((b[off + 1] & 0xFF)) +
				((b[off]) << 8));
	}

	public static int getInt(byte[] b, int off) {
		return ((b[off + 3] & 0xFF)) +
				((b[off + 2] & 0xFF) << 8) +
				((b[off + 1] & 0xFF) << 16) +
				((b[off]) << 24);
	}

	public static float getFloat(byte[] b, int off) {
		int i = ((b[off + 3] & 0xFF)) +
				((b[off + 2] & 0xFF) << 8) +
				((b[off + 1] & 0xFF) << 16) +
				((b[off]) << 24);
		return Float.intBitsToFloat(i);
	}
	
	public static long getLong(byte[] b) {
	    return getLong(b, 0);
	}
	
	public static long getLong(byte[] b, int off) {
		return ((b[off + 7] & 0xFFL)) +
				((b[off + 6] & 0xFFL) << 8) +
				((b[off + 5] & 0xFFL) << 16) +
				((b[off + 4] & 0xFFL) << 24) +
				((b[off + 3] & 0xFFL) << 32) +
				((b[off + 2] & 0xFFL) << 40) +
				((b[off + 1] & 0xFFL) << 48) +
				(((long) b[off]) << 56);
	}

	public static double getDouble(byte[] b, int off) {
		long j = ((b[off + 7] & 0xFFL)) +
				((b[off + 6] & 0xFFL) << 8) +
				((b[off + 5] & 0xFFL) << 16) +
				((b[off + 4] & 0xFFL) << 24) +
				((b[off + 3] & 0xFFL) << 32) +
				((b[off + 2] & 0xFFL) << 40) +
				((b[off + 1] & 0xFFL) << 48) +
				(((long) b[off]) << 56);
		return Double.longBitsToDouble(j);
	}

	public static void putBoolean(byte[] b, int off, boolean val) {
		b[off] = (byte) (val ? 1 : 0);
	}

	public static void putChar(byte[] b, int off, char val) {
		b[off + 1] = (byte) (val);
		b[off] = (byte) (val >>> 8);
	}

	public static void putShort(byte[] b, int off, short val) {
		b[off + 1] = (byte) (val);
		b[off] = (byte) (val >>> 8);
	}

	public static void putInt(byte[] b, int off, int val) {
		b[off + 3] = (byte) (val);
		b[off + 2] = (byte) (val >>> 8);
		b[off + 1] = (byte) (val >>> 16);
		b[off] = (byte) (val >>> 24);
	}

	public static void putFloat(byte[] b, int off, float val) {
		int i = Float.floatToIntBits(val);
		b[off + 3] = (byte) (i);
		b[off + 2] = (byte) (i >>> 8);
		b[off + 1] = (byte) (i >>> 16);
		b[off] = (byte) (i >>> 24);
	}

	public static void putLong(byte[] b, int off, long val) {
		b[off + 7] = (byte) (val);
		b[off + 6] = (byte) (val >>> 8);
		b[off + 5] = (byte) (val >>> 16);
		b[off + 4] = (byte) (val >>> 24);
		b[off + 3] = (byte) (val >>> 32);
		b[off + 2] = (byte) (val >>> 40);
		b[off + 1] = (byte) (val >>> 48);
		b[off] = (byte) (val >>> 56);
	}

	public static void putDouble(byte[] b, int off, double val) {
		long j = Double.doubleToLongBits(val);
		b[off + 7] = (byte) (j);
		b[off + 6] = (byte) (j >>> 8);
		b[off + 5] = (byte) (j >>> 16);
		b[off + 4] = (byte) (j >>> 24);
		b[off + 3] = (byte) (j >>> 32);
		b[off + 2] = (byte) (j >>> 40);
		b[off + 1] = (byte) (j >>> 48);
		b[off] = (byte) (j >>> 56);
	}
	
	public static byte [] getBytes(int val) {
	    byte b [] = new byte[8];
        b[3] = (byte) (val);
        b[2] = (byte) (val >>> 8);
        b[1] = (byte) (val >>> 16);
        b[0] = (byte) (val >>> 24);
        return b;
    }
	
	public static byte [] getBytes(double val) {
        long j = Double.doubleToLongBits(val);
        byte b [] = new byte[8];
        b[7] = (byte) (j);
        b[6] = (byte) (j >>> 8);
        b[5] = (byte) (j >>> 16);
        b[4] = (byte) (j >>> 24);
        b[3] = (byte) (j >>> 32);
        b[2] = (byte) (j >>> 40);
        b[1] = (byte) (j >>> 48);
        b[0] = (byte) (j >>> 56);
        return b;
    }
	
	public static void main(String[] args) {
        
	    
	    double d = 222.3333;
	    
	    byte [] s = getBytes(d);
	    
	    System.out.println(Bits.getDouble(s, 0));
	    
	    
	    
    }
	
	public static byte [] getBytes(long val) {
	    byte b [] = new byte[8];
        b[7] = (byte) (val);
        b[6] = (byte) (val >>> 8);
        b[5] = (byte) (val >>> 16);
        b[4] = (byte) (val >>> 24);
        b[3] = (byte) (val >>> 32);
        b[2] = (byte) (val >>> 40);
        b[1] = (byte) (val >>> 48);
        b[0] = (byte) (val >>> 56);
        return b;
    }
}
