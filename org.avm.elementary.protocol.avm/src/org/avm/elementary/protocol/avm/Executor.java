package org.avm.elementary.protocol.avm;

import org.avm.elementary.protocol.avm.state.ConnectionStateMachineContext;

public abstract class Executor {

	protected ConnectionStateMachineContext _state;

	public abstract void execute();

	public ConnectionStateMachineContext getState() {
		return _state;
	}

	public void setState(ConnectionStateMachineContext state) {
		_state = state;
	}

}
