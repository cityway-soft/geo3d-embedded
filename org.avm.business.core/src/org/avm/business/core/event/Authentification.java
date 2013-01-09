package org.avm.business.core.event;



public class Authentification implements Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int _matricule; // in

	private String _nom; // out



	private String _prenom; // out

	private int _matriculeRemplace = 0;

	private int _vehiculeRemplace;

	private transient boolean _isPrisePoste;

	private transient boolean _demoRole;

	private transient boolean _georefRole;

	
	public boolean isDemoRole() {
		return _demoRole;
	}

	public void setDemoRole(boolean role) {
		_demoRole = role;
	}
	
	public boolean isGeorefRole() {
		return _georefRole;
	}

	public void setGeorefRole(boolean role) {
		_georefRole = role;
	}

	public Authentification(int matricule, String nomConducteur,
			String prenomConducteur) {
		_matricule = matricule;
		_nom = nomConducteur;
		_prenom = prenomConducteur;
		_isPrisePoste = false;
	}

	public void setMatriculeRemplace(int replace) {
		_matriculeRemplace = replace;
	}

	public void setVehiculeRemplace(int replace) {
		_vehiculeRemplace = replace;
	}

	public boolean isPrisePoste() {
		return _isPrisePoste;
	}

	public void setPrisePosteDone(boolean realise) {
		_isPrisePoste = realise;
	}

	public int getMatriculeRemplace() {
		return _matriculeRemplace;
	}

	public int getVehiculeRemplace() {
		return _vehiculeRemplace;
	}

	public int getMatricule() {
		return _matricule;
	}

	public String getNom() {
		return _nom;
	}

	public String getPrenom() {
		return _prenom;
	}

	public String toString() {
		if (isPrisePoste()) {
			StringBuffer buf = new StringBuffer();
			buf.append(" (");
			buf.append(_prenom);
			buf.append(" ");
			buf.append(_nom);
			buf.append(") ");
			buf.append( ((_matriculeRemplace == 0) ? ""	: " remplace matricule "
											+ _matriculeRemplace));
			buf.append( ((_vehiculeRemplace == 0) ? ""
							: " au lieu du vehicule " + _vehiculeRemplace));
			buf.append(" [");
			buf.append(" georef=");
			buf.append(_georefRole);
			buf.append(", demo=");
			buf.append(_demoRole);
			buf.append(" ]");
			return buf.toString();
		} else {
			return "Pas de prise de poste effectuée (matricule actuel=" + _matricule +")";
		}
	}

	public boolean equals(Authentification aut) {
		return (aut._matricule == this._matricule);
	}

}
