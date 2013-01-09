package org.avm.elementary.common;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdmin;
import org.osgi.service.wireadmin.WireConstants;

import EDU.oswego.cs.dl.util.concurrent.BoundedLinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Channel;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

public abstract class AbstractConsumer implements Consumer, ManageableService {

	protected Logger _log;

	protected WireAdmin _wireadmin;

	protected Wire _wires[];

	protected ComponentContext _context;

	protected ServiceRegistration _consumerRegistration;

	protected ConsumerService _consumer;

	private Notifier _notifier;

	protected BoundedLinkedQueue _channel;

	protected static PooledExecutor _executor;

	public static final int CAPACITY = 50;

	private static final long TIMEOUT = 0;

	public AbstractConsumer(ComponentContext context, ConsumerService consumer) {
		this(context, consumer, CAPACITY, TIMEOUT);
	}

	public AbstractConsumer(ComponentContext context, ConsumerService consumer,
			int capacity) {
		this(context, consumer, capacity, TIMEOUT);
	}

	public AbstractConsumer(ComponentContext context, ConsumerService consumer,
			int capacity, long timeout) {
		_channel = new BoundedLinkedQueue(capacity);
		_executor = getExecutor();
		_notifier = new Notifier(_channel, timeout);
		_context = context;
		_consumer = consumer;
		_wireadmin = (WireAdmin) _context.locateService("wireadmin");
		_log = Logger.getInstance(this.getClass());
	}

	protected static class DefaultThreadFactory implements ThreadFactory {
		private int _count = 0;

		public Thread newThread(Runnable command) {
			Thread thread = new Thread(command);
			thread.setDaemon(true);
			thread.setName("[AVM] consumer : " + _count++);
			return thread;
		}
	}

	protected PooledExecutor getExecutor() {
		if (_executor == null) {
			ThreadFactory factory = new DefaultThreadFactory();
			_executor = new PooledExecutor();
			_executor.setThreadFactory(factory);
			_executor.setMinimumPoolSize(5);
			_executor.setKeepAliveTime(60 * 1000);
		}
		return _executor;
	}

	public void producersConnected(Wire[] wires) {
		_wires = wires;
		if (_log.isDebugEnabled()) {
			for (int i = 0; i < wires.length; i++) {
				Dictionary properties = wires[i].getProperties();
				_log.debug("Producer connected "
						+ properties.get(WireConstants.WIREADMIN_PRODUCER_PID));
			}
		}
	}

	public void createWire(String producerPID) {
		createWire(producerPID, null);
	}

	public void createWire(String producerPID, Dictionary properties) {
		_wireadmin.createWire(producerPID, getConsumerPID(), properties);
	}

	public void deleteWire(String producerPID) {
		Wire[] wires;
		try {
			String filter = "(&" + "(" + WireConstants.WIREADMIN_CONSUMER_PID
					+ "=" + getConsumerPID() + ")" + "("
					+ WireConstants.WIREADMIN_PRODUCER_PID + "=" + producerPID
					+ ")" + ")";

			wires = _wireadmin.getWires(filter);
			if (wires != null) {
				for (int i = 0; i < wires.length; i++) {
					Dictionary d = wires[i].getProperties();
					if (_log.isDebugEnabled()) {
						_log.debug("Delete wire for producer  "
								+ d.get(WireConstants.WIREADMIN_PRODUCER_PID));
					}
					_wireadmin.deleteWire(wires[i]);
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	protected abstract void createWires();

	protected abstract Class[] getConsumerFlavors();

	protected abstract String getConsumerPID();

	protected void deleteAllWires() {
		// delete all wires
		Wire[] wires;
		try {
			String filter = "(" + WireConstants.WIREADMIN_CONSUMER_PID + "="
					+ getConsumerPID() + ")";
			wires = _wireadmin.getWires(filter);
			if (wires != null) {
				for (int i = 0; i < wires.length; i++) {
					Dictionary d = wires[i].getProperties();
					if (_log.isDebugEnabled()) {
						_log.debug("Delete wire for producer  "
								+ d.get(WireConstants.WIREADMIN_PRODUCER_PID));
					}
					_wireadmin.deleteWire(wires[i]);
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public void start() {
		try {

			Hashtable properties = new Hashtable();
			properties.put(Constants.SERVICE_PID, getConsumerPID());
			properties.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS,
					getConsumerFlavors());
			_consumerRegistration = _context
					.getBundleContext()
					.registerService(Consumer.class.getName(), this, properties);

			deleteAllWires();
			createWires();
		} catch (Throwable e) {
			_log.error(e);
		}

	}

	public void stop() {
		try {
			_consumerRegistration.unregister();
			deleteAllWires();
		} catch (Throwable e) {
			_log.error(e);
		}

	}

	public void updated(Wire wire, final Object value) {
		try {
			if (_channel.capacity() - _channel.size() > 0) {
				_notifier.notify(value);
			} else {
				_log
						.error("[DSU] Echec notification consumer: capacite max atteinte !");
			}
		} catch (Exception e) {
			_log.error(e);
		}

	}

	class Notifier implements Runnable {

		protected final Channel _queue;

		protected final Object _lock = new Object();

		protected long _timeout;

		private volatile boolean _started = false;

		public Notifier(Channel queue, long timeout) {
			_queue = queue;
			_timeout = timeout;
		}

		public void notify(Object value) throws InterruptedException {

			_queue.offer(value, 10);

			// start reader
			synchronized (_lock) {
				if (!_started) {
					try {
						getExecutor().execute(this);
						_started = true;
					} catch (InterruptedException e) {
					}
				}
			}

		}

		public void run() {
			try {
				while (true) {

					Object value = _queue.poll(_timeout);
					if (value != null) {
						_consumer.notify(value);
						continue;
					}

					// stop reader
					synchronized (_lock) {
						if (_queue.peek() == null) {
							_started = false;
							break;
						}
					}
				}
			} catch (InterruptedException e) {
				_started = false;
			}
		}
	}
}
