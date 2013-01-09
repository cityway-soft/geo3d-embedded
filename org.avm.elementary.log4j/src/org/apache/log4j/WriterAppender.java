/*
 * Copyright 2003-2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

// Contibutors: Jens Uwe Pipka <jens.pipka@gmx.de>

/**
 * WriterAppender appends log events to a {@link java.io.Writer} or an
 * {@link java.io.OutputStream} depending on the user's choice.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author <a href="mailto:ralph.curtis@gabrielsoftware.com">Ralph Curtis</a>
 * @since 1.1
 */
public class WriterAppender extends AppenderSkeleton {

	/**
	 * This is where we will write to.
	 */
	protected Writer w;

	/**
	 * This default constructor does nothing.
	 */
	public WriterAppender() {
	}

	/**
	 * Instantiate a WriterAppender and set the output destination to a new
	 * {@link OutputStreamWriter} initialized with <code>os</code> as its
	 * {@link OutputStream}.
	 */
	// public
	// WriterAppender(Layout layout, OutputStream os) {
	// this(layout, new OutputStreamWriter(os));
	// }
	/**
	 * Instantiate a WriterAppender and set the output destination to
	 * <code>writer</code>.
	 * 
	 * <p>
	 * The <code>writer</code> must have been previously opened by the user.
	 */
	public WriterAppender(Layout layout, Writer writer) {
		this.layout = layout;
		this.setWriter(writer);
	}

	public void write(String string) {
		try {
			w.write(string);
		} catch (IOException e) {
			error("Failed to writing", e);
		}
	}

	/**
	 * Does nothing.
	 */
	public void activateOptions() {
	}

	/**
	 * This method is called by the {@link AppenderSkeleton#doAppend} method.
	 * 
	 * <p>
	 * If the output stream exists and is writable then write a log statement to
	 * the output stream. Otherwise, write a single warning message to
	 * <code>System.err</code>.
	 * 
	 * <p>
	 * The format of the output will depend on this appender's layout.
	 * 
	 */
	public void append(LoggingEvent event) {

		// Reminder: the nesting of calls is:
		//
		// doAppend()
		// - check threshold
		// - filter
		// - append();
		// - checkEntryConditions();
		// - subAppend();

		if (!checkEntryConditions()) {
			return;
		}
		write(this.layout.format(event));

		if (layout.ignoresThrowable()) {
			String s = event.getThrowableStr();
			if (s != null) {
				write(s);
			}
		}

		try {
			w.flush();
		} catch (IOException e) {
			error("Failed to flush", e);
		}
	}

	/**
	 * This method determines if there is a sense in attempting to append.
	 * 
	 * <p>
	 * It checks whether there is a set output target and also if there is a set
	 * layout. If these checks fail, then the boolean value <code>false</code>
	 * is returned.
	 */
	protected boolean checkEntryConditions() {
		if (this.closed) {
			return false;
		}

		if (this.w == null) {
			error("No Writer for [" + name + "]");
			return false;
		}

		if (this.layout == null) {
			error("No layout for [" + name + "]");
			return false;
		}
		return true;
	}

	/**
	 * Close this appender instance. The underlying stream or writer is also
	 * closed.
	 * 
	 * <p>
	 * Closed appenders cannot be reused.
	 * 
	 * @see #setWriter
	 * @since 0.8.4
	 */
	public synchronized void close() {
		if (this.closed)
			return;
		this.closed = true;
		closeWriter();
		this.w = null;
	}

	/**
	 * Close the underlying {@link java.io.Writer}.
	 */
	protected void closeWriter() {
		if (w != null) {
			try {
				w.close();
			} catch (IOException e) {
				LogLog.error("Failed closing", e);
			}
		}
	}

	/**
	 * <p>
	 * Sets the Writer where the log output will go. The specified Writer must
	 * be opened by the user and be writable.
	 * 
	 * <p>
	 * The <code>java.io.Writer</code> will be closed when the appender
	 * instance is closed.
	 * 
	 * 
	 * <p>
	 * <b>WARNING:</b> Logging to an unopened Writer will fail.
	 * <p>
	 * 
	 * @param writer
	 *            An already opened Writer.
	 */
	public synchronized void setWriter(Writer writer) {
		closeWriter();
		this.w = null;
		this.w = writer;
	}

	/**
	 * Clear internal references to the writer and other variables.
	 * 
	 * Subclasses can override this method for an alternate closing behavior.
	 */
	// protected
	// void reset() {
	// closeWriter();
	// this.w = null;
	// }
}
