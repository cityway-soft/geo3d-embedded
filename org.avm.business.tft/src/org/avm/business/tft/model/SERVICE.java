package org.avm.business.tft.model;

import java.util.Date;

public class SERVICE {

	private Date JEX_DATE = new Date();
	private int STATUT;
	private COURSE COURSE;
	private String[] MESSAGES;

	public SERVICE() {
		this(new Date(), 0);
	}

	public SERVICE(Date jex_date, int statut) {
		super();
		JEX_DATE = jex_date;
		STATUT = statut;
	}

	public Date getJEX_DATE() {
		return JEX_DATE;
	}

	public int getSTATUT() {
		return STATUT;
	}

	public COURSE getCOURSE() {
		return COURSE;
	}

	protected void setJEX_DATE(Date jex_date) {
		JEX_DATE = jex_date;
	}

	protected void setSTATUT(int statut) {
		STATUT = statut;
	}

	protected void setCOURSE(COURSE course) {
		COURSE = course;
	}

	public String[] getMESSAGES() {
		return MESSAGES;
	}

	public void setMESSAGES(String[] messages) {
		MESSAGES = messages;
	}
}