package org.avm.business.vocal;

public interface Vocal {
	public static final String KEY = "org.avm.business.vocal.key";

	public static final int CONDUCTEUR = 0;
	public static final int VOYAGEUR_INTERIEUR = 1;
	public static final int VOYAGEUR_EXTERIEUR = 2;

	public static final String CONFIGURATION_CONDUCTEUR = "conducteur";
	public static final String CONFIGURATION_VOYAGEUR_INTERIEUR = "voyageur-interieur";
	public static final String CONFIGURATION_VOYAGEUR_EXTERIEUR = "voyageur-exterieur";
	public static final String CONFIGURATION_DEFAUT = "default";

	public static final String EN_DIRECTION_DE = "direction";
	public static final String LIGNE = "ligne";
	public static final String MONTEE_INTERDITE = "montee-interdite";
	public static final String DESCENTE_INTERDITE = "descente-interdite";
	public static final String PROCHAIN = "prochain-arret";
	public static final String CONDUCTEUR_RECEPTION_MESSAGE = "message";
	public static final String CONDUCTEUR_ALARM_MATRICULE = "matricule";
	public static final String CONDUCTEUR_ALARM_SPEED = "vitesse";
	public static final String TEST_CONDUCTEUR = "test-conducteur";
	public static final String TEST_VOYAGEUR = "test-voyageur";

}
