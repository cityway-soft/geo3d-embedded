package org.avm.elementary.jdb.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
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

public class JDBAppender extends AppenderSkeleton {

	public static final int TOP_OF_TROUBLE = -1;

	public static final int TOP_OF_MINUTE = 0;

	public static final int TOP_OF_HOUR = 1;

	public static final int HALF_DAY = 2;

	public static final int TOP_OF_DAY = 3;

	public static final int TOP_OF_WEEK = 4;

	public static final int TOP_OF_MONTH = 5;

	private Logger _log = Logger.getInstance(JDBAppender.class.getName());

	private String _filename;

	private String _pattern = "'.'yyyy-MM-dd";

	private long _nextCheck = System.currentTimeMillis() - 1;

	private Date _now = new Date();

	private SimpleDateFormat _df;

	private RollingCalendar _calendar = new RollingCalendar();

	private JDBOutputStream _out;

	private int _size = 512;

	public JDBAppender(Layout layout, String filename, String pattern)
			throws IOException {
		this.layout = layout;
		this.name = this.getClass().getName();
		_filename = filename;
		_pattern = pattern;
		_df = new SimpleDateFormat(pattern);
		int type = computeCheckPeriod();
		printPeriodicity(type);
		_calendar.setType(type);
	}

	String getPattern() {
		return _pattern;
	}

	void setPattern(String pattern) {
		_pattern = pattern;
	}

	int getSize() {
		return _size;
	}

	void setSize(int size) {
		_size = size;
	}

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

	protected void rollOver() throws IOException {
		boolean append = false;

		String filename = getScheduledFilename(_now);
		_log.debug("roll over : " + filename);
		File fd = new File(filename);
		compressAndMoveAllOther(fd);

		if (fd.exists()) {
			Date modified = new Date(fd.lastModified());
			Date d0 = _calendar.getNextCheckDate(modified);
			Date d1 = _calendar.getNextCheckDate(_now);
			append = d1.equals(d0);
		}

		if (_out != null && append) {
			_log.debug("do nothing !");
			return;
		}

		if (_out != null) {
			_log.debug("close ouput stream.");
			close();
		}

		if (fd.exists() && !append) {
			_log.debug("delete file " + filename);
			fd.delete();
		}

		_log.debug("open ouput stream, append : " + append);

		_out = new JDBOutputStream(filename, _size);

	}

	private void compressAndMoveAllOther(final File current) {
		File tmp = new File(_filename);
		tmp = tmp.getParentFile();

		File destDir = new File(tmp.getParentFile().getAbsoluteFile()
				+ "/upload");

		JdbFileFilter filter = new JdbFileFilter(current);
		File[] content = tmp.listFiles(filter);

		if (content != null) {
			for (int i = 0; i < content.length; i++) {
				File file = content[i];
				try {
					compressAndMove(file, destDir);
				} catch (IOException e) {
					_log.error("Error on compressAndMove Jdb '"+file.getAbsolutePath()+"' : " + e.getMessage());
				}
			}
		}
	}

	private File compressAndMove(File file, File destDir) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));

		String filename = destDir.getAbsoluteFile() + "/" + file.getName()
				+ "_jdb.gz";

		GZIPOutputStream _zout = new GZIPOutputStream(new FileOutputStream(
				filename, true));
		byte[] data = new byte[1024];

		int c;

		while ((c = in.read(data)) != -1) {
			_zout.write(data, 0, c);
		}
		in.close();
		_zout.close();
		file.delete();
		return new File(filename);
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
		return _filename + _df.format(date);
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

	class JdbFileFilter implements FileFilter {

		private File current;

		public JdbFileFilter(File currentJdb) {
			this.current = currentJdb;
		}

		public boolean accept(File arg0) {
			boolean result = false;

			result = !arg0.getAbsoluteFile().equals(current.getAbsoluteFile());
			System.err.println("accept " + arg0.getAbsoluteFile() + " : "
					+ result);
			return result;
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
