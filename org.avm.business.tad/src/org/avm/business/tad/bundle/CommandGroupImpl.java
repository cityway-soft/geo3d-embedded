package org.avm.business.tad.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;

import org.avm.business.tad.Mission;
import org.avm.business.tad.TAD;
import org.avm.business.tad.TADConfig;
import org.avm.business.tad.TADImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "tad";

	private TAD _peer;

	CommandGroupImpl(ComponentContext context, TAD peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for TAD service.");
		_peer = peer;
	}

	// Add
	public final static String USAGE_ADD = "<id> <type> <destination> <description> <date>";

	public final static String[] HELP_ADD = new String[] { "Add missions", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		long id = Long.parseLong((String)opts.get("id"));
		String stype = ((String)opts.get("type")).toUpperCase();
		int type=Mission.TYPE_MONTEE_DESCENTE;
		if (stype.equals("M")){
			type = Mission.TYPE_MONTEE;
		}
		else if (stype.equals("D")){
			type = Mission.TYPE_DESCENTE;
		}
		String destination = (String)opts.get("destination");		
		String description = (String)opts.get("description");
		String date = (String)opts.get("date");
		Mission mission = new Mission(id, type , destination, description, date);
		_peer.add(mission);
		((TADConfig) _config).add(mission);
		_config.updateConfig(false);
		out.println("Mission : '" + mission + "' added.");
		return 0;
	}	


	// Remove
	public final static String USAGE_REMOVE = "<id>";

	public final static String[] HELP_REMOVE = new String[] { "Remove properties", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String sid = ((String) opts.get("id")).trim();
		Long id = new Long(Long.parseLong(sid));
		((TADConfig) _config).remove(id);
		_peer.remove(id);
		_config.updateConfig(false);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all missions", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Enumeration e = ((TADConfig) _config).elements();
		while(e.hasMoreElements()){
			out.println((Mission)e.nextElement());			
		}
		return 0;
	}

	// Service
	public final static String USAGE_SERVICE = "";

	public final static String[] HELP_SERVICE = new String[] { "List all missions for today service", };

	public int cmdService(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Enumeration e = _peer.getService().elements();
		while(e.hasMoreElements()){
			out.println((Mission)e.nextElement());			
		}
		return 0;
	}
	
	// Service
	public final static String USAGE_STATE = "<id>  <state>";

	public final static String[] HELP_STATE = new String[] { "Change mission state",
			"A_FAIRE = -1",	
	"EFFECTUE = 0",
"NON_REALISE_ANNULE = 1",
" NON_REALISE_CLIENT_ABSENT = 2", };

	public int cmdState(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String sstate = (String)opts.get("state");	
		int state = Integer.parseInt(sstate);
		String sid = (String)opts.get("id");
		Long id = new Long (Long.parseLong(sid));
		Mission mission = _peer.getService().get(id);
		mission.setState(state);
		return 0;
	}
	
	// Service
	public final static String USAGE_COMMIT = "";

	public final static String[] HELP_COMMIT = new String[] { "Validate changes", };

	public int cmdCommit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		((TADImpl)_peer).stateChanged();
		_config.updateConfig(false);
		return 0;
	}


	
}
