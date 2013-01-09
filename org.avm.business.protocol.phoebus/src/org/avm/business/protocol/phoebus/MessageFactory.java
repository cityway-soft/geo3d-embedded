package org.avm.business.protocol.phoebus;

import java.io.InputStream;
import java.text.ParseException;

public abstract class MessageFactory {

	public static java.util.Map factories = new java.util.HashMap();

	protected abstract Message unmarshal(InputStream in) throws Exception;

	protected abstract Message get(InputStream in) throws Exception;

	public static final Message parseXmlstream(InputStream in) throws Exception {
		in.reset();
		MessageFactory factory = (MessageFactory) factories.get(new Integer(0));
		Message message = factory.unmarshal(in);
		int type = message.getEntete().getType();
		in.reset();

		if (!factories.containsKey(new Integer(type))) {
			throw new ParseException("Message " + type + " inconnu", 0);
		}

		return ((MessageFactory) factories.get(new Integer(type)))
				.unmarshal(in);
	}

	public static final Message parseBitstream(InputStream in) throws Exception {
		in.reset();
		int protocol = in.read();

		if (protocol != Entete.PROTOCOL_ID) {
			throw new RuntimeException("Protocole id " + protocol
					+ "non supporte");
		}

		int type = in.read();
		in.reset();

		if (!factories.containsKey(new Integer(type))) {
			throw new ParseException("Message " + type + " inconnu", 0);
		}

		return ((MessageFactory) factories.get(new Integer(type))).get(in);
	}

}
