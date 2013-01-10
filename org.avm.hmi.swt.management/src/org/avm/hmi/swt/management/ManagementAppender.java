/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.avm.hmi.swt.management;

import java.io.PrintWriter;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

// Contributors: Yves Bossel <ybossel@opengets.cl>
// Christopher Taylor <cstaylor@pacbell.net>

/**
 * Use SyslogAppender to send log messages to a remote syslog daemon.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Anders Kristensen
 */
public class ManagementAppender extends AppenderSkeleton {
	// The following constants are extracted from a syslog.h file
	// copyrighted by the Regents of the University of California
	// I hope nobody at Berkley gets offended.

	// SyslogTracerPrintWriter stp;
	PrintWriter writer;

	public ManagementAppender(PrintWriter w) {
		layout = new org.apache.log4j.PatternLayout("%r %-5p [%c] %m%n");
		this.writer = w;
	}

	/**
	 * Release any resources held by this SyslogAppender.
	 * 
	 * @since 0.8.4
	 */
	synchronized public void close() {
		closed = true;
		// A SyslogWriter is UDP based and needs no opening. Hence, it
		// can't be closed. We just unset the variables here.
		writer = null;
	}

	public void append(LoggingEvent event) {

		// if (!isAsSevereAsThreshold(event.priority))
		// return;

		String buffer = layout.format(event);

		writer.write(buffer);
		/*
		 * String str = event.getThrowableStr(); if (str != null & str.length() >
		 * 0) {
		 * 
		 * StringTokenizer t = new StringTokenizer(str); for (int i = 0;
		 * t.hasMoreElements(); i++) { String s = (String) t.nextElement();
		 * 
		 * if (i > 0) sqw.write(TAB + s.trim()); else sqw.write(s.trim()); } }
		 */
	}

	/**
	 * This method returns immediately as options are activated when they are
	 * set.
	 */
	public void activateOptions() {
	}

	/**
	 * The SyslogAppender requires a layout. Hence, this method returns
	 * <code>true</code>.
	 * 
	 * @since 0.8.4
	 */
	public boolean requiresLayout() {
		return true;
	}

}
