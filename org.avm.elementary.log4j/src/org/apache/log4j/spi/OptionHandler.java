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

/**
 * A string based interface to configure package components.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public interface OptionHandler {

	/**
	 * Activate the options that were previously set with calls to option
	 * setters.
	 * 
	 * <p>
	 * This allows to defer activiation of the options until all options have
	 * been set. This is required for components which have related options that
	 * remain ambigous until all are set.
	 * 
	 */
	public void activateOptions();

	public String[] getOptionStrings();

	public void setOption(String key, String value);
}
