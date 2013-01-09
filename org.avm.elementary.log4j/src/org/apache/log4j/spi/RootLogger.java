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

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.LogLog;

/**
 * RootLogger sits at the top of the Logger hierachy. It is a regular Logger
 * except that it provides several guarantees.
 * 
 * <p>
 * First, it cannot be assigned a <code>null</code> priority. Second, since
 * root Logger cannot have a parent, the {@link #getChainedPriority} method
 * always returns the value of the priority field without walking the hierarchy.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author <a href="mailto:ralph.curtis@gabrielsoftware.com">Ralph Curtis</a>
 * 
 */
final public class RootLogger extends Logger {

	/**
	 * The root Logger names itself as "root". However, the root Logger cannot
	 * be retrieved by name.
	 */
	public RootLogger(Priority priority) {
		super("root");
		setPriority(priority);
	}

	/**
	 * Return the assigned priority value without walking the Logger hierarchy.
	 */
	final public Priority getChainedPriority() {
		return priority;
	}

	/**
	 * Setting a null value to the priority of the root Logger may have
	 * catastrophic results. We prevent this here.
	 * 
	 * @since 0.8.3
	 */
	final public void setPriority(Priority priority) {
		if (priority == null) {
			LogLog.error("null priority disallowed", new Throwable());
		} else {
			this.priority = priority;
		}
	}
}
