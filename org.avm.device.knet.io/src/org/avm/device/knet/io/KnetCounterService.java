package org.avm.device.knet.io;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.io.CounterDevice;
import org.avm.device.io.CounterDriver;
import org.avm.device.knet.sensor.Sensor;
import org.avm.device.knet.sensor.SensorInjector;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;

public class KnetCounterService extends ServiceTracker implements
		CounterDriver, SensorInjector {

	private Logger _log = Logger.getInstance(this.getClass());

	private CounterDevice _device;

	private ServiceRegistration _sr;

	private ComponentContext _context;

	private int[] _data = new int[1];

	private PrivateSensor _sensor;

	private Sensor _knetSensor;

	public KnetCounterService(ComponentContext context, ServiceReference device) {
		super(context.getBundleContext(), device, null);
		_context = context;
		_data[0] = 0;
		_sensor = new PrivateSensor();

		open();
		// _log.setPriority(Priority.DEBUG);
	}

	public int getCapability() {
		return _data.length;
	}

	public int getBitResolution() {
		return 16;
	}

	public int getValue(int index) {
		if (index >= _data.length)
			throw new IndexOutOfBoundsException();

		if (_device == null)
			return 0;

		return _data[index];
	}

	public void reset(int index) {
		if (index >= _data.length)
			throw new IndexOutOfBoundsException();

		if (_device == null)
			return;

		_data[index] = 0;
	}

	public Object addingService(ServiceReference reference) {
		Object o = super.addingService(reference);
		if (o instanceof CounterDevice) {
			_device = (CounterDevice) o;
			_device.setDriver(this);
			// start tache d'aquisition
			_sensor.start();

			_sr = context.registerService(this.getClass().getName(), this,
					new Properties());
		}

		return o;
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		if (service instanceof CounterDevice) {
			// stop tache d'aquisition
			_sensor.stop();

			_device.setDriver(null);
			_sr.unregister();
			_device = null;
		}
	}

	private class PrivateSensor implements Runnable {
		Thread _t;
		private boolean _canRead = false;

		protected PrivateSensor() {
			_t = new Thread(this);
		}

		public void start() {
			// _knetSensor = (Sensor) _context.locateService("sensor");
			_log.debug("_knetSensor = " + _knetSensor);

			_canRead = true;
			_t.start();
		}

		public void stop() {
			_knetSensor = null;
			_canRead = false;
			_t.interrupt();
			stop();
		}

		public void run() {
			_log.debug("Start read Compost");
			String compost = null;
			try {
				while (_canRead) {
					if (_knetSensor == null) {
						Thread.sleep(1000);
						continue;
					} else {
						compost = _knetSensor.readCompost();
					}
					if (compost == null) {
						_log.warn("NE DEVRAIT JAMAIS ARRIVER !!");
						break;
					}
					_data[0]++;
					compost = null;
					_log.debug("1 Compostage ");
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
