package fr.cityway.avm.billettique.atoumod.model;

import java.text.ParseException;

//@formatter:off
//-- extrait specs
//Offset 	Taille 		Champ 			Type 	Limite			Description
//2 		1			ETAT_BILL 		chBit					Etat système billettique 
//																		b0 = 1 : Aucune table valide
//																		b1 = 1 : Mémoire maître saturée
//																		b2 = 1 : Mémoire maître épuisée
//																		b3 = 1 : Blocage valideurs
//																		b4 = 1 : Tables téléchargées incohérentes
//																		b5 = 1 : Au moins un valideur en défaut
//																		b6 et b7 : non utilisés et à 0
//3 		1 			NB_VALIDEUR 	Entier	[1...14]		Nombre de valideur
//4 		1 			ETAT_VALID_1 	chBit					Etat valideur 1
//																		b0 = 1 : Mémoire saturée
//																		b1 = 1 : Mémoire épuisée
//																		b2 = 1 : Communication HS
//																		b3 = 1 : Coupleur HS matériel
//																		b4 = 1 : Erreur déverrouillage SAM
//																		b5 = 1 : Absence code déverrouillage SAM
//																		b6 et b7 : non utilisés et à 0
//5 		1			ETAT_VALID_2 	chBit 					Etat valideur 2 – Voir ci-avant
//6 		1 			ETAT_VALID_3 	chBit 					Etat valideur 3 – Voir ci-avant
//7 		1 			ETAT_VALID_n 	chBit 					Etat valideur n – Voir ci-avant
//@formatter:on

public class MessageReponseSurveillance extends Message {
	public static final int ID = 101;

	private static String fieldsName[] = { "ID", "LONGUEUR", "ETAT_BILL",
			"NB_VALIDEUR", "ETAT_VALID_1", "ETAT_VALID_2", "ETAT_VALID_3",
			"ETAT_VALID_4", "ETAT_VALID_5", "ETAT_VALID_6", "ETAT_VALID_7",
			"ETAT_VALID_8", "ETAT_VALID_9", "ETAT_VALID_10", "ETAT_VALID_11",
			"ETAT_VALID_12", "ETAT_VALID_13", "ETAT_VALID_14" };
	private static int fieldsSize[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1 };

	TicketingSystemState billettiqueState;
//	int validatorNumber;
	ValidatorState[] validatorState;

	public TicketingSystemState getBillettiqueState() {
		return billettiqueState;
	}

	public void setBillettiqueState(TicketingSystemState billettiqueState) {
		this.billettiqueState = billettiqueState;
	}

//	public int getValidatorNumber() {
//		return validatorNumber;
//	}
//
//	public void setValidatorNumber(int validatorNumber) {
//		this.validatorNumber = validatorNumber;
//	}

	public ValidatorState[] getValidatorState() {
		return validatorState;
	}

	public void setValidatorState(ValidatorState[] validatorState) {
		this.validatorState = validatorState;
	}

	
	public MessageReponseSurveillance() {
		type = ID;

	}
	
	protected static MessageReponseSurveillance create(String str)
			throws Exception {
		String string = str.trim();

		FrameReader reader = new FrameReader(string, fieldsName, fieldsSize);

		MessageReponseSurveillance msg = new MessageReponseSurveillance();

		String id = reader.getNextField();
		msg.setType(Integer.parseInt(id,16));
		msg.setLongueur(Integer.parseInt(reader.getNextField(), 16));
		msg.setBillettiqueState(new TicketingSystemState(Integer.parseInt(reader
				.getNextField(), 16)));
		int number = Integer.parseInt(reader.getNextField(),16);
		//msg.setValidatorNumber(number);
		ValidatorState[] validatorStates = new ValidatorState[number];
		for (int i = 0; i < number; i++) {
			validatorStates[i] = new ValidatorState(Integer.parseInt(reader
					.getNextField(),16));
		}
		msg.setValidatorState(validatorStates);
		return msg;
	}

	public String getData() {
		StringBuffer msg = new StringBuffer();
		msg.append(getBillettiqueState());
		ValidatorState[] validatorsState = getValidatorState();
		if (validatorsState != null){
			msg.append(toHex(validatorsState.length));
			for (int i = 0; i < validatorsState.length; i++) {
				msg.append(getValidatorState()[i]);
			}
		}
		else{
			//TODO : pas possible !
			msg.append(0);
		}
		return msg.toString();
	}

	public static class DefaultMessageFactory extends MessageFactory {
		protected Message create(String frame) throws Exception {
			Message message = MessageReponseSurveillance.create(frame);
			return message;
		}

		protected Message create() throws ParseException {
			return new MessageReponseSurveillance();
		}
	}

	static {
		MessageFactory.factories.put(new Integer(ID),
				new DefaultMessageFactory());
	}

	public String toDebug() {
		StringBuffer buf = new StringBuffer();
		buf.append(billettiqueState.toDebug());
		
		ValidatorState[] validatorsState = getValidatorState();
		if (validatorsState != null){
			buf.append(",nbvalideurs="+validatorsState.length);
			buf.append(", [");
			for (int i = 0; i < validatorsState.length; i++) {
				buf.append("valideur["+i+"]=");
				buf.append(getValidatorState()[i].toDebug());
				buf.append(",");
			}
			buf.append("]");
		}
		else{
			buf.append("nbvalideurs=0!");
		}
		return buf.toString();
	}
	
//	@Override
//	public String toString() {
//		StringBuffer buf = new StringBuffer();
//		buf.append(billettiqueState.toString());
//		
//		ValidatorState[] validatorsState = getValidatorState();
//		if (validatorsState != null){
//			buf.append(toHex(validatorsState.length));
//			for (int i = 0; i < validatorsState.length; i++) {
//				buf.append(getValidatorState()[i].toString());
//			}
//		}
//		else{
//			buf.append("00");
//		}
//		return buf.toString();
//	}


}
