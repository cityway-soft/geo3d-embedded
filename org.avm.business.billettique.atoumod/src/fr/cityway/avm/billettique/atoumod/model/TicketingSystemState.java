package fr.cityway.avm.billettique.atoumod.model;

public class TicketingSystemState extends Status {
	public static final int SERVICE_FERME = 0;

	public static final int SERVICE_OUVERT_COURSE_FERMEE = 1;

	public static final int SERVICE_OUVERT_COURSE_OUVERTE = 2;
	
	public static final String[] STATE={"Service Fermé", "Service ouvert, course fermée", "Service ouvert, course ouverte"};

	public TicketingSystemState(int s) {
		super(s);
	}

	public boolean isTablesValid() {
		// b0 = 1 : Aucune table valide
		return getBit(0) == false;
	}

	public boolean isMemoryFull() {
		// b1 = 1 : Mémoire maître saturée
		return getBit(1);
	}

	public boolean isMemoryFull2() {
		// b2 = 1 : Mémoire maître épuisée
		return getBit(2);
	}

	public boolean isValidatorsBlocked() {
		// b3 = 1 : Blocage valideurs
		return getBit(3);
	}

	public boolean isDownloadedTableOK() {
		// b4 = 1 : Tables téléchargées incohérentes
		return getBit(4) == false;
	}

	public boolean isAllValidatorsOK() {
		// b5 = 1 : Au moins un valideur en défaut
		return getBit(5);
	}

	// b6 et b7 : non utilisés et à 0

	public void setTablesValid(boolean b) {
		// b0 = 1 : Aucune table valide
		setBit(0, !b);
	}

	public void setMemoryFull(boolean b) {
		// b1 = 1 : Mémoire maître saturée
		setBit(1, b);
	}

	public void setMemoryFull2(boolean b) {
		// b2 = 1 : Mémoire maître épuisée
		setBit(2, b);
	}

	public void setValidatorsBlocked(boolean b) {
		// b3 = 1 : Blocage valideurs
		setBit(3, b);
	}

	public void setDownloadedTableOK(boolean b) {
		// b4 = 1 : Tables téléchargées incohérentes
		setBit(4, !b);
		;
	}

	public void setAllValidatorsOK(boolean b) {
		// b5 = 1 : Au moins un valideur en défaut
		setBit(5, !b);
	}

	public String toDebug() {
		StringBuffer buf = new StringBuffer();

		buf.append("isTablesValid=" + isTablesValid());
		buf.append(",isMemoryFull=" + isMemoryFull());
		buf.append(",isMemoryFull2=" + isMemoryFull2());
		buf.append(",isValidatorsBlocked=" + isValidatorsBlocked());
		buf.append(",isDownloadedTableOK=" + isDownloadedTableOK());
		buf.append(",isAllValidatorsOK=" + isAllValidatorsOK());
		return buf.toString();

	}
	
	public static String getOperatingState(int operatingState) {
		return STATE[operatingState];
	}

}