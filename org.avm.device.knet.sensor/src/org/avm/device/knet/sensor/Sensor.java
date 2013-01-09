package org.avm.device.knet.sensor;

public interface Sensor {
	// definition des constantes pour les Sensors
	public static final String PORTE_AV = "PORTE_AVANT";
	public static final String PORTE_ARR = "PORTE_ARRIERE";
	public static final String COMPOST = "COMPOSTEUR";

	public static final String POWEROFF = "POWEROFF";
	public static final String REQUESTED = "DEMANDE";

	public static final String PORTE_AV_ID = "12";
	public static final String PORTE_ARR_ID = "14";
	public static final String COMPOST_ID = "15";

	public static final String OUVERT = "OUVERT";
	public static final String FERME = "FERME";
	public static final String ACTIONNE = "ACTIONNE";

	public static final String OUVERT_ID = "0";
	public static final String FERME_ID = "1";
	public static final String ACTIONNE_ID = "1";

	// methodes du SensorManager
	public void beep(int inDuration);

	public String readCompost();

	public String readFrontDoor();

	public String readRearDoor();
}
