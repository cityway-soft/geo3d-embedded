package org.avm.business.recorder.action;

import org.avm.business.recorder.Action;
import org.avm.business.recorder.ActionFactory;
import org.avm.business.recorder.Journalizable;
import org.avm.elementary.variable.Variable;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.position.Position;

public class CO2Action extends Journalizable implements Action {

	private Variable _varCO2;

	private Variable _varOdometre;

	private Variable _varComptageVoyageur;

	private Variable _varConsomationInstantanee;

	private Variable _varCO2Voiture;

	public void compute(Object o) {
		if (o instanceof Position && _varCO2 != null && _varCO2Voiture != null
		// &&_varComptageVoyageur != null
				&& _varConsomationInstantanee != null && _varOdometre != null) {
			double odo = _varOdometre.getValue().getValue();

			double nbVoyageur = _varComptageVoyageur.getValue().getValue();
			double consoCar = _varConsomationInstantanee.getValue().getValue();// litres
																				// au
																				// 100
			double consoVoiture = 7;// litres au 100
			double carkgCO2 = odo * consoCar * 0.026d / 1000;
			double voiturekgCO2 = odo * consoVoiture * 0.026d / 1000;

			_varCO2.setValue(new Measurement(carkgCO2));
			_varCO2Voiture.setValue(new Measurement(voiturekgCO2));
		}
	}

	public void configure(Object o) {
		if (o instanceof Variable) {
			_log.info("CO2ACTION : Variable " + o);
			Variable v = (Variable) o;
			if (v.getName().equals("i-CO2")) {
				_varCO2 = v;
			} else if (v.getName().equals("odometre")) {
				_varOdometre = v;
			} else if (v.getName().equals("i-consommation")) {
				_varConsomationInstantanee = v;
			} else if (v.getName().equals("i-CO2Voiture")) {
				_varCO2Voiture = v;
			}
		}
	}

	static {
		ActionFactory.addAction(Position.class, new CO2Action());

	}

}
