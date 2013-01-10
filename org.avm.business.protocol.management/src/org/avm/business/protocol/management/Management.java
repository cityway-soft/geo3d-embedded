package org.avm.business.protocol.management;

import java.io.IOException;
import java.io.OutputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

import flavor.Bitstream;
import flavor.IBitstream;

public class Management {

	StringBuffer _text = new StringBuffer();

	public static final String MESSAGE_NAME = "management";

	public Management() {

	}

	public Management(String text) {
		this();
		setText(text);
	}

	public String getText() {
		return _text.toString();
	}

	public void setText(String text) {
		_text = new StringBuffer(text);
	}

	public String toString() {
		return MESSAGE_NAME + " [" + getText() + "]";
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
		_F_bs.putbits(MessageFactory.SPACE, 8);

		byte[] buffer = _text.toString().getBytes();
		for (int i = 0; i < buffer.length; i++) {
			_F_bs.putbits(buffer[i], 8);
		}
	}

	int get(IBitstream bs) throws IOException {
		int protocol = bs.getbits(8);
		int space = bs.getbits(8);
		if (protocol != MessageFactory.PROTOCOL_ID
				&& space != MessageFactory.SPACE) {
			throw new RuntimeException("Protocole id " + protocol
					+ "non supporte");
		}

		while (true) {
			try {
				int c = bs.getbits(8);
				_text.append((char) c);
			} catch (Exception e) {
				break;
			}
		}

		return 0;
	}
}
