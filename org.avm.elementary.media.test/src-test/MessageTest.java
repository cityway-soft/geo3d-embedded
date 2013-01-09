import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.avm.business.protocol.management.Management;
import org.avm.business.protocol.phoebus.Course;
import org.avm.business.protocol.phoebus.Horaire;
import org.avm.business.protocol.phoebus.Horodate;
import org.avm.business.protocol.phoebus.Planification;


public class MessageTest extends TestCase {

	protected void setUp() throws Exception {
	
	}
	
	public static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	
	private void send(byte[] msg) throws UnknownHostException, IOException{
		Socket client;
	    client = new Socket("localhost", 8888);
	    //System.out.println("client connected ? " + client.isConnected());
	    OutputStream out = client.getOutputStream();
	    client.setSendBufferSize(msg.length);
	    System.out.println("size : " + msg.length);
	    out.write(msg); 
	    out.flush();
	    out.close();
	}
	
	public void testPlanificationNormal() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Planification message = new Planification();

		
		message.getEntete().setDate(new Horodate());
		message.setService(1234);
		message.setPlanification(1111);
		message.setChecksum(2222);
		Course[] courses = new Course[2];
		courses[0] = new Course();
		courses[0].setDepart(hs(14,52));
		Horaire[] horaires = new Horaire[3];
		
		Horaire horaire = new Horaire();
		horaire.setArrivee(hs(14,52));
		horaire.setPoint(47);
		horaires[0] = horaire;
		
		horaire = new Horaire();
		horaire.setArrivee(hs(14,58));
		horaire.setPoint(57);
		horaires[1] = horaire;
		
		horaire = new Horaire();
		horaire.setArrivee(hs(15,00));
		horaire.setPoint(34);
		horaires[2] = horaire;

		courses[0].setHoraires(horaires);

		courses[1] = new Course();
		courses[1].setDepart(hs(10,50));
		horaires = new Horaire[3];
		
		horaire = new Horaire();
		horaire.setArrivee(hs(10,50));
		horaire.setPoint(61);
		horaires[0] = horaire;
		
		horaire = new Horaire();
		horaire.setArrivee(hs(10,59));
		horaire.setPoint(29);
		horaires[1] = horaire;
		
		horaire = new Horaire();
		horaire.setArrivee(hs(11,03));
		horaire.setPoint(31);
		horaires[2] = horaire;
		courses[1].setHoraires(horaires);
		
		message.setCourses(courses);
		

		message.put(out);
		
		System.out.println("SEND : " + toHexaString(out.toByteArray()));
		
		send(out.toByteArray());
		
	}
	
	
	public void testPlanificationTSE() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Planification message = new Planification();

		
		message.getEntete().setDate(new Horodate());
		message.setService(457);
		message.setPlanification(2237);
		message.setChecksum(4458);
		int crsidu=0;
		Course[] courses = new Course[2];
		courses[0] = new Course();
		courses[0].setDepart(hs(16,52));
		courses[0].setCourse(crsidu++);

		courses[1] = new Course();
		courses[1].setCourse(crsidu++);
		courses[1].setDepart(hs(18,52));
		
		message.setCourses(courses);
		

		message.put(out);
		System.out.println("SEND MESSAGE: " + message );
		System.out.println("SEND : " + toHexaString(out.toByteArray()));
		
		send(out.toByteArray());
		
	}

	private int hs(int heure, int minute){
		return heure*3600+minute*60;
	}
	
	public void testManagementStatus() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Management message = new Management();

		
		message.setText("/management start vocal");
		
		message.put(out);
		
		System.out.println("SEND : " + toHexaString(out.toByteArray()));
		
		send(out.toByteArray());
		
	}
	
	public void testPlanificationServiceExistant() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Planification message = new Planification();

		
		message.getEntete().setDate(new Horodate());
		message.setService(9118);
		message.setPlanification(839);
		message.setChecksum(1010);
		Course[] courses = new Course[0];
		
		message.setCourses(courses);
		

		message.put(out);
		
		System.out.println("SEND : " + toHexaString(out.toByteArray()));
		
		send(out.toByteArray());
		
	}
	
	
	
}
