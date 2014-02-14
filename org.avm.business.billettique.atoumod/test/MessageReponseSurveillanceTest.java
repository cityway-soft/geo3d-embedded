import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;

import fr.cityway.avm.billettique.atoumod.model.MessageFactory;
import fr.cityway.avm.billettique.atoumod.model.MessageInterrogationSurveillance;
import fr.cityway.avm.billettique.atoumod.model.MessageReponseSurveillance;
import fr.cityway.avm.billettique.atoumod.model.TicketingSystemState;
import fr.cityway.avm.billettique.atoumod.model.ValidatorState;

public class MessageReponseSurveillanceTest {

	@Test
	public void testCreation() {

		try {

			MessageReponseSurveillance m = new MessageReponseSurveillance();
			TicketingSystemState billettiqueState = new TicketingSystemState(0);
			billettiqueState.setAllValidatorsOK(true);
			m.setBillettiqueState(billettiqueState);

			ValidatorState[] v = new ValidatorState[2];
			v[0] = new ValidatorState(0);
			v[0].setLinkOK(false);
			v[0].setMemoryFull(true);

			v[1] = new ValidatorState(0);
			v[1].setLinkOK(false);

			m.setValidatorState(v);

			System.out.println("message rep:" + m.toDebug());
			System.out.println("message rep:" + m.toString());

			Assert.assertEquals((2 + 2 + v.length) * 2, m.toString().length());

		} catch (Exception e) {
			fail("Exception non prévue : " + e.getMessage());
		}
	}

	//0x05 0x65 0x20 0x01 0x04
	@Test
	public void testTrameFournieParACS() {

		try {
			String trame="0565200104";
			MessageReponseSurveillance m = (MessageReponseSurveillance) MessageFactory.parse(trame);
			
			Assert.assertEquals(5,m.getLongueur());
			Assert.assertEquals(101,m.getType());
			Assert.assertEquals(false,m.getBillettiqueState().isTablesValid());
			Assert.assertEquals(true,m.getBillettiqueState().isMemoryFull());
			Assert.assertEquals(true,m.getBillettiqueState().isMemoryFull2());
			Assert.assertEquals(true,m.getBillettiqueState().isValidatorsBlocked());
			Assert.assertEquals(false,m.getBillettiqueState().isAllValidatorsOK());

			
			
			Assert.assertEquals(true,m.getValidatorState()[0].isMemoryFull());
			Assert.assertEquals(true,m.getValidatorState()[0].isMemoryFull2());
			Assert.assertEquals(false,m.getValidatorState()[0].isLinkOK());
			Assert.assertEquals(false,m.getValidatorState()[0].isSAMUnlockOK());
			Assert.assertEquals(false,m.getValidatorState()[0].isSAMUnlockCodeOK());
			Assert.assertEquals(false,m.getValidatorState()[0].isCoupleurOK());

			System.out.println("message rep:" + m.toDebug());

		} catch (Exception e) {
			fail("Exception non prévue : " + e.getMessage());
		}
	}
	
	@Test
	public void testBoucle() {

		try {

			MessageReponseSurveillance m = new MessageReponseSurveillance();
			TicketingSystemState billettiqueState = new TicketingSystemState(0);
			billettiqueState.setAllValidatorsOK(true);
			m.setBillettiqueState(billettiqueState);

			ValidatorState[] v = new ValidatorState[2];
			v[0] = new ValidatorState(0);
			v[0].setLinkOK(false);
			v[0].setMemoryFull(true);

			v[1] = new ValidatorState(0);
			v[1].setLinkOK(false);

			m.setValidatorState(v);

			String trame = m.toString();
			MessageReponseSurveillance m2 = (MessageReponseSurveillance) MessageFactory
					.parse(trame);
			System.out.println("message rep 2:" + m2.toDebug());
			System.out.println("message rep 2 :" + m2.toString());

			Assert.assertEquals(trame, m2.toString());

		} catch (Exception e) {
			fail("Exception non prévue : " + e.getMessage());
		}
	}

}
