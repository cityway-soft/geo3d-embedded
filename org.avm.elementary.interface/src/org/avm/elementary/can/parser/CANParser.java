package org.avm.elementary.can.parser;

public interface CANParser {
	
	public static final byte CANMODE_NORMAL = 0;
	public static final byte CANMODE_REMOTE = 1;
	public static final byte CANMODE_EXTENDED = 2;
	
	public PGN makeObject(int key) throws Exception;
	
	public int getPgnId(byte[] buffer);
	
	public PGN get(byte[] buffer) throws Exception;

	public void put(PGN pgn, byte[] buffer) throws Exception;

}