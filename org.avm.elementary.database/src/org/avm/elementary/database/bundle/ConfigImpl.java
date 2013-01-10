package org.avm.elementary.database.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.database.impl.DatabaseConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements DatabaseConfig {

	public static String LOGIN_TAG = "login";
	public static String PASSWORD_TAG = "password";
	public static String URL_CONNECTION_TAG = "url.connection";
	public static String VERSION_TAG = "version";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected String getPid() {
		return Activator.getDefault().getPid();
	}

	public String getLogin() {
		return (String) _config.get(LOGIN_TAG);
	}

	public void setLogin(String login) {
		_config.put(LOGIN_TAG, login);
	}

	public String getPassword() {
		return (String) _config.get(PASSWORD_TAG);
	}

	public void setPassword(String password) {
		_config.put(PASSWORD_TAG, password);
	}

	public String getUrlConnection() {
		return (String) _config.get(URL_CONNECTION_TAG);
	}

	public void setUrlConnection(String uri) {
		_config.put(URL_CONNECTION_TAG, uri);
	}

	public String getVersion() {
		return (String) _config.get(VERSION_TAG);
	}

	public void setVersion(String version) {
		_config.put(VERSION_TAG, version);
	}

}
