package org.avm.elementary.useradmin.manager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.bundle.ConfigImpl;
import org.avm.elementary.useradmin.core.UserAdminInjector;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

public class UserAdminManagerImpl implements UserAdminManager,
		ConfigurableService, ManageableService, UserAdminInjector{


	private Logger _log;

	private ConfigImpl _config;;

	private String _preferenceFilename;

	private UserAdminControler _userAdminControler;

	public UserAdminManagerImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void start() {
		if (needRebuild()) {
			try {
				reinitMembers();
				needRebuild();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
	}

	public UserAdmin getUserAdminService(){
		return _userAdminControler.getUserAdminService();
	}


	private boolean needRebuild(){
		
		//-- nb users in useradmin base
		Role[] roles = getRoles();
		int nUseradminBase = 0;
		for (int i = 0; i < roles.length; i++) {
			Role role = roles[i];
			if (role instanceof Group) {
				// ignore
			} else if (role instanceof User) {
				nUseradminBase++;
			}
		}
		
		//-- nb default users (config)
		int nDefault=0;
		Properties p =  _config.getDefaultUsers();;
		Enumeration en = p.elements();
		while (en.hasMoreElements()) {
			Properties allUserProperties = (Properties) en.nextElement();
			
			Enumeration en2 = allUserProperties.keys();
			while (en2.hasMoreElements()) {
				String key = (String) en2.nextElement();
				if (key.startsWith(UserAdminManagerConfig.USER_TAG)){
					nDefault++;
				}
			}
		}
		
		
		//-- nb users in bunde data files
		Object[] arguments = { System.getProperty("org.avm.home") };
		String filename = MessageFormat.format(
				((UserAdminManagerConfig) _config).getFileName(), arguments);
		BufferedReader fic;
		int nb = 0;
		try {
			fic = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO8859-1"));
			String line = fic.readLine();
			while (line != null) {
				if (line.trim().length() != 0){
					nb++;
					//_log.info("#" + nb + "line:" + line );
				}
				line = fic.readLine();
			}
		} catch (IOException e) {
			
		}

		boolean rebuild=(nUseradminBase != (nb+nDefault)) || (nUseradminBase == 0);
		_log.info("#users in config="+nDefault+", #users in base="+nUseradminBase+", #users in datafiles="+nb+" (rebuild=" + rebuild +")");
		_log.info("exist 'admin' ? :"+(exists("admin")));
		return rebuild;
	}
	
	
	public void configure(Config config) {
		_config = (ConfigImpl) config;
	}

	public boolean exists(String role) {
		return getUserAdminService().getRole(role) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.avm.elementary.useradmin.manager.UserAdminManager#createUser(java
	 * .lang.String, java.util.Properties, java.util.Properties) sUser =
	 * identifiant de l'utilisateur (matricule dans notre cas) p = Proprietes de
	 * l'utilisateur et un ou plusieurs tags qui permettent de l'identifier. On
	 * y met le nom, Prenom et le matricule. c = Ce qui est necessaire pour
	 * l'authentifier. On y met le code secret.
	 */
	public void createUser(String sUser, Properties p, Properties c) {
		if (getUserAdminService() == null)
			return;
		User user;
		user = (User) getUserAdminService().getRole(sUser);
		if (user == null) {
			// long t0 = System.currentTimeMillis();
			user = (User) getUserAdminService().createRole(sUser, Role.USER);
			// long delta = System.currentTimeMillis() - t0;
			// System.out.println("Creating user " + user + " : " + delta
			// + " secs.");
		}
		Dictionary dic = user.getProperties();
		Enumeration e = p.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = p.getProperty(key);
			dic.put(key, value);
		}

		dic = user.getCredentials();
		e = c.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = c.getProperty(key);
			dic.put(key, value);
		}
	}

	public void createGroup(String group) {
		if (!exists(group)) {
			getUserAdminService().createRole(group, Role.GROUP);
		}
	}

	public void addMember(String sUser, String sGroup)
			throws NoSuchFieldException {
		if (getUserAdminService() != null) {
			Group group = (Group) getUserAdminService().getRole(sGroup);
			if (group != null) {
				Role role = getUserAdminService().getRole(sUser);
				group.addMember(role);
			} else {
				throw new NoSuchFieldException("Pas de groupe " + sGroup);
			}
		}
	}

	public void addRequiredMember(String sUser, String sGroup)
			throws NoSuchFieldException {
		Group group = (Group) getUserAdminService().getRole(sGroup);
		if (group != null) {
			Role role = getUserAdminService().getRole(sUser);
			group.addRequiredMember(role);
		} else {
			throw new NoSuchFieldException("Pas de groupe " + sGroup);
		}

	}

	public void removeMember(String sUser, String sGroup) {
		Group group = (Group) getUserAdminService().getRole(sGroup);
		Role role = getUserAdminService().getRole(sUser);
		group.removeMember(role);
	}

	public void removeUser(String sUser) {
		getUserAdminService().removeRole(sUser);
	}

	public void removeAllUsers() throws Throwable {
		Role[] roles = getRoles();
		for (int i = 0; i < roles.length; i++) {
			Role r = roles[i];
			if (r.getType() == Role.USER) {
				getUserAdminService().removeRole(r.getName());
			}
		}
	}

	public Role[] getRoles() {
		try {
			return getUserAdminService().getRoles(null);
		} catch (InvalidSyntaxException e) {
			return null;
		}
	}

	public User authenticate(String tagid, String id, String tagkey, String key) {
		User user = getUserAdminService().getUser(tagid, id);
		if (user == null)
			throw new SecurityException("No such user");
		if (!user.hasCredential(tagkey, key))
			throw new SecurityException("Invalid password");

		return user;
	}
	

	// LBR : quand un nouveau fichier de USERS est disponible, il faut
	// reinitialiser la liste des utilisateurs.
	public void reinitMembers() throws Throwable {
		_log.info("rebuild useradmin base");
		removeAllUsers();
		Object[] arguments = { System.getProperty("org.avm.home") };
		String filename = MessageFormat
				.format(_config.getFileName(), arguments);
		String groups[] = _config.getGroups();
		Properties defaultUsers = _config.getDefaultUsers();
		Data2UserAdmin ua = new Data2UserAdmin(_preferenceFilename);
		ua.setUserAdminManager(this);
		try {
			ua.initialize(filename, groups, defaultUsers);
		} catch (IOException e) {
			_log.error(e);
		} 
		_userAdminControler.restart();
	}

	public void setUserAdminPreferenceFilename(String preferenceFilename) {
		_preferenceFilename = preferenceFilename;
	}

	public void setUserAdminControler(UserAdminControler controler) {
		_userAdminControler = controler;
	}


}
