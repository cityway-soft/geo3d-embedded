package org.avm.elementary.jdb.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class JDBLayout extends Layout {

	private StringBuffer _buffer = new StringBuffer(128);

	protected SimpleDateFormat _df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected DecimalFormat _pf = new DecimalFormat("###.######");

	private Date _date = new Date();

	public JDBLayout() {
	}

	public void activateOptions() {
	}

	public String format(LoggingEvent e) {

		JDBEvent event = (JDBEvent) e;
		double lon = 0d;
		double lat = 0d;

		if (event.position != null) {
			lon = (event.position.getLongitude().getValue() * 180d / Math.PI);
			lat = (event.position.getLatitude().getValue() * 180d / Math.PI);
		}

		_buffer.setLength(0);
		_date.setTime(event.timeStamp);

		_buffer.append(_df.format(_date));
		_buffer.append(" ");
		_buffer.append(event.priority);
		_buffer.append(" ");
		_buffer.append(_pf.format(lon));
		_buffer.append(" ");
		_buffer.append(_pf.format(lat));
		_buffer.append(" [");
		_buffer.append(event.loggerName);
		_buffer.append("] ");
		_buffer.append(event.getMessage());
		_buffer.append(LINE_SEP);
		return _buffer.toString();
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
