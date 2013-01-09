package org.avm.elementary.media.sms.bundle;

import java.util.Dictionary;

import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Media;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.PublisherService;
import org.avm.elementary.media.sms.MediaSMS;
import org.avm.elementary.media.sms.MediaSMSImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Media {

	public static final String PID = MediaSMS.class.getName();

	private ConfigurationAdmin _cm;

	private MediaSMSImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private MediaListener _messenger;

	private Gsm _gsm;

	private ConsumerImpl _consumer;

	public Activator() {
		_peer = new MediaSMSImpl();
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeGsm();
		initializeMessenger();
		initializeConsumer();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeCommandGroup();
		disposeConsumer();
		disposeMessenger();
		disposeGsm();
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

	// consumer
	private void initializeConsumer() {

		if (_peer instanceof ConsumerService) {
			_consumer = new ConsumerImpl(_context, _peer);
			_consumer.start();
		}
	}

	private void disposeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer.stop();
		}
	}

	// messenger
	private void initializeMessenger() {
		_messenger = (MediaListener) _context.locateService("messenger");
		if (_peer instanceof PublisherService) {
			_peer.setMessenger(_messenger);
		}
	}

	private void disposeMessenger() {
		if (_peer instanceof PublisherService) {
			_peer.setMessenger(null);
		}
	}

	// gsm
	private void initializeGsm() {
		_gsm = (Gsm) _context.locateService("gsm");
		if (_peer instanceof GsmInjector) {
			_peer.setGsm(_gsm);
		}
	}

	private void disposeGsm() {
		if (_peer instanceof GsmInjector) {
			_peer.unsetGsm(_gsm);
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

	public String getMediaId() {
		return "SMS";
	}

	public int getPriority() {
		return _peer.getPriority();
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		if (_peer == null)
			return;
		_peer.send(header, data);
	}

}
