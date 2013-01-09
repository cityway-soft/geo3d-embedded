package org.avm.elementary.useradmin.manager;

import org.osgi.service.useradmin.UserAdmin;

public interface UserAdminControler {
	public void restart();
	public UserAdmin getUserAdminService();
}
