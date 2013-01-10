package org.avm.elementary.protocol.avm;

public interface ConnectionListener {

	public void keepalive(ConnectionEvent event);

	public void connected(ConnectionEvent event);

	public void disconnected(ConnectionEvent event);

}
