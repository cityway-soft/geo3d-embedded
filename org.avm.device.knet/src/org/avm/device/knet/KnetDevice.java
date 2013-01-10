/**
 * 
 */
package org.avm.device.knet;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;

/**
 * @author lbr
 * 
 */
public abstract class KnetDevice {
	private KnetAgent _agentKnet = null;
	private boolean _canReceive = false;
	private Logger _log = Logger.getInstance(this.getClass().getName());

	public void setAgent(KnetAgentFactory factory) {
		_agentKnet = factory.create(getKnetApp());
	}

	public void unsetAgent() {
		_agentKnet = null;
	}

	public abstract void receive(Kms kms);

	public abstract int getKnetApp();

	public abstract String getKnetAppAsString();

	protected void open(String host, int port, String login, String passwd,
			int from, int to, int id) throws KnetException {
		String fr = String.valueOf(from);
		String t = String.valueOf(to);
		String i = String.valueOf(id);
		open(host, port, login, passwd, fr, t, i);
	}

	protected void open(String host, int port, String login, String passwd,
			int from) throws KnetException {
		String fr = String.valueOf(from);
		open(host, port, login, passwd, fr, null, null);
	}

	protected void open(String host, int port, String login, String passwd,
			String from, String to, String id) throws KnetException {
		_agentKnet.open(host, port, login, passwd, from, to, id);
	}

	protected void close() throws KnetException {
		_agentKnet.close();
		_canReceive = false;
	}

	protected KmsMarshaller send(KmsMarshaller message) throws KnetException {
		return _agentKnet.send(message);
	}

	protected void post(KmsMarshaller message) throws KnetException {
		_agentKnet.post(message);
	}

	protected void startListen() {
		_canReceive = true;
		Thread t = new Thread(new PrivateReception());
		t.start();
	}

	private class PrivateReception implements Runnable {
		private KmsMarshaller _kms = null;

		public void run() {
			_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
					+ "Demarrage de la boucle d'ecoute des messages pour "
					+ getKnetAppAsString()
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);

			try {
				while (_canReceive) {
					_kms = _agentKnet.receiveKms();
					if (_kms == null) {
						_log.warn("NE DEVRAIT JAMAIS ARRIVER !!");
						continue;
					}
					receive(((KmsRoot) _kms));
				}
			} catch (Exception e) {
				_log.error("Le manager ne peut recevoir le message ", e);
			}
		}

	}

}
