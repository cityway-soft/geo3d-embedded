package org.avm.elementary.protocol.avm;

import java.util.ArrayList;
import java.util.List;

public class ConnectionListenerSupport {

	private Object _source;

	private List _listeners;

	public ConnectionListenerSupport(Object source) {
		if (source == null) {
			throw new NullPointerException();
		}
		_source = source;
	}

	public synchronized void addConnectionEventListener(
			ConnectionListener listener) {
		if (listener == null) {
			return;
		}

		if (_listeners == null) {
			_listeners = new ArrayList();
		}
		_listeners.add(listener);
	}

	public synchronized void removeConnectionEventListener(
			ConnectionListener listener) {
		if (listener == null) {
			return;
		}

		if (_listeners == null) {
			return;
		}
		_listeners.remove(listener);

	}

	protected void delegateFireConnectionEvent(final int type) {
		if (_listeners != null) {
			ConnectionListener[] list = getConnectionEventListeners();
			for (int i = 0; i < list.length; i++) {
				ConnectionListener target = (ConnectionListener) list[i];
				if (type == ConnectionEvent.KEEPALIVE) {
					target.keepalive(new ConnectionEvent(_source, type));
				} else if (type == ConnectionEvent.CONNECTED) {
					target.connected(new ConnectionEvent(_source, type));
				} else if (type == ConnectionEvent.DISCONNECTED) {
					target.disconnected(new ConnectionEvent(_source, type));
				}

			}
		}
	}

	public void fireConnectionEvent(final int type) {
		delegateFireConnectionEvent(type);
	}

	public synchronized ConnectionListener[] getConnectionEventListeners() {
		List result = new ArrayList();

		if (_listeners != null) {
			result.addAll(_listeners);
		}

		return (ConnectionListener[]) (result
				.toArray(new ConnectionListener[0]));
	}

	public synchronized boolean hasListeners(String propertyName) {
		if (_listeners != null && !_listeners.isEmpty()) {
			return true;
		}
		return false;
	}

}
