package org.avm.device.usbmass;

import java.io.File;

import org.apache.log4j.Logger;
import org.avm.device.usbmass.bundle.ConfigImpl;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.Scheduler;
import org.osgi.util.measurement.State;

/**
 * @author MERCUR : Didier LALLEMAND
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 * 
 */
public class UsbMassImpl implements UsbMass, ProducerService,
		ManageableService, ConfigurableService {

	private Logger _log = Logger.getInstance(this.getClass());

	private Scheduler _scheduler;

	private boolean _usbInserted = false;

	private File _mountPoint = null;

	private long _frequency;

	private ProducerManager _producer;

	private ConfigImpl _config;

	private String _name;

	private Object _taskId;

	public UsbMassImpl() {
		_scheduler = new Scheduler();
	}

	public void start() {
		if (_taskId != null) {
			_scheduler.cancel(_taskId);
		}
		if (_config != null) {
			_mountPoint = new File(_config.getMountPoint());
			_frequency = Long.parseLong(_config.getPollFrequency());
		}
		_taskId = _scheduler.schedule(new USBTimerTask(), _frequency * 1000,
				true);
		_name = UsbMass.class.getName() + "@" + _config.getMountPoint();
	}

	public void stop() {
		if (_taskId != null) {
			_scheduler.cancel(_taskId);
		}
	}

	public boolean isAvailable() {
		boolean result = (_mountPoint != null) && _mountPoint.exists();
		if (_log.isDebugEnabled()) {
			_log.debug("USB at " + _mountPoint + " is " + (result ? "" : "NOT")
					+ " available.");
		}
		return result;
	}

	class USBTimerTask implements Runnable {

		public void run() {
			try {
				if (_usbInserted != isAvailable()) {
					_usbInserted = isAvailable();
					State state = new State(_usbInserted ? 1 : 0, _name);
					publish(state);
				}
			} catch (Throwable t) {
				_log.error("Error", t);
			}
		}

	}

	private void publish(State statut) {
		if (_producer != null) {
			if (_log.isDebugEnabled()) {
				_log.debug("publish " + statut);
			}
			_producer.publish(statut);
		}
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
	}

}