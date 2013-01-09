package org.avm.elementary.can.generator;

public class Bitstream {
	private static final byte[] mask = { 0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f,
			0x3f, 0x7f, (byte) 0xff };

	private static final byte[] CLEAN = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00 };

	private byte[] buffer;

	private int offset;

	public void initialize(byte[] buffer, int offset) {
		this.buffer = buffer;
		this.offset = offset;
	}

	public long getbits(int start, int length) {

		long value = 0;

		// TODO check param

		int begin = offset * 8 + (start >>> 3); // index begin byte (0-7)
		int room = 8 - start % 8; // nb bit in first byte (1-8)

		// only byte
		if (room >= length) {
			value = buffer[begin] >> 8 - room & mask[length];
			return value;
		}

		// first byte
		value |= buffer[begin++] >> 8 - room & mask[room];

		// next byte
		int end = start + length >>> 3; // index end byte (0-8)
		int over = (start + length) % 8; // nb bit in last byte (0-7)
		for (; begin < end; begin++) {
			value <<= 8;
			value |= buffer[begin] & mask[8];
		}

		// last byte
		if (over > 0) {
			value <<= over;
			value |= buffer[begin] & mask[over];
		}

		return value;

	}

	public long little_getbits(int start, int length) {
		long value = 0;
		int offset = 0;

		// TODO check param

		int begin = offset * 8 + start >>> 3; // index begin byte (0-7)
		int room = 8 - start % 8; // nb bit in first byte (1-8)

		// only byte
		if (room >= length) {
			value = buffer[begin] >> 8 - room & mask[length];
			return value;
		}

		// first byte
		value |= buffer[begin++] >> 8 - room & mask[room];
		offset += room;

		// next byte
		int end = start + length >>> 3; // index end byte (0-8)
		int over = (start + length) % 8; // nb bit in last byte (0-7)
		for (; begin < end; begin++) {
			value |= (long) (buffer[begin] & mask[8]) << offset;
			offset += 8;
		}

		// last byte
		if (over > 0) {
			value |= (long) (buffer[begin] & mask[over]) << offset;
		}

		return value;

	}

	public void setbits(long value, int start, int length) {
		// TODO
	}

	public void little_setbits(long value, int start, int length) {
		// TODO
	}

}
