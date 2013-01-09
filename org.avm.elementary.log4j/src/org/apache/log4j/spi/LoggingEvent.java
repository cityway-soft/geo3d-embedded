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

package org.apache.log4j.spi;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

// Contributors: Nelson Minar <nelson@monkey.org>
// Wolf Siberski
// Anders Kristensen <akristensen@dynamicsoft.com>

/**
 * The internal representation of logging events. When an affirmative decision
 * is made to log then a <code>LoggingEvent</code> instance is created. This
 * instance is passed around to the different log4j components.
 * 
 * <p>
 * This class is of concern to those wishing to extend log4j.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author James P. Cakalic
 * @author <a href="mailto:ralph.curtis@gabrielsoftware.com">Ralph Curtis</a>
 * 
 * @since 0.8.2
 */
public class LoggingEvent {

	private static long startTime = System.currentTimeMillis();

	/**
	 * The logger of the logging event. The logger field is not serialized for
	 * performance reasons.
	 * 
	 * <p>
	 * It is set by the LoggingEvent constructor or set by a remote entity after
	 * deserialization.
	 */
	transient public Logger logger;

	/** The logger name. */
	public final String loggerName;

	/**
	 * Priority of logging event. Priority cannot be serializable because it is
	 * a flyweight. Due to its special seralization it cannot be declared final
	 * either.
	 */
	transient public Priority priority;

	/** The nested diagnostic context (NDC) of logging event. */
	// private String ndc;
	/**
	 * Have we tried to do an NDC lookup? If we did, there is no need to do it
	 * again. Note that its value is always false when serialized. Thus, a
	 * receiving SocketNode will never use it's own (incorrect) NDC. See also
	 * writeObject method.
	 */
	private boolean ndcLookupRequired = true;

	/** The application supplied message of logging event. */
	transient public String message;

	/** The name of thread in which this logging event was generated. */
	private String threadName;

	/**
	 * The throwable associated with this logging event.
	 * 
	 * This is field is transient because not all exception are serializable.
	 * More importantly, the stack information does not survive serialization.
	 */
	transient public Throwable throwable;

	/**
	 * This variable collects the info on a throwable. This variable will be
	 * shipped to
	 */
	public String throwableStr;

	/**
	 * The number of milliseconds elapsed from 1/1/1970 until logging event was
	 * created.
	 */
	public final long timeStamp;

	/**
	 * Instantiate a LoggingEvent from the supplied parameters.
	 * 
	 * <p>
	 * Except {@link #timeStamp} all the other fields of
	 * <code>LoggingEvent</code> are filled when actually needed.
	 * <p>
	 * 
	 * @param logger
	 *            The logger of this event.
	 * @param priority
	 *            The priority of this event.
	 * @param message
	 *            The message of this event.
	 * @param throwable
	 *            The throwable of this event.
	 */
	public LoggingEvent(Logger logger, Priority priority, Object message,
			Throwable throwable) {
		this.logger = logger;
		this.loggerName = logger.getName();
		this.priority = priority;
		this.message = (message != null) ? message.toString() : "";
		this.throwable = throwable;
		timeStamp = System.currentTimeMillis();
	}

	/**
	 * Return the message for this logging event.
	 * 
	 * <p>
	 * Before serialization, the returned object is the message passed by the
	 * user to generate the logging event. After serialization, the returned
	 * value equals the String form of the message possibly after object
	 * rendering.
	 * 
	 * @since 1.1
	 */
	public Object getMessage() {
		return message;
	}

	// public
	// String getMessageStr() {
	// return message.toString();
	// }

	// public
	// String getNDC() {
	// if(ndcLookupRequired) {
	// ndcLookupRequired = false;
	// ndc = NDC.get();
	// }
	// return ndc;
	// }

	/**
	 * Returns the time when the application started, in milliseconds elapsed
	 * since 01.01.1970.
	 */
	public static long getStartTime() {
		return startTime;
	}

	public String getThreadName() {
		if (threadName == null)
			threadName = (Thread.currentThread()).getName();
		return threadName;
	}

	/**
	 * Returns the throwable information contained within this event. May be
	 * <code>null</code> if there is no such information.
	 */
	public String getThrowableStr() {
		if (throwable == null) {
			return null;
		}

		if (throwableStr == null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			throwable.printStackTrace(pw);
			throwableStr = sw.toString();
		}
		return throwableStr;
	}
}
