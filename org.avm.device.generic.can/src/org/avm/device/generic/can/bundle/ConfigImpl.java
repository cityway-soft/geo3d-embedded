package org.avm.device.generic.can.bundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.avm.device.generic.can.CanConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements CanConfig {

	public static final String ID = "id";
	public static final String FILTER = "filter";
	public static final String MASK = "mask";

	private static final String BUFFER_SIZE_TAG = "buffersize";
	private static final String SLEEP_TIME_TAG = "sleep";
	private static final String THREAD_PRIORITY_TAG = "priority";
	private static final String DRIVER_FILTERS_TAG = "filters";
	public static final String URL_TAG = "url";
	public static final String FILTER_TAG = "filter";
	public static final String MODE_TAG = "mode";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected String getResourceFileName() {
		return "config-can.xml";
	}

	public String getUrl() {
		return (String) _config.get(URL_TAG);
	}

	public void setUrl(String uri) {
		_config.put(URL_TAG, uri);
	}

	public String getFilter() {
		return (String) _config.get(FILTER_TAG);
	}

	public void setFilter(String filter) {
		_config.put(FILTER_TAG, filter);
	}

	public String getMode() {
		return (String) _config.get(MODE_TAG);
	}

	public void setMode(String mode) {
		_config.put(MODE_TAG, mode);
	}

	public int getBufferSize() {
		return Integer.parseInt((String) _config.get(BUFFER_SIZE_TAG));
	}

	public void setBufferSize(int bufferSize) {
		_config.put(BUFFER_SIZE_TAG, "" + bufferSize);
	}

	public long getSleepTime() {
		return Long.parseLong((String) _config.get(SLEEP_TIME_TAG));
	}

	public void setSleepTime(long sleepTime) {
		_config.put(SLEEP_TIME_TAG, "" + sleepTime);
	}

	public int getThreadPriority() {
		return Integer.parseInt((String) _config.get(THREAD_PRIORITY_TAG));
	}

	public void setThreadPriority(int threadPriority) {
		_config.put(THREAD_PRIORITY_TAG, "" + threadPriority);
	}

	public List getDriverFilters() {
		List result = new ArrayList();
		String[] filters = (String[]) _config.get(DRIVER_FILTERS_TAG);
		if (filters != null) {
			for (int i = 0; i < filters.length; i++) {
				Properties p = load(filters[i]);
				result.add(p);
			}
		}
		return result;
	}

	public void setDriverFilters(List list) {
		String[] filters = new String[list.size()];
		int i = 0;
		for (Iterator iterator = list.iterator(); iterator.hasNext(); i++) {
			Properties p = (Properties) iterator.next();
			filters[i] = save(p);
		}
		_config.put(DRIVER_FILTERS_TAG, filters);
	}

	public String getUrlConnection() {
		StringBuffer text = new StringBuffer();
		text.append(getUrl());
		if (getBufferSize() > 0) {
			text.append(";" + BUFFER_SIZE_TAG + "=" + getBufferSize());
		}
		int i = 0;
		for (Iterator iterator = getDriverFilters().iterator(); iterator
				.hasNext();) {
			Properties p = (Properties) iterator.next();
			String filter = p.getProperty(ConfigImpl.FILTER);
			String mask = p.getProperty(ConfigImpl.MASK);
			text.append(";" + ConfigImpl.FILTER + i + "=" + filter);
			text.append(";" + ConfigImpl.MASK + i + "=" + mask);
			i++;
		}

		return text.toString();
	}

}
