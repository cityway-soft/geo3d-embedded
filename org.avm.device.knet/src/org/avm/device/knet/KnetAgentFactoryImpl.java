package org.avm.device.knet;

import java.util.Hashtable;

import org.apache.log4j.Logger;

public class KnetAgentFactoryImpl implements KnetAgentFactory {

	private static Hashtable _agentKnetFactory;
	private Logger _log = null;
	private KnetAgent _agent;
	private static boolean _noknet = false;

	public KnetAgentFactoryImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		_agentKnetFactory = new Hashtable();
	}

	public KnetAgent create(int typeApp) {
		KnetAgent agent = (KnetAgent) _agentKnetFactory
				.get(new Integer(typeApp));
		if (agent == null) {
			agent = new KnetAgentImpl();
			_agentKnetFactory.put(new Integer(typeApp), agent);
		}
		return agent;
	}

	public KnetAgent getKnet() {
		return _agent;
	}

}
