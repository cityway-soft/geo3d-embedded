/* @flavorc
 *
 * S_DCNX.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'avm.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.elementary.protocol.avm.parser;

import java.io.IOException;
import java.io.InputStream;

import flavor.IBitstream;
import flavor.MapResult;

public class S_DCNX extends Message {

	public static final int MESSAGE_TYPE = 4;

	public S_DCNX() {
		super(MESSAGE_TYPE);
	}

	public String toString() {
		return "disconnect: " + super.toString();
	}

	public static class DefaultMessageFactory extends MessageFactory {

		protected Message get(InputStream in) throws Exception {
			InputBitstream bs = new InputBitstream(in);
			Message message = new S_DCNX();
			message.get(bs);

			return message;
		}
	}

	static {
		MessageFactory.factories.put(new Integer(MESSAGE_TYPE),
				new DefaultMessageFactory());
	}

	public int get(IBitstream _F_bs) throws IOException {
		int _F_ret = 0;
		MapResult _F_mr;
		_F_ret += super.get(_F_bs);
		return _F_ret;
	}

	public int put(IBitstream _F_bs) throws IOException {
		int _F_ret = 0;
		MapResult _F_mr;
		_F_ret += super.put(_F_bs);
		return _F_ret;
	}
}
