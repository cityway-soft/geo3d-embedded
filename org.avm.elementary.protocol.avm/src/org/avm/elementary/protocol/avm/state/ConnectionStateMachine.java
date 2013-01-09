package org.avm.elementary.protocol.avm.state;

import org.avm.elementary.protocol.avm.parser.Message;

public interface ConnectionStateMachine {

	boolean connectingCallback();

	boolean connectedCallback(Message msg);

	void disconnectingCallback();

	boolean pingCallback(Message msg);

	boolean pongCallback(Message msg);

	boolean sendCallback(Message msg);

	boolean receiveCallback(Message msg);

	boolean acknolegeCallback(Message msg);

	void defaultCallback();

	void startTimer();

	void stopTimer();

}
