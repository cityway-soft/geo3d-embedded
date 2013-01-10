package org.avm.elementary.database.impl;

public interface DatabaseConfig {

	public String getLogin();

	public void setLogin(String login);

	public String getPassword();

	public void setPassword(String password);

	public String getUrlConnection();

	public void setUrlConnection(String uri);

	public String getVersion();

	public void setVersion(String version);
}
