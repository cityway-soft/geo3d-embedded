package org.avm.business.core;

import org.avm.elementary.alarm.AlarmProvider;

public interface Avm extends AlarmProvider {
	public static final String JDB_TAG = "avmcore";

	// interface homme-machine
	
	public void prisePoste(int vehicule, int matricule);

	public void priseService(int service);

	public void priseCourse(int course);

	public void depart();

	public void sortieItineraire();
	
	public void finCourse();

	public void finService();
	
	public void sortie(int balise);
	
	public void entree(int balise);
	
	public void setGeorefMode(boolean b);
	
	public void setVehiculeFull(boolean b);

	public void annuler();

	// model

	public AvmModel getModel();



}
