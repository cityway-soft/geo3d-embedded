package org.avm.elementary.common;

import java.util.Enumeration;
import java.util.Properties;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class AbstractDeviceConfig extends AbstractConfig implements
		DeviceConfig {

	public AbstractDeviceConfig(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getCategory() {
		return (String) _config.get(DEVICE_CATEGORY);
	}

	public String getManufacturer() {
		return (String) _config.get(DEVICE_MANUFACTURER);
	}

	public String getModel() {
		return (String) _config.get(DEVICE_MODEL);
	}

	public String getSerial() {
		return (String) _config.get(DEVICE_SERIAL);
	}

	public void setCategory(String category) {
		_config.put(DEVICE_CATEGORY, category);
	}

	public void setManufacturer(String manufacturer) {
		_config.put(DEVICE_MANUFACTURER, manufacturer);
	}

	public void setModel(String model) {
		_config.put(DEVICE_MODEL, model);
	}

	public void setSerial(String serial) {
		_config.put(DEVICE_SERIAL, serial);
	}

	public String getDescription() {
		return (String) _config.get(DEVICE_DESCRIPTION);
	}

	public String getName() {
		return (String) _config.get(DEVICE_NAME);
	}

	public String getVersion() {
		return (String) _config.get(DEVICE_VERSION);
	}

	public void setDescription(String description) {
		_config.put(DEVICE_DESCRIPTION, description);
	}

	public void setName(String name) {
		_config.put(DEVICE_NAME, name);
	}

	public void setVersion(String version) {
		_config.put(DEVICE_VERSION, version);
	}

	public String getParamerter(String name) {
		return getParameters().getProperty(name);
	}

	public void setParameters(String name, String value) {
		Properties p = getParameters();
		p.setProperty(name, value);
		putParameters(p);
	}

	public Properties getParameters() {
		Properties parametersp;
		String text = (String) _config.get("parameters");
		if (text != null) {
			parametersp = load(text);
		} else {
			parametersp = new Properties();
		}

		return parametersp;
	}

	public void putParameters(Properties p) {
		String text = save(p);
		_config.put("parameters", text);
	}

	public String toString() {
		StringBuffer text = new StringBuffer();
		text.append(DEVICE_CATEGORY + " : " + getCategory() + "\n");
		text.append(DEVICE_DESCRIPTION + " : " + getDescription() + "\n");
		text.append(DEVICE_MANUFACTURER + " : " + getManufacturer() + "\n");
		text.append(DEVICE_MODEL + " : " + getModel() + "\n");
		text.append(DEVICE_NAME + " : " + getName() + "\n");
		text.append(DEVICE_SERIAL + " : " + getSerial() + "\n");
		text.append(DEVICE_VERSION + " : " + getVersion() + "\n");

		Properties p = getParameters();
		for (Enumeration iterator = p.keys(); iterator.hasMoreElements();) {
			String name = (String) iterator.nextElement();
			text.append(name + " : " + p.getProperty(name) + "\n");
		}

		return text.toString();
	}

}
