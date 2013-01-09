package org.avm.elementary.protocol.avm;

import java.util.EventObject;

import org.avm.elementary.protocol.avm.parser.Message;

public class MessageEvent extends EventObject {

	public static final int MESSAGE = 1;

	public static final int ACKNOLEDGE = 2;

	protected int _type;

	protected Message _message;

	public MessageEvent(Object source, int type, Message message) {
		super(source);
		_type = type;
		_message = message;
	}

	public int getType() {
		return _type;
	}

	public Message getMessage() {
		return _message;
	}

}
