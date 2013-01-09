package org.avm.elementary.useradmin;


public interface UserSessionServiceInjector {
	public void setUserSessionService(UserSessionService service);
	public void unsetUserSessionService(UserSessionService service);
}
