package org.avm.elementary.alarm.impl;

import java.util.BitSet;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.Alerte;
import org.avm.business.protocol.phoebus.Anomalie;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.Horodate;
import org.avm.business.protocol.phoebus.Options;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;

public class AlarmServiceImpl implements AlarmService, ConfigurableService,
		MessengerInjector, ConsumerService {

	private Logger _log = Logger.getInstance(this.getClass());

	private Messenger _messenger;

	private AlarmServiceConfig _config;

	private BitSet _alarms;

	private Dictionary _map;

	private long _counter;

	public AlarmServiceImpl() {
		_alarms = new BitSet();
	}

	public void configure(Config config) {
		_config = (AlarmServiceConfig) config;
		if (_config != null) {
			_map = new Hashtable();
			for (Enumeration iter = _config.get().elements(); iter
					.hasMoreElements();) {
				Properties p = (Properties) iter.nextElement();
				_map.put(p.getProperty(Alarm.SOURCE), p);
			}
		}
	}

	public void setMessenger(Messenger messenger) {
		_messenger = messenger;
	}

	public void unsetMessenger(Messenger messenger) {
		_messenger = null;
	}

	public void notify(Object o) {
		if (o instanceof Alarm) {
			Alarm alarm = (Alarm) o;

			try {
				Properties p = (Properties) _map.get(alarm.source);

				if (p != null) {
					int index = Integer.parseInt(p.getProperty(Alarm.INDEX));
					boolean notify = Boolean.valueOf(
							p.getProperty(Alarm.NOTIFY)).booleanValue();
					boolean acknowledge = Boolean.valueOf(
							p.getProperty(Alarm.ACKNOWLEDGE)).booleanValue();
					if (alarm.status) {
						_alarms.set(index);
					} else {
						_alarms.clear(index);
					}
					if (alarm.priority > Alarm.NORM_PRIORITY && notify) {
						Alerte alerte = createAlerte((BitSet) _alarms.clone(),
								acknowledge);
						_log.debug("sending: " + alerte);
						sendAlerte(alerte);
					}
					_counter++;
				}

			} catch (NoSuchElementException e) {
			}
		}
	}

	public long getCounter() {
		return _counter;
	}

	public BitSet getAlarms() {
		return _alarms;
	}

	private void sendAlerte(Alerte data) {
		Hashtable header = new Hashtable();
		try {
			_messenger.send(header, data);
		} catch (Exception e) {
			_log.error("Erreur", e);
		}
	}

	private Alerte createAlerte(BitSet alarms, boolean acquittement) {
		Alerte alerte = new Alerte();
		Entete entete = alerte.getEntete();
		entete.setDate(new Horodate());
		entete.setAnomalie(new Anomalie());
		entete.setOptions(new Options());

		entete.getOptions().setAcquittement(acquittement ? 1 : 0);
		Anomalie anomalie = entete.getAnomalie();

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

}