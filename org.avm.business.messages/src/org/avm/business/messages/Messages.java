package org.avm.business.messages;

import java.text.SimpleDateFormat;
import java.util.Collection;

public interface Messages {
	public static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	public static final int CONDUCTEUR = 0;
	public static final int VOYAGEUR = 1;
	public static final String AFFECTATION_ALL= "*";
	
	public static final String ID = "id";
	public static final String RECEPTION = "reception";
	public static final String DEBUT = "debut";
	public static final String FIN = "fin";
	public static final String TYPE = "type";
	public static final String AFFECTATION = "affect";
	public static final String MESSAGE = "message";
	public static final String ACQUITTEMENT = "acq";
	public static final String PRIORITE = "prio";
	public static final String JOURSEMAINE = "joursem";

	

	
	
	public static final int IMPORTANCE_NORMALE = 0;
	public static final int IMPORTANCE_AVERTISSEMENT = 4;
	public static final int IMPORTANCE_CRITIQUE = 9;
	

	public Collection getMessages(int destinataire, String affectation);

	public void acquittement(String msgId);

}
