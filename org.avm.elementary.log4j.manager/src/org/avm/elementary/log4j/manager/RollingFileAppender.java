package org.avm.elementary.log4j.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class RollingFileAppender extends AppenderSkeleton {

	public static final int TOP_OF_TROUBLE = -1;

	public static final int TOP_OF_MINUTE = 0;

	public static final int TOP_OF_HOUR = 1;

	public static final int HALF_DAY = 2;

	public static final int TOP_OF_DAY = 3;

	public static final int TOP_OF_WEEK = 4;

	public static final int TOP_OF_MONTH = 5;

	private Logger _log = Logger.getInstance(RollingFileAppender.class
			.getName());

	private String _filenamePart1;
	private String _filenamePart2;

	private static final String DEFAULT_PATTERN = "'.'yyyy-MM-dd";

	private String _pattern = "'.'yyyy-MM-dd";

	private long _nextCheck = System.currentTimeMillis() - 1;

	private Date _now = new Date();

	private SimpleDateFormat _df;

	private RollingCalendar _calendar = new RollingCalendar();

	private RollingFileOutputStream _out;

//	private int _size = 512;

	public RollingFileAppender(Layout layout, String filename)
			throws IOException {
		this.layout = layout;
		this.name = this.getClass().getName();
		split(filename);
		if (_pattern == null) {
			_pattern = DEFAULT_PATTERN;
		}
		_df = new SimpleDateFormat(_pattern);
		int type = computeCheckPeriod();
		printPeriodicity(type);
		_calendar.setType(type);
	}

	private void split(String f) {
		int idx1 = f.indexOf("{");
		int idx2 = f.indexOf("}");

		if (idx1 > 0) {
			if (idx2 > 0) {
				_pattern = f.substring(idx1 + 1, idx2);
				_filenamePart1 = f.substring(0, idx1);
				_filenamePart2 = f.substring(idx2 + 1);
			} else {
				_pattern = f.substring(idx1 + 1);
				_filenamePart1 = f.substring(0, idx1);
				_filenamePart2 = "";
			}
		} else {
			_pattern = null;
			_filenamePart1 = f;
			_filenamePart2 = "";
		}

	}

	String getPattern() {
		return _pattern;
	}

	void setPattern(String pattern) {
		_pattern = pattern;
	}

//	int getSize() {
//		return _size;
//	}
//
//	void setSize(int size) {
//		_size = size;
//	}

	public void flush() {
		try {
			if (_out != null) {
				_out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			// throw new RuntimeException(e.getMessage());
		}
	}

	public void close() {
		try {
			if (_out != null) {
				_out.close();
				_out = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			// throw new RuntimeException(e.getMessage());
		}
	}

	public void compress(RollingFileOutputStream rdos) throws IOException {
		File logFile = new File(rdos.getFilename());
		FileInputStream in = new FileInputStream(logFile);

		String compressedFilename = System.getProperty("org.avm.home")
				+ "/data/upload/" + logFile.getName() + ".gz";
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
				compressedFilename));
		byte[] buff = new byte[1024 * 4];
		int c;

		while ((c = in.read(buff)) > 0) {
			out.write(buff, 0, c);
		}

		out.close();
		logFile.delete();

	}

	protected void rollOver() throws IOException {
		boolean append = false;

		String filename = getScheduledFilename(_now);
		File fd = new File(filename);

		if (fd.exists()) {
			Date modified = new Date(fd.lastModified());
			Date d0 = _calendar.getNextCheckDate(modified);
			Date d1 = _calendar.getNextCheckDate(_now);
			append = d1.equals(d0);
		}

		if (_out != null && append) {
			return;
		}

		if (_out != null) {
			compress(_out);
			close();
		}

		if (fd.exists() && !append) {
			fd.delete();
		}

		_out = new RollingFileOutputStream(filename);

	}

	protected void append(LoggingEvent event) {
		long n = System.currentTimeMillis();
		if (n >= _nextCheck) {
			_now.setTime(n);
			_nextCheck = _calendar.getNextCheckMillis(_now);
			try {
				rollOver();
			} catch (IOException ioe) {
				_log.error("rollOver() failed.", ioe);
			}
		}

		try {
			String text = this.layout.format(event);
			_out.write(text.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			// throw new RuntimeException(e.getMessage());
		}

	}

	int getCheckPeriod() {
		return _calendar.getType();
	}

	String getScheduledFilename(Date date) {
		String result;
		result = _filenamePart1 + _df.format(date) + _filenamePart2;

		return result;
	}

	private int computeCheckPeriod() {
		RollingCalendar rollingCalendar = new RollingCalendar(
				TimeZone.getTimeZone("GMT"), Locale.ENGLISH);

		Date epoch = new Date(0);
		if (_pattern != null) {
			for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						_pattern);
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				if (r0 != null && r1 != null && !r0.equals(r1)) {
					return i;
				}
			}
		}
		return TOP_OF_TROUBLE; // Deliberately head for trouble...
	}

	private void printPeriodicity(int type) {
		switch (type) {
		case TOP_OF_MINUTE:
			_log.info("Appender [" + name + "] to be rolled every minute.");
			break;
		case TOP_OF_HOUR:
			_log.info("Appender [" + name
					+ "] to be rolled on top of every hour.");
			break;
		case HALF_DAY:
			_log.info("Appender [" + name
					+ "] to be rolled at midday and midnight.");
			break;
		case TOP_OF_DAY:
			_log.info("Appender [" + name + "] to be rolled at midnight.");
			break;
		case TOP_OF_WEEK:
			_log.info("Appender [" + name + "] to be rolled at start of week.");
			break;
		case TOP_OF_MONTH:
			_log.info("Appender [" + name
					+ "] to be rolled at start of every month.");
			break;
		default:
			_log.warn("Unknown periodicity for appender [" + name + "].");
		}
	}

	class RollingCalendar extends GregorianCalendar {

		int type = TOP_OF_TROUBLE;

		RollingCalendar() {
			super();
		}

		RollingCalendar(TimeZone tz, Locale locale) {
			super(tz, locale);
		}

		void setType(int type) {
			this.type = type;
		}

		public long getNextCheckMillis(Date now) {
			return getNextCheckDate(now).getTime();
		}

		public Date getNextCheckDate(Date now) {

			this.setTime(now);

			switch (type) {
			case TOP_OF_MINUTE:
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MINUTE, 1);
				break;
			case TOP_OF_HOUR:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.HOUR_OF_DAY, 1);
				break;
			case HALF_DAY:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				int hour = get(Calendar.HOUR_OF_DAY);
				if (hour < 12) {
					this.set(Calendar.HOUR_OF_DAY, 12);
				} else {
					this.set(Calendar.HOUR_OF_DAY, 0);
					this.add(Calendar.DAY_OF_MONTH, 1);
				}
				break;
			case TOP_OF_DAY:
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.DATE, 1);
				break;
			case TOP_OF_WEEK:
				this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.WEEK_OF_YEAR, 1);
				break;
			case TOP_OF_MONTH:
				this.set(Calendar.DATE, 1);
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MONTH, 1);
				break;
			default:
				throw new IllegalStateException("Unknown periodicity type.");
			}
			return getTime();
		}

		int getType() {
			return type;
		}
	}

}
