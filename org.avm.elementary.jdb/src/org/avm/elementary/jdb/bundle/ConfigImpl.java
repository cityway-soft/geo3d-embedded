package org.avm.elementary.jdb.bundle;

import java.util.Dictionary;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.jdb.impl.JDBConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements JDBConfig {

	public static String FILENAME_TAG = "filename";

	private static final String PATTERN_TAG = "pattern";

	private static final String SIZE_TAG = "size";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public String getFilename() {
		return (String) _config.get(FILENAME_TAG);
	}

	public void setFilename(String filename) {
		_config.put(FILENAME_TAG, filename);
	}

	public String getPattern() {
		return (String) _config.get(PATTERN_TAG);
	}

	public void setPattern(String pattern) {
		_config.put(PATTERN_TAG, pattern);

	}

	public int getSize() {
		return ((Integer) _config.get(SIZE_TAG)).intValue();
	}

	public void setSize(int size) {
		_config.put(SIZE_TAG, new Integer(size));
	}

}
