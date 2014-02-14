package org.avm.elementary.alarm.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.alarm.impl.AlarmServiceConfig;
import org.avm.elementary.alarm.impl.AlarmServiceImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "alarm";

	private AlarmService _peer;

	CommandGroupImpl(ComponentContext context, AlarmService peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Gestion des alarmes");
		_peer = peer;

	}

	// Add
	public final static String USAGE_ADD = "-i #index# -n #name# -s #source#  -u #notify-up# -d #notify-down# -a #acknowledge# -t #type# -r #readonly#";

	public final static String[] HELP_ADD = new String[] { "Add alarm index", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		Integer index = Integer.valueOf((String) opts.get("-i"));
		String source = ((String) opts.get("-s")).trim();
		String name = ((String) opts.get("-n")).trim();
		Boolean notifyup = Boolean.valueOf((String) opts.get("-u"));
		Boolean notifydown = Boolean.valueOf((String) opts.get("-d"));
		Boolean acknowledge = Boolean.valueOf((String) opts.get("-a"));
		Boolean readonly = Boolean.valueOf((String) opts.get("-r"));
		Integer type = Integer.valueOf((String) opts.get("-t"));

		Properties p = new Properties();
		p.put(Alarm.INDEX, index.toString());
		p.put(Alarm.NAME, name.toString());
		p.put(Alarm.KEY, source.toString());
		p.put(Alarm.NOTIFY_DOWN, notifydown.toString());
		p.put(Alarm.NOTIFY_UP, notifyup.toString());
		p.put(Alarm.ACKNOWLEDGE, acknowledge.toString());
		p.put(Alarm.TYPE, type.toString());
		p.put(Alarm.READONLY, readonly.toString());

		((AlarmServiceConfig) _config).add(p);

		_config.updateConfig(false);

		return 0;
	}

	// Add
	public final static String USAGE_SET = "-i #index# [-n #name#] [-s #source#]  [-u #notify-up#] [-d #notify-down#] [-a #acknowledge#] [-t #type#] [-r #readonly#] [-v #visibility#]";

	public final static String[] HELP_SET = new String[] { "Set values to alarm index", };

	public int cmdSet(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String temp;
		Integer index = Integer.valueOf((String) opts.get("-i"));
		String source = ((String) opts.get("-s"));
		String name = ((String) opts.get("-n"));

		temp = (String) opts.get("-u");
		Boolean notifyup = (temp != null) ? Boolean.valueOf(temp) : null;

		temp = (String) opts.get("-d");
		Boolean notifydown = (temp != null) ? Boolean.valueOf(temp) : null;

		temp = (String) opts.get("-a");
		Boolean acknowledge = (temp != null) ? Boolean.valueOf(temp) : null;

		temp = (String) opts.get("-r");
		Boolean readonly = (temp != null) ? Boolean.valueOf(temp) : null;

		temp = (String) opts.get("-t");
		Integer type = (temp != null) ? Integer.valueOf(temp) : null;
		
		temp = (String) opts.get("-v");
		Boolean visibility = (temp != null) ? Boolean.valueOf(temp) : null;

		Properties p = ((AlarmServiceConfig) _config).get(index.toString());
		if (p == null) {
			out.print("Error : Alarm index " + index + " unknown");
		} else {
			if (name != null) {
				p.put(Alarm.NAME, name.toString());
			}
			if (source != null) {
				p.put(Alarm.KEY, source.toString());
			}
			if (notifydown != null) {
				p.put(Alarm.NOTIFY_DOWN, notifydown.toString());
			}
			if (notifyup != null) {
				p.put(Alarm.NOTIFY_UP, notifyup.toString());
			}
			if (acknowledge != null) {
				p.put(Alarm.ACKNOWLEDGE, acknowledge.toString());
			}
			if (type != null) {
				p.put(Alarm.TYPE, type.toString());
			}
			if (readonly != null) {
				p.put(Alarm.READONLY, readonly.toString());
			}			
			
			if (visibility != null) {
				p.put(Alarm.VISIBLE, visibility.toString());
			}

			((AlarmServiceConfig) _config).add(p);

			_config.updateConfig(true);

		}
		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-i #index#";

	public final static String[] HELP_REMOVE = new String[] { "Remove alarm index", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int index = Integer.parseInt(((String) opts.get("-i")).trim());
		((AlarmServiceConfig) _config).remove("" + index);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all alarm index", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Collection collection = _peer.getList();
		Iterator iter = collection.iterator();
		while (iter.hasNext()) {
			Alarm alarm = (Alarm) iter.next();
			out.println(alarm);
		}
		// out.println(_config.toString());
		return 0;
	}

	// setstate
	public final static String USAGE_SETSTATE = "<alarmid> [<state>]";

	public final static String[] HELP_SETSTATE = new String[] { "Set alarm state alarmid [0..32], state [true|false]", };

	public int cmdSetstate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int id = Integer.parseInt((String) opts.get("alarmid"));
		Alarm alarm = new Alarm(new Integer(id));
		if (alarm == null) {
			out.println("alarm id(" + id + ") unknown");
		} else {
			String sstate = ((String) opts.get("state"));
			if (sstate != null) {
				alarm.setStatus(sstate.equalsIgnoreCase("true"));
				((AlarmServiceImpl) _peer).notify(alarm);
			}
			alarm = _peer.getAlarm(new Integer(id));
			out.println(alarm.isStatus());
		}
		return 0;
	}

	// setstate
	public final static String USAGE_STATE = "-a #alarmid# [-s #state#] [-d #detail]";

	public final static String[] HELP_STATE = new String[] { "Set alarm state alarmid [0..32], state [true|false]", };

	public int cmdState(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String alarms = (String) opts.get("-a");
		String details = (String) opts.get("-d");
		if (alarms.equals("*")) {
			alarms = "0-31";
		}
		boolean fDetails = (details != null && details.equals("true"));
		if (alarms.indexOf("-") != -1) {
			StringBuffer list = null;
			StringTokenizer t = new StringTokenizer(alarms, "-");
			int min = Integer.parseInt(t.nextToken());
			int max = Integer.parseInt(t.nextToken());
			Collection collection = _peer.getList();
			Iterator iter = collection.iterator();
			while (iter.hasNext()) {
				Alarm alarm = (Alarm) iter.next();
				if (alarm.getIndex().intValue() >= min
						&& alarm.getIndex().intValue() <= max) {
					if (list != null) {
						list.append(",");
					} else {
						list = new StringBuffer();
					}
					list.append(alarm.getIndex());

				}
			}
			alarms = list.toString();
		}
		StringTokenizer t = new StringTokenizer(alarms, ",");
		while (t.hasMoreElements()) {
			int id = Integer.parseInt(t.nextToken());
			Alarm alarm = new Alarm(new Integer(id));
			if (alarm == null) {
				out.println("alarm id(" + id + ") unknown");
			} else {
				String sstate = ((String) opts.get("-s"));
				if (sstate != null) {
					alarm.setStatus(sstate.equalsIgnoreCase("true"));
					((AlarmServiceImpl) _peer).notify(alarm);
				}
				alarm = _peer.getAlarm(new Integer(id));
				out.println(id + (fDetails ? ("(" + alarm.getKey() + ")") : "")
						+ ": " + alarm.isStatus());
			}
		}

		return 0;
	}

}
