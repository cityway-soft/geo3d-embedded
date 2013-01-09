/* @flavorc
 *
 * C_CNX.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'avm.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.elementary.protocol.avm.parser;

import java.io.IOException;
import java.io.InputStream;

import flavor.IBitstream;
import flavor.MapResult;

public class C_CNX extends Message {
	int _longitude;
	int _latitude;
	char _source[] = new char[12];

	public static final int MESSAGE_TYPE = 1;

	public C_CNX() {
		super(MESSAGE_TYPE);
	}

	public C_CNX(int longitude, int latitude, String source) {
		super(MESSAGE_TYPE);
		_longitude = longitude;
		_latitude = latitude;
		setSource(source);
	}

	public int getLongitude() {
		return _longitude;
	}

	public void setLongitude(int longitude) {
		_longitude = longitude;
	}

	public int getLatitude() {
		return _latitude;
	}

	public void setLatitude(int latitude) {
		_latitude = latitude;
	}

	public String getSource() {
		return new String(_source);
	}

	public void setSource(String source) {
		if (source != null) {
			int end = Math.min(source.length(), 12);
			char[] value = source.substring(0, end).toCharArray();
			for (int i = 0; i < end; i++) {
				_source[i] = value[i];
			}
		}
	}

	public String toString() {
		return "connect: " + super.toString() + " lon = " + _longitude
				+ " lat = " + _latitude + " src = " + getSource();
	}

	public static class DefaultMessageFactory extends MessageFactory {

		protected Message get(InputStream in) throws Exception {
			InputBitstream bs = new InputBitstream(in);
			Message message = new C_CNX();
			message.get(bs);

			return message;
		}
	}

	static {
		MessageFactory.factories.put(new Integer(MESSAGE_TYPE),
				new DefaultMessageFactory());
	}

	public int get(IBitstream _F_bs) throws IOException {
		int _F_ret = 0;
		MapResult _F_mr;
		int _F_dim0, _F_dim0_end;
		_F_ret += super.get(_F_bs);
		_longitude = _F_bs.sgetbits(32);
		_latitude = _F_bs.sgetbits(32);
		_F_dim0_end = 12;
		for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
			_source[_F_dim0] = (char) _F_bs.getbits(8);
		}
		return _F_ret;
	}

	public int put(IBitstream _F_bs) throws IOException {
		int _F_ret = 0;
		MapResult _F_mr;
		int _F_dim0, _F_dim0_end;
		_F_ret += super.put(_F_bs);
		_F_bs.putbits(_longitude, 32);
		_F_bs.putbits(_latitude, 32);
		_F_dim0_end = 12;
		for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
			_F_bs.putbits(_source[_F_dim0], 8);
		}
		return _F_ret;
	}
}
