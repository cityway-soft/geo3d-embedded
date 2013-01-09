package org.angolight.hmi.swt.leds;

import java.io.IOException;

import org.angolight.device.leds.Leds;
import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.util.measurement.State;

public class LedsImpl implements Leds, ConfigurableService, ProducerService,
		ManageableService, LedsListener {

	private Logger _log;
	private ProducerManager _producer;
	private LedsConfig _config;
	private LedsDevice _peer;
	private Desktop _desktop;

	private Display _display;

	private LedsImpl _instance = this;
	private ShellLeds _shellLeds = null;

	private int _previous = -1;

	public LedsImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
		if (desktop != null) {
			_display = desktop.getDisplay();
		}
	}

	public void configure(Config config) {
		_config = (LedsConfig) config;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void start() {
		try {
			initialize();
		} catch (IOException e) {
			_log.error(e);
		}
	}

	public void stop() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_shellLeds != null && !_shellLeds.getShell().isDisposed()){
					_shellLeds.getShell().dispose();
					_shellLeds=null;
				}
				if (_peer != null) {
					_peer.close();
				}
			}
		});
	}

	private void initialize() throws IOException {
		_display.asyncExec(new Runnable() {
			public void run() {
				try {
					Composite component;
					if (_config.isInside() == false) {
						_shellLeds = new ShellLeds(_display, SWT.NONE);
						 component = _shellLeds.getShell();
					} else {
						component = _desktop.getRightPanel();
					}

					_peer = new LedsDeviceImpl(_instance, component, SWT.NONE);
					component.layout();
					_peer.open();

				} catch (Throwable t) {
					t.printStackTrace();
				}

			}
		});
	}

	public void setVisible(final boolean visible) {
		_display.syncExec(new Runnable() {
			public void run() {
				_peer.setVisible(visible);
			}
		});

	}

	public int M(short states, byte period, boolean check) {
		_log.debug("M." + Integer.toHexString(states) + "." + period);
		if (_previous != states) {
			_log.debug("setvisible=>true");
			_previous = states;
			setVisible(true);
		}
		return _peer.setState(states, period);
	}

	public int X(byte address, byte cycle, byte period, boolean check) {
		_log.debug("X===> address=" + address + " ,period=" + period
				+ ", cycle=" + cycle);
		if (_previous != address) {
			_log.debug("setvisible=>true");
			_previous = address;
			setVisible(true);
		}
		return _peer.executeSequence(address, cycle, period);
	}

	public int I(byte address, short states, byte period, boolean check) {
		_log.debug("I===> address=" + address + " ,state=" + states
				+ " ,period=" + period);
		return _peer.addState(address, states, period);
	}

	public int J(byte address, short states, byte period, boolean check) {
		_log.debug("J===> address=" + address + " ,state=" + states
				+ " ,period=" + period);
		return _peer.addStop(address, states, period);
	}

	public int L(byte brightness, boolean check) {
		_log.debug("L===> brightness=" + brightness);
		_peer.setBrightness(brightness);
		return 0;
	}

	public int R(byte address, boolean check) {
		_log.debug("R===> address=" + address);
		return 0;
	}

	public int S(boolean check) {
		_log.debug("S===>!");
		_peer.stopSequence();
		return 0;
	}

	public int T(boolean check) {
		_log.debug("T===>!");
		_peer.haltSequence();
		return 0;
	}

	public int V(boolean check) {
		return 1;
	}

	public void sequenceStopped() {
		_producer.publish(new State(SEQUENCE_STOPPED,
				org.angolight.device.leds.Leds.class.getName()));
	}

}
