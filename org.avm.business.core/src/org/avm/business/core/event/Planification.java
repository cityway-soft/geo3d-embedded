package org.avm.business.core.event;

import java.util.Calendar;
import java.util.Date;


public class Planification implements Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int 	_id;
	private int 	_checksum;
	private int 	_version;
	private int 	_service;
	private Date 	_date;
	private int 	_matricule;
	private boolean _confirmed=true;


	public Planification(int agt_idu, int pla_id, int checksum, int version,
			int service, Date plaDate, int nbcourses) {
		_matricule = agt_idu;
		_id = pla_id;
		_checksum = checksum;
		_version = version;
		_service = service;
		_date = plaDate;
	}
	
	public void confirm(boolean b){
		_confirmed = b;
	}
	
	public boolean isConfirmed(){
		return _confirmed;
	}
	
	public int getMatricule(){
		return _matricule;
	}
	
	public boolean equals(Planification p){
		return (p.getChecksum() == getChecksum() && p.getId() == getId() && p.getMatricule() == getMatricule());
	}


	public boolean isCorrect() {
		Calendar calReceive = Calendar.getInstance();

		
		if (_date == null) {
			return false;
		}

		boolean valid = (_id != 0);
		if (valid) {
			calReceive.setTime(_date);
			Calendar calCurrentTime = Calendar.getInstance();
			// _log.debug("cal receive=" + calReceive.getTime());
			// _log.debug("cal current=" + calCurrentTime.getTime());
			return (calReceive.get(Calendar.YEAR) == calCurrentTime
					.get(Calendar.YEAR))
					&& (calReceive.get(Calendar.MONTH) == calCurrentTime
							.get(Calendar.MONTH))
					&& (calReceive.get(Calendar.DAY_OF_MONTH) == calCurrentTime
							.get(Calendar.DAY_OF_MONTH));
		}
		return false;
	}
	
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(isCorrect() ? "valide": "NON VALIDE");
		buf.append(",");
		buf.append(isConfirmed() ? "confirme": "en attente confirmation");
		buf.append(", id=");
		buf.append(_id);
		buf.append(", date reception=");
		buf.append(_date);
		buf.append(", checksum=");
		buf.append(_checksum);
		buf.append(", version=");
		buf.append(_version);
		buf.append(", service=");
		buf.append(_service);
		buf.append(", ");
		return buf.toString();
	}

	public int getId() {
		return _id;
	}

	public int getChecksum() {
		return _checksum;
	}

	public int getServiceIdu() {
		return _service;
	}

	public Date getDate() {
		return _date;
	}

	public int getVersion() {
		return _version;
	}

}
