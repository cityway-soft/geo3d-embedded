package org.avm.business.tft.model;


public class MESSAGE {

	private String texte;
	private int priorite;

	public MESSAGE() {
		this("", 0);
	}

	public MESSAGE(String text, int priorite) {
		this.texte = text;
		this.priorite = priorite;
	}

	public String getTexte() {
		return texte;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public int getPriorite() {
		return priorite;
	}

	public void setPriorite(int priorite) {
		this.priorite = priorite;
	}


}