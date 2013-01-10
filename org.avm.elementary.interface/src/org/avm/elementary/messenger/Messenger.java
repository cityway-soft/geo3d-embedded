package org.avm.elementary.messenger;

import java.util.Dictionary;

public interface Messenger {
	public void send(Dictionary header, Object data) throws Exception;
}
