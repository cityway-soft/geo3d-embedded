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

import java.io.OutputStreamWriter;

import org.apache.log4j.helpers.LogLog;

/**
 * ConsoleAppender appends log events to <code>System.out</code> or
 * <code>System.err</code> using a layout specified by the user. The default
 * target is <code>System.out</code>.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.1
 * @author <a href="mailto:ralph.curtis@gabrielsoftware.com">Ralph Curtis</a>
 */
public class ConsoleAppender extends WriterAppender {

	public static final String SYSTEM_OUT = "System.out";
	public static final String SYSTEM_ERR = "System.err";
	private static final String DEFAULT_LAYOUT_PATTERN = "%d - %m%n";

	protected String target = SYSTEM_OUT;

	/**
	 * The default constructor sets the target to <code>System.out</code> with
	 * a default layout pattern of "%d - %m%n".
	 */
	public ConsoleAppender() {
		this(new PatternLayout(DEFAULT_LAYOUT_PATTERN));
	}

	/**
	 * Construct a new ConsoleAppender. Sets the target to
	 * <code>System.out</code> with the specified layout pattern of "%d -
	 * %m%n".
	 */
	public ConsoleAppender(Layout layout) {
		this(layout, SYSTEM_OUT);
	}

	/**
	 * Construct a new ConsoleAppender with specified layout pattern and target.
	 * 
	 * @param layout
	 * @param target
	 *            restricted to System.out or System.err. Defaults to
	 *            System.out.
	 */
	public ConsoleAppender(Layout layout, String target) {
		this.layout = layout;

		if (SYSTEM_OUT.equals(target)) {
			setWriter(new OutputStreamWriter(System.out));
		} else if (SYSTEM_ERR.equals(target)) {
			setWriter(new OutputStreamWriter(System.err));
		}
		setTarget(target);
	}

	/**
	 * Sets the value of the <b>target</b> option. Recognized values are
	 * "System.out" and "System.err". Any other value will be ignored.
	 */
	public void setTarget(String value) {
		String v = value.trim();

		if (SYSTEM_OUT.equalsIgnoreCase(v)) {
			target = SYSTEM_OUT;
		} else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
			target = SYSTEM_ERR;
		} else {
			targetWarn(value);
		}
	}

	/**
	 * Returns the current value of the <b>target</b> property. The default
	 * value of the option is "System.out".
	 * 
	 * See also {@link #setTarget}.
	 */
	public String getTarget() {
		return target;
	}

	void targetWarn(String val) {
		LogLog.warn("[" + val + "] should be System.out or System.err.");
		LogLog.warn("Using previously set target, System.out by default.");
	}

	public void activateOptions() {
		if (target.equals(SYSTEM_OUT)) {
			setWriter(new OutputStreamWriter(System.out));
		} else {
			setWriter(new OutputStreamWriter(System.err));
		}
	}

	/**
	 * This method overrides the parent {@link WriterAppender#closeWriter}
	 * implementation to do nothing because the console stream is not ours to
	 * close.
	 */
	protected final void closeWriter() {
	}
}
