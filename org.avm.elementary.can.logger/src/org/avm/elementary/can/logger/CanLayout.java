package org.avm.elementary.can.logger;

import java.text.MessageFormat;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.avm.elementary.can.parser.PGN;

public class CanLayout extends Layout {

	private long now;
	private long count;

	public CanLayout() {
	}

	public void activateOptions() {
	}

	public String format(LoggingEvent e) {
		if (count == 0) {
			now = System.currentTimeMillis();
		}
		PGN pgn = ((CanEvent) e).pgn;

		final String PATTERN = "({0,number,.000}) can0 {1}#{2}\n";
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[3 - i] = pgn.buffer[i];
		}
		Object[] arguments = {
				new Double((System.currentTimeMillis() - now) / 1000d),
				PGN.toHexaString(b), PGN.toHexaString(pgn.buffer, 6, 8) };
		count++;

		return MessageFormat.format(PATTERN, arguments);

	}

	public boolean ignoresThrowable() {
		return true;
	}

	public String[] getOptionStrings() {
		return null;
	}

	public void setOption(String key, String value) {

	}
}
