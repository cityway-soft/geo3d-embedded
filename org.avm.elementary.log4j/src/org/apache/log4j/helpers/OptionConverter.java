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

package org.apache.log4j.helpers;

import java.util.Properties;

public class OptionConverter {

	public static boolean toBoolean(String value, boolean defaultVal) {
		if (value == null)
			return defaultVal;
		String trimmedVal = value.trim();
		if ("true".equalsIgnoreCase(trimmedVal))
			return true;
		if ("false".equalsIgnoreCase(trimmedVal))
			return false;
		return defaultVal;
	}

	/**
	 * Very similar to <code>System.getProperty</code> except that the
	 * {@link SecurityException} is hidden.
	 * 
	 * @param key
	 *            The key to search for.
	 * @param def
	 *            The default value to return.
	 * @return the string value of the system property, or the default value if
	 *         there is no property with that key.
	 * 
	 * @since 1.1
	 */
	public static String getSystemProperty(String key, String def) {
		try {
			return System.getProperty(key, def);
		} catch (Throwable e) { // MS-Java throws
			// com.ms.security.SecurityExceptionEx
			LogLog.debug("Was not allowed to read system property \"" + key
					+ "\".");
			return def;
		}
	}

	public static Object instantiateByKey(Properties props, String key,
			Class superClass, Object defaultValue) {

		// Get the value of the property in string form
		String className = props.getProperty(key);
		if (className == null) {
			LogLog.error("Could not find value for " + key);
			return defaultValue;
		}
		// Trim className to avoid trailing spaces that cause problems.
		return OptionConverter.instantiateByClassName(className.trim(),
				superClass, defaultValue);
	}

	/**
	 * Instantiate an object given a class name. Check that the
	 * <code>className</code> is a subclass of <code>superClass</code>.
	 * 
	 */
	public static Object instantiateByClassName(String className,
			Class superClass, Object defaultValue) {
		if (className != null) {
			try {
				Class classObj = Class.forName(className);
				if (!superClass.isAssignableFrom(classObj))
					LogLog.error("A \"" + className
							+ "\" object is not assignable to a \""
							+ superClass.getName() + "\" object.");
				return classObj.newInstance();
			} catch (Exception e) {
				LogLog.error(
						"Could not instantiate class [" + className + "].", e);
			}
		}
		return defaultValue;
	}

}
