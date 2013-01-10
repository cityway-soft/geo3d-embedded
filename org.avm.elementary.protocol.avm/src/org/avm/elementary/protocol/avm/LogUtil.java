package org.avm.elementary.protocol.avm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
	public static final int ERROR = 0;

	public static final int WARN = 1;

	public static final int INFO = 2;

	public static final int DEBUG = 3;

	protected static boolean _debugEnabled;

	protected static final DateFormat _df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss,SSS");

	public static void setDebugEnabled(boolean debugEnabled) {
		_debugEnabled = debugEnabled;
	}

	public static boolean isDebugEnabled() {
		return _debugEnabled;	
	}

	public static void error(String msg, Throwable e) {
		if (msg != null)
			log(ERROR, msg);
		if (e != null)
			e.printStackTrace();
	}

	public static void warn(String msg) {
		if (msg != null)
			log(WARN, msg);
	}

	public static void info(String msg) {
		if (msg != null)
			log(INFO, msg);
	}

	public static void debug(String msg) {

		if (isDebugEnabled() && msg != null) {
			log(DEBUG, msg);
		}
	}

	protected static String toLevel(int level) {
		String result = "";
		switch (level) {
		case ERROR:
			result = "ERROR";
			break;

		case WARN:
			result = "WARN";
			break;
		case INFO:
			result = "INFO";
			break;

		case DEBUG:
			result = "DEBUG";
			break;

		default:
			break;
		}
		return result;
	}

	protected static void log(int level, String msg) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(_df.format(new Date()));
		buffer.append(" ");
		buffer.append(toLevel(level));
		buffer.append(" ");
		buffer.append(msg);
		System.out.println(buffer);
	}
}