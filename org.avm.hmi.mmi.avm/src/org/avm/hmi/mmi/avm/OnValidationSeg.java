package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.business.core.event.Course;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnValidationSeg implements ProcessRunnable {

	private Avm _avm;
	private AvmView _avmView;
	private static int _currentCourseIndex = 0;
	private boolean _isV = true;

	public OnValidationSeg(Avm avm, boolean v, AvmView view) {
		_avmView = view;
		_avm = avm;
		_isV = v;
	}

	public void init(String[] args) {
		try {
			_currentCourseIndex = new Integer(args[0]).intValue();
		} catch (NumberFormatException nfe) {
			System.out.println(">>>\tNumberFormatException : " + args[0]);
			_currentCourseIndex = 0;
		}
		System.out.println(">>>\tinit currentCourse a " + _currentCourseIndex);
	}

	public void run() {
		// On valide le segment : l'ecran suivant indique un depart.
		if (_isV) {
			System.out.println(">>>\tChargement de la course "
					+ _currentCourseIndex);
			Course course = _avm.getModel().getServiceAgent().getCourseByRang(
					_currentCourseIndex);
			if (course != null) {
				_avmView.getBase().startAttente();
				_avm.priseCourse(course.getIdu());
				_currentCourseIndex++;
			} else
				System.out.println("!!!!!!!!!!!!!!!!! Course "
						+ _currentCourseIndex + " null !!!");
		} else {
			// _avmView.activatePriseDeService(AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE);
			_avm.annuler();
		}
	}

	public static void initCourseIndex() {
		_currentCourseIndex = 0;
	}

	public static int getCourseIndex() {
		return _currentCourseIndex;
	}

}
