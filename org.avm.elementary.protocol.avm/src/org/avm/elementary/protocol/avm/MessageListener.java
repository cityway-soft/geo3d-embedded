package org.avm.elementary.protocol.avm;

public interface MessageListener {

	public void receive(MessageEvent event);

	public void acknoledge(MessageEvent event);

}
