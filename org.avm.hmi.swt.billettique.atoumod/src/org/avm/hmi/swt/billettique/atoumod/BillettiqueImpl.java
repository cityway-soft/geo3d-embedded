package org.avm.hmi.swt.billettique.atoumod;

import org.apache.log4j.Logger;
import org.avm.business.billettique.atoumod.Billettique;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class BillettiqueImpl implements BillettiqueIhm, ManageableService,
		ProducerService, ConsumerService {

	private BillettiqueIhmImpl _billettiqueIhm;

	private Desktop _desktop;

	private Logger _log;

	private Display _display;

	private ProducerManager _producer;

	private UserSessionService _session;

	private ComponentContext _context;

	private Billettique _billettique;

	private boolean authenticated;

	public BillettiqueImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.info("Create Billettique IHM");
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
		if (_desktop != null) {
			_display = _desktop.getDisplay();
		}
	}

	public void start() {

	}

	public void stop() {
		close();
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
		if (_billettiqueIhm != null) {
			_billettiqueIhm.setProducer(_producer);
		}
	}

	public String getProducerPID() {
		return BillettiqueIhm.class.getName();
	}

	public void notify(Object o) {
		if (_log.isDebugEnabled()) {
			_log.debug("Receive :" + o);
		}
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(UserSessionService.class.getName())) {
				if (state.getValue() == UserSessionService.AUTHENTICATED) {
					authenticated = true;
					open();
				} else {
					authenticated = false;
					close();
				}
			} else if (state.getName().equals(Billettique.class.getName())) {
				setConnected(state.getValue() == 1);
			}
		}
	}

	private void setConnected(final boolean b) {
		_display.asyncExec(new Runnable() {
			public void run() {
				if (_billettiqueIhm != null && ! _billettiqueIhm.isDisposed()) {

					_billettiqueIhm.setConnected(b);
				}
			}
		});
	}

	public void open() {
		if (authenticated) {
			_display.asyncExec(new Runnable() {
				public void run() {
					if (_billettiqueIhm == null || _billettiqueIhm.isDisposed()) {

						_billettiqueIhm = new BillettiqueIhmImpl(_desktop
								.getMiddlePanel(), SWT.NONE, TAB_NAME);
						_billettiqueIhm.setProducer(_producer);
						_billettiqueIhm.setContext(_context);
						_billettiqueIhm.setDesktop(_desktop);
						_billettiqueIhm.setBillettique(_billettique);

						_log.debug("Adding Billettique  to desktop...");
						if (_desktop != null) {
							_desktop.addTabItem(TAB_NAME, _billettiqueIhm);
							_log.debug("Added Code Girouette  to desktop...");

						}
					}
				}
			});
		}
	}

	public void close() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_billettiqueIhm != null
						&& _billettiqueIhm.isDisposed() == false) {
					_billettiqueIhm.dispose();
					_billettiqueIhm = null;
					_desktop.removeTabItem(TAB_NAME);
				}
			}
		});
	}

	public void setBillettique(Billettique girouette) {
		_billettique = girouette;
	}

	public void unsetGirouette(Billettique girouette) {
		_billettique = girouette;
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}
}
