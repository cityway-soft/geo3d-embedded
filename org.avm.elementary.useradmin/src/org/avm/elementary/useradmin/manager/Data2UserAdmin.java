package org.avm.elementary.useradmin.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class Data2UserAdmin {

	private Logger _log;

	private UserAdminManager _uam;

	public Data2UserAdmin(String prefsFilename) {
		_log = Logger.getInstance(this.getClass());
	}

	private void init(String[] groups, Properties defaultUsers) {
		for (int i = 0; i < groups.length; i++) {
			_uam.createGroup(groups[i]);
		}

		Properties p = defaultUsers;
		Enumeration en = p.elements();
		while (en.hasMoreElements()) {
			Properties allUserProperties = (Properties) en.nextElement();

			Enumeration en2 = allUserProperties.keys();
			String matricule = null;
			Properties userCredentials = new Properties();
			Properties userGroups = new Properties();
			Properties userProperties = new Properties();
			while (en2.hasMoreElements()) {
				String key = (String) en2.nextElement();
				if (key.startsWith(UserAdminManagerConfig.USER_TAG)) {
					matricule = allUserProperties.getProperty(key);
					userProperties.put(UserAdminManagerConfig.MATRICULE_TAG,
							matricule);
				} else if (key
						.startsWith(UserAdminManagerConfig.CREDENTIAL_TAG)) {
					String tag = key.substring(key.indexOf(".") + 1);
					String value = allUserProperties.getProperty(key);
					userCredentials.put(tag, value);
				} else if (key.startsWith(UserAdminManagerConfig.GROUPS_TAG)) {
					String group = allUserProperties.getProperty(key);
					userGroups.put(key, group);
				} else if (key.startsWith(UserAdminManagerConfig.PROPERTY_TAG)) {
					String tag = key.substring(key.indexOf(".") + 1);
					String value = allUserProperties.getProperty(key);
					userProperties.put(tag, value);
				}

			}

			_uam.createUser(matricule, userProperties, userCredentials);
			en2 = userGroups.elements();
			while (en2.hasMoreElements()) {
				String group = (String) en2.nextElement();
				try {
					_uam.addMember(matricule, group);
				} catch (NoSuchFieldException e) {
					_log.error("Error when adding member to group " + group
							+ ": " + e.toString());
				}
			}

		}

	}

	public void initialize(String filename, String copyFilename,
			String[] groups, Properties defaultUsers) throws IOException {
		init(groups, defaultUsers);
		BufferedReader fic = null;
		BufferedWriter fout = null;

		try {
			fic = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename), "ISO-8859-1"));
			fout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(copyFilename), "ISO-8859-1"));
			String line = fic.readLine();
			// idu;prenom;nom;codesecret;collection de roles sep par ,
			int count = 1;
			while (line != null) {
				if (!line.trim().equals("") && !line.startsWith("#")) {
					try {
						parseLine(line);
					} catch (Throwable e) {
						_log.error("line #" + count + " : " + line + "\n", e);
						e.printStackTrace();
					}
				}
				fout.write(line);
				fout.write(System.getProperty("line.separator"));
				line = fic.readLine();
				count++;
			}
		} catch (IOException e) {
			_log.error("Error when initialize data from : " + filename);
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			if (fic != null) {
				fic.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
	}

	private String getNextToken(StringTokenizer t) {
		String val = t.nextToken();
		if (";".equals(val))
			val = "";
		else if (t.hasMoreTokens())
			t.nextToken();
		return val;
	}

	public void parseLine(String line) throws Throwable {
		String idu = null, prenom = null, nom = null, codeS = null, roles = null;
		StringTokenizer t = new StringTokenizer(line, ";", true);
		idu = getNextToken(t);
		prenom = getNextToken(t);
		nom = getNextToken(t);
		codeS = getNextToken(t);
		roles = getNextToken(t);

		t = null;

		Properties p = new Properties();
		p.put(UserAdminManagerConfig.NOM_TAG, nom);
		p.put(UserAdminManagerConfig.PRENOM_TAG, prenom);
		p.put(UserAdminManagerConfig.MATRICULE_TAG, idu);

		Properties c = new Properties();
		c.put(UserAdminManagerConfig.CODESECRET_TAG, codeS);

		if (_log.isDebugEnabled()) {
			_log.debug("Creating user " + idu + " " + nom + " properties=" + p
					+ " cred=" + c);
		}
		_uam.createUser(idu, p, c);

		if (roles != null) {
			StringTokenizer str = new StringTokenizer(roles, ",");
			while (str.hasMoreTokens()) {
				String role = str.nextToken();

				if (_log.isDebugEnabled()) {
					_log.debug("adding member " + idu + " to " + role);
				}

				try {
					_uam.addMember(idu, role);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public void setUserAdminManager(UserAdminManagerImpl userAdminManagerImpl) {
		_uam = userAdminManagerImpl;
	}

}
