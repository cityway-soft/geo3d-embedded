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

// Contibutors: "Luke Blanshard" <Luke@quiq.com>
//              "Mark DONSZELMANN" <Mark.Donszelmann@cern.ch>
//              Anders Kristensen <akristensen@dynamicsoft.com>
package org.apache.log4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.OptionHandler;

/**
 * Allows the log4j configuration from an external file. See <b>{@link #doConfigure(String, Hierarchy)}</b>
 * for the expected format.
 * 
 * <p>
 * It is sometimes useful to see how log4j is reading configuration files. You
 * can enable log4j internal logging by defining the <b>log4j.debug</b>
 * variable.
 * 
 * <P>
 * At the initialization of the Logger class, the file <b>log4j.properties</b>
 * will be searched from the search path used to load classes. If the file can
 * be found, then it will be fed to the
 * {@link PropertyConfigurator#configure(java.net.URL)} method.
 * 
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author <a href="mailto:ralph.curtis@gabrielsoftware.com">Ralph Curtis</a>
 * @since log4jME 1.0
 */
public class PropertyConfigurator {

	/**
	 * Used internally to keep track of configured appenders.
	 */
	protected Hashtable registry = new Hashtable(11);

	static final String LOGGER_PREFIX = "log4j.logger.";
	static final String ADDITIVITY_PREFIX = "log4j.additivity.";
	static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
	static final String APPENDER_PREFIX = "log4j.appender.";

	static final private String INHERITED = "inherited";
	static final private String INTERNAL_ROOT_NAME = "root";

	/**
	 * Read configuration from a file. The existing configuration is not cleared
	 * nor reset.
	 * 
	 * <p>
	 * The configuration file consists of staments in the format
	 * <code>key=value</code>.
	 * 
	 * <h3>Appender configuration</h3>
	 * 
	 * <p>
	 * Appender configuration syntax is:
	 * 
	 * <pre>
	 * 	  # For appender named &lt;i&gt;appenderName&lt;/i&gt;, set its class.
	 * 	  # Note: The appender name can contain dots.
	 * 	  log4j.appender.appenderName=fully.qualified.name.of.appender.class
	 * 
	 * 	  # Set appender specific options.
	 * 	  log4j.appender.appenderName.option1=value1  
	 * 	  ...
	 * 	  log4j.appender.appenderName.optionN=valueN
	 * 	
	 * </pre>
	 * 
	 * For each named appender you can configure its {@link Layout}. The syntax
	 * for configuring an appender's layout is:
	 * 
	 * <pre>
	 * 	  log.appender.appenderName.layout=fully.qualified.name.of.layout.class
	 * 	  log.appender.appenderName.layout.option1=value1
	 * 	  ....
	 * 	  log.appender.appenderName.layout.optionN=valueN
	 * 	
	 * </pre>
	 * 
	 * <h3>Configuring Loggers</h3>
	 * 
	 * <p>
	 * The syntax for configuring the root Logger is:
	 * 
	 * <pre>
	 * 	    log4j.rootLogger=[FATAL|ERROR|WARN|INFO|DEBUG], appenderName, appenderName, ...
	 * 	
	 * </pre>
	 * 
	 * <p>
	 * This syntax means that one of the strings values ERROR, WARN, INFO or
	 * DEBUG can be supplied followed by appender names separated by commas.
	 * 
	 * <p>
	 * If one of the optional priority values ERROR, WARN, INFO or DEBUG is
	 * given, the root priority is set to the corresponding priority. If no
	 * priority value is specified, then the root priority remains untouched.
	 * 
	 * <p>
	 * The root Logger can be assigned multiple appenders.
	 * 
	 * <p>
	 * Each <i>appenderName</i> (seperated by commas) will be added to the root
	 * Logger. The named appender is defined using the appender syntax defined
	 * above.
	 * 
	 * <p>
	 * For non-root Loggers the syntax is almost the same:
	 * 
	 * <pre>
	 * 	  log4j.logger.logger_name=[INHERITED|FATAL|ERROR|WARN|INFO|DEBUG], appenderName, appenderName, ...
	 * 	
	 * </pre>
	 * 
	 * <p>
	 * Thus, one of the usual priority values FATAL, ERROR, WARN, INFO, or DEBUG
	 * can be optionally specified. For any any of these values the named Logger
	 * is assigned the corresponding priority. In addition however, the value
	 * INHERITED can be optionally specified which means that named Logger
	 * should inherit its priority from the Logger hierarchy.
	 * 
	 * <p>
	 * If no priority value is supplied, then the priority of the named Logger
	 * remains untouched.
	 * 
	 * <p>
	 * By default Loggers inherit their priority from the hierarchy. However, if
	 * you set the priority of a Logger and later decide that that Logger should
	 * inherit its priority, then you should specify INHERITED as the value for
	 * the priority value.
	 * 
	 * <p>
	 * Similar to the root Logger syntax, each <i>appenderName</i> (seperated
	 * by commas) will be attached to the named Logger.
	 * 
	 * <p>
	 * See the <a href="../../manual.html#additivity">appender additivity rule</a>
	 * in the user manual for the meaning of the <code>additivity</code> flag.
	 * 
	 * <h3>Example</h3>
	 * 
	 * <p>
	 * An example configuration is given below. Other configuration file
	 * examples are given in {org.apache.log4j.examples.Sort} class
	 * documentation.
	 * 
	 * <pre>
	 * 
	 * 	  # Set options for appender named &quot;A1&quot;. 
	 * 	  # Appender &quot;A1&quot; will be a FileAppender
	 * 	  log4j.appender.A1=org.apache.log4j.FileAppender
	 * 
	 * 	  # It will send its output to System.out
	 * 	  log4j.appender.A1.File=System.out
	 * 
	 * 	  # A1's layout is a PatternLayout, using the conversion pattern 
	 * 	  # &lt;b&gt;%-4r %-5p %c{2} - %m%n&lt;/b&gt;. Thus, the log output will
	 * 	  # include the relative time since the start of the application in
	 * 	  # milliseconds, followed by the priority of the log request,
	 * 	  # followed by the two rightmost components of the Logger name
	 * 	  # and finally the message itself.
	 * 	  # Refer to the documentation of {@link PatternLayout} for further information
	 * 	  # on the syntax of the ConversionPattern key.    
	 * 	  log4j.appender.A1.layout=org.apache.log4j.PatternLayout
	 * 	  log4j.appender.A1.layout.ConversionPattern=%-4r %-5p %c{2} - %m%n
	 * 
	 * 	  # Set options for appender named &quot;A2&quot;
	 * 	  # A2 should be a
	 * <code>
	 * FileAppender
	 * </code>
	 *  printing to the
	 * 	  # file
	 * <code>
	 * temp
	 * </code>
	 * .
	 * 	  log4j.appender.A2=org.apache.log4j.FileAppender
	 * 	  log4j.appender.A2.File=temp
	 * 
	 * 	  log4j.appender.A2.layout=org.apache.log4j.PatternLayout
	 * 	  log4j.appender.A2.layout.ConversionPattern=%-4r %-5p %c - %m%n
	 * 
	 * 	  # Root Logger set to DEBUG using the A2 appender defined above.
	 * 	  log4j.rootLogger=DEBUG, A2
	 * 
	 * 	  # Logger definions:
	 * 	  # The SECURITY Logger inherits is priority from root. However, it's output
	 * 	  # will go to A1 appender defined above. It's additivity is non-cumulative.
	 * 	  log4j.logger.SECURITY=INHERIT, A1
	 * 	  log4j.additivity.SECURITY=false
	 * 
	 * 	  # Only warnings or above will be logged for the Logger &quot;SECURITY.access&quot;.
	 * 	  # Output will go to A1.
	 * 	  log4j.logger.SECURITY.access=WARN
	 * 
	 * 	  
	 * 	  # The Logger &quot;class.of.the.day&quot; inherits its priority from the
	 * 	  # Logger hierarchy.  Output will go to the appender's of the root
	 * 	  # Logger, A2 in this case.
	 * 	  log4j.logger.class.of.the.day=INHERIT
	 * 	
	 * </pre>
	 * 
	 * <p>
	 * Refer to the <b>setOption</b> method in each Appender and Layout for
	 * class specific options.
	 * 
	 * <p>
	 * Use the <code>#</code> or <code>!</code> characters at the beginning
	 * of a line for comments.
	 * 
	 * <p>
	 * 
	 * @param configFileName
	 *            The name of the configuration file where the configuration
	 *            information is stored.
	 * 
	 */
	public void doConfigure(String configFileName, Hierarchy hierarchy) {
		Properties props = new Properties();
		try {
			FileInputStream istream = new FileInputStream(configFileName);
			props.load(istream);
			istream.close();
		} catch (IOException e) {
			LogLog.error(
					"Could not read config file [" + configFileName + "].", e);
			LogLog.error("Ignoring config file [" + configFileName + "].");
			return;
		}
		// If we reach here, then the config file is alright.
		doConfigure(props, hierarchy);
	}

	/**
	 */
	static public void configure(String configFilename) {
		new PropertyConfigurator().doConfigure(configFilename,
				Logger.defaultHierarchy);
	}

	/**
	 * Read configuration options from url <code>configURL</code>.
	 * 
	 * @since 0.8.2
	 */
	public static void configure(java.net.URL configURL) {
		new PropertyConfigurator().doConfigure(configURL,
				Logger.defaultHierarchy);
	}

	/**
	 * Read configuration options from <code>properties</code>.
	 * 
	 * See {@link #doConfigure(String, Hierarchy)} for the expected format.
	 */
	static public void configure(Properties properties) {
		new PropertyConfigurator().doConfigure(properties,
				Logger.defaultHierarchy);
	}

	/**
	 * Read configuration options from <code>properties</code>.
	 * 
	 * See {@link #doConfigure(String, Hierarchy)} for the expected format.
	 */
	public void doConfigure(Properties properties, Hierarchy hierarchy) {

		String value = properties.getProperty(LogLog.DEBUG_KEY);
		if (value != null) {
			LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
		}

		configureRootLogger(properties, hierarchy);
		parseCats(properties, hierarchy);

		// We don't want to hold references to appenders preventing their
		// garbage collection.
		registry.clear();
	}

	/**
	 * Read configuration options from url <code>configURL</code>.
	 */
	public void doConfigure(java.net.URL configURL, Hierarchy hierarchy) {
		Properties props = new Properties();
		LogLog.debug("Reading URL " + configURL);
		try {
			props.load(configURL.openStream());
		} catch (java.io.IOException e) {
			LogLog.error("Could not read config file [" + configURL + "].", e);
			LogLog.error("Ignoring config file [" + configURL + "].");
			return;
		}
		doConfigure(props, hierarchy);
	}

	// -------------------------------------------------------------------------------
	// Internal stuff
	// -------------------------------------------------------------------------------

	void configureOptionHandler(OptionHandler oh, String prefix,
			Properties props) {
		String[] options = oh.getOptionStrings();
		if (options == null)
			return;

		String value;
		for (int i = 0; i < options.length; i++) {
			value = props.getProperty(prefix + options[i]);
			LogLog.debug("Option " + options[i] + "=["
					+ (value == null ? "null" : value) + "].");
			// Some option handlers assume that null value are not passed to
			// them.
			// So don't remove this check
			if (value != null) {
				oh.setOption(options[i], value);
			}
		}
		oh.activateOptions();
	}

	void configureRootLogger(Properties props, Hierarchy hierarchy) {
		String value = props.getProperty(ROOT_LOGGER_PREFIX);
		if (value == null)
			LogLog.debug("Could not find root Logger information. Is this OK?");
		else {
			Logger root = hierarchy.getRoot();
			synchronized (root) {
				parseLogger(props, root, ROOT_LOGGER_PREFIX,
						INTERNAL_ROOT_NAME, value);
			}
		}
	}

	/**
	 * Parse non-root elements, such non-root Loggers and renderers.
	 */
	protected void parseCats(Properties props, Hierarchy hierarchy) {
		Enumeration enumeration = props.propertyNames();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			if (key.startsWith(LOGGER_PREFIX)) {
				String loggerName = key.substring(LOGGER_PREFIX.length());
				String value = props.getProperty(key);
				Logger cat = hierarchy.getInstance(loggerName);
				synchronized (cat) {
					parseLogger(props, cat, key, loggerName, value);
					parseAdditivityForLogger(props, cat, loggerName);
				}
			}
		}
	}

	/**
	 * Parse the additivity option for a non-root Logger.
	 */
	void parseAdditivityForLogger(Properties props, Logger cat,
			String loggerName) {
		String value = props.getProperty(ADDITIVITY_PREFIX + loggerName);
		LogLog.debug("Handling " + ADDITIVITY_PREFIX + loggerName + "=["
				+ value + "]");
		// touch additivity only if necessary
		if ((value != null) && (!value.equals(""))) {
			boolean additivity = OptionConverter.toBoolean(value, true);
			LogLog.debug("Setting additivity for \"" + loggerName + "\" to "
					+ additivity);
			cat.setAdditivity(additivity);
		}

	}

	/**
	 * This method must work for the root Logger as well.
	 */
	void parseLogger(Properties props, Logger logger, String optionKey,
			String logName, String value) {

		LogLog.debug("Parsing for [" + logName + "] with value=[" + value
				+ "].");
		// We must skip over ',' but not white space
		StringTokenizer st = new StringTokenizer(value, ",");

		// If value is not in the form ", appender.." or "", then we should set
		// the priority of the Logger.

		if (!(value.startsWith(",") || value.equals(""))) {

			// just to be on the safe side...
			if (!st.hasMoreTokens())
				return;

			String priorityStr = st.nextToken();
			LogLog.debug("Priority token is [" + priorityStr + "].");

			// If the priority value is inherited, set Logger priority value to
			// null. We also check that the user has not specified inherited for
			// the
			// root Logger.
			if (priorityStr.equalsIgnoreCase(INHERITED)
					&& !logName.equals(INTERNAL_ROOT_NAME))
				logger.setPriority(null);
			else
				logger.setPriority(Priority.toPriority(priorityStr));
			LogLog.debug("Logger " + logName + " set to "
					+ logger.getPriority());
		}

		// Remove all existing appenders. They will be reconstructed below.
		logger.removeAllAppenders();

		Appender appender;
		String appenderName;
		while (st.hasMoreTokens()) {
			appenderName = st.nextToken().trim();
			if (appenderName == null || appenderName.equals(","))
				continue;
			LogLog.debug("Parsing appender named \"" + appenderName + "\".");
			appender = parseAppender(props, appenderName);
			if (appender != null) {
				logger.addAppender(appender);
			}
		}
	}

	Appender parseAppender(Properties props, String appenderName) {
		Appender appender = registryGet(appenderName);
		if ((appender != null)) {
			LogLog.debug("Appender \"" + appenderName
					+ "\" was already parsed.");
			return appender;
		}
		// Appender was not previously initialized.
		String prefix = APPENDER_PREFIX + appenderName;
		String layoutPrefix = prefix + ".layout";

		appender = (Appender) OptionConverter.instantiateByKey(props, prefix,
				org.apache.log4j.Appender.class, null);
		if (appender == null) {
			LogLog.error("Could not instantiate appender named \""
					+ appenderName + "\".");
			return null;
		}
		appender.setName(appenderName);

		if (appender instanceof OptionHandler) {
			configureOptionHandler((OptionHandler) appender, prefix + ".",
					props);
			LogLog.debug("Parsed \"" + appenderName + "\" options.");

			Layout layout = (Layout) OptionConverter.instantiateByKey(props,
					layoutPrefix, Layout.class, null);
			if (layout != null) {
				appender.setLayout(layout);
				LogLog.debug("Parsing layout options for \"" + appenderName
						+ "\".");
				configureOptionHandler(layout, layoutPrefix + ".", props);
				LogLog.debug("End of parsing for \"" + appenderName + "\".");
			}
		}
		registryPut(appender);
		return appender;
	}

	void registryPut(Appender appender) {
		registry.put(appender.getName(), appender);
	}

	Appender registryGet(String name) {
		return (Appender) registry.get(name);
	}
}
