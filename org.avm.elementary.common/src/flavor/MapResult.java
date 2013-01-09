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

public class MapResult {
	boolean _hit;

	int _code;

	int _length;

	public int _iout;

	public float _fout;

	public double _dout;

	public Object _o;

	public MapResult(boolean hit, int code, int length) {
		_hit = hit;
		_code = code;
		_length = length;
	}

	public MapResult(boolean hit, int code, int length, int iout) {
		this(hit, code, length);
		_iout = iout;
	}

	public MapResult(boolean hit, int code, int length, float fout) {
		this(hit, code, length);
		_fout = fout;
	}

	public MapResult(boolean hit, int code, int length, double dout) {
		this(hit, code, length);
		_dout = dout;
	}

	public MapResult(boolean hit, int code, int length, Object o) {
		this(hit, code, length);
		_o = o;
	}

	public boolean isHit() {
		return _hit;
	}

	public int getCode() {
		return _code;
	}

	public int getCodeLength() {
		return _length;
	}
}