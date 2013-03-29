package org.avm.elementary.alarm.bundle;

import org.avm.business.protocol.phoebus.ClotureAlerte;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ConsumerImpl extends AbstractConsumer implements
		ServiceTrackerCustomizer {

	public static final String MESSENGER_PRODUCER_PID = Messenger.class
			.getName();

	private final ComponentContext _context;

	private final ServiceTracker _tracker;

	public ConsumerImpl(final ComponentContext context,
			final ConsumerService consumer) {

		super(context, consumer);
		this._context = context;
		this._tracker = new ServiceTracker(this._context.getBundleContext(),
				AlarmProvider.class.getName(), this);
	}

	public Object addingService(final ServiceReference reference) {

		final AlarmProvider provider = (AlarmProvider) this._context
				.getBundleContext().getService(reference);
		this.createWire(provider.getProducerPID());
		return provider;
	}

	public void modifiedService(final ServiceReference reference,
			final Object service) {

	}

	public void removedService(final ServiceReference reference,
			final Object service) {

		this._context.getBundleContext().ungetService(reference);
	}

	public void start() {

		super.start();
		this._tracker.open();
	}

	public void stop() {

		this._tracker.close();
		super.stop();
	}

	protected void createWires() {

		this._wireadmin.createWire(ConsumerImpl.MESSENGER_PRODUCER_PID,
				this.getConsumerPID(), null);

		// Object[] providers = _context.locateServices("alarms"); for (int i=0;
		// i<providers.length; i++){ AlarmProvider provider =
		// (AlarmProvider)providers[i]; createWire(provider.getProducerPID()); }

	}

	protected Class[] getConsumerFlavors() {

		final Class[] result = new Class[] { Alarm.class, ClotureAlerte.class };
		return result;
	}

	protected String getConsumerPID() {

		return Activator.getDefault().getPid();
	}
}
