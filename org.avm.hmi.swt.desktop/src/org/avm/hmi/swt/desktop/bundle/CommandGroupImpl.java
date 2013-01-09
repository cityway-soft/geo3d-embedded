package org.avm.hmi.swt.desktop.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "swt.desktop";

	public static final SimpleDateFormat DF = new SimpleDateFormat("HH:mm");

	private Desktop _peer;

	public CommandGroupImpl(ComponentContext context, Desktop peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for SWT Ihm.");
		_peer = peer;
	}

	// activate a tabbed pane
	public final static String USAGE_ACTIVATE = "<item>";

	public final static String[] HELP_ACTIVATE = new String[] { "Activate tab item", };

	public int cmdActivate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String item = ((String) opts.get("item")).trim();
		_peer.activateItem(item);

		out.println("Item activated");
		return 0;
	}

	// log level
	public final static String USAGE_LISTITEM = "";

	public final static String[] HELP_LISTITEM = new String[] { "List tab item", };

	public int cmdListitem(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Object[] obj = _peer.getItems();
		out.println("Items:");
		for (int i = 0; i < obj.length; i++) {
			out.println("    " + obj[i]);
		}
		return 0;
	}

	// set day mode range
	public final static String USAGE_SETDAYRANGE = "[<start>] [<stop>]";

	public final static String[] HELP_SETDAYRANGE = new String[] { "Set day range HH:MM - HH:MM ", };

	public int cmdSetdayrange(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Date end;
		Date begin;

		begin = ((ConfigImpl)_config).getBeginDay();
		end = ((ConfigImpl)_config).getEndDay();
		
		String sBegin = ((String) opts.get("start"));
		if (sBegin != null) {
			String sEnd = ((String) opts.get("stop"));

			try {
				begin = DF.parse(sBegin);
			} catch (ParseException e) {
				out.println("ERROR : Start Date '" + sBegin + "' is not valid");
				return 1;
			}
			((ConfigImpl)_config).setBeginDay(begin);		

			if (sEnd != null) {

				try {
					end = DF.parse(sEnd);
				} catch (ParseException e) {
					out.println("ERROR : Stop Date '" + sEnd
							+ "' is not valid");
					return 1;
				}
				((ConfigImpl)_config).setEndDay(end);
			}
			((DesktopImpl) _peer).setDayRange(begin, end);

			((DesktopImpl) _peer).checkNightMode();
			((ConfigImpl)_config).updateConfig(false);
			out.println("range updated.");
		}
		out.println("range: [ "+DF.format(begin)+", " +DF.format(end) + " ]");
		return 0;
	}

	// refresh desktop
	public final static String USAGE_REFRESH = "";

	public final static String[] HELP_REFRESH = new String[] { "Refresh desktop information", };

	public int cmdRefresh(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		((DesktopImpl) _peer).refresh();
		return 0;
	}
	
	// refresh desktop
	public final static String USAGE_SETNIGHTMODE = "<state>";

	public final static String[] HELP_SETNIGHTMODE = new String[] { "setnightmode <true|false>", };

	public int cmdSetnightmode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		boolean state = ((String) opts.get("state")).equalsIgnoreCase("true");
		((DesktopImpl) _peer).setNightMode(state);
		return 0;
	}
}
