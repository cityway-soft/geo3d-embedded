package org.avm.elementary.useradmin.manager;

import java.util.Properties;

import org.osgi.service.useradmin.Role;

public interface UserAdminManager {

	public void removeUser(String user);

	public void createUser(String sUser, Properties p, Properties credentials);

	public void createGroup(String group);

	public void addMember(String sUser, String sGroup)
			throws NoSuchFieldException;

	public void removeMember(String sUser, String sGroup);

	public Role[] getRoles();

	public void reinitMembers() throws Throwable;

	public void removeAllUsers() throws Throwable;


}
