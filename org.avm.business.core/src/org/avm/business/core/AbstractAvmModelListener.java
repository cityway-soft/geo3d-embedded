package org.avm.business.core;

import org.apache.log4j.Logger;
import org.avm.elementary.common.ConsumerService;
import org.osgi.util.measurement.State;

public abstract class AbstractAvmModelListener implements ConsumerService,
		AvmInjector {
	protected Avm _avm;

	private Logger _log = Logger.getInstance(this.getClass());

	public AbstractAvmModelListener() {
		super();
	}
	
	public AbstractAvmModelListener(Avm avm) {
		super();
		_avm = avm;
	}

	public void notify(Object o) {
		if (o instanceof State) {
			try {
				State state = (State) o;
				AvmModel model = _avm.getModel();
				if (_log.isDebugEnabled()){
				_log.debug("\n"
						+ org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
						+ model
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
				}
				switch (state.getValue()) {
				case AvmModel.STATE_INITIAL: {
					onStateInitial(model);
				}
					break;
				case AvmModel.STATE_ATTENTE_SAISIE_SERVICE: {
					onStateAttenteSaisieService(model);
				}
					break;
				case AvmModel.STATE_ATTENTE_SAISIE_COURSE: {
					onStateAttenteSaisieCourse(model);
				}
					break;
				case AvmModel.STATE_ATTENTE_DEPART: {
					onStateAttenteDepart(model);
				}
					break;
				case AvmModel.STATE_EN_COURSE_SERVICE_SPECIAL: {
					onStateEnCourseServiceSpecial(model);
				}
					break;
				case AvmModel.STATE_EN_COURSE_HORS_ITINERAIRE: {
					onStateEnCourseHorsItineraire(model);
				}
					break;
				case AvmModel.STATE_EN_COURSE_ARRET_SUR_ITINERAIRE: {
					onStateEnCourseArretSurItineraire(model);
				}
					break;
				case AvmModel.STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE: {
					onStateEnCourseInterarretSurItineraire(model);
				}
					break;
				case AvmModel.STATE_EN_PANNE: {
					onStateEnPanne(model);
				}
					break;
				}
			} catch (Exception e) {
				_log.error("Erreur trt du model", e);
			}
		} else {
			_log.debug("\n"
					+ org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS + o
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
			onNotify(o);
		}
	}

	public void setAvm(Avm avm) {
		_avm = avm;
	}

	public void unsetAvm(Avm avm) {
		_avm = null;
	}

	protected  void onNotify(Object o){
		
	}

	protected void onStateAttenteDepart(AvmModel model) {
	}

	protected void onStateAttenteSaisieCourse(AvmModel model) {
	}

	protected void onStateAttenteSaisieService(AvmModel model) {
	}

	protected void onStateEnCourseArretSurItineraire(AvmModel model) {
	}

	protected void onStateEnCourseHorsItineraire(AvmModel model) {
	}

	protected void onStateEnCourseInterarretSurItineraire(AvmModel model) {
	}

	protected void onStateEnCourseServiceSpecial(AvmModel model) {
	}

	protected void onStateEnCourseSurItineraire(AvmModel model) {
	}

	protected void onStateEnPanne(AvmModel model) {
	}

	protected void onStateInitial(AvmModel model) {
	}

	protected void onStatePause(AvmModel model) {
	}
}
