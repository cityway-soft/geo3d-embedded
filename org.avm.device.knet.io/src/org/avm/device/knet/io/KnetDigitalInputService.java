package org.avm.device.knet.io;

import java.util.BitSet;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.device.knet.sensor.Sensor;
import org.avm.device.knet.sensor.SensorInjector;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;

public class KnetDigitalInputService extends ServiceTracker implements
		DigitalIODriver, SensorInjector {

	private Logger _log = Logger.getInstance(this.getClass());

	private DigitalIODevice _device;

	private BitSet _data = new BitSet();

	private ServiceRegistration _sr;

	private ComponentContext _context;

	private PropertyChangeSupport _listeners;

	private Sensor _knetSensor;

	private PrivateSensorFrontDoor _sensorFD;
	private PrivateSensorRearDoor _sensorRD;

	public KnetDigitalInputService(ComponentContext context,
			ServiceReference device) {
		super(context.getBundleContext(), device, null);
		_context = context;
		for (int i = 0; i < _data.size(); i++) {
			_data.clear(i);
		}
		_listeners = new PropertyChangeSupport(this);
		_sensorFD = new PrivateSensorFrontDoor();
		_sensorRD = new PrivateSensorRearDoor();
		open();
		// _log.setPriority(Priority.DEBUG);
	}

	public int getCapability() {
		return _data.size();
	}

	public boolean getValue(int index) {
		if (index >= _data.size())
			throw new IndexOutOfBoundsException();
		boolean value = _data.get(index);
		_log.debug("I/O[" + index + "] = " + value);
		return value;
	}

	public void setValue(int index, boolean value) {
		// throw new UnsupportedOperationException();

		if (index >= _data.size())
			throw new IndexOutOfBoundsException();

		boolean oldValue = _data.get(index);

		if (value)
			_data.set(index);
		else
			_data.clear(index);

		_log.debug("I/O[" + index + "] = " + _data.get(index));

		_listeners.fireIndexedPropertyChange(null, index, oldValue, value);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}

	public Object addingService(ServiceReference reference) {
		Object o = super.addingService(reference);
		if (o instanceof DigitalIODevice) {
			_device = (DigitalIODevice) o;
			_device.setDriver(this);
			// start tache d'aquisition
			_sensorFD.start();
			_sensorRD.start();
			_log.debug("registerService(" + this.getClass().getName() + "...)");
			_sr = context.registerService(this.getClass().getName(), this,
					new Properties());
		}

		return o;
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		if (service instanceof DigitalIODevice) {
			// stop tache d'aquisition
			_sensorFD.stop();
			_sensorRD.stop();

			_device.setDriver(null);
			_sr.unregister();
			_device = null;
		}
	}

	private class PrivateSensorFrontDoor implements Runnable {
		Thread _t;
		private boolean _canRead = false;

		protected PrivateSensorFrontDoor() {
			_t = new Thread(this);
		}

		public void start() {
			// _knetSensor = (Sensor) _context.locateService("sensor");
			_log.debug("_knetSensor = " + _knetSensor);
			_canRead = true;
			_t.start();
		}

		public void stop() {
			// _knetSensor = null;
			_canRead = false;
			_t.interrupt();
			stop();
		}

		public void run() {
			_log.debug("Start read front door state");
			String porteAV = null;
			try {
				while (_canRead) {
					if (_knetSensor == null) {
						Thread.sleep(1000);
						continue;
					} else {
						porteAV = _knetSensor.readFrontDoor();
					}
					if (porteAV == null) {
						_log.warn("NE DEVRAIT JAMAIS ARRIVER !!");
						break;
					}
					_log.debug("Porte avant"
							+ (porteAV.equals(Sensor.FERME_ID) ? " fermee"
									: " ouverte"));
					int etat = Integer.parseInt(porteAV);
					if (etat == 0)
						setValue(Integer.parseInt(Sensor.PORTE_AV_ID), true);
					// _data.set(Integer.parseInt(Sensor.PORTE_AV_ID));
					else
						setValue(Integer.parseInt(Sensor.PORTE_AV_ID), false);
					// _data.clear(Integer.parseInt(Sensor.PORTE_AV_ID));
					porteAV = null;
				}
			} catch (Exception e) {
				_log.error("Le manager ne peut recevoir le message ", e);
			}
		}
	}

	private class PrivateSensorRearDoor implements Runnable {
		Thread _t;
		private boolean _canRead = false;

		protected PrivateSensorRearDoor() {
			_t = new Thread(this);
		}

		public void start() {
			// _knetSensor = (Sensor) _context.locateService("sensor");
			_log.debug("_knetSensor = " + _knetSensor);
			_canRead = true;
			_t.start();
		}

		public void stop() {
			// _knetSensor = null;
			_canRead = false;
			_t.interrupt();
			stop();
		}

		public void run() {
			_log.debug("Start read rear door state");
			String porteAR = null;
			try {
				while (_canRead) {
					if (_knetSensor == null) {
						Thread.sleep(1000);
						continue;
					} else {
						porteAR = _knetSensor.readRearDoor();
					}
					if (porteAR == null) {
						_log.warn("NE DEVRAIT JAMAIS ARRIVER !!");
						break;
					}
					_log.debug("Porte arriere"
							+ (porteAR.equals(Sensor.FERME_ID) ? " fermee"
									: " ouverte"));
					int etat = Integer.parseInt(porteAR);
					if (etat == 0)
						setValue(Integer.parseInt(Sensor.PORTE_ARR_ID), true);
					// _data.set(Integer.parseInt(Sensor.PORTE_ARR_ID));
					else
						setValue(Integer.parseInt(Sensor.PORTE_ARR_ID), false);
					// _data.clear(Integer.parseInt(Sensor.PORTE_ARR_ID));
					porteAR = null;
				}
			} catch (Exception e) {
				_log.error("Le manager ne peut recevoir le message ", e);
			}
		}
	}

	public void setSensor(Sensor sensor) {
		_log.debug("setSensor(" + sensor + ")");
		_knetSensor = sensor;
	}
}
