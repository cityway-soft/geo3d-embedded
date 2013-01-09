package org.avm.device.girouette.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.girouette.Girouette;
import org.avm.device.girouette.GirouetteDevice;
import org.avm.elementary.common.AbstractDeviceCommandGroup;
import org.avm.elementary.common.DeviceConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractDeviceCommandGroup {

	private GirouetteDevice _peer;

	public CommandGroupImpl(ComponentContext context, GirouetteDevice peer,
			ConfigImpl config) {
		super(context, config, "girouette");
		_peer = (GirouetteDevice) peer;
	}

	// code girouette
	public final static String USAGE_DESTINATION = "<code>";

	public final static String[] HELP_DESTINATION = new String[] { "Send destination code girouette", };

	public int cmdDestination(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String code = ((String) opts.get("code")).trim();

		Girouette girouette = (Girouette) _context.locateService("girouette");
		if (girouette != null) {
			girouette.destination(code);
		}
		return 0;
	}

}
