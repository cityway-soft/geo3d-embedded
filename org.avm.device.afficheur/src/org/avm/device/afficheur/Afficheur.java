package org.avm.device.afficheur;

public interface Afficheur {

	public void print(String message);

	public int getStatus() throws Exception;
 
}
