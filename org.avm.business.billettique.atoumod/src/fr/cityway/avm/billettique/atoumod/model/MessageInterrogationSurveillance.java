package fr.cityway.avm.billettique.atoumod.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//@formatter:off
//-- extrait specs
//Offset 	Taille 		Champ 		Type 	Limite			Description
//2 		1			ANNEE 		Entier 	[113...199] 	Année courante par rapport à 1900
//3 		1			MOIS 		Entier 	[1...12]		Mois courant
//4 		1 			JOUR		Entier 	[1...31] 		Jour courant
//5 		1 			HEURE 		Entier 	[0...23] 		Heure courante
//6 		1 			MINUTE 		Entier 	[0...59] 		Minute courante
//7 		1 			SECONDE 	Entier 	[0...59] 		Seconde courante
//8 		4 			NUM_COND 	Entier 	[0...999999] 	Matricule conducteur
//12 		2 			NUM_SERV 	Entier 	[0] Non applicable
//14 		1 			ETAT_EXPL 	Entier 	[0...4] 0 : hors service (service fermé), 1 : en HLP (service ouvert/course fermé), 2 : en service (service ouvert/course ouverte), 3 : passage en service impossible, 4 : véhicule non localisé
//15 		2 			NUM_LIGNE 	Entier 	[0...65535] Numéro de ligne
//17 		2 			NUM_COURSE 	Entier 	[0...65535] Numéro de course
//19 		2 			NUM_ARRET 	Entier 	[0...65535] Numéro d’arrêt
//21 		1 			SENS 		Entier 	[1,2] 1 : Aller, 2 : Retour
//@formatter:on

public class MessageInterrogationSurveillance extends Message {
	public static final int ID = 1;
	private static final SimpleDateFormat DF = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");
	// @formatter:off
	private static String fieldsName[] = {"ID", "LENGTH", "ANNEE", "MOIS", "JOUR", "HEURE",
			"MINUTE", "SECONDE", "NUM_COND", "NUM_SERV", "ETAT_EXPL",
			"NUM_LIGNE", "NUM_COURSE", "NUM_ARRET", "SENS" };
	private static int fieldsSize[] = { 1, 1, 1, 1, 1, 1, 1 ,1, 4, 2, 1, 2, 2, 2, 1 };
	// @formatter:on

	Date date;// annee, mois, jour, heure, minute, seconde
	int driver;
	int duty;
	int operatingState;
	int line;
	int journey;
	int stopPoint;
	boolean wayGo; // aller/ retour

	public MessageInterrogationSurveillance() {
		type = ID;
		date = new Date();

	}

	protected static MessageInterrogationSurveillance create(String string)
			throws Exception {
		FrameReader reader = new FrameReader(string, fieldsName, fieldsSize);

		MessageInterrogationSurveillance msg = new MessageInterrogationSurveillance();


		// -- longueur
		String result=reader.getNextField();
		int value = Integer.parseInt(result,16);
		msg.setLongueur(value);
		
		// -- type
		String id = reader.getNextField();
		msg.setType(Integer.parseInt(id,16));

		// -- annee,mois,jour,heure,minute,seconde
		String sAnnee = reader.getNextField();
		String sMois = reader.getNextField();
		String sJour = reader.getNextField();
		String sHeure = reader.getNextField();
		String sMinute = reader.getNextField();
		String sSeconde = reader.getNextField();
		Calendar cal = Calendar.getInstance();
		
		int annee = Integer.parseInt(sAnnee,16)+1900;
		int mois = Integer.parseInt(sMois,16)-1;
		int jour =  Integer.parseInt(sJour,16);
		int heure =  Integer.parseInt(sHeure,16);
		int minute = Integer.parseInt(sMinute,16);
		int seconde = Integer.parseInt(sSeconde,16);
		cal.set(Calendar.YEAR, annee);
		cal.set(Calendar.MONTH, mois);
		cal.set(Calendar.DAY_OF_MONTH, jour);
		cal.set(Calendar.HOUR_OF_DAY,heure);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, seconde);
		msg.setDate(cal.getTime());

		// -- conducteur
		msg.setDriver(Integer.parseInt(reader.getNextField(), 16));

		// -- service
		msg.setDuty(Integer.parseInt(reader.getNextField(), 16 ));

		// -- etat d'explotation
		msg.setOperatingState(Integer.parseInt(reader.getNextField(), 16));

		// -- ligne
		msg.setLine(Integer.parseInt(reader.getNextField(), 16));

		// -- course
		msg.setJourney(Integer.parseInt(reader.getNextField(), 16));

		// -- arret
		msg.setStopPoint(Integer.parseInt(reader.getNextField(), 16));

		// -- sens
		msg.setWayGo(Integer.parseInt(reader.getNextField(), 16) == 1);

		return msg;
	}

	public String getData() {
		StringBuffer msg = new StringBuffer();

		// --date
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate());
		int annee = cal.get(Calendar.YEAR) - 1900;
		int mois = cal.get(Calendar.MONTH)+1;
		int jour = cal.get(Calendar.DAY_OF_MONTH);
		int heure = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int seconde = cal.get(Calendar.SECOND);


		msg.append(toHex(annee));

		msg.append(toHex(mois));


		msg.append(toHex(jour));


		msg.append(toHex(heure));


		msg.append(toHex(minute));

		msg.append(toHex(seconde));



		// -- conducteur
		msg.append(toHex(getDriver(), 4));

		// -- service
		msg.append(toHex(getDuty(), 2));

		// -- etat d'exploitation
		msg.append(toHex(getOperatingState()));

		// -- ligne
		msg.append(toHex(getLine(), 2));

		// -- course
		msg.append(toHex(getJourney(),2));

		// -- arret
		msg.append(toHex(getStopPoint(),2));

		// -- sens
		msg.append(toHex(isWayGo() ? 1 : 2));

		return msg.toString();
	}

	public static MessageInterrogationSurveillance create() {
		return new MessageInterrogationSurveillance();
	}

	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDriver() {
		return driver;
	}

	public void setDriver(int driver) {
		this.driver = driver;
	}

	public int getDuty() {
		return duty;
	}

	public void setDuty(int duty) {
		this.duty = duty;
	}

	public int getOperatingState() {
		return operatingState;
	}

	public void setOperatingState(int operatingState) {
		this.operatingState = operatingState;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getJourney() {
		return journey;
	}

	public void setJourney(int journey) {
		this.journey = journey;
	}

	public int getStopPoint() {
		return stopPoint;
	}

	public void setStopPoint(int stopPoint) {
		this.stopPoint = stopPoint;
	}

	public boolean isWayGo() {
		return wayGo;
	}

	public void setWayGo(boolean way) {
		this.wayGo = way;
	}

	public static class DefaultMessageFactory extends MessageFactory {
		protected Message create(String frame) throws Exception {
			Message message = MessageInterrogationSurveillance.create(frame);
			return message;
		}

		protected Message create() throws ParseException {
			return new MessageInterrogationSurveillance();
		}
	}

	static {
		MessageFactory.factories.put(new Integer(ID),
				new DefaultMessageFactory());
	}

	public String toDebug() {
		StringBuffer buf = new StringBuffer();
		buf.append("date="+DF.format(date));// annee, mois, jour, heure, minute, seconde
		buf.append(", driver="+driver);
		buf.append(", duty="+ duty);
		buf.append(", operatingState="+ operatingState);
		buf.append(", line="+ line);
		buf.append(", journey="+ journey);
		buf.append(", stopPoint="+ stopPoint);
		buf.append(", way="+ wayGo);
		return buf.toString();
	}

}
