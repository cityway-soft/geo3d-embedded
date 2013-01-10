package org.avm.device.usbmass.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.usbmass.UsbMass;
import org.avm.device.usbmass.UsbMassConfig;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "usbmass";

	private UsbMass _peer;

	CommandGroupImpl(ComponentContext context, UsbMass peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP, "Configuration commands for usbmass device.");
		_peer = peer;
		_config = config;
	}
	
//	 log level
	public final static String USAGE_SETMOUNTPOINT = "<mount>";

	public final static String[] HELP_SETMOUNTPOINT = new String[] { "Set usb mount point", };

	public int cmdSetmountpoint(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String mount = ((String) opts.get("mount")).trim();
		((UsbMassConfig) _config).setMountPoint(mount);
		_config.updateConfig();
		return 0;
	}

	public final static String USAGE_SHOWMOUNTPOINT = "";

	public final static String[] HELP_SHOWMOUNTPOINT = new String[] { "Show usb mount point" };

	public int cmdShowmountpoint(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String mount = ((UsbMassConfig) _config).getMountPoint();
		out.println("Mount point:" + mount);
		return 0;
	}
	

	public final static String USAGE_ISAVAILABLE = "";

	public final static String[] HELP_ISAVAILABLE = new String[] { "Return true if usb mass is available" };

	public int cmdIsavailable(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Usb mass storage ("+((UsbMassConfig) _config).getMountPoint()+") is " + (_peer.isAvailable()?"":"NOT ") + "available.");
		return 0;
	}


}
