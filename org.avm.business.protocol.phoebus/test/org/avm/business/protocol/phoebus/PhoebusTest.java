package org.avm.business.protocol.phoebus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class PhoebusTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PhoebusTest.class);
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

	public static byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));

		}
		return data;
	}

	private byte[] test(byte[] buffer) throws Exception {

		try {

			// bin -> obj
			ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
			Message bMessage = MessageFactory.parseBitstream(bin);
			System.out.println("\n bin -> obj " + bMessage);

			// obj -> xml
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bMessage.marshal(out);
			System.out
					.println("\n obj -> xml " + new String(out.toByteArray()));

			// xml -> obj
			ByteArrayInputStream xml = new ByteArrayInputStream(out
					.toByteArray());
			Message xMessage = MessageFactory.parseXmlstream(xml);
			System.out.println("\n xml -> obj " + xMessage);

			// obj -> bin
			out.reset();
			xMessage.put(out);
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}

	public void testAlerte() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Alerte message = new Alerte();
		message.getEntete().setDate(new Horodate());
		message.getEntete().setPosition(new Position());
		message.getEntete().setService(new Service());
		message.getEntete().setAnomalie(new Anomalie());
		message.getEntete().setOptions(new Options());
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testPriseEnCharge() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PriseEnCharge message = new PriseEnCharge();
		message.getEntete().setDate(new Horodate());
		message.setTel("0153405244");
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testClotureAlerte() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ClotureAlerte message = new ClotureAlerte();
		message.getEntete().setDate(new Horodate());
		message.getEntete().setPosition(new Position());
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testDemandeStatut() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DemandeStatut message = new DemandeStatut();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testReponseStatut() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ReponseStatut message = new ReponseStatut();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testAvanceRetard() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AvanceRetard message = new AvanceRetard();
		message.getEntete().getChamps().setDate(1);
		message.getEntete().setDate(new Horodate());
		message.getEntete().getChamps().setPosition(1);
		message.getEntete().setPosition(new Position());
		message.getEntete().getChamps().setService(1);
		message.getEntete().setService(new Service());
		message.getEntete().getChamps().setProgression(1);
		message.getEntete().setProgression(new Progression());
		message.getEntete().getProgression().setRetard(-189);
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testPassageArret() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PassageArret message = new PassageArret();
		message.getEntete().getChamps().setDate(1);
		message.getEntete().setDate(new Horodate());
		message.getEntete().getChamps().setPosition(1);
		message.getEntete().setPosition(new Position());
		message.getEntete().getChamps().setService(1);
		message.getEntete().setService(new Service());
		message.getEntete().getChamps().setProgression(1);
		message.getEntete().setProgression(new Progression());
		message.getEntete().getProgression().setRetard(-189);
		message.setAttente(110);
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testMessageText() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessageText message = new MessageText();
		Entete entete = message.getEntete();
		message.setMessage("Méssage de Test");
		message.setType(1);
		message.setAffectation(new IntArray(new int[]{1,3,5}));
		Reference reference = new Reference(2,1,0);
		entete.setReference(reference);
		entete.getChamps().setReference(1);
		entete.setOptions(new Options(9,0));
		entete.getChamps().setOptions(1);
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testPrisePoste() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrisePoste message = new PrisePoste();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testFinPrisePoste() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FinPrisePoste message = new FinPrisePoste();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testPriseService() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PriseService message = new PriseService();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testFinPriseService() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FinPriseService message = new FinPriseService();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testDepartCourse() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DepartCourse message = new DepartCourse();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testFinCourse() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FinCourse message = new FinCourse();
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testPlanification() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Planification message = new Planification();

		message.getEntete().setDate(new Horodate());

		Course[] courses = new Course[2];
		courses[0] = new Course();
		Horaire[] horaires = new Horaire[3];
		horaires[0] = new Horaire();
		horaires[1] = new Horaire();
		horaires[2] = new Horaire();

		courses[0].setHoraires(horaires);

		courses[1] = new Course();
		message.setCourses(courses);

		Arret[] points = new Arret[2];
		points[0] = new Arret(1,1,2,new CString("NOM"),new CString("CODE"));
		points[1] = new Arret(2,1,2,new CString("NOM"),new CString("CODE"));
		
		message.setPoints(points);
		
		message.put(out);
		out.close();

		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}

	public void testDeviation() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Deviation message = new Deviation();
		message.getEntete().getChamps().setDate(1);
		message.getEntete().setDate(new Horodate());
		message.getEntete().getChamps().setPosition(1);
		message.getEntete().setPosition(new Position());
		message.getEntete().getChamps().setService(1);
		message.getEntete().setService(new Service());
		message.getEntete().getChamps().setProgression(1);
		message.getEntete().setProgression(new Progression());
		message.getEntete().getProgression().setRetard(-189);
		message.getEntete().getProgression().setDeviation(1);
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}
/*
	public void testAnnulation() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Annulation message = new Annulation();
		Entete entete = message.getEntete();
		entete.setId(3);
		entete.getChamps().setDate(1);
		entete.setDate(new Horodate());
		Identite source = entete.getSource();
		source.setExploitant(1);
		source.setId(1);
		source.setType(1);
		Identite destination = entete.getDestination();
		destination.setExploitant(1);
		destination.setType(0);
		destination.setId(116);
		Reference reference = new Reference(2);
		entete.setReference(reference);
		entete.getChamps().setReference(1);
		
		message.put(out);
		out.close();
		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}
*/
}
