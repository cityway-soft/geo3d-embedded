package fr.cityway.avm.billettique.atoumod.model;

public class ValidatorState extends Status {

	public ValidatorState(int s) {
		super(s);
	}

	public boolean isMemoryFull() {
		// b0 = 1 : Mémoire saturée
		return getBit(0);
	}

	public boolean isMemoryFull2() {
		// b1 = 1 : Mémoire épuisée
		return getBit(2);
	}

	public boolean isLinkOK() {
		// b2 = 1 : Communication HS
		return getBit(3) == false;
	}

	public boolean isCoupleurOK() {
		// b3 = 1 : Coupleur HS matériel
		return getBit(3) == false;
	}

	public boolean isSAMUnlockOK() {
		// b4 = 1 : Erreur déverrouillage SAM
		return getBit(4) == false;
	}

	public boolean isSAMUnlockCodeOK() {
		// b5 = 1 : Absence code déverrouillage SAM
		return getBit(5) == false;
	}

	// b6 et b7 : non utilisés et à 0

	public void setMemoryFull(boolean b) {
		// b0 = 1 : Mémoire saturée
		setBit(0, b);
	}

	public void setMemoryFull2(boolean b) {
		// b1 = 1 : Mémoire épuisée
		setBit(2, b);
	}

	public void setLinkOK(boolean b) {
		// b2 = 1 : Communication HS
		setBit(3, !b);
	}

	public void setCoupleurOK(boolean b) {
		// b3 = 1 : Coupleur HS matériel
		setBit(3, !b);
	}

	public void setSAMUnlockOK(boolean b) {
		// b4 = 1 : Erreur déverrouillage SAM
		setBit(4, !b);
	}

	public void setSAMUnlockCodeOK(boolean b) {
		// b5 = 1 : Absence code déverrouillage SAM
		setBit(5, !b);
	}

	public String toDebug() {
		StringBuffer buf = new StringBuffer();

		buf.append("isMemoryFull=" + isMemoryFull());
		buf.append(",isMemoryFull2=" + isMemoryFull2());
		buf.append(",isLinkOK=" + isLinkOK());
		buf.append(",isCoupleurOK=" + isCoupleurOK());
		buf.append(",isSAMUnlockOK=" + isSAMUnlockOK());
		buf.append(",isSAMUnlockCodeOK=" + isSAMUnlockCodeOK());
		return buf.toString();

	}

}