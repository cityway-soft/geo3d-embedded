package org.avm.elementary.can.parser;

public class Bitstream {
	public static final long[] _mask = { 0x00000000, 0x00000001, 0x00000003,
			0x00000007, 0x0000000f, 0x0000001f, 0x0000003f, 0x0000007f,
			0x000000ff, 0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff,
			0x00001fff, 0x00003fff, 0x00007fff, 0x0000ffff, 0x0001ffff,
			0x0003ffff, 0x0007ffff, 0x000fffff, 0x001fffff, 0x003fffff,
			0x007fffff, 0x00ffffff, 0x01ffffff, 0x03ffffff, 0x07ffffff,
			0x0fffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };

	public static final long[] _cmask = { 0xffffffff, 0xfffffffe, 0xfffffffc,
			0xfffffff8, 0xfffffff0, 0xffffffe0, 0xffffffc0, 0xffffff80,
			0xffffff00, 0xfffffe00, 0xfffffc00, 0xfffff800, 0xfffff000,
			0xffffe000, 0xffffc000, 0xffff8000, 0xffff0000, 0xfffe0000,
			0xfffc0000, 0xfff80000, 0xfff00000, 0xffe00000, 0xffc00000,
			0xff800000, 0xff000000, 0xfe000000, 0xfc000000, 0xf8000000,
			0xf0000000, 0xe0000000, 0xc0000000, 0x80000000, 0x00000000 };

	private byte[] _buffer;

	public byte[] getBuffer() {
		return _buffer;
	}

	public void setBuffer(byte[] buffer) {
		_buffer = buffer;
	}

	public void initialize(byte[] buffer) {
		_buffer = buffer;
	}

	public long getbits(int start, int length) {
		long value = 0;

		int i = (start >>> 3);
		int end = ((start + length - 1) >>> 3);
		int room = 8 - (start % 8);
		int over = (start + length) % 8;

		// aligned bytes
		if (start % 8 == 0 && length % 8 == 0) {
			for (; i <= end; i++) {
				value <<= 8;
				value |= (long) (_buffer[i] & _mask[8]);
			}
			return value;
		}

		// only one byte
		if (room >= length) {
			value = (_buffer[i] >> (8 - room)) & _mask[length];
			return value;
		}

		// first byte
		value |= (_buffer[i] >> (8 - room)) & _mask[room];

		// next bytes
		for (i++; i < end; i++) {
			value <<= 8;
			value |= (long) (_buffer[i] & _mask[8]);
		}

		// last byte
		value <<= over;
		value |= (long) (_buffer[i] & _mask[over]);

		return value;
	}

	public long little_getbits(int start, int length) {
		long value = 0;

		int i = (start >>> 3);
		int end = ((start + length - 1) >>> 3);
		int room = 8 - (start % 8);
		int over = (start + length) % 8;

		// aligned bytes
		if (start % 8 == 0 && length % 8 == 0) {
			int offset = 0;
			for (; i <= end; i++, offset += 8) {
				value |= (_buffer[i] & _mask[8]) << offset;
			}
			return value;
		}

		// only one byte
		if (room >= length) {
			value = (_buffer[i] >> (8 - room)) & _mask[length];
			return value;
		}

		// first byte
		value |= (_buffer[i] >> (8 - room)) & _mask[room];

		// next bytes
		int offset = room;
		for (i++; i < end; i++) {
			value |= (_buffer[i] & _mask[8]) << offset;
			offset += 8;
		}

		// last byte
		value |= (_buffer[i] & _mask[over]) << offset;

		return value;
	}

	public void setbits(long value, int start, int length) {

		int j = ((start + length - 1) >>> 3);
		int begin = (start >>> 3);
		int room = 8 - (start % 8);
		int over = (start + length) % 8;

		// aligned bytes
		if (start % 8 == 0 && length % 8 == 0) {
			for (; j >= begin; j--, value >>>= 8) {
				_buffer[j] = 0;
				_buffer[j] |= value & _mask[8];
			}
			return;
		}

		// only one byte
		if (room >= length) {
			_buffer[j] &= ~(_mask[length] << 8 - room);
			_buffer[j] |= (value & _mask[length]) << 8 - room;
			return;
		}

		// first byte
		_buffer[j] &= _cmask[over];
		_buffer[j] |= value & _mask[over];
		value >>>= over;

		// next bytes
		for (j--; j > begin; j--) {
			_buffer[j] = 0;
			_buffer[j] |= value & _mask[8];
			value >>>= 8;
		}

		// last byte
		_buffer[j] &= _mask[8 - room];
		_buffer[j] |= (value & _mask[room]) << 8 - room;

	}

	public void little_setbits(long value, int start, int length) {

		int i = (start >>> 3);
		int end = ((start + length - 1) >>> 3);
		int room = 8 - (start % 8);
		int over = (start + length) % 8;

		// aligned bytes
		if (start % 8 == 0 && length % 8 == 0) {
			for (; i <= end; i++, value >>>= 8) {
				_buffer[i] = 0;
				_buffer[i] |= value & _mask[8];
			}
			return;
		}
		// only one byte
		if (room >= length) {
			_buffer[i] &= ~(_mask[length] << 8 - room);
			_buffer[i] |= (value & _mask[length]) << 8 - room;
			return;
		}

		// first byte
		_buffer[i] &= _mask[8 - room];
		_buffer[i] |= (value & _mask[room]) << 8 - room;
		value >>= room;

		// next bytes
		for (i++; i < end; i++) {
			_buffer[i] = 0;
			_buffer[i] |= value & _mask[8];
			value >>>= 8;
		}

		// last byte
		_buffer[i] &= _cmask[over];
		_buffer[i] |= value & _mask[over];

	}
}
