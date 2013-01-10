package org.avm.business.site.client.common;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class AVMModel {

	private final COURSE _course = new COURSE();

	private final SERVICE _service = new SERVICE();

	private int _time;

	private int _state = 0;

	private String[] _messages;

	public COURSE getCourse() {
		return _course;
	}

	public SERVICE getService() {
		return _service;
	}

	public int getState() {
		return _state;
	}

	public int getTime() {
		return _time;
	}

	/* convenance */

	public String getDestination() {
		return _course.getPOINTS()[_course.getPOINTS().length - 1].getPNT_NOM();
	}

	public POINT getArret() {
		final boolean found = false;
		POINT dernier = null;
		POINT p;

		for (int i = 0; i < _course.getPOINTS().length; i++) {
			p = _course.getPOINTS()[i];
			if (p.getHOD_STATUT() == 1) {
				dernier = p;
			} else {
				break;
			}
		}
		return dernier;
	}

	public POINT getProchainArret() {
		POINT prochain = null;
		try {
			final POINT dernier = getArret();

			if (dernier == null) {
				prochain = _course.getPOINTS()[0];
			} else {
				final int rangProchain = dernier.getHOD_RANG() + 1;
				if ((rangProchain - 1) < _course.getPOINTS().length) {
					prochain = _course.getPOINTS()[rangProchain - 1];
				}
			}
		} catch (final Throwable e) {
			ExchangeControler.error("Erreur (getProchainArret): "
					+ e.getMessage());
		}
		return prochain;
	}

	public void update(final JSONObject json) {
		try {
			updateHeure(json);
			updateState(json);
			updateCourse(json);
			updateMessages(json);
		} catch (final Throwable t) {
			ExchangeControler.error("Erreur set AVM Model : " + json
					+ " ERROR:" + t.getMessage());
		}

	}

	// @UnsafeNativeLong
	// @UnsafeNativeLong
	public static native double UTC2LocaleTime(double time)/*-{
		return $wnd.UTC2LocaleTime(time);
	}-*/;;

	// @UnsafeNativeLong
	public static native int getTimeZoneOffset()/*-{
		return new Date().getTimezoneOffset();
	}-*/;

	public static int getTime(final JSONObject json) {
		final JSONObject jex_date = json.get(Constantes.JEX_DATE).isObject();
		long time = (long) jex_date.get(Constantes.TIME).isNumber().getValue();
		if (getTimeZoneOffset() == 0) {
			/* BIDOUILLE PB CALC COPEP EN TIMEZONE GMT */
			final long delta = 1 * 3600 * 1000; // 1 HEURE
			time += delta;
		}
		time = (long) UTC2LocaleTime(time);
		return millis2hhmm(time);
	}

	private void updateHeure(final JSONObject json) {
		_time = getTime(json);
	}

	private static int millis2hhmm(final long time) {
		int t;

		final int milliseconds = (int) (time % 1000);
		final int seconds = (int) ((time / 1000) % 60);
		final int minutes = (int) ((time / 60000) % 60);
		final int hours = (int) ((time / 3600000) % 24);

		t = (int) (hours * 3600 + minutes * 60 + seconds);

		return t;
	}

	private void updateCourse(final JSONObject json) {
		final JSONObject jsonCourse = json.get(Constantes.COURSE).isObject();
		if (jsonCourse != null) {

			_course.setCRS_ID((int) jsonCourse.get(Constantes.CRS_ID)
					.isNumber().getValue());
			_course.setCRS_IDU((int) jsonCourse.get(Constantes.CRS_IDU)
					.isNumber().getValue());

			// _course.setCRS_NOM(jsonCourse.get(Constantes.CRS_NOM).isString().
			// stringValue()) ;

			_course.setCRS_DEPART((int) jsonCourse.get(Constantes.CRS_DEPART)
					.isNumber().getValue());

			_course.setLGN_IDU((int) jsonCourse.get(Constantes.LGN_IDU)
					.isNumber().getValue());

			_course.setLGN_AMPLITUDE((int) jsonCourse
					.get(Constantes.LGN_AMPLITUDE).isNumber().getValue());

			_course.setLGN_NOM(jsonCourse.get(Constantes.LGN_NOM).isString()
					.stringValue());

			_course.setLGN_CHEVAUCHEMENT((int) jsonCourse
					.get(Constantes.LGN_CHEVAUCHEMENT).isNumber().getValue());
			_course.setCRD_STATUT((int) jsonCourse.get(Constantes.CRD_STATUT)
					.isNumber().getValue());

			final JSONArray array = jsonCourse.get(Constantes.POINTS).isArray();
			final int size = array.size();
			final POINT[] pts = new POINT[size];

			for (int i = 0; i < size; i++) {
				final JSONObject jsonPoint = array.get(i).isObject();
				pts[i] = new POINT();

				pts[i].setPNT_ID((int) jsonPoint.get(Constantes.PNT_ID)
						.isNumber().getValue());

				pts[i].setPNT_IDU((int) jsonPoint.get(Constantes.PNT_IDU)
						.isNumber().getValue());

				pts[i].setPNT_NOM(jsonPoint.get(Constantes.PNT_NOM).isString()
						.stringValue());

				pts[i].setGRP_NOM(jsonPoint.get(Constantes.GRP_NOM).isString()
						.stringValue());

				pts[i].setPNT_X((float) jsonPoint.get(Constantes.PNT_X)
						.isNumber().getValue());

				pts[i].setPNT_Y((float) jsonPoint.get(Constantes.PNT_Y)
						.isNumber().getValue());

				pts[i].setPSI_DISTANCE((int) jsonPoint
						.get(Constantes.PSI_DISTANCE).isNumber().getValue());

				pts[i].setPSP_GIR((int) jsonPoint.get(Constantes.PSP_GIR)
						.isNumber().getValue());

				pts[i].setHOD_ARRIVEE((int) jsonPoint
						.get(Constantes.HOD_ARRIVEE).isNumber().getValue());

				pts[i].setHOD_ATTENTE((int) jsonPoint
						.get(Constantes.HOD_ATTENTE).isNumber().getValue());

				pts[i].setHOD_ARRIVEE_THEORIQUE((int) jsonPoint
						.get(Constantes.HOD_ARRIVEE_THEORIQUE).isNumber()
						.getValue());

				pts[i].setHOD_ATTENTE_THEORIQUE((int) jsonPoint
						.get(Constantes.HOD_ATTENTE_THEORIQUE).isNumber()
						.getValue());

				pts[i].setHOD_RANG((int) jsonPoint.get(Constantes.HOD_RANG)
						.isNumber().getValue());

				pts[i].setHOD_STATUT((int) jsonPoint.get(Constantes.HOD_STATUT)
						.isNumber().getValue());
			}

			_course.setPOINTS(pts);
		}

	}

	public static int getState(final JSONObject json) {
		int result = -1;
		try {
			result = (int) (json.get(Constantes.STATUT).isNumber().getValue());
		} catch (final Throwable t) {
		}
		return result;
	}

	private void updateState(final JSONObject json) {
		_state = getState(json);
	}

	private void updateMessages(final JSONObject json) {
		final JSONArray array = json.get(Constantes.MESSAGES).isArray();
		final int size = array.size();
		_messages = new String[size];

		for (int i = 0; i < size; i++) {
			_messages[i] = array.get(i).isString().stringValue();
		}
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("model\n");

		final int CRS_DEPART = getCourse() == null ? 0 : getCourse()
				.getCRS_DEPART();
		final String PNT_NOM_courant = getArret() == null ? "" : getArret()
				.getPNT_NOM();
		final String PNT_NOM_prochain = getProchainArret() == null ? ""
				: getProchainArret().getPNT_NOM();
		final int HOD_ARRIVEE_prochain = getProchainArret() == null ? 0
				: getProchainArret().getHOD_ARRIVEE();

		buf.append("time=");
		buf.append(_time);
		buf.append("\n");

		buf.append("state=");
		buf.append(_state);
		buf.append("\n");

		buf.append("destination=");
		buf.append(getDestination());
		buf.append("\n");

		buf.append("heure depart course=");
		buf.append(Util.formatHeure(CRS_DEPART));
		buf.append("(");
		buf.append(CRS_DEPART);
		buf.append(")");
		buf.append("\n");

		buf.append("arret=");
		buf.append(PNT_NOM_courant);
		buf.append("\n");

		buf.append("prochainArret=");
		buf.append(PNT_NOM_prochain);
		buf.append("\n");

		buf.append("heureArriveeProchainArret=");
		buf.append(Util.formatHeure(HOD_ARRIVEE_prochain));
		buf.append("(");
		buf.append(HOD_ARRIVEE_prochain);
		buf.append(")");

		buf.append("\n");

		buf.append("arrets=");
		if (getCourse() != null && getCourse().getPOINTS() != null) {
			for (int i = 0; i < getCourse().getPOINTS().length; i++) {
				buf.append(getCourse().getPOINTS()[i].getPNT_NOM() + ", ");
			}
		}
		buf.append("\n");

		buf.append("messages=");
		if (_messages != null) {
			for (int i = 0; i < _messages.length; i++) {
				buf.append(_messages[i] + ", ");
			}
		}
		buf.append("\n");

		return buf.toString();
	}

	public String[] getMessages() {
		return _messages;
	}

}
