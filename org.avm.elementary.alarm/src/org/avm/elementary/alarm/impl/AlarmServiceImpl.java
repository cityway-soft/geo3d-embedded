package org.avm.elementary.alarm.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.Alerte;
import org.avm.business.protocol.phoebus.Anomalie;
import org.avm.business.protocol.phoebus.ClotureAlerte;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.Options;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.osgi.util.measurement.State;

public class AlarmServiceImpl implements AlarmService, ConfigurableService,
		MessengerInjector, ConsumerService, ProducerService, JDBInjector {

	private static final String JDB_TAG = "ALARM";

	private final Logger _log = Logger.getInstance(this.getClass());

	private Messenger _messenger;

	private AlarmServiceConfig _config;

	private final BitSet _alarms;

	private Dictionary _map;

	private HashMap _hashAlarmByKey;

	private boolean _isAlarm;

	private HashMap _hashAlarmById;

	private ProducerManager _producer;

	private JDB jdb;

	public AlarmServiceImpl() {

		this._alarms = new BitSet();
	}

	public void configure(final Config config) {

		this._hashAlarmById = new HashMap();
		this._hashAlarmByKey = new HashMap();
		this._config = (AlarmServiceConfig) config;
		if (this._config != null) {
			this._map = new Hashtable();

			for (final Enumeration iter = this._config.get().elements(); iter
					.hasMoreElements();) {
				final Properties p = (Properties) iter.nextElement();
				Integer id = new Integer(p.getProperty(Alarm.INDEX));
				this._map.put(id, p);

				final boolean status = false;
				final String name = p.getProperty(Alarm.NAME);
				final Date date = new Date();
				final String key = p.getProperty(Alarm.KEY);
				final int type = Integer.parseInt(p.getProperty(Alarm.TYPE));
				final int index = Integer.parseInt(p.getProperty(Alarm.INDEX));
				final String temp = p.getProperty(Alarm.READONLY);
				final boolean readonly = (temp == null) ? false : temp
						.equals("true");
				final int order = Integer.parseInt(p.getProperty(Alarm.ORDER));
				final Alarm alarm = new Alarm(new Integer(index), order,
						status, name, date, key, type, readonly);
				this._hashAlarmByKey.put(key, alarm);
				this._hashAlarmById.put(alarm.getIndex(), alarm);
			}
		}
	}

	public Alarm getAlarmByKey(String name) {
		return (Alarm) this._hashAlarmByKey.get(name);
	}

	public Alarm getAlarm(final Integer id) {

		return (Alarm) this._hashAlarmById.get(id);
	}

	public boolean isAlarm() {
		return this._isAlarm;
	}

	private void checkAlarm() {
		boolean isAlarm = false;
		int i = 0;
		while (i < _alarms.size() && !isAlarm) {
			isAlarm = _alarms.get(i);
			i++;
		}
		this._isAlarm = isAlarm;
	}

	public Collection getList() {
		List copie = new ArrayList();
		Iterator iter = _hashAlarmById.keySet().iterator();
		while (iter.hasNext()) {
			Integer key = (Integer) iter.next();
			Alarm alarm = (Alarm) _hashAlarmById.get(key);
			Alarm alarmCopie = new Alarm(alarm.getIndex(), alarm.getOrder(),
					alarm.isStatus(), alarm.getName(), alarm.getDate(),
					alarm.getKey(), alarm.getType(), alarm.isReadOnly());
			copie.add(alarmCopie);
		}

		Collections.sort(copie, new OrderComparator());

		return copie;
	}

	public void notify(final Object o) {

		if (o instanceof Alarm) {
			boolean changed = false;
			Alarm alarm = (Alarm) o;
			Alarm previous = (Alarm) this._hashAlarmById.get(alarm.getIndex());
			changed = (previous.isStatus() != alarm.isStatus());
			try {
				final Properties p = (Properties) this._map.get(alarm
						.getIndex());
				if (p != null) {
					final int index = Integer.parseInt(p
							.getProperty(Alarm.INDEX));
					final boolean notifyup = Boolean.valueOf(
							p.getProperty(Alarm.NOTIFY_UP)).booleanValue();
					final boolean notifydown = Boolean.valueOf(
							p.getProperty(Alarm.NOTIFY_DOWN)).booleanValue();
					final boolean acknowledge = Boolean.valueOf(
							p.getProperty(Alarm.ACKNOWLEDGE)).booleanValue();
					previous.setStatus(alarm.isStatus());
					if (alarm.isStatus()) {
						this._alarms.set(index);
						_isAlarm = true;
					} else {
						this._alarms.clear(index);
						checkAlarm();
					}
					if (changed) {
						journalize(previous.getKey() + ";" + alarm.isStatus());
						if ((!alarm.isStatus() && notifydown)
								|| (alarm.isStatus() && notifyup)) {
							final Alerte alerte = this.createAlerte(
									(BitSet) this._alarms.clone(), acknowledge);
							this._log.debug("sending: " + alerte);
							this.sendAlerte(alerte);
						}
					}
					this._producer.publish(new State(alarm.isStatus() ? 1 : 0,
							AlarmService.class.getName()));

				}
			} catch (final NoSuchElementException e) {
			}
		} else if (o instanceof ClotureAlerte) {
			final ClotureAlerte cloture = (ClotureAlerte) o;
			this.update(cloture);
			this._producer.publish(new State(0, AlarmService.class.getName()));
		}
	}

	public void setMessenger(final Messenger messenger) {

		this._messenger = messenger;
	}

	public void setProducer(final ProducerManager producer) {

		this._producer = producer;
	}

	public void unsetMessenger(final Messenger messenger) {

		this._messenger = null;
	}

	public void update(final ClotureAlerte cloture) {

		final Anomalie anomalie = cloture.getEntete().getAnomalie();
		Alarm alarm;
		if (anomalie.getAlarme_0() != 0) {
			alarm = this.getAlarm(new Integer(0));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_1() != 0) {
			alarm = this.getAlarm(new Integer(1));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_2() != 0) {
			alarm = this.getAlarm(new Integer(2));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_3() != 0) {
			alarm = this.getAlarm(new Integer(3));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_4() != 0) {
			alarm = this.getAlarm(new Integer(4));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_5() != 0) {
			alarm = this.getAlarm(new Integer(5));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_6() != 0) {
			alarm = this.getAlarm(new Integer(6));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_7() != 0) {
			alarm = this.getAlarm(new Integer(7));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_8() != 0) {
			alarm = this.getAlarm(new Integer(8));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_9() != 0) {
			alarm = this.getAlarm(new Integer(9));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_10() != 0) {
			alarm = this.getAlarm(new Integer(10));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_11() != 0) {
			alarm = this.getAlarm(new Integer(11));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_12() != 0) {
			alarm = this.getAlarm(new Integer(12));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_13() != 0) {
			alarm = this.getAlarm(new Integer(13));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_14() != 0) {
			alarm = this.getAlarm(new Integer(14));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_15() != 0) {
			alarm = this.getAlarm(new Integer(15));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_16() != 0) {
			alarm = this.getAlarm(new Integer(16));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_17() != 0) {
			alarm = this.getAlarm(new Integer(17));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_18() != 0) {
			alarm = this.getAlarm(new Integer(18));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_19() != 0) {
			alarm = this.getAlarm(new Integer(19));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_20() != 0) {
			alarm = this.getAlarm(new Integer(20));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_21() != 0) {
			alarm = this.getAlarm(new Integer(21));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_22() != 0) {
			alarm = this.getAlarm(new Integer(22));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_23() != 0) {
			alarm = this.getAlarm(new Integer(23));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_24() != 0) {
			alarm = this.getAlarm(new Integer(24));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_25() != 0) {
			alarm = this.getAlarm(new Integer(25));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_26() != 0) {
			alarm = this.getAlarm(new Integer(26));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_27() != 0) {
			alarm = this.getAlarm(new Integer(27));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_28() != 0) {
			alarm = this.getAlarm(new Integer(28));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_29() != 0) {
			alarm = this.getAlarm(new Integer(29));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_30() != 0) {
			alarm = this.getAlarm(new Integer(30));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
		if (anomalie.getAlarme_31() != 0) {
			alarm = this.getAlarm(new Integer(31));
			if (alarm != null) {
				alarm.setStatus(false);
			}
		}
	}

	private Alerte createAlerte(final BitSet alarms, final boolean acquittement) {

		final Alerte alerte = new Alerte();
		final Entete entete = alerte.getEntete();
		// entete.setDate(new Horodate());
		entete.setAnomalie(new Anomalie());
		entete.setOptions(new Options());
		entete.getOptions().setAcquittement(acquittement ? 1 : 0);
		final Anomalie anomalie = entete.getAnomalie();
		anomalie.setAlarme_0(alarms.get(0) ? 1 : 0);
		anomalie.setAlarme_1(alarms.get(1) ? 1 : 0);
		anomalie.setAlarme_2(alarms.get(2) ? 1 : 0);
		anomalie.setAlarme_3(alarms.get(3) ? 1 : 0);
		anomalie.setAlarme_4(alarms.get(4) ? 1 : 0);
		anomalie.setAlarme_5(alarms.get(5) ? 1 : 0);
		anomalie.setAlarme_6(alarms.get(6) ? 1 : 0);
		anomalie.setAlarme_7(alarms.get(7) ? 1 : 0);
		anomalie.setAlarme_8(alarms.get(8) ? 1 : 0);
		anomalie.setAlarme_9(alarms.get(9) ? 1 : 0);
		anomalie.setAlarme_10(alarms.get(10) ? 1 : 0);
		anomalie.setAlarme_11(alarms.get(11) ? 1 : 0);
		anomalie.setAlarme_12(alarms.get(12) ? 1 : 0);
		anomalie.setAlarme_13(alarms.get(13) ? 1 : 0);
		anomalie.setAlarme_14(alarms.get(14) ? 1 : 0);
		anomalie.setAlarme_15(alarms.get(15) ? 1 : 0);
		anomalie.setAlarme_16(alarms.get(16) ? 1 : 0);
		anomalie.setAlarme_17(alarms.get(17) ? 1 : 0);
		anomalie.setAlarme_18(alarms.get(18) ? 1 : 0);
		anomalie.setAlarme_19(alarms.get(19) ? 1 : 0);
		anomalie.setAlarme_20(alarms.get(20) ? 1 : 0);
		anomalie.setAlarme_21(alarms.get(21) ? 1 : 0);
		anomalie.setAlarme_22(alarms.get(22) ? 1 : 0);
		anomalie.setAlarme_23(alarms.get(23) ? 1 : 0);
		anomalie.setAlarme_24(alarms.get(24) ? 1 : 0);
		anomalie.setAlarme_25(alarms.get(25) ? 1 : 0);
		anomalie.setAlarme_26(alarms.get(26) ? 1 : 0);
		anomalie.setAlarme_27(alarms.get(27) ? 1 : 0);
		anomalie.setAlarme_28(alarms.get(28) ? 1 : 0);
		anomalie.setAlarme_29(alarms.get(29) ? 1 : 0);
		anomalie.setAlarme_30(alarms.get(30) ? 1 : 0);
		anomalie.setAlarme_31(alarms.get(31) ? 1 : 0);
		return alerte;
	}

	private void sendAlerte(final Alerte data) {

		final Hashtable header = new Hashtable();
		try {
			this._messenger.send(header, data);
		} catch (final Exception e) {
			this._log.error("Erreur", e);
		}
	}

	public void setJdb(JDB jdb) {
		this.jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		this.jdb = null;
	}

	public void journalize(String value) {
		if (jdb != null) {
			jdb.journalize(JDB_TAG, value);
		}
	}

	class OrderComparator implements Comparator {

		public int compare(Object object1, Object object2) {
			Alarm a1 = (Alarm) object1;
			Alarm a2 = (Alarm) object2;

			if (a1.getOrder() > a2.getOrder()) {
				return 1;
			} else if (a1.getOrder() < a2.getOrder()) {
				return -1;
			} else {
				return 0;
			}

		}

	}

}