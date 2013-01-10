package org.avm.elementary.directory.bundle;

import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.directory.impl.CMDataXLMSerializer;
import org.avm.elementary.directory.impl.CMDataXMLParser;
import org.avm.elementary.directory.impl.DirectoryConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements DirectoryConfig {

	public static String FILENAME_TAG = "filename";
	public static String VERSION_TAG = "version";

	private Properties _mainProperties;

	public ConfigImpl(final ComponentContext context,
			final ConfigurationAdmin cm) {
		super(context, cm);
		//updateProperties();
	}

	public boolean loadProperties(String filename) {
		if (filename == null) {
			final Object[] arguments = { System.getProperty("org.avm.home") };
			final String text = MessageFormat.format(getFileName(), arguments);
			filename = text + "/config.xml";
		}
		_mainProperties = CMDataXMLParser.parseXML(filename);
		return _mainProperties != null;
	}
	
	public void createDefaultProperties (){
		_mainProperties = new Properties ();
	}

	protected Dictionary getDefault() {
		final Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public boolean addProperty(final String key, final Properties p) {
		boolean ret = false;
		final String text = save(p);
		//_config.put(key, text);
		if (_mainProperties != null) {
			_mainProperties.put(key, text);
			ret = true;
		}
		return ret;
	}
	
	public Properties getProperty(final String key) {
		if (key == null) {
			return getProperties();
		} else {
			if (_mainProperties != null) {
				final String text = (String) _mainProperties.get(key);
				return load(text);
			}
			return null;
		}
	}
	
	public void removeProperty(final String key) {
		if (_mainProperties != null) {
			_mainProperties.remove(key);
		}
	}

	private Properties getProperties() {
		final Properties props = new Properties();
		if (_mainProperties != null) {
			for (final Enumeration it = _mainProperties.keys(); it
					.hasMoreElements();) {
				final String key = (String) it.nextElement();
				if ("service.pid".equals(key) || "config.date".equals(key)
						|| "service.bundleLocation".equals(key)
						|| "org.avm.elementary.dummy.property".equals(key)
						|| "org.avm.config.version".equals(key)) {
					continue;
				}
				final Properties p = getProperty(key);
				if (p != null) {
					props.put(key, p);
				}
			}
		}
		return props;
	}

	public String toString() {
		final StringBuffer text = new StringBuffer();
		text.append(super.toString());
		text.append("\nDataDictionary");
		if (_mainProperties != null) {
			for (final Enumeration it = _mainProperties.keys(); it
					.hasMoreElements();) {
				final String key = (String) it.nextElement();
				if ("service.pid".equals(key) || "config.date".equals(key)
						|| "service.bundleLocation".equals(key)
						|| "org.avm.elementary.dummy.property".equals(key)
						|| "org.avm.config.version".equals(key)) {
					continue;
				}
				final Properties p = getProperty(key);
				if (p != null) {
					text.append(p.toString() + "\n");
				}
			}
		}
		return text.toString();
	}

	public String getFileName() {
		return (String) _config.get(FILENAME_TAG);
	}

	public void setFileName(final String uri) {
		_config.put(FILENAME_TAG, uri);
	}

	public String getVersion() {
		return (String) _config.get(VERSION_TAG);
	}

	public void setVersion(final String version) {
		_config.put(VERSION_TAG, version);
	}

	public boolean saveProperties(String filename) {
		if (filename == null) {
			final Object[] arguments = { System.getProperty("org.avm.home") };
			final String text = MessageFormat.format(getFileName(), arguments);
			filename = text + "/config.xml";
		}
		if (_mainProperties != null){
		CMDataXLMSerializer.save(filename, _mainProperties);
		return true;
		}
		return false;
	}

}
