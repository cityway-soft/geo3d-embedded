package org.avm.elementary.useradmin.manager;

import java.util.Properties;

public interface UserAdminManagerConfig {

	public static final String FILENAME_TAG = "filename";
	public static final String MATRICULE_TAG = "matricule";
	public static final String NOM_TAG = "nom";
	public static final String PRENOM_TAG = "prenom";
	public static final String CODESECRET_TAG = "codesecret";
	public static final String GROUPES_TAG = "groupes";
	
	public static final String USER_TAG = "user";
	public static final String CREDENTIAL_TAG = "credential";
	public static final String GROUPS_TAG = "groups";
	public static final String PROPERTY_TAG = "property";
	public static final String AUTHENTICATE_TAG = "authenticate";

	public static final String DEFAULT_ROLES_TAG = "default-roles";


	public void addProperty(String key, Properties properties);

	public void removeProperty(String key);

	public String getProperty(String key);

	public Properties getProperties();

	public String getFileName();

	public void setFileName(String path);

	public boolean isAuthentication();

	public void setAuthentication(boolean b);
	
	public String[] getDefaultRoles();

	public void setDefaultRoles(String[] roles);
}
