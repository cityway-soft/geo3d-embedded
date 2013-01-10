package org.angolight.hmi.swt.leds.bundle;

import org.angolight.device.leds.Leds;
import org.angolight.hmi.swt.leds.LedsImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.hmi.swt.desktop.Desktop;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Leds {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private LedsImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private ProducerImpl _producer;

	public Activator() {
		_plugin = this;
		_peer = new LedsImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeDesktop();
		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeProducer();
		disposeCommandGroup();
		disposeConfiguration();
		disposeDesktop();
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

	// Base
	private void initializeDesktop() {
		Desktop desktop = (Desktop) _context.locateService("desktop");
		_peer.setDesktop(desktop);
	}

	private void disposeDesktop() {
		_peer.setDesktop(null);
	}

	public int I(byte address, short states, byte period, boolean check) {
		return _peer.I(address, states, period, check);
	}

	public int J(byte address, short states, byte period, boolean check) {
		return _peer.J(address, states, period, check);
	}

	public int L(byte value, boolean check) {
		return _peer.L(value, check);
	}

	public int M(short states, byte period, boolean check) {
		return _peer.M(states, period, check);
	}

	public int R(byte address, boolean check) {

		return _peer.R(address, check);
	}

	public int S(boolean check) {

		return _peer.S(check);
	}

	public int T(boolean check) {

		return _peer.T(check);
	}

	public int V(boolean check) {

		return _peer.V(check);
	}

	public int X(byte address, byte cycle, byte period, boolean check) {
		return _peer.X(address, cycle, period, check);
	}
}
