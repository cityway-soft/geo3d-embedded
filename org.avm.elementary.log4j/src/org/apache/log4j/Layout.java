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

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

/**
 * Extend this abstract class to create your own log layout format.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * 
 */

public abstract class Layout implements OptionHandler {

	// Note that the line.separator property can be looked up even by
	// applets.
	public final static String LINE_SEP = System.getProperty("line.separator");
	public final static int LINE_SEP_LEN = LINE_SEP.length();

	/**
	 * Implement this method to create your own layout format.
	 */
	abstract public String format(LoggingEvent event);

	/**
	 * If the layout handles the throwable object contained within
	 * {@link LoggingEvent}, then the layout should return <code>false</code>.
	 * Otherwise, if the layout ignores throwable object, then the layout should
	 * return <code>true</code>.
	 * 
	 * @since 1.0
	 */
	abstract public boolean ignoresThrowable();

}
