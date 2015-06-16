package org.avm.elementary.useradmin.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
		ConfigurableService, ManageableService, UserAdminInjector {

	private Logger _log;

	private ConfigImpl _config;;

	private String _preferenceFilename;

	private UserAdminControler _userAdminControler;

	private UserAdmin userAdminService = null;

	private boolean reinitMemberRequest;

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

	public UserAdmin getUserAdminService() {
		return userAdminService;
	}

	private String getUserCopyFilename(String userFilename) {
		File newFile = new File(userFilename);
		String csvUserCopy = newFile.getParent() + "/installed-"
				+ newFile.getName();
		return csvUserCopy;
	}

	private boolean needRebuild() {
		boolean rebuild = false;

		Object[] arguments = { System.getProperty("org.avm.home") };
		String csvUserNew = MessageFormat.format(_config.getFileName(),
				arguments);
		String cvsuserCopy = getUserCopyFilename(csvUserNew);
		File oldFile = new File(cvsuserCopy);
		if (oldFile.exists() == false) {
			return true;
		}

		File newFile = new File(csvUserNew);
		String md5New = genMD5(newFile);
		String md5Old = genMD5(oldFile);

		rebuild = !md5New.equals(md5Old);

		return rebuild;
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
		if (_config != null) {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String csvUserFilename = MessageFormat.format(
					_config.getFileName(), arguments);
			File file = new File(csvUserFilename);
			if (file.getParentFile().exists() == false) {
				file.getParentFile().mkdir();
			}
		}
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
			user = (User) getUserAdminService().createRole(sUser, Role.USER);
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
				if (getUserAdminService() != null) {
					getUserAdminService().removeRole(r.getName());
				}
			}
		}
	}

	public Role[] getRoles() {
		if (getUserAdminService() != null) {
			try {
				return getUserAdminService().getRoles(null);
			} catch (InvalidSyntaxException e) {
			}
		}
		return null;
	}

	public User authenticate(String tagid, String id, String tagkey, String key) {
		User user = null;
		if (getUserAdminService() != null) {

			user = getUserAdminService().getUser(tagid, id);
			if (user == null)
				throw new SecurityException("No such user");
			if (!user.hasCredential(tagkey, key))
				throw new SecurityException("Invalid password");

		}
		return user;
	}

	public static String genMD5(File file) {
		MessageDigest md;
		StringBuffer output = null;
		try {
			md = MessageDigest.getInstance("MD5");
			InputStream inStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = inStream.read(buffer)) > 0) {
				md.update(buffer, 0, read);
			}
			byte[] md5sum = md.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			output = new StringBuffer(bigInt.toString(16));

			inStream.close();

			while (output.length() < 32) {
				output.insert(0, "0");
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	public static void writeMD5(String md5, File file) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter(
				file.getAbsolutePath() + ".md5"));
		br.write(md5);
		br.write("  ");
		br.write(file.getName());
		br.write("\n");
		br.close();
	}

	public static boolean checkFile(File md5file) {
		boolean result = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(md5file.getAbsolutePath()));
			String line = br.readLine();
			int idx = line.indexOf(" ");
			String md5 = line.substring(0, idx).trim();
			String filename = line.substring(idx + 1).trim();

			String genmd5 = genMD5(new File(md5file.getParentFile()
					.getAbsoluteFile() + "/" + filename));

			result = (genmd5 != null && md5.equals(genmd5));
			System.out.println("genmd5=" + genmd5 + ", md5=" + md5 + "(check="
					+ result + ")");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	// LBR : quand un nouveau fichier de USERS est disponible, il faut
	// reinitialiser la liste des utilisateurs.
	public void reinitMembers() throws Throwable {
		if (userAdminService != null) {
			_log.info("rebuild useradmin base");
			removeAllUsers();
			Object[] arguments = { System.getProperty("org.avm.home") };
			String filename = MessageFormat.format(_config.getFileName(),
					arguments);
			String groups[] = _config.getGroups();
			Properties defaultUsers = _config.getDefaultUsers();
			Data2UserAdmin ua = new Data2UserAdmin(_preferenceFilename);
			ua.setUserAdminManager(this);
			try {
				String copyFilename = getUserCopyFilename(filename);
				ua.initialize(filename, copyFilename, groups, defaultUsers);
				reinitMemberRequest = false;
			} catch (IOException e) {
				_log.error(e);
			}
			_userAdminControler.restart();
		} else {
			_log.warn("userAdminService not available");
			reinitMemberRequest = true;
		}
	}

	public void setUserAdminPreferenceFilename(String preferenceFilename) {
		_preferenceFilename = preferenceFilename;
	}

	public void setUserAdminControler(UserAdminControler controler) {
		_userAdminControler = controler;
	}

	public void setUserAdmin(UserAdmin ua) {
		userAdminService = ua;
		if (ua != null) {
			if (reinitMemberRequest) {
				try {
					_log.info("Re-init memberd request...");
					reinitMembers();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

}
