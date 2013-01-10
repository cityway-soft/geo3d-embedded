package org.avm.elementary.protocol.avm.parser;

import java.io.EOFException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public abstract class MessageFactory {

	public static Map factories = new HashMap();

	protected abstract Message get(InputStream in) throws Exception;

	public static final Message read(InputStream in) throws Exception {
		int type = -1;

		in.mark(1);
		type = in.read();
		if (type == -1) {
			System.out.println("MessageFactory.read(): EOF !!!!!!!!");
			throw new EOFException();
		}
		in.reset();

		if (!factories.containsKey(new Integer(type))) {
			throw new ParseException("Message " + type + " inconnu", 0);
		}

		MessageFactory factory = (MessageFactory) factories.get(new Integer(
				type));
		Message result = factory.get(in);
		return result;
	}

	static {
		try {
			Class.forName(C_CNX.class.getName());
			Class.forName(C_DCNX.class.getName());
			Class.forName(C_ACK.class.getName());
			Class.forName(C_MSG.class.getName());
			Class.forName(PING.class.getName());
			Class.forName(PONG.class.getName());
			Class.forName(S_CNX.class.getName());
			Class.forName(S_DCNX.class.getName());
			Class.forName(S_ACK.class.getName());
			Class.forName(S_MSG.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
