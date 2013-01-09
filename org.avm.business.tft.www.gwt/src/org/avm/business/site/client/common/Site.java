package org.avm.business.site.client.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

public class Site {
	private final HashMap _sequences = new HashMap();

	private Sequence _currentSequence;

	private final AVMModel _model;

	private Zone _currentZone;

	private final SequenceTimer _timer;

	private Vector _staticZones;

	private static Site _instance = null;

	public static Site getInstance() {
		if (_instance == null) {
			_instance = new Site();
		}
		return _instance;
	}

	private Site() {
		_model = new AVMModel();
		_timer = new SequenceTimer();

		Zone z1 = new ZoneArretDestination(10000);
		final Zone z2 = new ZoneDestinationArret(10000);

		final Zone z3 = new ZoneProchainArretImage(10000);
		final Zone z4 = new ZoneProchainArretDestination(5000);
		final Zone z5 = new ZoneInformationVoyageurProchainArret(15000);

		final Zone z6 = new ZoneDestination(15000);
		final Zone z7 = new ZoneGraphiqueLigne(10000);

		final Zone z8 = new ZoneInformationVoyageurArret(10000);

		final Zone zLigne = new ZoneImageLigne(-1);

		Sequence sequence;

		sequence = new Sequence("Attente depart");
		sequence.addStaticZone(zLigne);
		sequence.addZone(z6);
		_sequences.put(new Integer(Constantes.STATE_ATTENTE_DEPART), sequence);

		sequence = new Sequence("Arret sur itineraire");
		sequence.addStaticZone(zLigne);
		sequence.addZone(z8);
		sequence.addZone(z1);
		sequence.addZone(z7);

		sequence.addZone(z2);

		_sequences.put(new Integer(
				Constantes.STATE_EN_COURSE_ARRET_SUR_ITINERAIRE), sequence);

		sequence = new Sequence("Inter arret");
		sequence.addStaticZone(zLigne);
		sequence.addZone(z3);
		sequence.addZone(z5);
		sequence.addZone(z7);
		sequence.addZone(z4);
		sequence.addZone(z5);
		sequence.addZone(z7);
		_sequences
				.put(new Integer(
						Constantes.STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE),
						sequence);

		z1 = new ZoneDeviation(5000);

		sequence = new Sequence("Hors itineraire");
		sequence.addStaticZone(zLigne);
		sequence.addZone(z1);
		_sequences.put(new Integer(Constantes.STATE_EN_COURSE_HORS_ITINERAIRE),
				sequence);

	}

	private Sequence getSequence(final Integer state) {
		return (Sequence) _sequences.get(state);
	}

	private void setClock(final String heure) {
		final Element clockElm = DOM.getElementById("clock");
		DOM.setInnerText(clockElm, heure);
	}

	private void doNextZone() {
		final Zone zone = _currentSequence.getNext();
		setCurrentZone(zone);
		refresh();
		Debug.setText("sequence", _currentSequence.toString());
	}

	private void setCurrentZone(final Zone zone) {
		if (_currentZone != null) {
			if (_currentZone != zone) {
				_currentZone.activate(false);
				_currentZone.setData(_model);
			}
		}
		_currentZone = zone;
	}

	private void setStaticZones(final Vector zones) {
		if (zones == null) {
			return;
		}
		if (_staticZones == null) {
			_staticZones = zones;
			final Iterator i = zones.iterator();
			while (i.hasNext()) {
				final Zone zone = (Zone) i.next();
				zone.activate(true);
				zone.setData(_model);
			}
		} else {
			Iterator i = _staticZones.iterator();
			while (i.hasNext()) {
				final Zone zone = (Zone) i.next();
				if (zones.indexOf(zone) == -1) {
					zone.activate(false);
				}
			}
			i = zones.iterator();
			while (i.hasNext()) {
				final Zone zone = (Zone) i.next();
				zone.activate(true);
				zone.setData(_model);
			}
		}
	}

	private void refresh() {
		final Zone zone = _currentZone;
		if (zone != null) {
			zone.setData(_model);
			if (zone.isPrintable(_model)) {
				zone.activate(true);
				_timer.scheduleRepeating(zone.getDelay());
			} else {
				doNextZone();
			}
			Debug.setText("zone",
					zone == null ? "aucune zone" : zone.toString());
		}
	}

	public synchronized void setData(final JSONObject json) {
		try {
			setClock(Util.formatHeure(AVMModel.getTime(json)));
			final int state = AVMModel.getState(json);
			final Sequence newSequence = getSequence(new Integer(state));
			if (newSequence != null) {
				_model.update(json);
				setVisible("init", false);
				setVisible("cadre", true);

				newSequence.setData(_model);
				_currentSequence = newSequence;
				setCurrentZone(_currentSequence.getCurrentZone());
				refresh();

				Debug.setText("model", _model.toString());
				Debug.setText("sequence",
						_currentSequence == null ? "aucune sequence"
								: _currentSequence.toString());
			} else {
				if (_currentSequence != null) {
					_currentSequence.activate(false);
					_currentSequence = null;
				}
				setVisible("cadre", false);
				setVisible("init", true);
			}
		} catch (final Throwable e) {
			if (_currentSequence != null) {
				_currentSequence.activate(false);
				_currentSequence = null;
			}
			ExchangeControler.error("Error setData : " + e.getMessage());
		}
	}

	public void setVisible(final String id, final boolean b) {
		DOM.setStyleAttribute(RootPanel.get(id).getElement(), "visibility",
				b ? "visible" : "hidden");
	}

	public class SequenceTimer extends Timer {
		long t;

		@Override
		public void run() {
			doNextZone();
		}
	}
}
