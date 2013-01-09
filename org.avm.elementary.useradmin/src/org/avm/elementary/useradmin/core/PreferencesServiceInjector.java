package org.avm.elementary.useradmin.core;

import org.osgi.service.prefs.PreferencesService;

public interface PreferencesServiceInjector {
	public void setPreferencesService(PreferencesService ps);
}
