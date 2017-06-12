package org.avm.business.comptage;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.comptage.bundle.Activator;
import org.avm.business.core.AbstractAvmModelListener;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.device.comptage.ComptageInjector;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.geofencing.Balise;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.util.measurement.State;

public class ComptageImpl implements Comptage, ManageableService,
		ConfigurableService, ComptageInjector, AvmInjector, ConsumerService,
		JDBInjector {

	private Logger _log = Activator.getDefault().getLogger();
	// private ComptageConfig _config;
	private org.avm.device.comptage.Comptage _comptage;
	private ModelListener _listener;
	private boolean _initialized;
	private Avm _avm;
	private JDB _jdb;
	int _last;
	boolean _isStopDone = false;
	boolean _isEndComptageDone = false;
	private boolean _onecourse;
	private Point _firstPoint = null;
	private String _nomCourse = "";
	private String _nomLigne = "";
	private int _codeGirouette = 0;
	private String _lastName;
	private boolean hasInitComptage = false;

	public void configure(Config config) {
		// _config = (ComptageConfig) config;
	}

	public void setComptage(org.avm.device.comptage.Comptage comptage) {
		_log.info("set comptage " + comptage);
		_comptage = comptage;
		initialize();
	}

	public void unsetComptage(org.avm.device.comptage.Comptage comptage) {
		_comptage = null;
		_initialized = false;
	}

	public void setAvm(Avm avm) {
		_avm = avm;
		initialize();
	}

	public void unsetAvm(Avm avm) {
		_listener = null;
		_initialized = false;
	}

	public void start() {

	}

	private void endComptage() {
		Properties pp = getStatus();
		StringBuffer log = new StringBuffer();
		log.append("ENDCOMPTAGE;");
		log.append(pp
				.getProperty(org.avm.device.comptage.Comptage.NOMBRE_MONTEES));
		log.append(';');
		log.append(pp
				.getProperty(org.avm.device.comptage.Comptage.NOMBRE_DESCENTES));
		_jdb.journalize("comptage", log.toString());
		hasInitComptage = false;
		// _comptage.miseAZero();
	}

	public void stop() {

	}

	private void initialize() {
		if (!_initialized) {
			_log.info("initialize comptage");
			if (_avm != null && _comptage != null) {
				_listener = new ModelListener(_avm);
				_listener.notify(_avm.getModel().getState());
				// _comptage.miseAZero();
				_initialized = true;
			}
		}
	}

	public void notify(Object o) {
		if (o instanceof State) {
			// State state = (State) o;
			if (_listener != null) {
				_listener.notify(o);
			}
		} else if (o instanceof Balise) {
			if (_firstPoint != null) {
				Balise b = (Balise) o;
				if (b.getId() == _firstPoint.getId()) {
					initComptage();
					_firstPoint = null;
				}
			}

		}
	}

	private void initComptage() {
		if (!hasInitComptage) {
			Properties pp = getStatus();
			String nbmonte = "0";
			String nbdesc = "0";
			if (pp != null) {
				nbmonte = pp
						.getProperty(org.avm.device.comptage.Comptage.NOMBRE_MONTEES);
				nbdesc = pp
						.getProperty(org.avm.device.comptage.Comptage.NOMBRE_DESCENTES);
			}
			StringBuffer log = new StringBuffer();
			log.append("INITPASSAGERS;");
			log.append(_nomLigne);
			log.append(';');
			log.append(_nomCourse);
			log.append(';');
			log.append(_codeGirouette);
			log.append(';');
			log.append(nbmonte);
			log.append(';');
			log.append(nbdesc);
			_log.info("onStateAttenteSaisieCourse: journalize");
			_jdb.journalize("comptage", log.toString());
			_onecourse = true;
			miseAZero();
			hasInitComptage = true;
		}
	}

	class ModelListener extends AbstractAvmModelListener {

		private Avm _avm;

		public ModelListener(Avm avm) {
			super(avm);
			_avm = avm;
			_onecourse = false;
		}

		protected void onStateAttenteSaisieCourse(AvmModel model) {
			_log.info("onStateAttenteSaisieCourse");
			if (_onecourse) {
				if (!_isStopDone) {
					journalizeArret();
				}
				endComptage();
			}
			else{
				hasInitComptage = false;
			}
		}

		protected void onStateAttenteDepart(AvmModel model) {
			_log.info("onStateAttenteDepart");

			Course course = model.getCourse();
			_nomLigne = "";
			_nomCourse = "";
			_codeGirouette = 0;
			if (course != null) {
				_nomCourse = course.getNom();
				_nomLigne = course.getLigneNom();
			}
			_codeGirouette = model.getCodeGirouette();

			Point last = model.getDernierPoint();
			if (last != null) // on est dans le point de départ
			{
				initComptage();
				_last = last.getId();
				_lastName = last.getNom();
				_firstPoint = null;
			} else {
				_firstPoint = model.getProchainPoint();
				_lastName = _firstPoint.getNom();
				_last = _firstPoint.getId();
			}
		}

		protected void onStateEnCourseArretSurItineraire(AvmModel model) {
			Point point = _avm.getModel().getDernierPoint();
			if (point != null) {
				_last = point.getId();
				_lastName = point.getNom();
				_isStopDone = false;
			}
			initComptage();
		}

		protected void onStateEnCourseInterarretSurItineraire(AvmModel model) {
			_log.info("onStateEnCourseInterarretSurItineraire " + _jdb);
			journalizeArret();
		}

		private void journalizeArret() {
			Properties pp = getStatus();
			if (pp != null) {
				_jdb.journalize(
						"comptage",
						"PASSAGERS;"
								+ _last
								+ ";"
								+ _lastName
								+ ";"
								+ pp.getProperty(org.avm.device.comptage.Comptage.NOMBRE_MONTEES)
								+ ";"
								+ pp.getProperty(org.avm.device.comptage.Comptage.NOMBRE_DESCENTES)
								+ ";"
								+ pp.getProperty(org.avm.device.comptage.Comptage.STATUS));
				miseAZero();
				_isStopDone = true;
			}
		}
	}

	private void sleepms(int time) {
		try {
			Object o = new Object();
			synchronized (o) {
				o.wait(time);
			}
		} catch (InterruptedException e) {

		}
	}

	private boolean miseAZero() {
		boolean ret = _comptage.miseAZero();
		if (ret == false) {
			int i = 0;
			for (i = 0; i < 5; ++i) {
				ret = _comptage.miseAZero();
				if (ret) {
					break;
				}
				sleepms(2000);
			}
			if (!ret) {
				_jdb.journalize("comptage", "ERROR;reset impossible");
			} else if (i != 0) {
				_jdb.journalize("comptage", "ERROR;reset after " + i + " retry");
			}
		}
		return ret;
	}

	private Properties getStatus() {
		Properties pp = null;
		pp = _comptage.status();
		if (pp == null) {
			int i = 0;
			for (i = 0; i < 5; ++i) {
				sleepms(2000);
				pp = _comptage.status();
				if (pp != null) {
					break;
				}
			}
			if (pp == null) {
				_jdb.journalize("comptage", "ERROR;no value for status");
			} else {
				_jdb.journalize("comptage", "ERROR;value for status after " + i
						+ " retry");
			}
		}
		return pp;
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

}