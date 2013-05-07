package org.avm.device.generic.io.memory;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.gps.Gps;
import org.avm.device.io.CounterDevice;
import org.avm.device.io.CounterDriver;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.position.Position;
import org.osgi.util.tracker.ServiceTracker;

public class OdometerCounterService extends ServiceTracker implements
		CounterDriver, ConsumerService {

	private static final double LIMIT = 1d;

	private Logger _log = Logger.getInstance(this.getClass());

	private CounterDevice _device;

	private Consumer _consumer;

	private ServiceRegistration _sr;

	private ComponentContext _context;

	private Position _current;

	private double _counter;

	private int[] _data = new int[1];

	public OdometerCounterService(ComponentContext context,
			ServiceReference device) {
		super(context.getBundleContext(), device, null);
		_context = context;
		_data[0] = 0;
		open();
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

		_counter = 0d;
		_data[index] = 0;
	}

	public Object addingService(ServiceReference reference) {
		Object o = super.addingService(reference);
		if (o instanceof CounterDevice) {
			_device = (CounterDevice) o;
			_device.setDriver(this);
			_consumer = new Consumer(_context, this, _device.toString());
			_consumer.start();
			_sr = context.registerService(this.getClass().getName(), this,
					new Properties());
		}

		return o;
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		if (service instanceof CounterDevice) {
			_consumer.stop();
			_device.setDriver(null);
			_sr.unregister();
			_device = null;
		}
	}

	public void notify(Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;

			if (p.getLatitude().getValue() == 0d
					&& p.getLongitude().getValue() == 0d) {
				return;
			}

			if (_current == null) {
				_current = p;
				_counter = 0d;
				_data[0] = 0;
				return;
			}

			double speed = p.getSpeed().getValue();

			if (speed > LIMIT) {
				double haversine = computeHaversineFormula(_current, p);
				_counter += haversine;
				_data[0] = (int) (((int) Math.ceil(_counter)) % 0xffff);
				if (_log.isDebugEnabled()){
				_log.debug("counter="+_counter +", data[0]="+_data[0]);
				}
				_current = p;
			}
		}
	}

	/**
	 * @see http://mathforum.org/library/drmath/view/51879.html
	 */
	private double computeHaversineFormula(Position p1, Position p2) {

		final double R = 6371008.8;

		double lon1 = p1.getLongitude().getValue();
		double lat1 = p1.getLatitude().getValue();

		double lon2 = p2.getLongitude().getValue();
		double lat2 = p2.getLatitude().getValue();

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		_log.debug("dlon=" + dlon + ", dlat=" + dlat);

		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;

		return d;
	}

	class Consumer extends AbstractConsumer {

		public final String PRODUCER_PID = Gps.class.getName();

		public String CONSUMER_PID;

		public Consumer(ComponentContext context, ConsumerService consumer,
				String pid) {
			super(context, consumer);
			CONSUMER_PID = pid;
		}

		protected String getConsumerPID() {
			return CONSUMER_PID;
		}

		protected void createWires() {
			_wireadmin.createWire(PRODUCER_PID, getConsumerPID(), null);
		}

		protected Class[] getConsumerFlavors() {
			Class[] result = new Class[] { Position.class };
			return result;
		}

	}
}
