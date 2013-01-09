package org.avm.business.recorder.bundle;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.recorder.Recorder;
import org.avm.business.recorder.RecorderImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.database.Database;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.variable.Variable;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Recorder {

	public static final String PID = Recorder.class.getName();

	private ConfigurationAdmin _cm;

	private RecorderImpl _peer;

	private ConfigImpl _config;

	private ConsumerImpl _consumer;

	private CommandGroupImpl _commands;

	private AbstractProducer _producer;

	public Activator() {
		_peer = new RecorderImpl();
	}

	protected void start(ComponentContext context) {
		try {
			initializeConfiguration();
			initializeConsumer();
			initializeProducer();
			initializeCommandGroup();
			initializeAVM();
			initializeJDB();
			// initializeVAR();
			initializeDatabase();
			startService();
		} catch (Exception e) {
			_log.error("Activating error :", e);
		}
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeDatabase();
		// disposeVAR();
		disposeJDB();
		disposeAVM();
		disposeCommandGroup();
		disposeProducer();
		disposeConsumer();
		disposeConfiguration();
	}

	public void setVariable(Variable var) {
		_log.debug("setVariable(" + var + ")");
		_peer.setVariable(var);
	}

	public void unsetVariable(Variable var) {
		_log.debug("unsetVariable(" + var + ")");
		_peer.setVariable(null);
	}

	// avmcore
	private void initializeAVM() {
		_log.debug("initializeAVM");
		Avm avm = (Avm) _context.locateService("avm");
		if (_peer instanceof AvmInjector)
			((AvmInjector) _peer).setAvm(avm);
	}

	private void disposeAVM() {
		if (_peer instanceof AvmInjector)
			((AvmInjector) _peer).setAvm(null);
	}

	private void initializeJDB() {
		_log.debug("initializeJDB");
		JDB jdb = (JDB) _context.locateService("jdb");
		if (_peer instanceof JDBInjector)
			((JDBInjector) _peer).setJdb(jdb);
	}

	private void disposeJDB() {
		if (_peer instanceof JDBInjector)
			((JDBInjector) _peer).setJdb(null);
	}

	// database
	private void initializeDatabase() {
		Database database = (Database) _context.locateService("database");
		_peer.setDatabase(database);
	}

	private void disposeDatabase() {
		_peer.setDatabase(null);
	}

	// config
	private void initializeConfiguration() {
		_log.debug("initializeConfiguration");
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
		// if (_peer instanceof ConfigurableService) {
		// ((ConfigurableService) _peer).configure(null);
		// }
	}

	// consumer
	private void initializeConsumer() {
		_log.debug("initializeConsumer");
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
		_log.debug("initializeCommandGroup");
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
		_log.debug("startService");
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public void setAvm(Avm avm) {
		_peer.setAvm(avm);
	}

	public void sunetAvm(Avm avm) {
		_peer.setAvm(null);
	}

	public void syncJdbLight() {
		_peer.syncJdbLight();
	}

}
