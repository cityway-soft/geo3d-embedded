package org.avm.device.generic.girouette.hanover.protocol;

public class SOCRIE_B extends GTMH_1 {

	static {
		GirouetteProtocolFactory.factory.put(SOCRIE_B.class, new SOCRIE_B());
	}

	public SOCRIE_B() {

	}

	protected byte[] toHexaBytes(byte data) {
		byte[] result = new byte[2];
		int rValue = data & 0x0F;
		int lValue = (data >> 4) & 0x0F;

		result[0] = (byte) ((lValue > 9) ? lValue + 0x37 : lValue + 0x30);
		result[1] = (byte) ((rValue > 9) ? rValue + 0x37 : rValue + 0x30);

		return result;
	}

}
