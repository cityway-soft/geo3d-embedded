package org.avm.device.generic.can.bundle;

import org.avm.device.can.Can;
import org.avm.device.generic.can.CanImpl;
import org.avm.elementary.can.parser.CANParser;
import org.avm.elementary.can.parser.CANParserInjector;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator extends AbstractActivator implements Can {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private CanImpl _peer;

	private AbstractProducer _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private CANParser _parser;

	private ServiceTracker _tracker;

	public Activator() {
		_peer = new CanImpl();
		_plugin = this;
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		initializeParser();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeParser();
		disposeCommandGroup();
		disposeProducer();
		disposeConfiguration();
	}

	// config
	private void initializeConfiguration() {

		_cm = (ConfigurationAdmin) _context.locateService("cm");
		try {
			_config = new ConfigImpl(_context, _cm);
			_config.start();
			if (_peer instanceof ConfigurableService) {
				((ConfigurableService) _peer).configure(_config);

			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

	}

	private void disposeConfiguration() {
		_config.stop();
		if (_peer instanceof ConfigurableService) {
			((ConfigurableService) _peer).configure(null);
		}
	}

	// producer
	private void initializeProducer() {
		if (_peer instanceof ProducerService) {
			_producer = new ProducerImpl(_context);
			_producer.start();
			((ProducerService) _peer).setProducer(_producer);
		}
	}

	private void disposeProducer() {
		if (_peer instanceof ProducerService) {
			((ProducerService) _peer).setProducer(null);
			_producer.stop();
		}
	}

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	private void startService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	// parser
	private void initializeParser() {
		_tracker = new CanParserTracker(_context.getBundleContext(),
				getFilter());
		_tracker.open();
	}

	private void disposeParser() {
		_tracker.close();
	}

	public void send(PGN pgn) throws Exception {
		_peer.send(pgn);
	}

	private Filter getFilter() {
		String protocol = (String) _context.getProperties()
				.get("protocol.name");
		String filter = "(&" + " ( " + org.osgi.framework.Constants.OBJECTCLASS
				+ "=" + CANParser.class.getName() + ")" + " ( "
				+ org.osgi.framework.Constants.SERVICE_PID + "=" + protocol
				+ ")" + ")";
		try {
			return _context.getBundleContext().createFilter(filter);
		} catch (InvalidSyntaxException e) {
			_log.debug(e);
			return null;
		}

	}

	class CanParserTracker extends ServiceTracker implements
			ServiceTrackerCustomizer {

		public CanParserTracker(BundleContext context, Filter filter) {
			super(context, filter, null);
		}

		public Object addingService(ServiceReference reference) {
			_parser = (CANParser) context.getService(reference);
			if (_peer instanceof CANParserInjector) {
				((CANParserInjector) _peer).setCANParser(_parser);
			}
			return _parser;
		}

		public void removedService(ServiceReference reference, Object service) {
			if (_peer instanceof CANParserInjector) {
				((CANParserInjector) _peer).unsetCANParser(_parser);
			}
			_parser = null;
			context.ungetService(reference);
		}

	}
}
