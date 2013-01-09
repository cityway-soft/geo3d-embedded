package org.avm.elementary.common;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdmin;
import org.osgi.service.wireadmin.WireConstants;

public abstract class AbstractProducer implements Producer, ProducerManager,
		ManageableService {

	protected Logger _log;

	protected Wire _wires[];

	protected Object _mutex = new Object();

	protected ComponentContext _context;

	protected WireAdmin _wireadmin;

	protected ServiceRegistration _producerRegistration;

	public AbstractProducer(ComponentContext context) {
		_context = context;
		_wireadmin = (WireAdmin) _context.locateService("wireadmin");
		_log = Logger.getInstance(this.getClass());
	}

	protected abstract String getProducerPID();

	protected abstract Class[] getProducerFlavors();

	public Object polled(Wire wire) {
		return null;
	}

	public void consumersConnected(Wire[] wires) {
		if (wires != null) {
			if (_log.isDebugEnabled()) {
				for (int i = 0; i < wires.length; i++) {
					Dictionary properties = wires[i].getProperties();
					_log.debug("Consummer connected "
							+ properties
									.get(WireConstants.WIREADMIN_CONSUMER_PID));
				}
			}
		} else {
			_log.debug("Consumers connected is null");
		}
		synchronized (_mutex) {
			_wires = wires;
		}
	}

	public void publish(Object o) {
		if (_wireadmin == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Notification du message " + o.getClass().getName());
		}

		if (_wires != null) {
			synchronized (_mutex) {
				for (int i = 0; i < _wires.length; i++) {
					Wire wire = _wires[i];
					wire.update(o);
				}
			}

		}
	}

	public void start() {
		try {

			Hashtable properties = new Hashtable();
			properties.put(Constants.SERVICE_PID, getProducerPID());
			properties.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS,
					getProducerFlavors());
			_producerRegistration = _context
					.getBundleContext()
					.registerService(Producer.class.getName(), this, properties);
		} catch (Throwable e) {
			_log.error(e.getMessage(), e);
		}

	}

	public void stop() {
		try {
			_producerRegistration.unregister();
		} catch (Throwable e) {
			_log.error(e.getMessage(), e);
		}
	}

}
