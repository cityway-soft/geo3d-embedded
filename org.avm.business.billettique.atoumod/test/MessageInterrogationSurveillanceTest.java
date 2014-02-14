import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import fr.cityway.avm.billettique.atoumod.model.MessageFactory;
import fr.cityway.avm.billettique.atoumod.model.MessageInterrogationSurveillance;

public class MessageInterrogationSurveillanceTest {

	private static final SimpleDateFormat DF = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");

	@Test
	public void testCreationParsing() {
		try {
			MessageInterrogationSurveillance m = new MessageInterrogationSurveillance();
			m.setDate(new Date());
			m.setDriver(111);
			m.setDuty(222);
			m.setJourney(333);
			m.setLine(444);
			m.setOperatingState(1);
			m.setStopPoint(666);
			m.setWayGo(false);

			System.out.println("message:" + m.toDebug());
			System.out.println("message:" + m.toString());

			Assert.assertEquals((20 + 2) * 2, m.toString().length());
		} catch (Exception e) {
			fail("Exception non prévue : " + e.getMessage());
		}
	}

	@Test
	public void testParsing() {
		try {
			String trame = "16017109010a1e2d0000006f00de0101bc014d029a02";
			MessageInterrogationSurveillance m = (MessageInterrogationSurveillance) MessageFactory
					.parse(trame);
			System.out.println("message:" + m.toDebug());
			System.out.println("message:" + m.toString());
		} catch (Exception e) {
			fail("Exception non prévue : " + e.getMessage());
		}
	}

	@Test
	public void testTrameFournieParACS() {
		try {
			// 1601710a090a1f040000007b0002021c20000200e202
			String trame = "1601710a090a1f040000007b0002021c20000200e202";
			MessageInterrogationSurveillance m = (MessageInterrogationSurveillance) MessageFactory
					.parse(trame);

			Assert.assertEquals(22, m.getLongueur());
			Assert.assertEquals(1, m.getType());

			Assert.assertEquals("2013-10-09 10:31:04", DF.format(m.getDate()));
			Assert.assertEquals(123, m.getDriver());
			Assert.assertEquals(2, m.getDuty());
			Assert.assertEquals(2,m.getOperatingState());
			Assert.assertEquals(7200,m.getLine());
			Assert.assertEquals(2,m.getJourney());
			Assert.assertEquals(226,m.getStopPoint());
			Assert.assertEquals(false,m.isWayGo());
			
			System.out.println("message:" + m.toDebug());
			System.out.println("message:" + m.toString());
		} catch (Exception e) {
			fail("Exception non prévue : " + e.getMessage());
		}
	}

	@Test
	public void testBoucle() {
		try {
			MessageInterrogationSurveillance m = new MessageInterrogationSurveillance();
			m.setDate(new Date());
			m.setDriver(999999);
			m.setDuty(65535);
			m.setJourney(65535);
			m.setLine(65535);
			m.setOperatingState(4);
			m.setStopPoint(65535);
			m.setWayGo(false);

			System.out.println("message 1:" + m.toDebug());
			String trame = m.toString();
			MessageInterrogationSurveillance m2 = (MessageInterrogationSurveillance) MessageFactory
					.parse(trame);

			System.out.println("message 2:" + m2.toDebug());
			System.out.println("message 2:" + m2.toString());

		} catch (Exception e) {
			fail("Exception non prévue : " + e.getMessage());
		}
	}

}
