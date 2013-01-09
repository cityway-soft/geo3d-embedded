package org.avm.device.can;

import org.avm.elementary.can.parser.PGN;

public interface Can {
	
	public static final int FRAME_LEN = 14;

	public void send(PGN pgn) throws Exception;

}
