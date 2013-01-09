package org.avm.elementary.messenger.bundle;

import java.util.Dictionary;

import org.avm.elementary.command.CommandChain;
import org.avm.elementary.command.CommandChainInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Media;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.MediaService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.impl.MessengerImpl;
import org.avm.elementary.parser.Parser;
import org.avm.elementary.parser.ParserService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Messenger,
		MediaListener, MediaService, ParserService {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private MessengerImpl _peer;

	private AbstractProducer _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private CommandChain _command;

	public Activator() {
		_plugin = this;
		_peer = new MessengerImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		initializeCommandChain();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeCommandChain();
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

	// command chain
	private void initializeCommandChain() {
		_command = (CommandChain) _context.locateService("command");
		if (_peer instanceof CommandChainInjector) {
			((CommandChainInjector) _peer).setCommandChain(_command);
		}
	}

	private void disposeCommandChain() {
		if (_peer instanceof CommandChainInjector) {
			((CommandChainInjector) _peer).setCommandChain(null);
		}
	}

	public void send(Dictionary header, Object data) throws Exception {
		if (_peer == null)
			return;
		_peer.send(header, data);

	}

	public void receive(Dictionary header, byte[] data) {
		if (_peer == null)
			return;
		_peer.receive(header, data);

	}

	public void setMedia(Media media) {
		_log.debug("Initialisation du media " + media.getMediaId());
		if (_peer == null)
			return;
		_peer.setMedia(media);
	}

	public void unsetMedia(Media media) {
		_log.debug("Supression du media " + media.getMediaId());
		if (_peer == null)
			return;
		_peer.unsetMedia(media);
	}

	public void setParser(Parser parser) {
		_log.debug("Initialisation du parser " + parser.getProtocolName());
		if (_peer == null)
			return;
		_peer.setParser(parser);

	}

	public void unsetParser(Parser parser) {
		_log.debug("Supression du parser " + parser.getProtocolName());
		if (_peer == null)
			return;
		_peer.unsetParser(parser);

	}
}
