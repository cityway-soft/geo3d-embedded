package org.avm.device.comptage.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.device.comptage.Comptage;
import org.avm.device.comptage.ComptageDevice;
import org.avm.device.comptage.ComptageException;
import org.avm.elementary.common.AbstractDeviceCommandGroup;
import org.avm.elementary.common.DeviceConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractDeviceCommandGroup {

	private ComptageDevice _peer;

	public CommandGroupImpl(ComponentContext context, ComptageDevice peer,
			ConfigImpl config) {
		super(context, config, "comptage");
		_peer = (ComptageDevice) peer;
	}

	public final static String USAGE_PASSENGERSCOUNT = "<type>";

	public final static String[] HELP_PASSENGERSCOUNT = new String[] { "Retrieve passengers count", };

	public int cmdPassengerscount(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String type = ((String) opts.get("type")).trim();

		Comptage comptePassagers = (Comptage) _context
				.locateService("comptage");
		if (comptePassagers != null) {
			try {
				int val = comptePassagers.nombrePassagers(type);
				out.print(val+"\n");
			} catch (ComptageException e) {
				out.print(e.getMessage()+"\n");
			}
		}
		return 0;
	}

	public final static String USAGE_RESET = "";

	public final static String[] HELP_RESET = new String[] { "Reset counters", };

	public int cmdReset(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Comptage comptePassagers = (Comptage) _context
				.locateService("comptage");
		if (comptePassagers != null) {
			if (comptePassagers.miseAZero()) {
				out.print("reset ok\n");
			} else {
				out.print("comm error\n");
			}
		}
		return 0;
	}

	public final static String USAGE_STATUS = "";

	public final static String[] HELP_STATUS = new String[] { "Status", };

	public int cmdStatus(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Comptage comptePassagers = (Comptage) _context
				.locateService("comptage");
		if (comptePassagers != null) {
			Properties props = comptePassagers.status();
			if (props != null){
			Enumeration en = props.keys();
			while (en.hasMoreElements()) {
				String type = (String) en.nextElement();
				out.print(type + ":" + props.getProperty(type) +"\n");
			}
			}
			else{
				out.print("error communicating with elinap");
			}
		}
		return 0;
	}

}
