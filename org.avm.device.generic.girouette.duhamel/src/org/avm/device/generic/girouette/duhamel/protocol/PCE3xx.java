
package org.avm.device.generic.girouette.duhamel.protocol;

import java.text.MessageFormat;

import org.avm.device.girouette.GirouetteProtocol;

public class PCE3xx extends GirouetteProtocol {
	private static final byte STX = 0x02;

	private static final byte ETX = 0x03;

	private static final byte SEP = ':';

	private static final byte NULL = '0';

	private byte[] DEFAULT_VALUE = { STX, SEP, NULL, NULL, NULL, NULL, NULL,
			NULL, ETX };

	private byte[] _buffer = DEFAULT_VALUE;


	static {
		GirouetteProtocolFactory.factory.put(PCE3xx.class, new PCE3xx());
	}

	public PCE3xx() {

	}

	public byte[] generateDestination(final String code) {
		try {
			Integer value = new Integer(Integer.parseInt(code));
			Object[] tab = { value };
			byte[] buffer = MessageFormat.format("{0,number,000000}", tab)
					.getBytes();

			for (int i = 0; i < buffer.length; i++) {
				_buffer[i + 2] = buffer[i];
			}
		} catch (Throwable e) {
			_buffer = DEFAULT_VALUE;
		}
		return _buffer;
	}

}
