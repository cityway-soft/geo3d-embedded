package org.avm.hmi.swt.ecall;

import org.apache.log4j.Logger;
import org.avm.business.ecall.EcallService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.util.measurement.State;

public class EcallImpl implements Ecall, ConsumerService, ManageableService {
	protected static final String NAME = "EcallService";

	private EcallIhm _ecallIhm;

	Desktop _base;

	private EcallImpl _instance;

	private Logger _log;

	private Display _display;

	private EcallService _ecallService;

	public EcallImpl() {
		_instance = this;
		_log = Logger.getInstance(this.getClass());
	}

	public void setBase(Desktop base) {
		_base = base;
		if (base != null) {
			_display = _base.getDisplay();
		}
	}

	public void start() {
		_display.asyncExec(new Runnable() {
			public void run() {
				_ecallIhm = new EcallIhm(_base.getRightPanel(), SWT.BORDER);
				_ecallIhm.setEcallService(_ecallService);
			}
		});
	}

	public void stop() {
		_display.syncExec(new Runnable() {
			public void run() {
				_ecallIhm.dispose();
			}
		});
	}

	public void notify(final Object o) {
		_log.debug("Event : " + o);
		if (o instanceof State) {
			_display.syncExec(new Runnable() {
				public void run() {
					_ecallIhm.stateChange((State) o);
				}
			});
		}
	}

	public void setEcallService(final EcallService ecallService) {
		_ecallService = ecallService;
		if (_ecallIhm != null) {
			_display.syncExec(new Runnable() {
				public void run() {
					_ecallIhm.setEcallService(ecallService);
				}
			});
		}
	}

	public void etatAttentePriseEnCharge() {
		_display.syncExec(new Runnable() {
			public void run() {
				_ecallIhm.etatAttentePriseEnCharge();
			}
		});
	}

	public void etatEcouteDiscrete() {
		_display.syncExec(new Runnable() {
			public void run() {
				_ecallIhm.etatEcouteDiscrete();
			}
		});
	}

	public void etatInitial() {
		_display.syncExec(new Runnable() {
			public void run() {
				_ecallIhm.etatInitial();
			}
		});
	}

}