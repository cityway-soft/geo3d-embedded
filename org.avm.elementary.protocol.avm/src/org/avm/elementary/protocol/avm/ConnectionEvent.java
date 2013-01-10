package org.avm.elementary.protocol.avm;

import java.util.EventObject;

public class ConnectionEvent extends EventObject {

	public static final int CONNECTED = 1;

	public static final int DISCONNECTED = 2;

	public static final int KEEPALIVE = 3;

	protected int _type;

	public ConnectionEvent(Object source, int type) {
		super(source);
		_type = type;
	}

	public int getType() {
		return _type;
	}
}
