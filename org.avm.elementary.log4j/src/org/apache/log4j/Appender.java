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

/**
 * Implement this interface for your own strategies for printing log statements.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public interface Appender {

	/**
	 * Release any resources allocated within the appender such as file handles,
	 * network connections, etc.
	 * 
	 * <p>
	 * It is a programming error to append to a closed appender.
	 * 
	 * @since 0.8.4
	 */
	public void close();

	/**
	 * Log in Appender specific way.
	 */
	public void doAppend(LoggingEvent event);

	/**
	 * Get the name of this appender. The name uniquely identifies the appender.
	 */
	public String getName();

	/**
	 * Set the {@link Layout} for this appender.
	 * 
	 * @since 0.8.1
	 */
	public void setLayout(Layout layout);

	/**
	 * Returns this appenders layout.
	 * 
	 * @since 1.1
	 */
	public Layout getLayout();

	/**
	 * Set the name of this appender. The name is used by other components to
	 * identify this appender.
	 * 
	 * @since 0.8.1
	 */
	public void setName(String name);

}
