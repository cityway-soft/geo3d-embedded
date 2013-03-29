package org.avm.elementary.useradmin;

import java.util.Dictionary;

import org.osgi.util.measurement.State;

public interface UserSessionService {
	
	public static final String MATRICULE = "matricule";

	public static final String NOM = "nom";

	public static final String PRENOM = "prenom";

	public static final String CODESECRET = "codesecret";
	
	public static final int NOT_AUTHENTICATED=0;
	
	public static final int AUTHENTICATED=1;
	
	public static final State STATE_AUTHENTICATED = new State(AUTHENTICATED,
			UserSessionService.class.getName());
	public static final State STATE_NOT_AUTHENTICATED = new State(
			NOT_AUTHENTICATED, UserSessionService.class.getName());
	
	public void login(String matricule, String codesecret) throws SecurityException;

	public void logout();
	
	public State getState();
	
	public Dictionary getUserProperties();

	public boolean hasRole(String role);
}
