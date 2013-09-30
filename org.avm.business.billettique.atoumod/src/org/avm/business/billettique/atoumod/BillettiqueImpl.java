package org.avm.business.billettique.atoumod;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.core.AbstractAvmModelListener;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.core.AvmModel;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.directory.Directory;
import org.osgi.util.measurement.State;

public class BillettiqueImpl implements ConfigurableService, AvmInjector,
		ManageableService, ConsumerService, Billettique, Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	private BillettiqueConfig _config;

	private ModelListener _listener;

	private Avm _avm;

	private boolean _initialized = false;

	private Properties _specialCodes;

	private AlarmService _alarmService;

	public void configure(Config config) {
		_config = (BillettiqueConfig) config;
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
			if (_avm != null) {
				_listener = new ModelListener(_avm);
				_listener.notify(_avm.getModel().getState());
				_initialized = true;
			}
		}
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
		_initialized = false;
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(AlarmService.class.getName())) {

			}
		} else if (_listener != null) {
			_listener.notify(o);
		}
	}

	class ModelListener extends AbstractAvmModelListener {

		public ModelListener(Avm avm) {
			super(avm);

		}

		public void onStateAttenteDepart(AvmModel model) {

		}

		public void onStateAttenteSaisieCourse(AvmModel model) {

		}

		public void onStateAttenteSaisieService(AvmModel model) {

		}

		public void onStateEnCourseArretSurItineraire(AvmModel model) {
		}

		public void onStateEnCourseHorsItineraire(AvmModel model) {

		}

		public void onStateEnCourseInterarretSurItineraire(AvmModel model) {

		}

		public void onStateEnCourseServiceSpecial(AvmModel model) {

		}

		public void onStateEnPanne(AvmModel model) {

		}

		public void onStateInitial(AvmModel model) {

		}

		public void onStatePause(AvmModel model) {

		}
	}


}
