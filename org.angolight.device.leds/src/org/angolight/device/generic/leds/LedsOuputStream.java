package org.angolight.device.generic.leds;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LedsOuputStream extends FilterOutputStream {

	public LedsOuputStream(OutputStream out) {
		super(out);
	}

	public void writeln(String data) throws IOException {
		byte[] buffer = (data + "\r").getBytes();
		write(buffer);
		flush();
	}

}
