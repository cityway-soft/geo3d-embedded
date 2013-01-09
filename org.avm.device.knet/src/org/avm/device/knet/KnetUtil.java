package org.avm.device.knet;

public class KnetUtil {
	static char digits[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'+', '/' };

	private static final byte equalSign = (byte) '=';

	public static byte[] encodeBase64(byte[] data) {
		int sourceChunks = data.length / 3;
		int len = ((data.length + 2) / 3) * 4;
		byte[] result = new byte[len];
		int extraBytes = data.length - (sourceChunks * 3);
		// Each 4 bytes of input (encoded) we end up with 3 bytes of output
		int dataIndex = 0;
		int resultIndex = 0;
		int allBits = 0;
		for (int i = 0; i < sourceChunks; i++) {
			allBits = 0;
			// Loop 3 times gathering input bits (3 * 8 = 24)
			for (int j = 0; j < 3; j++) {
				allBits = (allBits << 8) | (data[dataIndex++] & 0xff);
			}

			// Loop 4 times generating output bits (4 * 6 = 24)
			for (int j = resultIndex + 3; j >= resultIndex; j--) {
				result[j] = (byte) digits[(allBits & 0x3f)]; // Bottom 6 bits
				allBits = allBits >>> 6;
			}
			resultIndex += 4; // processed 4 result bytes
		}
		// Now we do the extra bytes in case the original (non-encoded) data
		// is not multiple of 4 bytes
		switch (extraBytes) {
		case 1:
			allBits = data[dataIndex++]; // actual byte
			allBits = allBits << 8; // 8 bits of zeroes
			allBits = allBits << 8; // 8 bits of zeroes
			// Loop 4 times generating output bits (4 * 6 = 24)
			for (int j = resultIndex + 3; j >= resultIndex; j--) {
				result[j] = (byte) digits[(allBits & 0x3f)]; // Bottom 6 bits
				allBits = allBits >>> 6;
			}
			// 2 pad tags
			result[result.length - 1] = (byte) '=';
			result[result.length - 2] = (byte) '=';
			break;
		case 2:
			allBits = data[dataIndex++]; // actual byte
			allBits = (allBits << 8) | (data[dataIndex++] & 0xff); // actual
			// byte
			allBits = allBits << 8; // 8 bits of zeroes
			// Loop 4 times generating output bits (4 * 6 = 24)
			for (int j = resultIndex + 3; j >= resultIndex; j--) {
				result[j] = (byte) digits[(allBits & 0x3f)]; // Bottom 6 bits
				allBits = allBits >>> 6;
			}
			// 1 pad tag
			result[result.length - 1] = (byte) '=';
			break;
		}
		return result;
	}

	public static byte[] decodeBase64(byte[] data) {
		int lastRealDataIndex;
		for (lastRealDataIndex = data.length - 1; data[lastRealDataIndex] == equalSign; lastRealDataIndex--)
			;
		// original data digit is 8 bits long, but base64 digit is 6 bits long
		int padBytes = data.length - 1 - lastRealDataIndex;
		int byteLength = data.length * 6 / 8 - padBytes;
		byte[] result = new byte[byteLength];
		// Each 4 bytes of input (encoded) we end up with 3 bytes of output
		int dataIndex = 0;
		int resultIndex = 0;
		int allBits = 0;
		// how many result chunks we can process before getting to pad bytes
		int resultChunks = (lastRealDataIndex + 1) / 4;
		for (int i = 0; i < resultChunks; i++) {
			allBits = 0;
			// Loop 4 times gathering input bits (4 * 6 = 24)
			for (int j = 0; j < 4; j++) {
				allBits = (allBits << 6) | decodeDigit(data[dataIndex++]);
			}

			// Loop 3 times generating output bits (3 * 8 = 24)
			for (int j = resultIndex + 2; j >= resultIndex; j--) {
				result[j] = (byte) (allBits & 0xff); // Bottom 8 bits
				allBits = allBits >>> 8;
			}
			resultIndex += 3; // processed 3 result bytes
		}
		// Now we do the extra bytes in case the original (non-encoded) data
		// was not multiple of 3 bytes

		switch (padBytes) {
		case 1: // 1 pad byte means 3 (4-1) extra Base64 bytes of input, 18
			// bits, of which only 16 are meaningful
			// Or: 2 bytes of result data
			allBits = 0;
			// Loop 3 times gathering input bits
			for (int j = 0; j < 3; j++) {
				allBits = (allBits << 6) | decodeDigit(data[dataIndex++]);
			}
			// NOTE - The code below ends up being equivalent to allBits =
			// allBits>>>2
			// But we code it in a non-optimized way for clarity

			// The 4th, missing 6 bits are all 0
			allBits = allBits << 6;

			// The 3rd, missing 8 bits are all 0
			allBits = allBits >>> 8;
			// Loop 2 times generating output bits
			for (int j = resultIndex + 1; j >= resultIndex; j--) {
				result[j] = (byte) (allBits & 0xff); // Bottom 8 bits
				allBits = allBits >>> 8;
			}
			break;

		case 2: // 2 pad bytes mean 2 (4-2) extra Base64 bytes of input, 12 bits
			// of data, of which only 8 are meaningful
			// Or: 1 byte of result data
			allBits = 0;
			// Loop 2 times gathering input bits
			for (int j = 0; j < 2; j++) {
				allBits = (allBits << 6) | decodeDigit(data[dataIndex++]);
			}
			// NOTE - The code below ends up being equivalent to allBits =
			// allBits>>>4
			// But we code it in a non-optimized way for clarity

			// The 3rd and 4th, missing 6 bits are all 0
			allBits = allBits << 6;
			allBits = allBits << 6;

			// The 3rd and 4th, missing 8 bits are all 0
			allBits = allBits >>> 8;
			allBits = allBits >>> 8;
			result[resultIndex] = (byte) (allBits & 0xff); // Bottom 8 bits
			break;
		}
		return result;
	}

	static int decodeDigit(byte data) {
		char charData = (char) data;
		if (charData <= 'Z' && charData >= 'A')
			return (int) (charData - 'A');

		if (charData <= 'z' && charData >= 'a')
			return (int) (charData - 'a' + 26);
		if (charData <= '9' && charData >= '0')
			return (int) (charData - '0' + 52);

		switch (charData) {
		case '+':
			return 62;
		case '/':
			return 63;
		default:
			throw new IllegalArgumentException();
		}
	}

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
}
