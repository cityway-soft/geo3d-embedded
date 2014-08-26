package org.avm.elementary.management.addons.usb;

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
public class USBManagementImpl implements USBManagement {

	private Logger _logger;

	private ManagementService _management;

	public USBManagementImpl(ManagementService management) {
		_logger = Logger.getInstance(this.getClass());
		_logger.setPriority(Priority.DEBUG);
		_management = management;
	}

	public void notify(Object o) {
		try {
			State state = (State) o;
			boolean isconnected = state.getValue() > 0;
			_logger.debug("USBStatut : "
					+ ((isconnected) ? "connected" : "disconnected"));
			if (isconnected) {
				_logger.info("Try to run script (usb is currently available)");
				String mount = getMountPoint(state);

				try {
					_management.setDownloadURL(new URL("file:////" + mount+ "/avm/$n/bundles"));
					_management.setUploadURL(new URL("file:////" + mount	+ "/avm/upload"));
					execute(mount);
				} catch (Exception e) {
					_logger.error("Usb/management synchronization failed.", e);
				}
			} else {
				try {
					_management.setDownloadURL(null);
					_management.setUploadURL(null);
				} catch (Exception e) {
					_logger.error("Usb/management synchronization failed.", e);
				}

			}
		} catch (Throwable t) {
			_logger.error("notify : " + t.getMessage(), t);
		}
		finally{
			try {
				_management.setDownloadURL(null);
				_management.setUploadURL(null);
			} catch (Exception e) {
				_logger.error("Usb/management synchronization failed.", e);
			}
		}
	}

	private String getMountPoint(State state) {
		String name = state.getName();
		String mount = name.substring(name.indexOf("@") + 1);
		return mount;
	}

	private void execute(String mount) {
		URL url;
		try {
			url = new URL("file:////" + mount + "/avm/" + SCRIPT_FILENAME);
			_management.runScript(url);
		} catch (MalformedURLException e) {
			_logger.error("Error : " + e.getMessage());
		}

	}

}