package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.BundleAction;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementImpl;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.core.utils.DataUploadClient;
import org.avm.elementary.management.core.utils.Utils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class ReportCommand extends AbstractCommand implements BundleAction {
	private ManagementService _management;

	private static final String SMS_PROTOCOL_TAG = "sms://";
	private static final String FTP_PROTOCOL_TAG = "ftp://";
	private static final String CTW_PROTOCOL_TAG = "ctw";

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {
		_management = management;

		String command = null;
		String destination = null;
		if (p != null) {
			command = p.getProperty("command").trim();
			destination = p.getProperty("destination");
		}

		String result = ((ManagementImpl) _management).runScript(command);
		result = (result == null) ? "null" : result.trim();
		if (destination != null) {
			int idx;
			if (destination.startsWith(SMS_PROTOCOL_TAG)) {
				idx = destination.indexOf(SMS_PROTOCOL_TAG);
				String number = destination.substring(idx
						+ SMS_PROTOCOL_TAG.length());
				result = result.substring(0, Math.min(result.length(), 159));
				result = result.replace('\n', ',');
				result = result.replace('\r', ' ');
				String cmd = "/media.sms sendmsg  -d " + number + " -t texto -m \""
						+ result + "\"";
				out.println("Execute cmd :" + cmd);
				result = ((ManagementImpl) _management).runScript(cmd);
				out.println("result :" + result);
			} else if (destination.startsWith(FTP_PROTOCOL_TAG)) {
				String path = Utils.formatURL(destination, false);
				String remotefile = Utils.formatURL("$i_response.txt", false);
				out.println("ftp path :" + path);
				try {
					DataUploadClient client = new DataUploadClient(new URL(path));
					client.put(new StringBuffer(result), remotefile );
				} catch (IOException e) {
					out.println("ERROR:" + e.getMessage());
				}
			}else if (destination.startsWith(CTW_PROTOCOL_TAG)) {
				String response = URLEncoder.encode(result);
				_management.send(response);
			}
		}

	}

	public static class ReportCommandFactory extends CommandFactory {
		protected Command create() {
			return new ReportCommand();
		}
	}

	static {
		CommandFactory.factories.put(ReportCommand.class.getName(),
				new ReportCommandFactory());
	}

	public void execute(Bundle b, BundleContext context, PrintWriter out) {

	}

}
