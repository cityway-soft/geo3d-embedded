package org.angolight.indicator.bundle;

import java.util.Date;
import java.util.Map;

import org.angolight.indicator.Indicator;
import org.angolight.indicator.impl.IndicatorDeployer;
import org.angolight.indicator.impl.IndicatorService;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Indicator,
		JDBInjector {
	private ConfigurationAdmin _cm;

	private IndicatorService _peer;

	private ConsumerImpl _consumer;

	private ProducerImpl _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private IndicatorDeployer _deployer;

	private static Activator _plugin;

	private JDBProxy _jdb;

	public Activator() {
		_plugin = this;
		_peer = new IndicatorService();
		_jdb = new JDBProxy();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		initializeJDB();
		initializeDeployer();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeDeployer();
		disposeJDB();
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

	// consumer
	private void initializeConsumer() {

		if (_peer instanceof ConsumerService) {
			_consumer = new ConsumerImpl(_context, _peer, _config);
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
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	// jdb
	private void initializeJDB() {
		if (_peer instanceof JDBInjector)
			((JDBInjector) _peer).setJdb(_jdb);
	}

	private void disposeJDB() {
		if (_peer instanceof JDBInjector)
			((JDBInjector) _peer).setJdb(null);
	}

	// deployer
	private void initializeDeployer() {
		try {
			_deployer = new IndicatorDeployer(_context.getBundleContext(),
					_config);
			_deployer.start();
		} catch (Exception e) {
			_log.error(e.getMessage());
		}
	}

	private void disposeDeployer() {
		if (_deployer != null)
			_deployer.stop();
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

	public Map evaluate() {
		return _peer.evaluate();
	}

	public Map merge(Map measures) {
		return _peer.merge(measures);
	}

	public void reset() {
		_peer.reset();
	}

	public void setJdb(JDB jdb) {
		_jdb.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_jdb.unsetJdb(jdb);
	}

	class JDBProxy implements JDB, JDBInjector {

		protected JDB _peer;

		public void setJdb(JDB jdb) {
			_peer = jdb;
		}

		public void unsetJdb(JDB jdb) {
			_peer = null;
		}

		public JDBProxy() {
		}

		public int getCheckPeriod() {
			if (_peer != null)
				return _peer.getCheckPeriod();
			return 0;
		}

		public String getRootPath() {
			if (_peer != null)
				return _peer.getRootPath();
			return null;
		}

		public String getScheduledFilename(Date date) {
			if (_peer != null)
				return _peer.getScheduledFilename(date);
			return null;
		}

		public void journalize(String category, String message) {
			if (_peer != null) {
				_peer.journalize(category, message);
			}
		}

		public void sync() {
			if (_peer != null) {
				_peer.sync();
			}
		}

	}
}
