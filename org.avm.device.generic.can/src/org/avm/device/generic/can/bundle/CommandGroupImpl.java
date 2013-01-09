package org.avm.device.generic.can.bundle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.avm.device.can.Can;
import org.avm.device.generic.can.CanConfig;
import org.avm.device.generic.can.CanImpl;
import org.avm.elementary.can.parser.CANParser;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.SPN;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	private Can _peer;

	CommandGroupImpl(ComponentContext context, Can peer, ConfigImpl config) {
		super(context, config, getCommandGroup(),
				"Configuration commands for the can.");
		_peer = peer;
	}

	protected static String getCommandGroup() {
		String pid = Activator.getDefault().getPid();
		return pid.substring(pid.lastIndexOf('.') + 1);
	}

	// URL
	public final static String USAGE_SETURL = "<uri>";

	public final static String[] HELP_SETURL = new String[] { "Set URL", };

	public int cmdSeturl(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String uri = ((String) opts.get("uri")).trim();
		((CanConfig) _config).setUrl(uri);
		_config.updateConfig();

		out.println("Current URL : " + ((CanConfig) _config).getUrl());
		return 0;
	}

	public final static String USAGE_SHOWURL = "";

	public final static String[] HELP_SHOWURL = new String[] { "Show current URL", };

	public int cmdShowurl(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current URL : " + ((CanConfig) _config).getUrl());
		return 0;
	}

	// Filter
	public final static String USAGE_SETFILTER = "<filter>";

	public final static String[] HELP_FILTER = new String[] { "Set filter class name", };

	public int cmdSetfilter(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String filter = ((String) opts.get("filter")).trim();
		((CanConfig) _config).setFilter(filter);
		_config.updateConfig();

		out.println("Current filter : " + ((CanConfig) _config).getFilter());
		return 0;
	}

	public final static String USAGE_SHOWFILTER = "";

	public final static String[] HELP_SHOWFILTER = new String[] { "Show  filter class name", };

	public int cmdShowfilter(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current filter : " + ((CanConfig) _config).getFilter());
		return 0;
	}

	// Mode
	public final static String USAGE_SETMODE = "<mode>";

	public final static String[] HELP_SETMODE = new String[] { "Set io mode (r-rw-w)", };

	public int cmdSetmode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String mode = ((String) opts.get("mode")).trim();
		((CanConfig) _config).setMode(mode);
		_config.updateConfig();

		out.println("Current mode  : " + ((ConfigImpl) _config).getMode());
		return 0;
	}

	public final static String USAGE_SHOWMODE = "";

	public final static String[] HELP_SHOWMODE = new String[] { "Show current  io mode (r-rw-w)", };

	public int cmdShowmode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current mode  : " + ((CanConfig) _config).getMode());
		return 0;
	}

	// BufferSize
	public final static String USAGE_SETBUFFERSIZE = "<size>";

	public final static String[] HELP_SETBUFFERSIZE = new String[] { "Set buffer size (frames)", };

	public int cmdSetbuffersize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int size = Integer.parseInt(((String) opts.get("size")).trim());
		((CanConfig) _config).setBufferSize(size * Can.FRAME_LEN);
		_config.updateConfig();

		out.println("Current buffer size : "
				+ ((CanConfig) _config).getBufferSize() / Can.FRAME_LEN);
		return 0;
	}

	public final static String USAGE_SHOWBUFFERSIZE = "";

	public final static String[] HELP_SHOWBUFFERSIZE = new String[] { "Show buffer size( frames)", };

	public int cmdShowbuffersize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current buffer size : "
				+ ((CanConfig) _config).getBufferSize() / Can.FRAME_LEN);
		return 0;
	}

	// Thread priority
	public final static String USAGE_SETPRIORITY = "<priority>";

	public final static String[] HELP_SETPRIORITY = new String[] { "Set thread priority (1-10)", };

	public int cmdSetpriority(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int priority = Integer.parseInt(((String) opts.get("priority")).trim());
		((CanConfig) _config).setThreadPriority(priority);
		_config.updateConfig();

		out.println("Current thread priority : "
				+ ((CanConfig) _config).getThreadPriority());
		return 0;

	}

	public final static String USAGE_SHOWPRIORITY = "";

	public final static String[] HELP_SHOWPRIORITY = new String[] { "Show thread priority", };

	public int cmdShowpriority(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current thread priority : "
				+ ((CanConfig) _config).getThreadPriority());
		return 0;
	}

	// Sleep time
	public final static String USAGE_SETSLEEPTIME = "<time>";

	public final static String[] HELP_SETSLEEPTIME = new String[] { "Set sleep time (ms)", };

	public int cmdSetsleeptime(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		long sleep = Long.parseLong(((String) opts.get("time")).trim());
		((CanConfig) _config).setSleepTime(sleep);
		_config.updateConfig();

		out.println("Current sleep time : "
				+ ((CanConfig) _config).getSleepTime());
		return 0;

	}

	public final static String USAGE_SHOWSLEEPTIME = "";

	public final static String[] HELP_SHOWSLEEPTIME = new String[] { "Show sleep time", };

	public int cmdShowsleeptime(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current sleep time : "
				+ ((CanConfig) _config).getSleepTime());
		return 0;
	}

	//

	// Add filter
	public final static String USAGE_ADD = "<filter> <mask>";

	public final static String[] HELP_ADD = new String[] { "Add filter", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		long filter = Long.parseLong(((String) opts.get("filter")).trim(), 16);
		long mask = Long.parseLong(((String) opts.get("mask")).trim(), 16);

		Properties p = new Properties();
		p.put(ConfigImpl.FILTER, Long.toHexString(filter));
		p.put(ConfigImpl.MASK, Long.toHexString(mask));

		List list = ((CanConfig) _config).getDriverFilters();
		list.add(p);
		((CanConfig) _config).setDriverFilters(list);
		print(out, list);
		_config.updateConfig(false);

		return 0;
	}

	// Remove filter
	public final static String USAGE_REMOVE = "<id>";

	public final static String[] HELP_REMOVE = new String[] { "Remove filter", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int id = Integer.parseInt(((String) opts.get("id")).trim());

		List list = ((CanConfig) _config).getDriverFilters();
		list.remove(id);
		((CanConfig) _config).setDriverFilters(list);
		print(out, list);
		_config.updateConfig(false);

		return 0;
	}

	// Remove all filter
	public final static String USAGE_REMOVEALL = "";

	public final static String[] HELP_REMOVEALL = new String[] { "Remove all filter", };

	public int cmdRemoveall(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		List list = new ArrayList();
		((CanConfig) _config).setDriverFilters(list);
		print(out, list);
		_config.updateConfig();

		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all filters ", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		List list = ((CanConfig) _config).getDriverFilters();
		print(out, list);

		return 0;
	}

	private void print(PrintWriter out, List filters) {
		int i = 0;
		for (Iterator iterator = filters.iterator(); iterator.hasNext();) {
			Properties p = (Properties) iterator.next();
			out.println("filter: " + i + "\t" + p.get(ConfigImpl.FILTER) + "\t"
					+ p.get(ConfigImpl.MASK));
			i++;
		}
	}

	// send PGN
	public final static String USAGE_SENDPGN = "<pgn>";

	public final static String[] HELP_SENDPGN = new String[] { "send pgn on can device", };

	public int cmdSendpgn(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		int key = Integer.parseInt(((String) opts.get("pgn")).trim(), 16);
		CANParser parser = ((CanImpl) _peer).getCanParser();
		PGN pgn;
		try {
			pgn = parser.makeObject(key);
			for (Iterator iterator = pgn.iterator(); iterator.hasNext();) {
				SPN spn = (SPN) iterator.next();
				boolean valid = false;
				while (!valid) {
					out.print(spn.getDescription() + " (" + spn.getName()
							+ " - " + spn.getType() + " - " + spn.getUnit()
							+ ") : ");
					out.flush();

					String line = readLine(in);
					if ((0 == line.compareToIgnoreCase("NA"))
							|| (0 == line.compareToIgnoreCase("N/A"))) {
						// TODO [DSU] spn.setAvailable(false);
					} else {
						spn.setValue(Double.parseDouble(line));
						valid = spn.isValid();
					}

				}
			}
			_peer.send(pgn);
		} catch (Exception e) {
			out.print(e.getMessage());
			out.flush();
		}

		return 0;
	}

	public static String readLine(Reader in) throws IOException {
		StringBuffer s = new StringBuffer();

		while (true) {
			int c = in.read();
			if (c == -1)
				break;
			if (c != '\r') {
				s.append((char) (c & 0xff));
			} else {
				if (s.length() > 0)
					break;
			}
		}

		return s.toString();
	}

	// URL connection
	public final static String USAGE_SHOWURLCONNECTION = "";

	public final static String[] HELP_SHOWURLCONNECTION = new String[] { "Show current URL connection", };

	public int cmdShowurlconnection(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current URL connection : "
				+ ((CanConfig) _config).getUrlConnection());
		return 0;
	}

}
