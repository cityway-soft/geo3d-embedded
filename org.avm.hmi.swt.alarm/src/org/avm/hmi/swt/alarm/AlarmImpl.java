package org.avm.hmi.swt.alarm;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.util.measurement.State;

public class AlarmImpl implements AlarmIhm, ManageableService, ProducerService,
		ConsumerService, AlarmProvider {

	private AlarmIhmImpl _ihmAlarmExploitation;
	private AlarmIhmImpl _ihmAlarmDefaut;

	private Desktop _desktop;

	private Logger _log;

	private Display _display;

	private ProducerManager _producer;

	private AlarmService _alarmService;

	// private boolean authenticated=false;

	public AlarmImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.info("Create Alarm SWT");
	}

	public void setBase(Desktop base) {
		_desktop = base;
		if (_desktop != null) {
			_display = _desktop.getDisplay();
		}
	}

	public void start() {
		open();
	}

	public void stop() {
		close();
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
		if (_ihmAlarmExploitation != null) {
			_ihmAlarmExploitation.setProducer(_producer);
		}
	}

	public List getAlarm() {
		if (_ihmAlarmExploitation != null) {
			Iterator iter = _ihmAlarmExploitation.getButtons().iterator();
			LinkedList list = null;
			while (iter.hasNext()) {
				Integer alarmId = (Integer) iter.next();
				Alarm alarm = _alarmService.getAlarm(alarmId);
				if (alarm.isStatus()) {
					if (list == null) {
						list = new LinkedList();
					}
					list.add(alarm);
				}
			}
			return list;
		}
		return null;
	}

	public String getProducerPID() {
		return AlarmIhm.class.getName();
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			_log.debug("receive state : " + state.getName());
			// if (state.getName().equals(UserSessionService.class.getName())) {
			// if (state.getValue() == UserSessionService.AUTHENTICATED) {
			// authenticated = true;
			// open();
			// } else {
			// authenticated = false;
			// close();
			// }
			// } else

			if (state.getName().equals(AlarmService.class.getName())) {
				Collection alarms = _alarmService.getList();
				Iterator iter = alarms.iterator();
				while (iter.hasNext()) {
					Alarm alarm = (Alarm) iter.next();
					update(alarm);
				}
			}
		}
	}

	private void update(final Alarm alarm) {
		_display.asyncExec(new Runnable() {
			public void run() {
				_log.debug("update alarm " + alarm);
				if (alarm.isVisible()) {
					if (alarm.getType() == Alarm.ALARM_EXPLOITATION) {
						if (_ihmAlarmExploitation == null) {
							addExploitationAlarmTab();
						}
						_ihmAlarmExploitation.update(alarm);
					} else {
						if (_ihmAlarmDefaut == null) {
							addDefautAlarmTab();
						}
						_ihmAlarmDefaut.update(alarm);
					}
				}

			}
		});

	}

	private void addExploitationAlarmTab() {
		_log.debug("Adding Exploitation Alarm...");
		if (_ihmAlarmExploitation == null || _ihmAlarmExploitation.isDisposed()) {
			_log.info("Creating Exploitation Alarm...");

			_ihmAlarmExploitation = new AlarmIhmImpl(_desktop.getMainPanel(),
					SWT.NONE, EXPLOITATION_ALARM_TAB_NAME);
			_ihmAlarmExploitation.setProducer(_producer);
			_ihmAlarmExploitation.setDesktop(_desktop);

			if (_desktop != null) {
				TabItem item[] = ((TabFolder) (_desktop.getMainPanel()))
						.getItems();
				int index = 0;
				if (item != null) {
					index = Math.min(item.length, 1);
				}
				_desktop.addTabItem(EXPLOITATION_ALARM_TAB_NAME,
						_ihmAlarmExploitation, index);
//				_desktop.activateItem(EXPLOITATION_ALARM_TAB_NAME);
				_log.info("Added Exploitation Alarm to desktop...");

			}
		}
	}

	private void removeExploitationAlarmTab() {
		if (_ihmAlarmExploitation != null
				&& _ihmAlarmExploitation.isDisposed() == false) {
			_ihmAlarmExploitation.dispose();
			_ihmAlarmExploitation = null;
			_desktop.removeTabItem(EXPLOITATION_ALARM_TAB_NAME);
		}
	}

	private void addDefautAlarmTab() {
		_log.debug("Adding Defaut Alarm...");

		if (_ihmAlarmDefaut == null || _ihmAlarmDefaut.isDisposed()) {
			_log.info("Creating Defaut Alarm...");

			_ihmAlarmDefaut = new AlarmIhmImpl(_desktop.getMainPanel(),
					SWT.NONE, DEFAUT_ALARM_TAB_NAME);
			_ihmAlarmDefaut.setProducer(_producer);
			_ihmAlarmDefaut.setDesktop(_desktop);

			if (_desktop != null) {
				TabItem item[] = ((TabFolder) (_desktop.getMainPanel()))
						.getItems();
				int index = 0;
				if (item != null) {
					index = Math.min(item.length, 2);
				}
				_desktop.addTabItem(DEFAUT_ALARM_TAB_NAME, _ihmAlarmDefaut,
						index);
				_log.info("Added Default Alarm to desktop...");

			}
		}
	}

	private void removeDefautAlarmTab() {
		if (_ihmAlarmDefaut != null && _ihmAlarmDefaut.isDisposed() == false) {
			_ihmAlarmDefaut.dispose();
			_ihmAlarmDefaut = null;
			_desktop.removeTabItem(DEFAUT_ALARM_TAB_NAME);
		}
	}

	public void open() {
		// if (authenticated) {
		_display.asyncExec(new Runnable() {
			public void run() {
				Collection alarms = _alarmService.getList();
				Iterator iter = alarms.iterator();
				while (iter.hasNext()) {
					Alarm alarm = (Alarm) iter.next();
					update(alarm);
				}

			}
		});
		// }
	}

	public void close() {
		_display.syncExec(new Runnable() {
			public void run() {
				removeExploitationAlarmTab();
				removeDefautAlarmTab();
			}
		});
	}

	public void setAlarmService(AlarmService service) {
		_alarmService = service;

	}

	public void unsetAlarmService(AlarmService service) {
		_alarmService = service;
	}

}