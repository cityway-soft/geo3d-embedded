package org.avm.elementary.useradmin.manager;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.avm.elementary.common.AbstractManager;
import org.avm.elementary.common.Config;
import org.avm.elementary.useradmin.bundle.Activator;
import org.osgi.framework.BundleContext;

public class UserDataManager extends AbstractManager {

	protected UserAdminManagerConfig _config;
	private UserAdminManager _peer;

	public UserDataManager(BundleContext context, UserAdminManagerConfig config)
			throws Exception {
		super(context,Activator.getDefault().getLogger());
		_config = config;
	}

	protected URL getUrlRepository() {
		_log.debug("getUrlRepository()");
		URL url = null;
		try {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat.format(_config.getFileName(),
					arguments);
			url = new URL("file://" +text);
			_log.debug("url = " + url);
		} catch (MalformedURLException e) {
			_log.error(e.getMessage());
		}

		return url;
	}

	public void setPeer(UserAdminManager uam) {
		_peer = uam;
	}

	protected void updateUrlRepository(URL url, String version) {
		_log.debug("updateUrlRepository");
		_config.setFileName(url.getPath());
		((Config) _config).updateConfig(_started);
		_log.debug("launch reinitMembers....");
		try {
			_peer.reinitMembers();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String getDeployerPID() {
		String pid = Activator.getDefault().getPid();
		String result = pid.substring(0, pid.lastIndexOf('.'));
		return result + ".data";
	}

}
