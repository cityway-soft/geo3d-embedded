package org.avm.device.comptage;

import java.util.Properties;

public interface Comptage {

	
	public static String NOMBRE_MONTEES = "in";
	public static String NOMBRE_DESCENTES = "out";
	public static String STATUS = "status";
	public static String CHARGE = "load";
	
	public int nombrePassagers(String type) throws ComptageException;
	
	public Properties status ();
	
	public boolean miseAZero();
}
