package org.avm.business.protocol.management;

import java.io.InputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import flavor.Bitstream;

public class MessageFactory {

	public static final String PROTOCOL_NAME = "MANAGEMENT";

	public static final int PROTOCOL_ID = '*';

	public static final int SPACE = ' ';

	public static Management unmarshal(InputStream in) throws Exception {
		IBindingFactory factory = BindingDirectory.getFactory(Management.class);
		IUnmarshallingContext context = factory.createUnmarshallingContext();
		return (Management) context.unmarshalDocument(in, null);
	}

	public static Management get(InputStream in) throws Exception {
		Bitstream bs = new Bitstream(in);
		Management message = new Management();
		message.get(bs);
		bs.close();
		return message;
	}
}
