package org.angolight.hmi.leds.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import org.angolight.bo.BoState;
import org.angolight.hmi.leds.Leds;
import org.angolight.hmi.leds.LedsConfig;
import org.angolight.hmi.leds.LedsImpl;
import org.angolight.hmi.leds.SequenceManager;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "hmi.leds";
	private Leds _peer;

	public CommandGroupImpl(ComponentContext context, Leds peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the hmi leds.");
		_peer = peer;
	}
	
	// show state
	public final static String USAGE_STATE = "";

	public final static String[] HELP_STATE = new String[] { "Show Current states", };

	public int cmdState(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		StringBuffer buf = new StringBuffer();
		String[] names = ((LedsImpl)_peer).getStatesName();
		for (int i = 0; i < names.length; i++) {
			int state = ((LedsImpl) _peer).getState(i);
			buf.append(state);
			buf.append("\t");
			buf.append(" [");
			buf.append(i);
			buf.append("] ");			
			buf.append("\t");
			buf.append(" (");
			buf.append(names[i]);
			buf.append(") ");
			buf.append("\n");
		}
		
		out.println(buf);
		
		return 0;
	}


	// add state
	public final static String USAGE_ADDSTATE = "<state><cmd>";

	public final static String[] HELP_ADDSTATE = new String[] { "Add a new state and associate a led command", };

	public int cmdAddstate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String state = ((String) opts.get("state")).trim();
		if (!state.startsWith(Leds.BO_STATE)){
			String[] names = ((LedsImpl)_peer).getStatesName();
			boolean found=false;
			for (int i = 0; i < names.length; i++) {
				if (names[i].equals(state)){
					found=true;
					break;
				}
			}
			if (!found){
				String list = ((ConfigImpl) _config).getStateList();
				((ConfigImpl) _config).setStateList(list+","+state);
			}
		}
		String command = ((String) opts.get("cmd")).trim();
		((ConfigImpl) _config).addState(state, command);
		((LedsImpl) _peer).clear();
		((ConfigImpl) _config).updateConfig(false);
		return 0;
	}

	// remove state
	public final static String USAGE_REMOVESTATE = "<state>";

	public final static String[] HELP_REMOVESTATE = new String[] { "Add a new state and associate a led cmd and a led period", };

	public int cmdRemovestate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String state = ((String) opts.get("state")).trim();
		if (!state.startsWith(Leds.BO_STATE)){
			String[] names = ((LedsImpl)_peer).getStatesName();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				if (!names[i].equals(state)){
					buf.append(names[i]);
					buf.append(",");
				}
			}
			((ConfigImpl) _config).setStateList(buf.toString());
			
		}
		((ConfigImpl) _config).removeState(state);
		((ConfigImpl) _config).updateConfig(false);
		return 0;
	}

	// list states
	public final static String USAGE_LISTSTATE = "";

	public final static String[] HELP_LISTSTATE = new String[] { "List all specific cases", };

	public int cmdListstate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
			Properties props = ((ConfigImpl) _config).getProperties();
			Enumeration e = props.keys();
			while (e.hasMoreElements()) {
				String state = (String) e.nextElement();
				if (state.indexOf(Leds.STATE_TAG) != -1){
					out.println(state + " : " + props.getProperty(state));
				}
			}
		return 0;
	}

	// add priority states
	public final static String USAGE_ADDPRIORITYSTATES = "[<bostate>] [<args>] ...";

	public final static String[] HELP_ADDPRIORITYSTATES = new String[] { "Defined state priority to print in IHM Led (default is BO)", };

	public int cmdAddprioritystates(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String[] names = ((LedsImpl)_peer).getStatesName();
		String bostate = ((String) opts.get("bostate"));
		if (bostate == null){
			out.println("Usage: addpriority <bostate> <ID> [<ID>...]" );
			for (int i = 1; i < names.length; i++) {
				out.println("ID="+i + "=> " +names[i]);
			}
			return 0;
		}
		
		String[] args = (String[]) opts.get("args");
		if (args == null){
			out.println("You must give state(s) ID to evaluate (prioritier than BO state): " );
			for (int i = 1; i < names.length; i++) {
				out.println("ID="+i + "=> " + names[i]);
			}
			return 0;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			int val = Integer.parseInt(args[i]);
			if (val < 1 || val > names.length) {
				out.println("Error for " + (i+1)
						+ " parameter : ID must be  > 0 and <"
						+ names.length);
				buf = null;
				break;
			}
			buf.append(val);
			if ((i + 1) < args.length) {
				buf.append(",");
			}
		}

		if (buf.length()>0) {
			((ConfigImpl) _config).addPriority(bostate, buf.toString());
			((ConfigImpl) _config).updateConfig(false);
		}
		return 0;
	}

	// remove case
	public final static String USAGE_REMOVEPRIORITY = "<bostate>";

	public final static String[] HELP_REMOVEPRIORITY = new String[] { "Add a new state and associate a led cmd and a led period", };

	public int cmdRemovepriority(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String bostate = ((String) opts.get("bostate")).trim();
		((ConfigImpl) _config).removePriority(bostate);
		((ConfigImpl) _config).updateConfig(false);

		return 0;
	}

	// list priority events
	public final static String USAGE_LISTPRIORITY = "";

	public final static String[] HELP_LISTPRIORITY = new String[] { "List all specific cases", };

	public int cmdListpriority(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String[] names = ((LedsImpl)_peer).getStatesName();

		for (int i = BoState.VminState; i < BoState.VmaxState; i++) {
			int[] prior = ((LedsImpl) _peer).getPriorityEvents(i);
			if (prior != null){
				StringBuffer buf = new StringBuffer();
				buf.append("BO=");
				buf.append(i);
				buf.append(" => ");
				for (int j = 0; j < prior.length; j++) {
					buf.append(names[prior[j]]);
					if (j+1< prior.length){
						buf.append(",");
					}
				}
				out.println(buf.toString());
			}
		}

		return 0;
	}

	// display
	public final static String USAGE_DISPLAY = "<state>";

	public final static String[] HELP_DISPLAY = new String[] { "Display a state on led device", };

	public int cmdDisplay(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String state = ((String) opts.get("state")).trim();
		int result = _peer.display(state);
		out.println("Status : " + result);
		return 0;
	}

	
	// add sequences
	public final static String USAGE_ADDSEQUENCE = "<id><sequence>";

	public final static String[] HELP_ADDSEQUENCE = new String[] { "add sequence", };

	public int cmdAddsequence(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String text = null;
		text = ((String) opts.get("id"));
		Integer id = Integer.valueOf(text.trim());
		String sequence = ((String) opts.get("sequence")).trim();

		Properties p = ((LedsConfig) _config).getSequences();
		p.put("" + id, sequence);
		((LedsConfig) _config).setSequences(p);
		_config.updateConfig(false);

		return 0;
	}

	// remove sequences
	public final static String USAGE_REMOVESEQUENCE = "<id>";

	public final static String[] HELP_REMOVESEQUENCE = new String[] { "remove sequences", };

	public int cmdRemovesequence(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String text = null;
		text = ((String) opts.get("id"));
		Integer id = Integer.valueOf(text.trim());

		Properties p = ((LedsConfig) _config).getSequences();
		p.remove("" + id);
		((LedsConfig) _config).setSequences(p);
		_config.updateConfig(false);

		return 0;
	}

	// update sequences
	public final static String USAGE_UPDATESEQUENCES = "";

	public final static String[] HELP_UPDATESEQUENCES = new String[] { "update sequences", };

	public int cmdUpdatesequences(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties p = ((LedsConfig) _config).getSequences();
		if (p != null) {
			TreeSet list = new TreeSet(p.keySet());
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				String id = (String) iterator.next();
				String sequence = p.getProperty(id);
				out.println(id + " = " + sequence);
			}
		}
		_config.updateConfig();
		return 0;
	}

	// list sequences
	public final static String USAGE_LISTSEQUENCE = "";

	public final static String[] HELP_LISTSEQUENCE = new String[] { "list all sequences", };

	public int cmdListsequence(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties p = ((LedsConfig) _config).getSequences();
		if (p != null) {
			TreeSet list = new TreeSet(p.keySet());
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				String id = (String) iterator.next();
				String sequence = p.getProperty(id);
				out.println(id + " = " + sequence);
			}
		}
		return 0;
	}

	// execute sequence
	public final static String USAGE_EXECUTESEQUENCE = "<id> [<cycle>][<period>][<check>]";

	public final static String[] HELP_EXECUTESEQUENCE = new String[] { "execute sequence cycle [0-255] period [0-255] checksum[true,false]" };

	public int cmdExecutesequence(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String text = null;
		text = ((String) opts.get("id"));
		int id = Integer.valueOf(text.trim()).intValue();
		text = ((String) opts.get("cycle"));
		byte cycle = (text == null) ? 1 : Byte.parseByte(text.trim());
		text = ((String) opts.get("period"));
		byte period = (byte) ((text == null) ? 0 : Byte.parseByte(text.trim())/10);
		text = ((String) opts.get("check"));	
		boolean check = (text == null) ? false : Boolean.valueOf(text.trim())
				.booleanValue();

		byte address = ((SequenceManager) _peer).getSequenceAddress("" + id);

		int result = ((LedsImpl)_peer).X(address, cycle, period, check);
		if (result < 0) {
			out.println("Echec execute sequence");
		}

		return 0;
	}

	// stop sequence
	public final static String USAGE_STOPSEQUENCE = "[<check>]";

	public final static String[] HELP_STOPSEQUENCE = new String[] { "Stop sequence checksum[true,false]", };

	public int cmdStopsequence(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String text = ((String) opts.get("check"));
		boolean check = (text == null) ? false : Boolean.valueOf(text.trim())
				.booleanValue();

		int result = ((LedsImpl)_peer).S(check);
		if (result < 0) {
			out.println("Failed stop sequence");
		}


		return 0;
	}

	// halt sequence
	public final static String USAGE_HALTSEQUENCE = "[<check>]";

	public final static String[] HELP_HALTSEQUENCE = new String[] { "Halt sequence checksum[true,false]", };

	public int cmdHaltsequence(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String text = ((String) opts.get("check"));
		boolean check = (text == null) ? false : Boolean.valueOf(text.trim())
				.booleanValue();

		int result = ((LedsImpl)_peer).T(check);
		if (result < 0) {
			out.println("Failed halt sequence");
		}

		return 0;
	}
	
	
	
	
}
