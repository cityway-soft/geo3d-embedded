package org.avm.elementary.time.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;

import org.avm.device.timemanager.TimeManager;
import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.time.TimeManagerConfig;
import org.avm.elementary.time.TimeManagerImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "time";

	private TimeManager _peer;

	CommandGroupImpl(ComponentContext context, TimeManager peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for time update from gps.");
		_peer = peer;
	}

	// set Freq
	public final static String USAGE_SETFREQ = "<freq>";

	public final static String[] HELP_SETFREQ = new String[] { "Set time update frequency (seconds)", };

	public int cmdSetfreq(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String freq = ((String) opts.get("freq")).trim();
		((TimeManagerConfig) _config).setFrequency(Integer.parseInt(freq));
		_config.updateConfig();

		return 0;
	}

	// show Freq
	public final static String USAGE_SHOWFREQ = "";

	public final static String[] HELP_SHOWFREQ = new String[] { "Show time update frequency", };

	public int cmdShowfreq(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		out.println("Current frequency : "
				+ ((TimeManagerConfig) _config).getFrequency());
		return 0;
	}

	// show Freq
	public final static String USAGE_UPDATETIME = "";

	public final static String[] HELP_UPDATETIME = new String[] { "Update time from gps immediately", };

	public int cmdUpdatetime(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("before:" + new Date());
		_peer.update();
		out.println("after :" + new Date());
		return 0;
	}

	// show Freq
	public final static String USAGE_TIME = "";

	public final static String[] HELP_TIME = new String[] { "Print current time", };

	public int cmdTime(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(new Date());
		return 0;
	}
	
	
	// show Freq
	public final static String USAGE_SETTIME = "<ddMMyyyyHHmmss>";

	public final static String[] HELP_SETTIME = new String[] { "Set time update frequency from gps", "settime <ddMMyyyyHHmmss>" };

	public int cmdSettime(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String sdate = ((String)opts.get("ddMMyyyyHHmmss")).trim();
		out.println("=>" + sdate);
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
		try {
			out.println("before:" + new Date());
			Date date = formatter.parse(sdate);
			((TimeManagerImpl)_peer).setTime(date.getTime());
			out.println("after :" + new Date());
		} catch (ParseException e) {
			out.println("Erreur : " + e.getMessage());
			e.printStackTrace(out);
		}
		
		
		return 0;
	}
	
	// is on time
	public final static String USAGE_ISONTIME = "";

	public final static String[] HELP_ISONTIME = new String[] { "Check if time is updated" };

	public int cmdIsontime(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		boolean isOnTime = org.avm.device.plateform.System.isOnTime();
		out.println("IsOnTime:" + isOnTime);
		
		return 0;
	}
	
	// set Check position
	public final static String USAGE_CHECKPOSITION = "[<check>]";

	public final static String[] HELP_CHECKPOSITION = new String[] { "Tell if position must be checked (not null) before setting time", };

	public int cmdCheckposition(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String check = ((String) opts.get("check"));
		if (check != null){
			((TimeManagerConfig) _config).setPositionCheck(check.toLowerCase().equals("true"));
			_config.updateConfig();
		}
		
		out.println("Check if position not 0,0 : " + ((TimeManagerConfig) _config).isPositionChecked());

		return 0;
	}
	
	
}
