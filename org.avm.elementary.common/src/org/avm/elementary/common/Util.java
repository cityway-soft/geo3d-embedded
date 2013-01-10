package org.avm.elementary.common;

public class Util {

	public static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	public static byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));

		}
		return data;
	}

	public static short swapShort(short value) {
		return (short) ((((value >> 0) & 0xff) << 8) + (((value >> 8) & 0xff) << 0));
	}

	public static int swapInt(int value) {
		return (((value >> 0) & 0xff) << 24) + (((value >> 8) & 0xff) << 16)
				+ (((value >> 16) & 0xff) << 8) + (((value >> 24) & 0xff) << 0);
	}

	public static long swapLong(long value) {
		return (((value >> 0) & 0xff) << 56) + (((value >> 8) & 0xff) << 48)
				+ (((value >> 16) & 0xff) << 40)
				+ (((value >> 24) & 0xff) << 32)
				+ (((value >> 32) & 0xff) << 24)
				+ (((value >> 40) & 0xff) << 16)
				+ (((value >> 48) & 0xff) << 8) + (((value >> 56) & 0xff) << 0);
	}

	public static float swapFloat(float value) {
		return Float.intBitsToFloat(swapInt(Float.floatToIntBits(value)));
	}

	public static double swapDouble(double value) {
		return Double
				.longBitsToDouble(swapLong(Double.doubleToLongBits(value)));
	}

	public static void putShort(byte[] b, int off, boolean be, short value) {
		if (be) {
			b[off++] = (byte) ((value >> 8) & 0xff);
			b[off] = (byte) (value & 0xff);
		} else {
			b[off++] = (byte) (value & 0xff);
			b[off] = (byte) ((value >> 8) & 0xff);
		}
	}

	public static short getShort(byte[] b, int off, boolean be) {
		if (be) {
			return (short) ((b[off++] << 8) | (b[off] & 0x00ff));
		} else {
			return (short) ((b[off++] & 0x00ff) | (b[off] << 8));
		}
	}

	public static void putInt(byte[] b, int off, boolean be, int value) {
		if (be) {
			b[off++] = (byte) ((value >> 24) & 0xff);
			b[off++] = (byte) ((value >> 16) & 0xff);
			b[off++] = (byte) ((value >> 8) & 0xff);
			b[off] = (byte) (value & 0xff);
		} else {
			b[off++] = (byte) (value & 0xff);
			b[off++] = (byte) ((value >> 8) & 0xff);
			b[off++] = (byte) ((value >> 16) & 0xff);
			b[off] = (byte) ((value >> 24) & 0xff);
		}
	}

	public static int getInt(byte[] b, int off, boolean be) {
		if (be) {
			return (b[off++] << 24) | ((b[off++] << 16) & 0x00ff0000)
					| ((b[off++] << 8) & 0x0000ff00) | (b[off] & 0x000000ff);
		} else {
			return (b[off++] & 0x000000ff) | ((b[off++] << 8) & 0x0000ff00)
					| ((b[off++] << 16) & 0x00ff0000) | (b[off] << 24);
		}
	}

	public static void putLong(byte[] b, int off, boolean be, long value) {
		if (be) {
			b[off++] = (byte) ((value >> 56) & 0xff);
			b[off++] = (byte) ((value >> 48) & 0xff);
			b[off++] = (byte) ((value >> 40) & 0xff);
			b[off++] = (byte) ((value >> 32) & 0xff);
			b[off++] = (byte) ((value >> 24) & 0xff);
			b[off++] = (byte) ((value >> 16) & 0xff);
			b[off++] = (byte) ((value >> 8) & 0xff);
			b[off++] = (byte) (value & 0xff);
		} else {
			b[off++] = (byte) (value & 0xff);
			b[off++] = (byte) ((value >> 8) & 0xff);
			b[off++] = (byte) ((value >> 16) & 0xff);
			b[off++] = (byte) ((value >> 24) & 0xff);
			b[off++] = (byte) ((value >> 32) & 0xff);
			b[off++] = (byte) ((value >> 40) & 0xff);
			b[off++] = (byte) ((value >> 48) & 0xff);
			b[off] = (byte) ((value >> 56) & 0xff);
		}
	}

	public static long getLong(byte[] b, int off, boolean be) {
		if (be) {
			long high = getInt(b, off, be);
			long low = getInt(b, off + 4, be);
			return low | (high << 32);
		} else {
			long low = getInt(b, off, be);
			long high = getInt(b, off + 4, be);
			return low | (high << 32);
		}
	}

	public static void putFloat(byte[] b, int off, boolean be, float value) {
		putInt(b, off, be, Float.floatToIntBits(value));
	}

	public static float getFloat(byte[] b, int off, boolean be) {
		return Float.intBitsToFloat(getInt(b, off, be));
	}

	public static void putDouble(byte[] b, int off, boolean be, double value) {
		putLong(b, off, be, Double.doubleToLongBits(value));
	}

	public static double getDouble(byte[] b, int off, boolean be) {
		return Double.longBitsToDouble(getLong(b, off, be));
	}
}