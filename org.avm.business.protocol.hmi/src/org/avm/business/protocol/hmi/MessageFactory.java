package org.avm.business.protocol.hmi;

import java.io.InputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import flavor.Bitstream;

public class MessageFactory {

	public static final String PROTOCOL_NAME = "HMI";

	public static final int PROTOCOL_ID = 9;

	public static State unmarshal(InputStream in) throws Exception {
		IBindingFactory factory = BindingDirectory.getFactory(State.class);
		IUnmarshallingContext context = factory.createUnmarshallingContext();
		return (State) context.unmarshalDocument(in, null);
	}

	public static State get(InputStream in) throws Exception {
		Bitstream bs = new Bitstream(in);
		State message = new State();
		message.get(bs);
		bs.close();
		return message;
	}
}
