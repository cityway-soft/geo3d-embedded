package org.avm.business.core;

public interface AvmStateMachine extends Avm {

	void actionPrisePoste(int replaceVehicule, int replaceMatricule);

	void actionService(boolean correct, int service);

	void actionCourse(boolean correct, int course);

	boolean isServiceNormal(int service);

	boolean isServiceSpecial(int service);

	boolean isCourseCorrect(int course);

	void actionFinPoste();

	void actionFinService();

	void actionFinCourse();

	void actionDepart();

	void actionAnnuler();

	// --

	void stateChange(int state);

	void startGestionDefautPrisePoste();

	void stopGestionDefautPrisePoste();

	void resetService();

	void resetCourse();

	void stopSuiviItineraire();

	void startSuiviItineraire();

	boolean isArret(int balise);

	boolean isProchainArret(int balise);

	boolean isTerminusDepart(int balise);

	boolean isTerminusArrivee(int balise);

	boolean isArretCourant(int balise);

	void notifyProgression(int pourcentage);

	void entryEnPanne();

	void entryEnCourseServiceSpecial();

	void checkPlanification();

	void actionFinHorsItineraire(int balise);

	void actionEntreeArret(int balise);

	void actionSortieArret(int balise);

	void actionSortieArret();

	void actionSortieItineraire();

	void entryHorsItineraire();

	void exitHorsItineraire();

	void synchronize(String reason);

	void checkSuiviItineraire();

	void attentePlanification();

	void checkForcePriseService();

	void serialize();

	boolean isGeorefMode();

	void checkCourse();

	void journalize(String string);

	void showMessage();

	void checkAutomaticCourse();

}
