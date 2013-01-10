package org.avm.business.core;

public class AvmDatabaseException extends Exception {
	public static final int ERR_AUCUNE_COURSE_VALIDE = 0;
	public static final int ERR_SERVICE_AGENT_INCONNU = 1;
	public static final int ERR_BASE_PERIMEE = 2;
	public static final int ERR_BASE_INTROUVABLE = 3;
	public static final int ERR_MODE_PLANIFICATION_SANS_BASE = 4;

	private static final String[] ERRORS = { "AUCUNE_COURSE_VALIDE",
			"SERVICE_AGENT_INCONNU", "BASE_PERIMEE", "BASE_INTROUVABLE" , "MODE_PLANIFICATION_SANS_BASE"};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int _errCode;

	public AvmDatabaseException(int errorCode) {
		_errCode = errorCode;
	}

	public String getError() {
		return getError(_errCode);
	}

	public static String getError(int err) {
		String msg = null;
		if (err >= 0 && err < ERRORS.length) {
			msg = Messages.getString("Erreur." + ERRORS[err]);
		}
		return msg;
	}

}
