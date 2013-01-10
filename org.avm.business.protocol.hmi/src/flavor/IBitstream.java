/*
 * Copyright (c) 1997-2004 Alexandros Eleftheriadis, Danny Hong and 
 * Yuntai Kyong.
 *
 * This file is part of Flavor, developed at Columbia University
 * (http://flavor.sourceforge.net).
 *
 * Flavor is free software; you can redistribute it and/or modify
 * it under the terms of the Flavor Artistic License as described in
 * the file COPYING.txt. 
 *
 */

/*
 * Authors:
 * Alexandros Eleftheriadis <eleft@ee.columbia.edu>
 * Yuntai Kyong <yuntaikyong@ieee.org>
 * Danny Hong <danny@ee.columbia.edu>
 *
 */

package flavor;

/**
 * IBitsteam interface. The definition of the Bitstream I/O interface. This is
 * the definition of the methods that the Flavor translator expects from the
 * underlying class that performs bitstream I/O.
 * 
 * @see flavor.FlIOException
 */
public interface IBitstream {
	/**
	 * Input bitstream type
	 */
	public static final int BS_INPUT = 0;

	/**
	 * Output bitstream type
	 */
	public static final int BS_OUTPUT = 1;

	/** *********** */
	/* Big endian */
	/** *********** */

	/**
	 * Probe 'n' bits as <b>unsigned</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int nextbits(int n) throws FlIOException;

	/**
	 * Probe 'n' bits as <b>signed</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int snextbits(int n) throws FlIOException;

	/**
	 * Get next 'n' bits as <b>unsigned</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int getbits(int n) throws FlIOException;

	/**
	 * Get next 'n' bits as <b>signed</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The integer value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int sgetbits(int n) throws FlIOException;

	/**
	 * Probe 'n'(<= 64 bits) bits as <b>unsigned</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long nextlong(int n) throws FlIOException;

	/**
	 * Probe 'n' (<= 64 bits) bits as <b>signed</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long snextlong(int n) throws FlIOException;

	/**
	 * Get next 'n' (<= 64 bits) bits as <b>unsigned</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long getlong(int n) throws FlIOException;

	/**
	 * Get next (<= 64 bits) 'n' bits as <b>signed</b> value (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The long value of the n bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long sgetlong(int n) throws FlIOException;

	/**
	 * Probe a float value from the next 32 bits (input only)
	 * 
	 * @return The float value of the 32 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	float nextfloat() throws FlIOException;

	/**
	 * Get a float value from the next 32 bits (input only)
	 * 
	 * @return The float value of the 32 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	float getfloat() throws FlIOException;

	/**
	 * Probe a double value from the next 64 bits (input only)
	 * 
	 * @return The double value of the 64 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	double nextdouble() throws FlIOException;

	/**
	 * Get a double value from the next 64 bits (output only)
	 * 
	 * @return The double value of the 64 bits (MSB first)
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	double getdouble() throws FlIOException;

	/**
	 * Put 'n' bits (output only)
	 * 
	 * @param value
	 *            The value to put
	 * @param n
	 *            The number of bits (<= 32 bits) to put (MSB first)
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int putbits(int value, int n) throws FlIOException;

	/**
	 * Put 'n' bits (output only)
	 * 
	 * @param value
	 *            The long value to put
	 * @pararm n The number of bits (<= 64 bits) to put (MSB first)
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long putlong(long value, int n) throws FlIOException;

	/**
	 * Put a float value using 32 bits (output only) (MSB first)
	 * 
	 * @param value
	 *            The float value to put
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	float putfloat(float value) throws FlIOException;

	/**
	 * Put a double value using 64 bits (output only) (MSB first)
	 * 
	 * @param value
	 *            The double value to put
	 * @return value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	double putdouble(double value) throws FlIOException;

	/** ************** */
	/* Little endian */
	/** ************** */

	/**
	 * Probe 'n' bits as <b>unsigned</b> value using the little-endian method
	 * (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The integer value of next n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int little_nextbits(int n) throws FlIOException;

	/**
	 * Probe 'n' bits as <b>signed</b> value using the little-endian method
	 * (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The integer value of next n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int little_snextbits(int n) throws FlIOException;

	/**
	 * Get next 'n' bits as <b>unsigned</b> value using the little-endian
	 * method (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The integer value of next n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int little_getbits(int n) throws FlIOException;

	/**
	 * Get next 'n' bits as <b>signed</b> value using the little-endian method
	 * (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The integer value of next n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int little_sgetbits(int n) throws FlIOException;

	/**
	 * Probe 'n'(<= 64 bits) bits as <b>unsigned</b> value using the
	 * little-endian method (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The long value of the n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long little_nextlong(int n) throws FlIOException;

	/**
	 * Probe 'n' (<= 64 bits) bits as <b>signed</b> value using the
	 * little-endian method (input only)
	 * 
	 * @param n
	 *            The number of bits to probe
	 * @return The long value of the n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long little_snextlong(int n) throws FlIOException;

	/**
	 * Get next 'n' (<= 64 bits) bits as <b>unsigned</b> value using the
	 * little-endian method (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The long value of the n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long little_getlong(int n) throws FlIOException;

	/**
	 * Get next 'n' (<= 64 bits) bits as <b>signed</b> value using the
	 * little-endian method (input only)
	 * 
	 * @param n
	 *            The number of bits to get
	 * @return The long value of the n bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long little_sgetlong(int n) throws FlIOException;

	/**
	 * Probe a float value from the next 32 bits using the little-endian method
	 * (input only)
	 * 
	 * @return The float value of the 32 bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	float little_nextfloat() throws FlIOException;

	/**
	 * Get a float value from the next 32 bits using the little-endian method
	 * (input only)
	 * 
	 * @return The float value of the 32 bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	float little_getfloat() throws FlIOException;

	/**
	 * Probe a double value from the next 64 bits using the little-endian method
	 * (input only)
	 * 
	 * @return The double value of the 64 bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	double little_nextdouble() throws FlIOException;

	/**
	 * Get a double value from the next 64 bits using the little-endian method
	 * (input only)
	 * 
	 * @return The double value of the 64 bits
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	double little_getdouble() throws FlIOException;

	/**
	 * Puts 'n' bits using the little-endian method (output only)
	 * 
	 * @param value
	 *            The value to put
	 * @param n
	 *            The number of bits (<= 32 bits) to put (LSB first)
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	int little_putbits(int value, int n) throws FlIOException;

	/**
	 * Put 'n' bits (output only)
	 * 
	 * @param value
	 *            The long value to put
	 * @param n
	 *            The number of bits (<= 64 bits) to put (LSB first)
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	long little_putlong(long value, int n) throws FlIOException;

	/**
	 * Put a float value using 32 bits (output only) (LSB first)
	 * 
	 * @param value
	 *            The float value to put
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	float little_putfloat(float value) throws FlIOException;

	/**
	 * Put a double value using 64 bits (output only) (LSB first)
	 * 
	 * @param value
	 *            The double value to put
	 * @return The value put into the bitstream
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	double little_putdouble(double value) throws FlIOException;

	/**
	 * Skip next 'n' bits (both input/output)
	 * 
	 * @param n
	 *            The number of bits to skip
	 * @exception FlIOException
	 *                if an I/O error occurs
	 */
	void skipbits(int n) throws FlIOException;

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
	int align(int n) throws FlIOException;

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
	int next(int n, int big, int sign, int alen) throws FlIOException;

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
	int nextcode(int code, int n, int alen) throws FlIOException;

	/**
	 * Get current bit position (both input/output)
	 * 
	 * @return The current bit position
	 */
	int getpos();

	/**
	 * Test end of data
	 * 
	 * @return True when bitsream reaches end-of-data, false otherwise
	 */
	boolean atend();

	/**
	 * Return the mode
	 * 
	 * @return BS_INPUT or BS_OUPUT
	 */
	int getmode();
}