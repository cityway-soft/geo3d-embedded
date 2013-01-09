package org.avm.elementary.alarm.bundle;

import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ConsumerImpl extends AbstractConsumer implements
		ServiceTrackerCustomizer {

	private ComponentContext _context;

	private ServiceTracker _tracker;

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
		_context = context;
		_tracker = new ServiceTracker(_context.getBundleContext(),
				AlarmProvider.class.getName(), this);
	}

	public void start() {
		super.start();
		_tracker.open();
	}

	public void stop() {
		_tracker.close();
		super.stop();
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		/*
		 * Object[] providers = _context.locateServices("alarms"); for (int i=0;
		 * i<providers.length; i++){ AlarmProvider provider =
		 * (AlarmProvider)providers[i]; createWire(provider.getProducerPID()); }
		 */
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { Alarm.class };
		return result;
	}

	public Object addingService(ServiceReference reference) {
		AlarmProvider provider = (AlarmProvider) _context.getBundleContext()
				.getService(reference);
		createWire(provider.getProducerPID());
		return provider;
	}

	public void modifiedService(ServiceReference reference, Object service) {
	}

	public void removedService(ServiceReference reference, Object service) {
		_context.getBundleContext().ungetService(reference);
	}

}
