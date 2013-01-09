package org.avm.elementary.protocol.avm;

import java.util.ArrayList;
import java.util.List;

import org.avm.elementary.protocol.avm.parser.C_ACK;
import org.avm.elementary.protocol.avm.parser.C_MSG;
import org.avm.elementary.protocol.avm.parser.Message;
import org.avm.elementary.protocol.avm.parser.S_ACK;
import org.avm.elementary.protocol.avm.parser.S_MSG;

public class MessageListenerSupport {

	private Object _source;

	private List _listeners;

	public MessageListenerSupport(Object source) {
		if (source == null) {
			throw new NullPointerException();
		}
		_source = source;
	}

	public synchronized void addConnectionEventListener(MessageListener listener) {
		if (listener == null) {
			return;
		}

		if (_listeners == null) {
			_listeners = new ArrayList();
		}
		_listeners.add(listener);
	}

	public synchronized void removeConnectionEventListener(
			MessageListener listener) {
		if (listener == null) {
			return;
		}

		if (_listeners == null) {
			return;
		}
		_listeners.remove(listener);

	}

	protected void delagateFireMessageEvent(Message message) {
		if (_listeners != null) {
			MessageListener[] list = getMessageListeners();
			for (int i = 0; i < list.length; i++) {
				MessageListener target = (MessageListener) list[i];
				if (message instanceof C_MSG || message instanceof S_MSG) {
					target.receive(new MessageEvent(_source,
							MessageEvent.MESSAGE, message));
				} else if (message instanceof C_ACK || message instanceof S_ACK) {
					target.acknoledge(new MessageEvent(_source,
							MessageEvent.ACKNOLEDGE, message));
				}
			}
		}
	}

	public void fireMessageEvent(final Message message) {
		delagateFireMessageEvent(message);
	}

	public synchronized MessageListener[] getMessageListeners() {
		List result = new ArrayList();

		if (_listeners != null) {
			result.addAll(_listeners);
		}

		return (MessageListener[]) (result.toArray(new MessageListener[0]));
	}

	public synchronized boolean hasListeners(String propertyName) {
		if (_listeners != null && !_listeners.isEmpty()) {
			return true;
		}
		return false;
	}

}
