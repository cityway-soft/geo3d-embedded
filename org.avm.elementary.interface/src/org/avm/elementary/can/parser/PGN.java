package org.avm.elementary.can.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.pool.KeyedObjectPool;

public abstract class PGN {

	public byte[] buffer = null;
	protected int _counter;
	protected KeyedObjectPool _pool;
	protected HashMap _map;

	public PGN() {
		_map = new HashMap();
		_map.values().iterator();
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int getCounter() {
		return _counter;
	}

	public void setCounter(int counter) {
		_counter = counter;
	}

	public KeyedObjectPool getPool() {
		return _pool;
	}

	public void setPool(KeyedObjectPool pool) {
		_pool = pool;
	}

	public abstract Integer getId();

	public Iterator iterator() {
		return _map.values().iterator();
	}

	public SPN getSPN(String name) {
		return (SPN) _map.get(name);
	}

	public abstract void get(Bitstream bs) throws IOException;

	public abstract void put(Bitstream bs) throws IOException;

	public void dispose() {
		try {
			_counter--;
			if (_pool != null && _counter == 0)
				_pool.returnObject(getId(), this);
		} catch (Exception e) {

		}
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PGN other = (PGN) obj;
		if (_map == null) {
			if (other._map != null)
				return false;
		} else if (!_map.equals(other._map))
			return false;
		return true;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("\"");
		result.append(Integer.toHexString(getId().intValue()).toUpperCase());
		result.append("\":{");
		for (Iterator iterator = iterator(); iterator.hasNext();) {
			SPN spn = (SPN) iterator.next();
			result.append(spn.toString());
			result.append(',');
		}
		result.append("}");
		return result.toString();
	}

	public static String toHexaString(byte[] data) {
		return toHexaString(data, 0, data.length);
	}

	public static String toHexaString(byte[] data, int offset, int length) {
		byte[] buffer = new byte[length * 2];

		for (int i = offset; i < offset + length;i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[(i - offset) *2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[(i- offset) *2 +1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

}
