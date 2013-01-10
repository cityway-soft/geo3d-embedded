package org.avm.business.protocol.hmi;

import java.io.IOException;
import java.io.OutputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

import flavor.Bitstream;
import flavor.IBitstream;

public class State {
	int _value;
	long _time;
	String _name;

	public static final String MESSAGE_NAME = "state";

	public State() {

	}

	public State(int value, String name, long time) {
		setValue(value);
		setTime(time);
		setName(name);
	}

	public int getValue() {
		return _value;
	}

	public void setValue(int value) {
		_value = value;
	}

	public long getTime() {
		return _time;
	}

	public void setTime(long time) {
		_time = time;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = new String(name);
	}

	public String toString() {
		return MESSAGE_NAME + " [" + getName() + ": " + getValue() + "]";
	}

	public void put(OutputStream out) throws Exception {
		Bitstream bs = new Bitstream(out);
		put(bs);
		bs.close();
	}

	public void marshal(OutputStream out) throws Exception {
		IBindingFactory factory = BindingDirectory.getFactory(this.getClass());
		IMarshallingContext context = factory.createMarshallingContext();
		context.marshalDocument(this, null, null, out);
	}

	void put(IBitstream _F_bs) throws IOException {
		_F_bs.putbits(MessageFactory.PROTOCOL_ID, 8);
		_F_bs.putbits(_value, 32);
		_F_bs.putlong(_time, 64);

		byte[] buffer = _name.toString().getBytes();
		for (int i = 0; i < buffer.length; i++) {
			_F_bs.putbits(buffer[i], 8);
		}
	}

	int get(IBitstream _F_bs) throws IOException {
		int protocol = _F_bs.getbits(8);
		_value = _F_bs.getbits(32);
		_time = _F_bs.getlong(64);

		if (protocol != MessageFactory.PROTOCOL_ID) {
			throw new RuntimeException("Protocole id " + protocol
					+ "non supporte");
		}

		StringBuffer text = new StringBuffer();
		while (true) {
			try {
				int c = _F_bs.getbits(8);
				text.append((char) c);
			} catch (Exception e) {
				break;
			}
		}
		_name = text.toString();
		return 0;
	}
}
