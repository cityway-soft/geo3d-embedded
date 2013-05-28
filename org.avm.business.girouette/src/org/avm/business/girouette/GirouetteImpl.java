package org.avm.business.girouette;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.core.AbstractAvmModelListener;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.ServiceAgent;
import org.avm.device.girouette.GirouetteInjector;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.directory.Directory;
import org.avm.elementary.directory.DirectoryInjector;
import org.osgi.util.measurement.State;

public class GirouetteImpl implements ConfigurableService, AvmInjector,
		GirouetteInjector, ManageableService, ConsumerService, Girouette,
		Constants, DirectoryInjector {

	private Logger _log = Logger.getInstance(this.getClass());

	private GirouetteConfig _config;

	private org.avm.device.girouette.Girouette _girouette;

	private static final int ALARM_SURCHARGE = 5;
	
	public boolean alarmSurcharge = false;

	private int _code = -1;

	private ModelListener _listener;

	private Avm _avm;

	private boolean _initialized = false;

	private Directory _directory;

	private Properties _specialCodes;

	private AlarmService _alarmService;

	public void configure(Config config) {
		_config = (GirouetteConfig) config;
	}

	public void setAvm(Avm avm) {
		_avm = avm;
		initialize();
	}

	public void unsetAvm(Avm avm) {
		_listener = null;
		_initialized = false;
	}

	private void initialize() {
		if (!_initialized) {
			if (_avm != null && _girouette != null) {
				_listener = new ModelListener(_avm);
				_listener.notify(_avm.getModel().getState());
				_initialized = true;
			}
		}
	}

	public void setGirouette(org.avm.device.girouette.Girouette girouette) {
		_girouette = girouette;
		_code = 0;
		initialize();
	}

	public void unsetGirouette(org.avm.device.girouette.Girouette girouette) {
		_girouette = null;
		_initialized = false;
	}

	public void setAlarmService(AlarmService service) {
		_alarmService = service;
	}

	public void unsetAlarmService(AlarmService service) {
		_alarmService = service;
	}

	public void start() {

	}

	public void stop() {
		destination(getSpecialCode(CODE_HORSSERVICE));
		_initialized = false;
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(AlarmService.class.getName())) {
				if(alarmSurcharge != isSurcharge()){
					alarmSurcharge = isSurcharge();
					destination(_avm.getModel().getCodeGirouette());
				}
			}
		} else if (_listener != null) {
			_listener.notify(o);
		}
	}

	private void send(String code) {
		if (_girouette != null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Sending dest code :" + code);
			}
			_girouette.destination(code);
			if (_log.isDebugEnabled()) {
				_log.debug("Girouette code set to :" + code);
			}
		}
	}

	private boolean isSurcharge() {
		boolean result = false;
		if (_alarmService != null) {
			Alarm alarm = _alarmService.getAlarm(new Integer(ALARM_SURCHARGE));
			result = alarm.isStatus();
		}
		return result;
	}

	public void destination(int code) {
		// if (_avm.getModel().isVehiculeFull()) {
		if (isSurcharge()) {
			code = getSpecialCode(CODE_COMPLET);
		}

		if (code > 0 && code != _code) {
			_code = code;
			send(Integer.toString(_code));
		}

	}

	class ModelListener extends AbstractAvmModelListener {

		public ModelListener(Avm avm) {
			super(avm);

		}

		public void onStateAttenteDepart(AvmModel model) {
			destination(model.getCodeGirouette());
		}

		public void onStateAttenteSaisieCourse(AvmModel model) {
			destination(getSpecialCode(CODE_HORSCOURSE));
		}

		public void onStateAttenteSaisieService(AvmModel model) {
			destination(getSpecialCode(CODE_HORSSERVICE));
		}

		public void onStateEnCourseArretSurItineraire(AvmModel model) {
			destination(model.getCodeGirouette());
		}

		public void onStateEnCourseHorsItineraire(AvmModel model) {

		}

		public void onStateEnCourseInterarretSurItineraire(AvmModel model) {
			destination(model.getCodeGirouette());
		}

		public void onStateEnCourseServiceSpecial(AvmModel model) {

			ServiceAgent sa = model.getServiceAgent();
			if (sa == null) {
				return;
			}
			if (sa.getIdU() == ServiceAgent.SERVICE_KM_A_VIDE) {
				destination(getSpecialCode(CODE_HLP));
			}
			if (sa.getIdU() == ServiceAgent.SERVICE_OCCASIONNEL) {
				destination(getSpecialCode(CODE_SPECIAL));
			}

		}

		public void onStateEnPanne(AvmModel model) {
			_log.debug(model.getState());
			destination(getSpecialCode(CODE_ENPANNE));
		}

		public void onStateInitial(AvmModel model) {
			destination(getSpecialCode(CODE_HORSSERVICE));
		}

		public void onStatePause(AvmModel model) {
			destination(getSpecialCode(CODE_HORSSERVICE));
		}
	}

	public void setDirectory(Directory directory) {
		_directory = directory;
		if (_specialCodes == null) {
			_specialCodes = new Properties();
		}
		if (_directory != null) {
			Properties all = _directory.getProperty(null);
			Enumeration e = all.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				Properties props = _directory.getProperty(key);
				String type = props.getProperty("type");
				if (type != null && type.equalsIgnoreCase("girouette")) {
					_specialCodes.put(key, props.get("code"));
				}
			}
		}
	}

	public void unsetDirectory(Directory directory) {
		_directory = null;
	}

	public int getSpecialCode(String name) {
		int code = -1;
		try {
			code = Integer.parseInt((String) _specialCodes.get(name));
			_log.info("Code Girouette " + name + " found : " + code);
		} catch (Throwable t) {
			_log.error("No girouette code for name : '" + name + "' (=>code="
					+ code + ")");
		}

		return code;
	}

}
