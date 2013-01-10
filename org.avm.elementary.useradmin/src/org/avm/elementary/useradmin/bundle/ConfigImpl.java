package org.avm.elementary.useradmin.bundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.useradmin.manager.UserAdminManagerConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements
		UserAdminManagerConfig {

	private String[] _groups;

	private static final String TAG_USER = "user.";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public boolean isAuthentication() {
		String s = (String) _config.get(AUTHENTICATE_TAG);
		return (s != null && s.toLowerCase().equals("true"));
	}

	public void setAuthentication(boolean b) {
		_config.put(AUTHENTICATE_TAG, (b ? "true" : "false"));
	}

	public String getFileName() {
		return (String) _config.get(FILENAME_TAG);
	}

	public void setFileName(String fileName) {
		_config.put(FILENAME_TAG, fileName);
	}

	public void addProperty(String key, Properties p) {
		String text = save(p);
		_config.put(key, text);
	}

	public String getProperty(String key) {
		String text = (String) _config.get(key);
		return text;
	}

	public Properties getProperties() {
		Properties props = new Properties();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			String value = getProperty(key);
			if (value != null) {
				props.put(key, value);
			}
		}
		return props;
	}

	public void removeProperty(String key) {
		_config.remove(key);
	}

	public String toString() {
		StringBuffer text = new StringBuffer();

		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			String value = getProperty(key);
			if (value != null) {
				text.append(key);
				text.append("=");
				text.append(value);
				text.append("\n");
			}
		}
		return text.toString();
	}

	public String[] getGroups() {
		if (_groups == null) {
			String groups = getProperty("groups");
			StringTokenizer t = new StringTokenizer(groups, ",");
			int size = t.countTokens();
			_groups = new String[size];
			int i = 0;
			while (t.hasMoreElements()) {
				String object = (String) t.nextElement();
				_groups[i] = object;
				i++;
			}
		}
		return _groups;
	}

	public Properties getDefaultUsers() {
		Properties users = new Properties();
		Properties props = getProperties();
		Enumeration e = props.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (key.startsWith(TAG_USER)) {
				String val = props.getProperty(key);
				Properties p = new Properties();
				ByteArrayInputStream in = new ByteArrayInputStream(val
						.getBytes());
				try {
					p.load(in);
					in.close();
				} catch (IOException ex) {
				}
				users.put(key, p);
			}

		}

		return users;
	}

	public String[] getDefaultRoles() {
		String s = (String) _config.get(DEFAULT_ROLES_TAG);
		String[] values = null;
		if (s != null) {
			StringTokenizer t = new StringTokenizer(s, ";");
			values = new String[t.countTokens()];
			int i = 0;
			while (t.hasMoreElements()) {
				String role = (String) t.nextElement();
				values[i] = role;
				i++;
			}
		}
		return values;
	}

	public void setDefaultRoles(String[] roles) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < roles.length; i++) {
			buf.append(roles[i]);
			buf.append(";");
		}
		_config.put(DEFAULT_ROLES_TAG, buf.toString());
	}

}
