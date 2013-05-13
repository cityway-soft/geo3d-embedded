package org.avm.hmi.swt.girouette;

import org.apache.log4j.Logger;
import org.avm.business.girouette.Girouette;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class GirouetteImpl implements GirouetteIhm, ManageableService,
		ProducerService, ConsumerService {

	private GirouetteIhmImpl _codeGirouetteIhm;

	private Desktop _desktop;

	private Logger _log;

	private Display _display;

	private ProducerManager _producer;

	private UserSessionService _session;

	private ComponentContext _context;

	private Girouette _girouette;

	private boolean authenticated;

	public GirouetteImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.info("Create Girouette IHM");
	}

	public void validation(Object obj, Object data) {
		if (data == null)
			return;
		if (obj instanceof CodeSelection) {
			String code = data.toString();
			try {
				_girouette.destination(Integer.parseInt(code));
			} catch (Throwable e) {
			}
		}
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
		if (_codeGirouetteIhm != null) {
			_codeGirouetteIhm.setProducer(_producer);
		}
	}

	public String getProducerPID() {
		return GirouetteIhm.class.getName();
	}

	public void notify(Object o) {
		if (_log.isDebugEnabled()) {
			_log.debug("Receive :" + o);
		}
		if (o instanceof State) {
			State state = (State) o;
			if (state.getValue() == UserSessionService.AUTHENTICATED) {
				authenticated = true;
				open();
			} else {
				authenticated = false;
				close();
			}
		}
	}

	public void open() {
		if (authenticated) {
			_display.asyncExec(new Runnable() {
				public void run() {
					if (_codeGirouetteIhm == null
							|| _codeGirouetteIhm.isDisposed()) {
					
						_codeGirouetteIhm = new GirouetteIhmImpl(_desktop
								.getMainPanel(), SWT.NONE, TAB_NAME);
						_codeGirouetteIhm.setProducer(_producer);
						_codeGirouetteIhm.setContext(_context);
						_codeGirouetteIhm.setDesktop(_desktop);

						_codeGirouetteIhm.getCodeSelection()
								.setSelectionListener(new ChoiceListener() {

									public void validation(Object obj,
											Object data) {
										GirouetteImpl.this
												.validation(obj, data);
									}
								});
						_log.debug("Adding Code Girouette  to desktop...");
						if (_desktop != null) {
							_desktop.addTabItem(TAB_NAME, _codeGirouetteIhm);
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
				if (_codeGirouetteIhm != null
						&& _codeGirouetteIhm.isDisposed() == false) {
					_codeGirouetteIhm.dispose();
					_codeGirouetteIhm = null;
					_desktop.removeTabItem(TAB_NAME);
				}
			}
		});
	}

	public void setGirouette(Girouette girouette) {
		_girouette = girouette;
	}

	public void unsetGirouette(Girouette girouette) {
		_girouette = girouette;
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}
}
