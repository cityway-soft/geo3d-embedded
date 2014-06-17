package org.avm.hmi.swt.avm.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.hmi.swt.avm.AvmIhm;
import org.avm.hmi.swt.avm.AvmIhmConfig;
import org.avm.hmi.swt.avm.AvmImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "swt.avm";

	private AvmImpl _peer;

	CommandGroupImpl(ComponentContext context, AvmImpl peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the HMI Avm.");
		_peer = peer;
	}
	
	//SetAR in seconds
	public final static String USAGE_SETAR = "[<value>]";

	public final static String[] HELP_SETAR= new String[] { "Set A/R", };

	public int cmdSetar(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		
		String sval = (String)opts.get("value");
		if (sval != null){
			int val=Integer.parseInt(sval) ;
			_peer.setDemoAR(val);
		}
		else{
			out.println("value is mandatory");
		}
		
		return 0;
	}


	// Setperiode
	public final static String USAGE_SETPERIODE = "[<periode>]";

	public final static String[] HELP_SETPERIODE = new String[] { "Set periode", };

	public int cmdSetperiode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		
		String speriode = (String)opts.get("periode");
		if (speriode != null){
			int periode=Integer.parseInt(speriode) ;
			((AvmIhmConfig) _config).setPeriode(periode);
			_config.updateConfig(false);
			_peer.setDemoPeriode(periode);
		}
		else{
			int val = ((AvmIhmConfig) _config).getPeriode();	
			out.println(val);
		}
		
		return 0;
	}
	
}
