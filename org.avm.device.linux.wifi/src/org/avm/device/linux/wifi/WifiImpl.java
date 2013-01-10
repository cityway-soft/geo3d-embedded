package org.avm.device.linux.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.wifi.Wifi;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.Scheduler;
import org.osgi.util.measurement.State;

public class WifiImpl implements Wifi, ConfigurableService, ProducerService,
		ManageableService {

	private static final String GATEWAY = "ftpserver.avm.org";

	private WifiConfig _config;

	private ProducerManager _producer;

	private Logger _log = Logger.getInstance(this.getClass());

	private boolean _started = false;
	
	private Scheduler _scheduler ;
	
	private Object _taskid;

	public WifiImpl() {

	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (WifiConfig) config;
	}

	public void start() {
		_scheduler = new Scheduler();
	}

	public void stop() {
		_scheduler = null;
	}

	public void connect() {
		boolean result = false;

		final MessageFormat args = new MessageFormat(
				"ifup-wlan -d {0} -m {1} -e {2} - k {3} -r {4} -c {5} -f{6}");

		Object[] objects = { _config.getDevice(), _config.getMode(),
				_config.getEssid(), _config.getKey(), _config.getRate(),
				_config.getChannel(), _config.getFreq() };
		String[] array = { "sh", "-c", args.format(objects) };
		exec(array);
		
		_scheduler.execute(new StateNoticationTask(false));


	}

	public void disconnect() {
		final MessageFormat args = new MessageFormat("ifdown-wlan -d {0}");

		Object[] objects = { _config.getDevice() };
		String[] array = { "sh", "-c", args.format(objects) };
		exec(array);

		_scheduler.execute(new StateNoticationTask(true));
	}

	public boolean isConnected() {

		final String args = "iwgetid -ar " + _config.getDevice();

		boolean result = false;

		String[] array = { "sh", "-c", args };
		Process process;
		List list = new ArrayList();
		try {
			process = Runtime.getRuntime().exec(array);
			process.waitFor();
			result = (process.exitValue() == 0);
			if (result) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				String line = reader.readLine();
				if (line != null && !line.equals("00:00:00:00:00:00")) {
					result = true;
				} else {
					result = false;
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return result;
	}

	public Properties getProperties() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	private boolean exec(String[] args) {
		boolean result = false;
		try {
			_log.info("[DSU] exec " + args);
			Process process = Runtime.getRuntime().exec(args);
			process.waitFor();
			result = (process.exitValue() == 0);
		} catch (Exception e) {
			_log.error(e);
		}
		return result;
	}

	private void sleep(long value) {
		try {
			Thread.sleep(value);
		} catch (InterruptedException e) {
		}
	}

	class StateNoticationTask implements Runnable {

		private static final int TIMEOUT = 30;

		private boolean _reverse;

		public StateNoticationTask(boolean reverse) {
			_reverse = reverse;
		}

		public void run() {
			int count = TIMEOUT;

			while ((_reverse ^ !isConnected()) && count-- > 0) {
				sleep(1000);
			}
			if (isConnected()) {

				_producer.publish(new State(1, Wifi.class.getPackage()
						.getName()
						+ ".connected"));
			} else {
				_producer.publish(new State(0, Wifi.class.getPackage()
						.getName()
						+ ".disconnected"));
			}

		}
	}
}