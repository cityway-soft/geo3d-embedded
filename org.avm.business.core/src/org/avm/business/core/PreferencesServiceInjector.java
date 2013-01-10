package org.avm.business.core;

import org.osgi.service.prefs.PreferencesService;

public interface PreferencesServiceInjector {
	public void setPreferencesService(PreferencesService prefs);

	public void unsetPreferencesService(PreferencesService prefs);
}
