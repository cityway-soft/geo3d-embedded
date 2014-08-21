package org.avm.elementary.management.addons.bundle;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementImpl;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.core.Management;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.knopflerfish.service.console.ConsoleService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class Activator extends AbstractActivator implements
		ManagementService {

	static final String PID = ManagementService.class.getName();

	private ConfigurationAdmin _cm;

	private ManagementImpl _peer;

	private ConsumerImpl _consumer;

	private ProducerImpl _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_log = Logger.getInstance(this.getClass());
		_peer = new ManagementImpl();
		_log.setPriority(Priority.DEBUG);
	}

	protected void start(ComponentContext context) {
		_peer.setBundleContext(context.getBundleContext());
		initializeConfiguration();
		initializeConsoleService();
		initializeConsumer();
		initializeProducer();
		initializeSyslog();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeCommandGroup();
		disposeSyslog();
		disposeProducer();
		disposeConsumer();
		disposeConsoleService();
		disposeConfiguration();
	}

	// service sys logger
	private void initializeSyslog() {
		org.avm.elementary.log4j.Logger syslog = (org.avm.elementary.log4j.Logger) _context
				.locateService("syslog");
		_peer.setSyslog(syslog);
	}

	private void disposeSyslog() {
		_peer.setSyslog(null);
	}

	public void setSyslog(org.avm.elementary.log4j.Logger syslog) {
		_peer.setSyslog(syslog);
	}

	// console
	private void initializeConsoleService() {
		ConsoleService console = (ConsoleService) _context
				.locateService("console");
		_peer.setConsoleService(console);
	}

	private void disposeConsoleService() {
		_peer.unsetConsoleService(null);
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
		if (_commands == null) {
			_commands = new CommandGroupImpl(_context, _peer, _config);
			_commands.start();
		}
	}

	private void disposeCommandGroup() {
		if (_commands != null) {
			_commands.stop();
		}
		_commands = null;
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

	public void notify(Object o) {
		_peer.notify(o);
	}

	public void execute(String commandName, Properties params, PrintWriter out)
			throws CommandException, IOException {
		_peer.execute(commandName, params, out);
	}

	public void synchronize(PrintWriter out) throws Exception {
		_peer.synchronize(out);
	}

	public void synchronizeData() throws Exception {
		_peer.synchronizeData();
	}

	public Management getManagementService() {
		return (Management) _context.locateService("management");
	}

	public void setMessenger(Messenger messenger) {
		if (_peer instanceof MessengerInjector) {
			_peer.setMessenger(messenger);
		}
	}

	public void unsetMessenger(Messenger messenger) {
		if (_peer instanceof MessengerInjector) {
			_peer.unsetMessenger(messenger);
		}
	}

	public StartLevel getStartLevelService() {
		return _peer.getStartLevelService();
	}

	public PackageAdmin getPackageAdminService() {
		return _peer.getPackageAdminService();
	}

	public void setDownloadURL(URL url) throws Exception {
		_peer.setDownloadURL(url);
	}

	public void setUploadURL(URL url) throws Exception {
		_peer.setUploadURL(url);
	}

	public URL getDownloadURL() throws Exception{
		return _peer.getDownloadURL();
	}

	public URL getUploadURL()throws Exception {
		return _peer.getUploadURL();
	}

	public void runScript(URL url) {
		_peer.runScript(url);
	}

	public void shutdown(PrintWriter out, int waittime, int exitCode)throws Exception {
		_peer.shutdown(out, waittime, exitCode);
	}

	public void send(String response) {
		_peer.send(response);
	}

	public boolean isWLANMode() {
		return _peer.isWLANMode();
	}

	public void setWLANMode(boolean b) throws MalformedURLException {
		_peer.setWLANMode(b);
	}

	public void setPublicMode() throws Exception {
		_peer.setPublicMode();
	}

	public void setPrivateMode() throws Exception {
		_peer.setPrivateMode();
	}
}
