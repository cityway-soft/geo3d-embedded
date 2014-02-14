package fr.cityway.avm.billettique.atoumod.model;

//@formatter:off
//-- extrait specs
//Offset 	Taille 		Champ 		Type 	Limite		Description
//0 		1 			Longueur	Entier 	2 à 128		Nombre total d’octets du message (champs Longueur et Type inclus) - Obligatoire
//1 		1 			Type 		Entier 				Type du message - Obligatoire
//2 		0 à 126 	Message 						Données associées au message - Facultatif
//@formatter:on

public abstract class Message {
	
	protected static final String SEP = "";

	int type;

	int longueur;

	public int getLongueur() {
		return longueur;
	}

	public void setLongueur(int longueur) {
		this.longueur = longueur;
	}

	public int getType() {
		return type;
	}

	protected void setType(int type) {
		this.type = type;
	}

	public abstract String getData();
	
	public abstract String toDebug();

	public String toString() {
		StringBuffer buf = new StringBuffer();

		// -- champ 0 : longueur
		String data = getData();
		int length = (data.length()/2) + 2;
		String l = toHex(length);
		buf.append(l);


		// -- champ 1 : type
		buf.append(toHex(type));

		// -- champ 2 : message
		buf.append(data);
		return buf.toString();
	}
	
	public static String toHex(int value){
		return toHex(value, 1);
	}
	
	public static String toHex(int value, int format){
		int size=format*2;
		String result=Integer.toHexString(value);
		StringBuffer buf = new StringBuffer(result);
		while(buf.length() < (size)){
			buf.insert(0, "0");
		}
		
		if (buf.length() > size){
			buf.substring(result.length()-size);
		}
		
		return buf.toString();
	}

}
