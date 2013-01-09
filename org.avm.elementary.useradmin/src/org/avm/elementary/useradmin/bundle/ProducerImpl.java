package org.avm.elementary.useradmin.bundle;

import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Wire;
import org.osgi.util.measurement.State;

public class ProducerImpl extends AbstractProducer {
	private UserSessionService _peer;

	public ProducerImpl(UserSessionService peer, ComponentContext context) {
		super(context);
		_peer = peer;
	}

	public Object polled(Wire wire) {
		Object obj = _peer.getState(); 
		return obj;
	}

	protected String getProducerPID() {
		return Activator.getDefault().getPid();
	}

	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] { State.class };
		return result;
	}
}
