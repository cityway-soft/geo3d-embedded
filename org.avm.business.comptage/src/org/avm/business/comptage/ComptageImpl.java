package org.avm.business.comptage;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
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
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.util.measurement.State;

public class ComptageImpl implements Comptage, ManageableService,
		ConfigurableService, ComptageInjector, AvmInjector, ConsumerService,
		JDBInjector {

	private Logger _log = Logger.getInstance(this.getClass());
	private ComptageConfig _config;
	private org.avm.device.comptage.Comptage _comptage;
	private ModelListener _listener;
	private boolean _initialized;
	private Avm _avm;
	private JDB _jdb;

	public ComptageImpl() {
		_log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (ComptageConfig) config;
	}

	public void setComptage(org.avm.device.comptage.Comptage comptage) {
		_log.debug("set comptage " + comptage);
		_comptage = comptage;
		initialize();
	}

	public void unsetComptage(org.avm.device.comptage.Comptage comptage) {
		_log.debug("unset comptage " + comptage);
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

	public void stop() {

	}

	private void initialize() {
		if (!_initialized) {
			_log.debug("Initialize / Comptage service=" + _comptage);
			if (_avm != null && _comptage != null) {
				_listener = new ModelListener(_avm);
				_listener.notify(_avm.getModel().getState());
				_initialized = true;
			}
		}
	}

	public void notify(Object o) {
		if (o instanceof State) {
			if (_listener != null) {
				_listener.notify(o);
			}
		}
	}

	class ModelListener extends AbstractAvmModelListener {

		private Avm _avm;
		Point _lastPoint;

		// private String _lastName;
		//
		// int _last;

		public ModelListener(Avm avm) {
			super(avm);
			_avm = avm;
		}

		protected void onStateAttenteDepart(AvmModel model) {
			_log.debug("Comptage service=" + _comptage);
			_comptage.miseAZero();
			Properties pp = _comptage.status();

			Course course = model.getCourse();
			String nomLigne = "";
			String nomCourse = "";
			if (course != null) {
				nomCourse = course.getNom();
				nomLigne = course.getLigneNom();
			}
			StringBuffer log = new StringBuffer();
			log.append("INITPASSAGERS;");
			log.append(nomLigne);
			log.append(';');
			log.append(nomCourse);
			log.append(';');
			log.append(model.getCodeGirouette());
			log.append(';');
			log.append(pp
					.getProperty(org.avm.device.comptage.Comptage.NOMBRE_DESCENTES));
			log.append(';');
			log.append(pp
					.getProperty(org.avm.device.comptage.Comptage.NOMBRE_MONTEES));
			_jdb.journalize("comptage", log.toString());
		}

		protected void onStateEnCourseArretSurItineraire(AvmModel model) {
			_lastPoint = _avm.getModel().getDernierPoint();
			// _last = _avm.getModel().getDernierPoint().getId();
			// _lastName = _avm.getModel().getDernierPoint().getNom();
		}

		protected void onStateEnCourseInterarretSurItineraire(AvmModel model) {
			if (_lastPoint == null) {
				_log.warn("lastPoint is null ? => à analyser ! (model=" + model
						+ ")");

				_lastPoint = model.getDernierPoint();
			}
			int last = _avm.getModel().getDernierPoint().getId();
			String lastName = _avm.getModel().getDernierPoint().getNom();
			_log.debug("Comptage service=" + _comptage);
			Properties pp = _comptage.status();
			_jdb.journalize(
					"comptage",
					"PASSAGERS;"
							+ last
							+ ";"
							+ lastName
							+ ";"
							+ pp.getProperty(org.avm.device.comptage.Comptage.NOMBRE_MONTEES)
							+ ";"
							+ pp.getProperty(org.avm.device.comptage.Comptage.NOMBRE_DESCENTES));
		}
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

}
