package org.avm.elementary.protocol.avm.parser;

import java.io.InputStream;
import java.io.OutputStream;

public class Parser {

	public Object get(InputStream in) throws Exception {
		return MessageFactory.read(in);
	}

	public void put(Object n, OutputStream out) throws Exception {
		if (n instanceof Message) {
			Message message = (Message) n;
			message.put(out);
		} else {
			throw new RuntimeException("Protocole non supporte");
		}
	}
}
