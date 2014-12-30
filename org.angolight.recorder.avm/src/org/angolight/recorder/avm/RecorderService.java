package org.angolight.recorder.avm;

import java.util.Iterator;
import java.util.Map;

import org.angolight.indicator.Indicator;
import org.angolight.indicator.IndicatorInjector;
import org.angolight.indicator.Measure;
import org.angolight.recorder.Recorder;
import org.apache.log4j.Logger;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmModel;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.json.JSONObject;
import org.osgi.util.measurement.State;

public class RecorderService implements Recorder, ConfigurableService,
		ConsumerService, ManageableService, JDBInjector, IndicatorInjector {

	private static final Object NAME = Avm.class.getName();
	private RecorderConfig _config;
	private Logger _log;
	private JDB _jdb;
	private Indicator _indicator;
	private int _stateValue = -1 ;

	public RecorderService() {
		_log = Logger.getInstance(this.getClass());
	}

	public void configure(Config config) {
		_config = (RecorderConfig) config;
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	public void setIndicator(Indicator indicator) {
		_indicator = indicator;
		_indicator.reset();
	}

	public void unsetIndicator(Indicator indicator) {
		_indicator = null;
	}

	public void start() {
		_jdb.journalize(Indicator.CATEGORY, "RECORDER;ON");
	}

	public void stop() {
		if (_indicator != null) {
			Map map = _indicator.evaluate();
			serialize(map);
		}
		if (_jdb != null) {
			_jdb.journalize(Indicator.CATEGORY, "RECORDER;OFF");
			_jdb.sync();
		}
	}

	public void notify(Object o) {
		
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(NAME) && _stateValue != state.getValue()) {
				
				if (_indicator == null) {
					_log.info("Indicator is null on notify !");
					return;
				}
					
				_stateValue  = state.getValue();
				int key = state.getValue();
				switch (key) {
				case AvmModel.STATE_INITIAL: {
					// anonyme (fin poste)
					_log.info("serialize for fin poste");
					Map map = _indicator.evaluate();
					_indicator.reset();
					serialize(map);
				}
					break;
				case AvmModel.STATE_ATTENTE_SAISIE_SERVICE: {
					// authentifie
					_log.debug("_indicator is null ? => "+(_indicator==null));
					Map map = _indicator.evaluate();
					_indicator.reset();
					serialize(map);
				}
					break;
				case AvmModel.STATE_ATTENTE_SAISIE_COURSE: {
					// en service (hors course)
					Map map = _indicator.evaluate();
					_indicator.reset();
					serialize(map);
				}
					break;
				case AvmModel.STATE_ATTENTE_DEPART: {
					// en course (attente depart)
					Map map = _indicator.evaluate();
					_indicator.reset();
					serialize(map);
				}
					break;
				case AvmModel.STATE_EN_COURSE_ARRET_SUR_ITINERAIRE:
				case AvmModel.STATE_EN_COURSE_SERVICE_SPECIAL: {
					// en course + chaque passage aux arrets
					Map map = _indicator.evaluate();
					_indicator.reset();
					serialize(map);
				}
					break;
				}
			}
		}
	}
	
	

	private void serialize(Map map) {
		try {
			JSONObject result = new JSONObject();
			for (Iterator iterator = map.values().iterator(); iterator
					.hasNext();) {
				Measure measure = (Measure) iterator.next();
				result.put(measure.getName(), measure.getValue());
			}
			_jdb.journalize(Indicator.CATEGORY,
					"INDICATORS;" + result.toString()+";V2");
			_jdb.sync();
		} catch (Exception e) {
		}
	}

}
