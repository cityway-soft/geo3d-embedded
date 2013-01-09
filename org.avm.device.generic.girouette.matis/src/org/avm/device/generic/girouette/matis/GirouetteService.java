package org.avm.device.generic.girouette.matis;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.avm.device.girouette.Girouette;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class GirouetteService extends AbstractDriver implements Girouette {

	private static final long WAIT = 15000;

	private transient boolean _started;

	private Port _port;

	private long _last;

	private Object _lock = new Object();

	private Timer _timer;

	private TimerTask _task;

	/*
	 * exemple configuration setmanufacturer matis.com setdescription Controleur
	 * de girouette Matis setmodel org.avm.device.girouette.matis setname
	 * org.avm.device.girouette.matis setserial
	 * 4df3687a-9b67-46c5-b83f-b581c98feff2 setcategory
	 * org.avm.device.girouette.Girouette setparameters url
	 * "comm:1;baudrate=1200;stopbits=1;parity=even;bitsperchar=8;blocking=off"
	 */

	public GirouetteService(ComponentContext context, ServiceReference device) {
		super(context, device);
	}

	protected void start(DeviceConfig config) {
		String url = config.getParamerter("url");
		_port = new Port(url);
		_started = true;
	}

	protected void stop() {
		_started = false;
		_port.close();
	}

	public void destination(String code) {

		if (!_started) {
			_log.error("[DSU] driver not started");
			return;
		}

		_log.info("Call sendCodeGirouette :" + code);

		byte[] buffer = ProtocolHelper.generate(code);
		String debug = new String(buffer);
		_log.info("Frame : (" + debug.length() + ")" + debug.trim());
		try {
			send(buffer);
		} catch (IOException e) {
			// retry one
			try {
				send(buffer);
			} catch (IOException e1) {
				_log.error(e.getMessage(), e);
			}
		}
	}

	private void send(byte[] buffer) throws IOException {
		if (_timer != null) {
			_timer.cancel();
			_timer = null;
		}
		_timer = new Timer();
		_task = new GirouetteTask(buffer);

		//TODO DLA : refaire le test.
		long t = System.currentTimeMillis();
		long delta = (WAIT-(t - _last));
		delta = (delta>0)?delta:0;
		_log.debug("wait " + delta + " ms.");
		_timer.schedule(_task, delta);
	}

	public class GirouetteTask extends TimerTask {
		byte[] buffer;

		public GirouetteTask(byte[] buffer) {
			this.buffer = buffer;
		}

		public void run() {
			try {
				_log.debug("sending....");
				OutputStream out = _port.getOutputStream();
				out.write(buffer);
				_last = System.currentTimeMillis();
			} catch (IOException e) {
				_port.close();
			}
		}

	}
}
