package org.avm.business.tft.bundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.messages.Messages;
import org.avm.business.messages.MessagesInjector;
import org.avm.business.tft.Tft;
import org.avm.business.tft.TftImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Tft {

	private static Activator _plugin;
	private ConfigImpl _config;
	private CommandGroupImpl _commands;
	private TftImpl _peer;
	private ConsumerImpl _consumer;
	private Logger _log;

	public Activator() {
		_plugin = this;
		_peer = new TftImpl();
		_log = Logger.getInstance(this.getClass());
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeConsumer();
		initializeCommandGroup();
		initializeAvm();
		initializeMessages();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeMessages();
		disposeAvm();
		disposeCommandGroup();
		disposeConsumer();
		disposeConfiguration();
	}

	// config
	private void initializeConfiguration() {
		ConfigurationAdmin cm = (ConfigurationAdmin) _context
				.locateService("cm");
		try {
			_config = new ConfigImpl(_context, cm);
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

	// avm
	private void initializeAvm() {
		if (_peer instanceof AvmInjector) {
			Avm avm = (Avm) _context.locateService("avm");
			((AvmInjector) _peer).setAvm(avm);
		}
	}

	private void disposeAvm() {
		if (_peer instanceof AvmInjector) {
			Avm avm = (Avm) _context.locateService("avm");
			((AvmInjector) _peer).unsetAvm(avm);
		}
	}

	// messages
	private void initializeMessages() {
		if (_peer instanceof MessagesInjector) {
			Messages messages = (Messages) _context.locateService("messages");
			((MessagesInjector) _peer).setMessages(messages);
		}
	}

	private void disposeMessages() {
		if (_peer instanceof MessagesInjector) {
			Messages messages = (Messages) _context.locateService("messages");
			((MessagesInjector) _peer).setMessages(messages);
		}
	}

	public void process(HttpServletRequest request, HttpServletResponse response)
			throws Throwable {
		_peer.process(request, response);
	}

	public void refresh() {
		_peer.refresh();
	}

	public Config getConfig() {
		return _config;
	}

	public void reloadPage() {
		_peer.reloadPage();
	}
}
