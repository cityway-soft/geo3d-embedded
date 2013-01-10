package org.avm.device.generic.girouette.matis;


public class ProtocolHelper  {
	/*
	 * 
	 * 	Extrait specs
	  	 0: En-tête de message ( : )
		 1: Toujours 0x10
		 2: Toujours 0x0000
		 3: Toujours 0x00
		 4: Toujours 0x0000
		 5: Code de fonction « Envoi du code Message de Destination» = 0x03
		 6: Toujours 0x0A
		 7: Code ASCII pour les milliers
		 8: Code ASCII pour les centaines
		 9: Code ASCII pour les dizaines
		10: Code ASCII pour les unités Code Destination 3 pour l’exemple ci-dessus
		11: Toujours 0x0000000000000000
		12: Checksum (INTEL HEX)
		13: Fin de message (toujours CR LF)
	 */


	private static final String STX=":";
	private static final String P1="10";
	private static final String P2="0000";
	private static final String P3="00";
	private static final String P4="0000";
	private static final String DEFAULT_FUNCTION="03";
	private static final String P6="0A";
	private static final String DEFAULT_CODE="20202020";
	private static final String P11="0000000000000000";
	private static final String CS="00";
	private static final String ETX="\r\n";


	protected static byte[] generate(String destination) {
		StringBuffer buf = new StringBuffer();

		StringBuffer code= new StringBuffer();
		for (int i=0; i< destination.length(); i++){
			code.append("3");
			code.append(destination.charAt(i));
		}
		for (int i=0; code.length()<8; i++){
			code.insert(0, "20");
		}

		buf.append(P1);
		buf.append(P2);
		buf.append(P3);
		buf.append(P4);
		buf.append(DEFAULT_FUNCTION);
		buf.append(P6);
		buf.append(code.toString());
		buf.append(P11);
		
		String cs = checksum(buf.toString().getBytes());
		
		buf.insert(0,STX);
		buf.append(cs);
		buf.append(ETX);
		
		return buf.toString().getBytes();
	}
	
	protected static String checksum(byte[] buffer){
		StringBuffer octet = new StringBuffer(2);
		int sum=0;
		int i=0;
		int temp;

		while(i<buffer.length){
			octet.append((char)buffer[i]);
			octet.append((char)buffer[i+1]);
			int val=Integer.parseInt(octet.toString(), 16);
			sum+=val;
			temp =  0x100 - (sum & 0xFF);
			octet.delete(0, 2);
			i+=2;
		}
		int val;
		val=(sum & 0xFF);
		val = 0x100 - val ;
		String res = Integer.toHexString(val).toUpperCase();
		if (res.length()<2){
			res="0"+res;
		}
		else if (res.length()>2){
			res=res.substring(1);
		}
		return res;
	}
	
	

}
