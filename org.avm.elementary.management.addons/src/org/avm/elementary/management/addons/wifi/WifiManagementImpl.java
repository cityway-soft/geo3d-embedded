package org.avm.elementary.management.addons.wifi;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.util.measurement.State;

/**
 * @author MERCUR : Didier LALLEMAND
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 * 
 */
public class WifiManagementImpl implements WifiManagement {

	private Logger _logger;

	private ManagementService _management;

	private org.avm.elementary.log4j.Logger _syslog;

	public WifiManagementImpl(ManagementService management) {
		_logger = Logger.getInstance(this.getClass());
		_logger.setPriority(Priority.DEBUG);
		_management = management;
	}

	public void notify(Object o) {
		State state = (State) o;
		boolean isconnected = state.getValue() > 0;
		_logger.debug("WifiStatut : "
				+ ((isconnected) ? "connected" : "disconnected"));
		if (isconnected) {
			_logger
					.info("Try 'update automatic' (wifi is currently available)");
			try {
				_management
						.setDownloadURL(new URL(
								System
										.getProperty(org.avm.elementary.management.core.Management.PRIVATE_DOWNLOAD_URL_TAG)));
				_management
						.setUploadURL(new URL(
								System
										.getProperty(org.avm.elementary.management.core.Management.PRIVATE_UPLOAD_URL_TAG)));
				_management.synchronize(new PrintWriter(System.out));
			} catch (Exception e) {
				_logger.error("Wifi/management synchronization failed.", e);
			}
		} else {
			try {
				_management.setDownloadURL(null);
				_management.setUploadURL(null);
			} catch (MalformedURLException e) {
				_logger
						.error(
								"Wifi/management set default management url failed.",
								e);
			}

			if (_syslog != null) {
				_syslog.disableSyslog();
			}
		}
	}

	public void setSyslog(org.avm.elementary.log4j.Logger syslog) {
		_syslog = syslog;
	}
}