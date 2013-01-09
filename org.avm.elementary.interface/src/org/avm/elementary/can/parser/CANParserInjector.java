package org.avm.elementary.can.parser;

public interface CANParserInjector {
	public void setCANParser(CANParser parser);

	public void unsetCANParser(CANParser parser);
}
