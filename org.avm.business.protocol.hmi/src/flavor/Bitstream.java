/*
 * Copyright (c) 1997-2004 Alexandros Eleftheriadis, Danny Hong and Yuntai
 * Kyong. This file is part of Flavor, developed at Columbia University
 * (http://flavor.sourceforge.net). Flavor is free software; you can
 * redistribute it and/or modify it under the terms of the Flavor Artistic
 * License as described in the file COPYING.txt.
 */

/*
 * Authors: Alexandros Eleftheriadis <eleft@ee.columbia.edu> Danny Hong
 * <danny@ee.columbia.edu> Yuntai Kyong <yuntaikyong@ieee.org>
 */

package flavor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Bitstream implements IBitstream {

	public static final int BUF_LEN = 1024; // Default buffer size

	public static int MAX_SIZE_OF_BITS = 32;

	// Masks for bitstream manipulation
	public static final int[] mask = { 0x00000000, 0x00000001, 0x00000003,
			0x00000007, 0x0000000f, 0x0000001f, 0x0000003f, 0x0000007f,
			0x000000ff, 0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff,
			0x00001fff, 0x00003fff, 0x00007fff, 0x0000ffff, 0x0001ffff,
			0x0003ffff, 0x0007ffff, 0x000fffff, 0x001fffff, 0x003fffff,
			0x007fffff, 0x00ffffff, 0x01ffffff, 0x03ffffff, 0x07ffffff,
			0x0fffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };

	// Complement masks (used for sign extension)
	public static final int[] cmask = { 0xffffffff, 0xfffffffe, 0xfffffffc,
			0xfffffff8, 0xfffffff0, 0xffffffe0, 0xffffffc0, 0xffffff80,
			0xffffff00, 0xfffffe00, 0xfffffc00, 0xfffff800, 0xfffff000,
			0xffffe000, 0xffffc000, 0xffff8000, 0xffff0000, 0xfffe0000,
			0xfffc0000, 0xfff80000, 0xfff00000, 0xffe00000, 0xffc00000,
			0xff800000, 0xff000000, 0xfe000000, 0xfc000000, 0xf8000000,
			0xf0000000, 0xe0000000, 0xc0000000, 0x80000000, 0x00000000 };

	// Sign masks (used for sign extension)
	public static final int[] smask = { 0x00000000, 0x00000001, 0x00000002,
			0x00000004, 0x00000008, 0x00000010, 0x00000020, 0x00000040,
			0x00000080, 0x00000100, 0x00000200, 0x00000400, 0x00000800,
			0x00001000, 0x00002000, 0x00004000, 0x00008000, 0x00010000,
			0x00020000, 0x00040000, 0x00080000, 0x00100000, 0x00200000,
			0x00400000, 0x00800000, 0x01000000, 0x02000000, 0x04000000,
			0x08000000, 0x10000000, 0x20000000, 0x40000000, 0x80000000 };

	// Complement masks (used for sign extension)
	public static final long[] clongmask = { 0xffffffffffffffffL,
			0xfffffffffffffffeL, 0xfffffffffffffffcL, 0xfffffffffffffff8L,
			0xfffffffffffffff0L, 0xffffffffffffffe0L, 0xffffffffffffffc0L,
			0xffffffffffffff80L, 0xffffffffffffff00L, 0xfffffffffffffe00L,
			0xfffffffffffffc00L, 0xfffffffffffff800L, 0xfffffffffffff000L,
			0xffffffffffffe000L, 0xffffffffffffc000L, 0xffffffffffff8000L,
			0xffffffffffff0000L, 0xfffffffffffe0000L, 0xfffffffffffc0000L,
			0xfffffffffff80000L, 0xfffffffffff00000L, 0xffffffffffe00000L,
			0xffffffffffc00000L, 0xffffffffff800000L, 0xffffffffff000000L,
			0xfffffffffe000000L, 0xfffffffffc000000L, 0xfffffffff8000000L,
			0xfffffffff0000000L, 0xffffffffe0000000L, 0xffffffffc0000000L,
			0xffffffff80000000L, 0xffffffff00000000L, 0xfffffffe00000000L,
			0xfffffffc00000000L, 0xfffffff800000000L, 0xfffffff000000000L,
			0xffffffe000000000L, 0xffffffc000000000L, 0xffffff8000000000L,
			0xffffff0000000000L, 0xfffffe0000000000L, 0xfffffc0000000000L,
			0xfffff80000000000L, 0xfffff00000000000L, 0xffffe00000000000L,
			0xffffc00000000000L, 0xffff800000000000L, 0xffff000000000000L,
			0xfffe000000000000L, 0xfffc000000000000L, 0xfff8000000000000L,
			0xfff0000000000000L, 0xffe0000000000000L, 0xffc0000000000000L,
			0xff80000000000000L, 0xff00000000000000L, 0xfe00000000000000L,
			0xfc00000000000000L, 0xf800000000000000L, 0xf000000000000000L,
			0xe000000000000000L, 0xc000000000000000L, 0x8000000000000000L,
			0x0000000000000000L };

	// Sign masks (used for sign extension)
	public static final long[] slongmask = { 0x0000000000000000L,
			0x0000000000000001L, 0x0000000000000002L, 0x0000000000000004L,
			0x0000000000000008L, 0x0000000000000010L, 0x0000000000000020L,
			0x0000000000000040L, 0x0000000000000080L, 0x0000000000000100L,
			0x0000000000000200L, 0x0000000000000400L, 0x0000000000000800L,
			0x0000000000001000L, 0x0000000000002000L, 0x0000000000004000L,
			0x0000000000008000L, 0x0000000000010000L, 0x0000000000020000L,
			0x0000000000040000L, 0x0000000000080000L, 0x0000000000100000L,
			0x0000000000200000L, 0x0000000000400000L, 0x0000000000800000L,
			0x0000000001000000L, 0x0000000002000000L, 0x0000000004000000L,
			0x0000000008000000L, 0x0000000010000000L, 0x0000000020000000L,
			0x0000000040000000L, 0x0000000080000000L, 0x0000000100000000L,
			0x0000000200000000L, 0x0000000400000000L, 0x0000000800000000L,
			0x0000001000000000L, 0x0000002000000000L, 0x0000004000000000L,
			0x0000008000000000L, 0x0000010000000000L, 0x0000020000000000L,
			0x0000040000000000L, 0x0000080000000000L, 0x0000100000000000L,
			0x0000200000000000L, 0x0000400000000000L, 0x0000800000000000L,
			0x0001000000000000L, 0x0002000000000000L, 0x0004000000000000L,
			0x0008000000000000L, 0x0010000000000000L, 0x0020000000000000L,
			0x0040000000000000L, 0x0080000000000000L, 0x0100000000000000L,
			0x0200000000000000L, 0x0400000000000000L, 0x0800000000000000L,
			0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L,
			0x8000000000000000L };

	protected int type; // Input or Output

	protected boolean close_fd = false; // True if file needs to be closed

	protected boolean eof = false; // EOF of data flag

	protected byte[] buf; // Input buffer

	protected int buf_len = BUF_LEN; // Usable buffer size (for partially

	// filled

	// buffers)

	protected int cur_bit; // Current bit position in buffer

	protected int total_bits; // Total bits read/written

	protected InputStream in = null; // Input file handle

	protected OutputStream out = null; // Output file handle

	/**
	 * Constructor
	 */
	protected Bitstream() {

	}

	/**
	 * Constructor
	 * 
	 * @param _out
	 *            output
	 */
	public Bitstream(OutputStream _out) throws FlIOException {
		this(_out, BUF_LEN);
	}

	/**
	 * Constructor
	 * 
	 * @param _out
	 *            output
	 * @param _buf_len
	 *            buffer size
	 */
	public Bitstream(OutputStream _out, int _buf_len) throws FlIOException {
		cur_bit = 0;
		total_bits = 0;
		type = BS_OUTPUT;
		buf_len = _buf_len;
		buf = new byte[buf_len];

		out = new BufferedOutputStream(_out);

		close_fd = true;
	}

	/**
	 * Constructor
	 * 
	 * @param _in
	 *            input
	 */
	public Bitstream(InputStream _in) throws FlIOException {
		this(_in, BUF_LEN);
	}

	/**
	 * Constructor
	 * 
	 * @param _in
	 *            input
	 * @param _buf_len
	 *            buffer size
	 */
	public Bitstream(InputStream _in, int _buf_len) throws FlIOException {
		cur_bit = 0;
		total_bits = 0;
		type = BS_INPUT;
		buf_len = _buf_len;
		buf = new byte[buf_len];

		try {
			in = new BufferedInputStream(_in);
			cur_bit = BUF_LEN << 3; // Fake we are at the eof of buffer
			fill_buf();
		} catch (IOException e) {
			throw new FlIOException(FlIOException.FILEOPENFAILED, e.toString());
		}

		close_fd = true;
	}

	/**
	 * Constructor
	 * 
	 * @param filename
	 *            File name to open
	 * @param _type
	 *            I/O type (BS_INPUT or BS_OUTPUT)
	 */
	public Bitstream(String filename, int _type) throws FlIOException {
		this(filename, _type, BUF_LEN);
	}

	/**
	 * Constructor
	 * 
	 * @param filename
	 *            File name to open
	 * @param _type
	 *            I/O type (BS_INPUT or BS_OUTPUT)
	 * @param _buf_len
	 *            Buffer size
	 */
	public Bitstream(String filename, int _type, int _buf_len)
			throws FlIOException {
		type = _type;
		buf = new byte[_buf_len];
		buf_len = _buf_len;
		cur_bit = 0;
		total_bits = 0;

		switch (type) {
		case BS_INPUT:
			try {
				in = new BufferedInputStream(new FileInputStream(filename));
				cur_bit = BUF_LEN << 3; // Fake we are at the eof of buffer
				fill_buf();
			} catch (IOException e) {
				throw new FlIOException(FlIOException.FILEOPENFAILED, e
						.toString());
			}
			break;
		case BS_OUTPUT:
			try {
				out = new BufferedOutputStream(new FileOutputStream(filename));
			} catch (IOException e) {
				throw new FlIOException(FlIOException.FILEOPENFAILED, e
						.toString());
			}
			break;
		default:
			throw new FlIOException(FlIOException.INVALIDIOTYPE);
		}
		close_fd = true;
	}

	/**
	 * Close opened files
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	/**
	 * Close file
	 */
	public void close() throws FlIOException {
		try {
			if (type == BS_OUTPUT) {
				flushbits();
				if (close_fd) {
					out.close();
					close_fd = false;
				}
			} else {
				if (close_fd) {
					in.close();
					close_fd = false;
				}
			}
		} catch (IOException e) {
			throw new FlIOException(FlIOException.SYSTEMIOFAILED, e.toString());
		}
	}

	/** *********** */
	/* Big endian */
	/** *********** */

	/**
	 * Probe 'n' bits as <b>unsigned </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public int nextbits(int n) throws FlIOException {
		if (n == 0)
			return 0;
		if (n > MAX_SIZE_OF_BITS || n < 1)
			throw new FlIOException(FlIOException.INVALIDBITSIZE);
		if (cur_bit + n > buf_len << 3)
			fill_buf();
		if ((buf_len << 3) - cur_bit < n)
			throw new FlIOException(FlIOException.NOTENOUGHDATA);

		int x = 0; // Output value
		int j = cur_bit >>> 3; // Current byte position
		int end = (cur_bit + n - 1) >>> 3; // End byte position
		int room = 8 - (cur_bit % 8); // Room in the first byte

		if (room >= n) {
			x = (buf[j] >> room - n) & mask[n];
			return x;
		}
		int leftover = (cur_bit + n) % 8; // Leftover bits in the last byte

		x |= buf[j] & mask[room]; // Fill out first byte

		for (j++; j < end; j++) {
			x <<= 8; // Shift and
			x |= buf[j] & mask[8]; // Put next byte
		}
		if (leftover > 0) {
			x <<= leftover; // Make room for remaining bits
			x |= (buf[j] >> (8 - leftover)) & mask[leftover]; // And put
		} else {
			x <<= 8; // Shift
			x |= buf[j] & mask[8]; // Put next byte
		}
		return x;
	}

	/**
	 * Probe 'n' bits as <b>signed </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public int snextbits(int n) throws FlIOException {
		int x = nextbits(n);
		if (n > 1 && ((smask[n] & x) != 0))
			return x | cmask[n];
		else
			return x;
	}

	/**
	 * Get next 'n' bits as <b>unsigned </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public int getbits(int n) throws FlIOException {
		int x = nextbits(n);
		cur_bit += n;
		total_bits += n;
		return x;
	}

	/**
	 * Get next 'n' bits as <b>signed </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public int sgetbits(int n) throws FlIOException {
		int x = snextbits(n);
		cur_bit += n;
		total_bits += n;
		return x;
	}

	/**
	 * Probe 'n'( <= 64 bits) bits as <b>unsigned </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public long nextlong(int n) throws FlIOException {
		if (n == 0)
			return 0;
		if (n > 2 * MAX_SIZE_OF_BITS || n < 1)
			throw new FlIOException(FlIOException.INVALIDBITSIZE);
		if (cur_bit + n > buf_len << 3)
			fill_buf();
		if (cur_bit + n > buf_len << 3)
			fill_buf();
		if ((buf_len << 3) - cur_bit < n)
			throw new FlIOException(FlIOException.NOTENOUGHDATA);

		long x = 0; // Output value
		int j = cur_bit >>> 3; // Current byte position
		int end = (cur_bit + n - 1) >>> 3; // End byte position
		int room = 8 - (cur_bit % 8); // Room in the first byte

		if (room >= n) {
			x = (buf[j] >> room - n) & mask[n];
			return x;
		}
		int leftover = (cur_bit + n) % 8; // Leftover bits in the last byte

		x |= buf[j] & mask[room]; // Fill out first byte

		for (j++; j < end; j++) {
			x <<= 8; // Shift and
			x |= buf[j] & mask[8]; // Put next byte
		}
		if (leftover > 0) {
			x <<= leftover; // Make room for remaining bits
			x |= (buf[j] >> (8 - leftover)) & mask[leftover]; // And put
		} else {
			x <<= 8; // Shift
			x |= buf[j] & mask[8]; // Put next byte
		}
		return x;
	}

	/**
	 * Probe 'n' ( <= 64 bits) bits as <b>signed </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public long snextlong(int n) throws FlIOException {
		long x = nextlong(n);
		if (n > 1 && ((slongmask[n] & x) != 0))
			return x | clongmask[n];
		else
			return x;
	}

	/**
	 * Get next 'n' ( <= 64 bits) bits as <b>unsigned </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public long getlong(int n) throws FlIOException {
		long l = nextlong(n);
		skipbits(n);
		return l;
	}

	/**
	 * Get next ( <= 64 bits) 'n' bits as <b>signed </b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public long sgetlong(int n) throws FlIOException {
		long l = snextlong(n);
		skipbits(n);
		return l;
	}

	/**
	 * Probe a float value from the next 32 bits (input only)
	 * 
	 * @return The float value of the 32 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public float nextfloat() throws FlIOException {
		return Float.intBitsToFloat(nextbits(32));
	}

	/**
	 * Get a float value from the next 32 bits (input only)
	 * 
	 * @return The float value of the 32 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public float getfloat() throws FlIOException {
		float f = nextfloat();
		skipbits(32);
		return f;
	}

	/**
	 * Probe a double value from the next 64 bits (input only)
	 * 
	 * @return The double value of the 64 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public double nextdouble() throws FlIOException {
		if (cur_bit + 64 > (buf_len << 3))
			fill_buf();

		long l = sgetbits(32);
		l <<= 32;

		int i = getbits(32);
		l |= i;

		cur_bit -= 64;
		total_bits -= 64;
		return Double.longBitsToDouble(l);
	}

	/**
	 * Get a double value from the next 64 bits (output only)
	 * 
	 * @return The double value of the 64 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public double getdouble() throws FlIOException {
		double d = nextdouble();
		skipbits(64);
		return d;
	}

	/**
	 * Put 'n' bits (output only)
	 * 
	 * @param value
	 *            The value to put
	 * @param n
	 *            The number of bits ( <= 32 bits) to put (MSB first)
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public int putbits(int value, int n) throws FlIOException {
		int x = value;

		// Sanity check
		if (n == 0)
			return value;
		if (n > MAX_SIZE_OF_BITS || n < 1)
			throw new FlIOException(FlIOException.INVALIDBITSIZE);

		// Make sure we have enough room
		if ((cur_bit + n) > (buf_len << 3))
			flush_buf();

		int j = (cur_bit + n - 1) >>> 3; // End byte position
		int begin = cur_bit >>> 3; // Current byte position

		int room = 8 - (cur_bit % 8); // Room in the first byte of the buffer
		if (room >= n) { // Enough room
			buf[begin] &= cmask[room];
			buf[begin] |= mask[room] & (x << room - n);
			cur_bit += n;
			total_bits += n;
			return value;
		}

		int remain = (n - room) % 8; // Number of bits to put in the last
		// byte
		if (remain > 0) {
			buf[j] = 0;
			buf[j] |= (x << 8 - remain) & mask[8]; // Put the bits in the head
			// of byte
			x >>= remain; // And eat up
			j--;
		}
		for (; j > begin; j--) {
			buf[j] = 0;
			buf[j] |= x & mask[8]; // Put next byte
			x >>>= 8; // Shift
		}
		buf[j] &= cmask[room];
		buf[j] |= x & mask[room];

		cur_bit += n;
		total_bits += n;
		return value;
	}

	/**
	 * Put 'n' bits (output only)
	 * 
	 * @param value
	 *            The long value to put
	 * @pararm n The number of bits ( <= 64 bits) to put (MSB first)
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public long putlong(long value, int n) throws FlIOException {
		if (n > 32) {
			int i = (int) (value >>> 32);
			putbits(i, n - 32);
			i = (int) (value & 0x00000000FFFFFFFF);
			putbits(i, 32);
			return value;
		} else
			return putbits((int) value, n);
	}

	/**
	 * Put a float value using 32 bits (output only) (MSB first)
	 * 
	 * @param value
	 *            The float value to put
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public float putfloat(float value) throws FlIOException {
		putbits(Float.floatToIntBits(value), 32);
		return value;
	}

	/**
	 * Put a double value using 64 bits (output only) (MSB first)
	 * 
	 * @param value
	 *            The double value to put
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public double putdouble(double value) throws FlIOException {
		long l = Double.doubleToLongBits(value);
		int i = (int) (l >>> 32);
		putbits(i, 32);
		i = (int) (l & 0x00000000FFFFFFFF);
		putbits(i, 32);
		return value;
	}

	/** ************** */
	/* Little endian */
	/** ************** */

	public int little_nextbits(int n) throws FlIOException {
		if (n == 0)
			return 0;
		if (n > MAX_SIZE_OF_BITS || n < 1)
			throw new FlIOException(FlIOException.INVALIDBITSIZE);

		int x = 0; // Output value

		int bytes = n >>> 3; // Number of bytes to read
		int leftbits = n % 8; // Number of bits to read
		int byte_x = 0;
		int i = 0;
		for (; i < bytes; i++) {
			byte_x = nextbits(8);
			cur_bit += 8;
			byte_x <<= (8 * i);
			x |= byte_x;
		}
		if (leftbits > 0) {
			byte_x = nextbits(leftbits);
			byte_x <<= (8 * i);
			x |= byte_x;
		}
		cur_bit -= i * 8;
		return x;
	}

	public int little_snextbits(int n) throws FlIOException {
		int x = little_nextbits(n);
		if (n > 1 && ((smask[n] & x) != 0))
			return x | cmask[n];
		else
			return x;
	}

	public int little_getbits(int n) throws FlIOException {
		int x = little_nextbits(n);
		cur_bit += n;
		total_bits += n;
		return x;
	}

	public int little_sgetbits(int n) throws FlIOException {
		int x = little_snextbits(n);
		cur_bit += n;
		total_bits += n;
		return x;
	}

	public long little_nextlong(int n) throws FlIOException {
		if (n == 0)
			return 0;
		if (n > 2 * MAX_SIZE_OF_BITS || n < 1)
			throw new FlIOException(FlIOException.INVALIDBITSIZE);

		long x = 0; // Output value
		int bytes = n >>> 3; // Number of bytes to read +
		int leftbits = n % 8; // Number of bits to read

		int byte_x = 0;
		int i = 0;
		for (; i < bytes; i++) {
			byte_x = nextbits(8);
			cur_bit += 8;
			byte_x <<= (8 * i);
			x |= byte_x;
		}
		if (leftbits > 0) {
			byte_x = nextbits(leftbits);
			byte_x <<= (8 * i);
			x |= byte_x;
		}
		cur_bit -= i * 8;
		return x;
	}

	public long little_snextlong(int n) throws FlIOException {
		long x = little_nextlong(n);
		if (n > 1 && ((slongmask[n] & x) != 0))
			return x | clongmask[n];
		else
			return x;
	}

	public long little_getlong(int n) throws FlIOException {
		long l = little_nextlong(n);
		skipbits(n);
		return l;
	}

	public long little_sgetlong(int n) throws FlIOException {
		long l = little_snextlong(n);
		skipbits(n);
		return l;
	}

	public float little_nextfloat() throws FlIOException {
		return Float.intBitsToFloat(little_nextbits(32));
	}

	public float little_getfloat() throws FlIOException {
		float f = little_nextfloat();
		skipbits(32);
		return f;
	}

	public double little_nextdouble() throws FlIOException {
		if (cur_bit + 64 > (buf_len << 3))
			fill_buf();

		int i = getbits(32);

		long l = little_sgetbits(32);
		l <<= 32;

		l |= i;

		cur_bit -= 64;
		total_bits -= 64;
		return Double.longBitsToDouble(l);
	}

	public double little_getdouble() throws FlIOException {
		double d = little_nextdouble();
		skipbits(64);
		return d;
	}

	public int little_putbits(int value, int n) throws FlIOException {
		int x = value;

		// Sanity check
		if (n == 0)
			return value;
		if (n > MAX_SIZE_OF_BITS || n < 1)
			throw new FlIOException(FlIOException.INVALIDBITSIZE);

		int bytes = n >>> 3; // Number of bytes to write +
		int leftbits = n % 8; // NUmber of bits to write

		int byte_x = 0;
		int i = 0;
		for (; i < bytes; i++) {
			byte_x = (x >> (8 * i)) & mask[8];
			putbits(byte_x, 8);
		}
		if (leftbits > 0) {
			byte_x = (x >> (8 * i)) & mask[leftbits];
			putbits(byte_x, leftbits);
		}
		return value;
	}

	public long little_putlong(long value, int n) throws FlIOException {
		if (n > 32) {
			int i = (int) (value >>> 32);
			little_putbits(i, n - 32);
			i = (int) (value & 0x00000000FFFFFFFF);
			little_putbits(i, 32);
			return value;
		} else
			return little_putbits((int) value, n);
	}

	public float little_putfloat(float value) throws FlIOException {
		little_putbits(Float.floatToIntBits(value), 32);
		return value;
	}

	public double little_putdouble(double value) throws FlIOException {
		long l = Double.doubleToLongBits(value);
		int i = (int) (l & 0x00000000FFFFFFFF);
		little_putbits(i, 32);
		i = (int) (l >>> 32);
		little_putbits(i, 32);
		return value;
	}

	/**
	 * Skip next 'n' bits (both input/output)
	 * 
	 * @param n
	 *            The number of bits to skip
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	public void skipbits(int n) throws FlIOException {
		int x = n;
		int buf_size = buf_len << 3;

		while (cur_bit + x > buf_size) {
			x -= buf_size - cur_bit;
			cur_bit = buf_size;
			if (type == BS_INPUT)
				fill_buf();
			else
				flush_buf();
		}
		cur_bit += x;
		total_bits += n;
	}

	/**
	 * Align the bitstream (n must be a multiple of 8, both input/output);
	 * returns bits skipped
	 * 
	 * @param n
	 *            The number of bits to align
	 * @return The number of bits skipped
	 * @exception FlIOException
	 *                if an I/O error occurs or invalid alignment requested
	 */
	public int align(int n) throws FlIOException {
		if ((n % 8) != 0)
			throw new FlIOException(FlIOException.INVALIDALIGNMENT);

		int s = 0;

		// Align on next byte boundary
		if ((total_bits % 8) != 0) {
			s = 8 - (total_bits % 8);
			skipbits(s);
		}
		// Skip bits until total_bits are aligned on n
		while ((total_bits % n) != 0) {
			s += 8;
			skipbits(8);
		}
		return s;
	}

	/**
	 * Probe next n bits as an integer (input) or return 0 (output)
	 * 
	 * @param n
	 *            The number of bits to look-ahead read
	 * @param big
	 *            The byte-ordering used to represent the integer; 0:
	 *            little-endian, 1: big-endian
	 * @param sign
	 *            If sign=0, then no sign extension; otherwise, sign extension.
	 * @param alen
	 *            If alen>0, then the number is probed at alen-bit boundary
	 *            (alen must be multiple of 8).
	 * @return The integer value of the n bits
	 * @exception FlIOException
	 *                if an I/O error occurs or invalid alignment requested
	 */
	public int next(int n, int big, int sign, int alen) throws FlIOException {
		if (alen > 0)
			align(alen);
		if (type == BS_INPUT) {
			if (big > 0) {
				if (sign > 0)
					return snextbits(n);
				else
					return nextbits(n);
			} else {
				if (sign > 0)
					return little_snextbits(n);
				else
					return little_nextbits(n);
			}
		} else
			return 0;
	}

	/**
	 * Search for a specified code (input) or output bits up to the specified
	 * alen-bit boundary (output)
	 * 
	 * @param code
	 *            The code to search for
	 * @param n
	 *            The number of bits used to represent the code
	 * @param alen
	 *            If alen>0, then output bits up to the specified alen-bit
	 *            boundary (output);
	 * @return The number of bits skipped, excluding the code.
	 * @exception FlIOException
	 *                if an I/O error occurs or invalid alignment requested
	 */
	public int nextcode(int code, int n, int alen) throws FlIOException {
		int s = 0;

		if (type == BS_INPUT) {
			if (alen == 0) {
				while (code != nextbits(n)) {
					s += 1;
					skipbits(1);
				}
			} else {
				s += align(alen);
				while (code != nextbits(n)) {
					s += alen;
					skipbits(alen);
				}
			}
		} else if (type == BS_OUTPUT)
			s += align(alen);
		return s;
	}

	/**
	 * Get current bit position (both input/output)
	 * 
	 * @return The current bit position
	 */
	public int getpos() {
		return total_bits;
	}

	/**
	 * Test end of data
	 * 
	 * @return True when bitsream reaches end-of-data, false otherwise
	 */
	public boolean atend() {
		return eof;
	}

	/**
	 * Return the mode
	 * 
	 * @return BS_INPUT or BS_OUPUT
	 */
	public int getmode() {
		return type;
	}

	/**
	 * Flush all content in the buffer
	 */
	public void flushbits() throws FlIOException {
		flush_buf();
		if (cur_bit == 0)
			return;
		try {
			out.write(buf, 0, 1);
		} catch (IOException e) {
			throw new FlIOException(FlIOException.SYSTEMIOFAILED, e.toString());
		}
		buf[0] = 0;
		cur_bit = 0;
	}

	/**
	 * Flush the buffer excluding the left-over bits
	 * 
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	private void flush_buf() throws FlIOException {
		int l = cur_bit >>> 3; // Size of data in the buffer (in bytes)
		try {
			out.write(buf, 0, l); // File output
		} catch (IOException e) {
			throw new FlIOException(FlIOException.SYSTEMIOFAILED, e.toString());
		}
		cur_bit &= 0x7; // Keep left-over bits only
		if (cur_bit != 0)
			buf[0] = buf[l];
	}

	/**
	 * Fill out internal buffer from the file
	 * 
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	protected void fill_buf() throws FlIOException {
		int n, u, l;
		n = cur_bit >>> 3; // Current byte offset
		u = buf_len - n; // Remaining bytes

		System.arraycopy(buf, n, buf, 0, u); // Copy remaining data into the
		// head of the buffer

		try {
			// Now we have a room for n bytes from offset u in the buffer
			l = in.read(buf, u, n);
		} catch (IOException e) {
			throw new FlIOException(FlIOException.SYSTEMIOFAILED, e.toString());
		}
		if (l == -1) {
			eof = true;
			throw new FlIOException(FlIOException.ENDOFDATA);
		}
		if (l < n) {
			buf_len = u + l; // Adjust buffer size
			eof = true;
		}
		cur_bit &= 7; // Now we are at the first byte
	}

	public static void main(String args[]) {
		return;
	}
}