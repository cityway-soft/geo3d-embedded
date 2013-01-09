/*
 * Copyright (c) 1997-2004 Alexandros Eleftheriadis, Danny Hong and G
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

// Utility functions
package flavor;

import java.util.Locale;

public class Util {
	static boolean trace_print_header = true;

	// Error handling
	public static void flerror(String msg) {
		System.out.println(msg);
	}

	public static void trace(int pos, int size, int val, String desc) {
		int nbytes, i;
		int b[] = { 0, 0, 0, 0 };
		Locale loc = Locale.ENGLISH;

		/* Print header, if necessary */
		if (trace_print_header) {
			System.out.println("   At Bit  Size    Value    Description");
			trace_print_header = false;
		}

		/* Print bit position and size of quantity */
		if (size > 0) {
			System.out.print(new PrintfFormat(loc, "%9d").sprintf(pos));
			System.out.print(new PrintfFormat(loc, ": %4d ").sprintf(size));
		} else
			System.out.print(new PrintfFormat(loc, "%9d:      ").sprintf(pos));

		/* Split value into bytes */
		b[3] = 0xFF & (val >> 24);
		b[2] = 0xFF & (val >> 16);
		b[1] = 0xFF & (val >> 8);
		b[0] = 0xFF & val;

		/* Output bits */
		if (size == 0)
			nbytes = 0;
		else
			nbytes = (size - 1) / 8 + 1;

		for (i = 4; i > nbytes; i--)
			System.out.print("   ");
		for (i = nbytes - 1; i >= 0; i--)
			System.out.print(new PrintfFormat(loc, "%.2X ").sprintf(b[i]));

		System.out.println(desc);
	}

	public static void trace(int pos, int size, long val, String desc) {
		int nbytes, i;
		long b[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
		Locale loc = Locale.ENGLISH;

		/* Print header, if necessary */
		if (trace_print_header) {
			System.out.println("   At Bit  Size    Value    Description");
			trace_print_header = false;
		}

		/* Print bit position and size of quantity */
		if (size > 0) {
			System.out.print(new PrintfFormat(loc, "%9d").sprintf(pos));
			System.out.print(new PrintfFormat(loc, ": %4d ").sprintf(size));
		} else
			System.out.print(new PrintfFormat(loc, "%9d:      ").sprintf(pos));

		/* Split value into bytes */
		b[7] = 0xFF & (val >> 56);
		b[6] = 0xFF & (val >> 48);
		b[5] = 0xFF & (val >> 40);
		b[4] = 0xFF & (val >> 32);
		b[3] = 0xFF & (val >> 24);
		b[2] = 0xFF & (val >> 16);
		b[1] = 0xFF & (val >> 8);
		b[0] = 0xFF & val;

		/* Output bits */
		if (size == 0)
			nbytes = 0;
		else
			nbytes = (size - 1) / 8 + 1;

		for (i = 4; i > nbytes; i--)
			System.out.print("   ");
		for (i = nbytes - 1; i >= 0; i--)
			System.out.print(new PrintfFormat(loc, "%.2X ").sprintf(b[i]));

		System.out.println(desc);
	}

	public static void trace(int pos, int size, double val, String desc) {
		Locale loc = Locale.ENGLISH;

		/* Print header, if necessary */
		if (trace_print_header) {
			System.out.println("   At Bit  Size    Value    Description");
			trace_print_header = false;
		}

		/* Print bit position,size, and value of quantity */
		if (size > 0) {
			System.out.print(new PrintfFormat(loc, "%9d").sprintf(pos));
			System.out.print(new PrintfFormat(loc, ": %4d").sprintf(size));
			System.out.print(new PrintfFormat(loc, " %11g ").sprintf(val));
		} else
			System.out.print(new PrintfFormat(loc, "%9d:      ").sprintf(pos));

		/* Print description */
		System.out.println(desc);
	}
}