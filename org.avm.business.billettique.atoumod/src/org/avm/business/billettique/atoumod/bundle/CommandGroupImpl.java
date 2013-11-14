package org.avm.business.billettique.atoumod.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.Dictionary;

import org.avm.business.billettique.atoumod.Billettique;
import org.avm.business.billettique.atoumod.BillettiqueConfig;
import org.avm.business.billettique.atoumod.BillettiqueImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "billettique";

	private ConfigImpl _config;

	private Billettique _peer;

	CommandGroupImpl(ComponentContext context, Billettique peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for 'billettique'.");
		_peer = peer;
		_config = config;
	}

	// Enable
	public final static String USAGE_ENABLE = "<state>";

	public final static String[] HELP_ENABLE = new String[] { "", };

	public int cmdEnable(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		boolean enabled = ((String)opts.get("state")).equalsIgnoreCase("true");
		
		_peer.setEnable(enabled);
		return 0;
	}
	
	// set port
	public final static String USAGE_SETPORT = "[<port>]";

	public final static String[] HELP_SETPORT = new String[] { "Set server port", };

	public int cmdSetport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String port = ((String) opts.get("port"));
		if (port != null){
			((BillettiqueConfig) _config).setPort(Integer.parseInt(port));
			_config.updateConfig();
		}
		else{
			out.println(((BillettiqueConfig) _config).getPort());
		}
		

		return 0;
	}
	
	
	// set port
	public final static String USAGE_SETLOCALPORT = "[<port>]";

	public final static String[] HELP_SETLOCALPORT = new String[] { "Set server port", };

	public int cmdSetlocalport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String port = ((String) opts.get("port"));
		if (port != null){
			((BillettiqueConfig) _config).setLocalPort(new Integer(Integer.parseInt(port)));
			_config.updateConfig();
		}
		else{
			out.println(((BillettiqueConfig) _config).getLocalPort());
		}
		

		return 0;
	}
	
	
	// set host
	public final static String USAGE_SETHOST = "[<host>]";

	public final static String[] HELP_SETHOST = new String[] { "Set ticketing server host", };

	public int cmdSethost(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String host = ((String) opts.get("host"));
		if (host != null){
			((BillettiqueConfig) _config).setHost(host);
			_config.updateConfig();
		}
		else{
			out.println(((BillettiqueConfig) _config).getHost());
		}
		

		return 0;
	}
	
	// set tsurv
	public final static String USAGE_SETTSURV = "[<tsurv>]";

	public final static String[] HELP_SETTSURV = new String[] { "Set tsurv timeout", };

	public int cmdSettsurv(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String tsurv = ((String) opts.get("tsurv"));
		if (tsurv != null){
			((BillettiqueConfig) _config).setTSurv(Integer.parseInt(tsurv));
			_config.updateConfig();
		}
		else{
			out.println(((BillettiqueConfig) _config).getTSurv());
		}

		return 0;
	}
	
	
	
	// set nsurv
	public final static String USAGE_SETNSURV = "[<nsurv>]";

	public final static String[] HELP_SETNSURV = new String[] { "Set nsurv", };

	public int cmdSetnsurv(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String tsurv = ((String) opts.get("nsurv"));
		if (tsurv != null){
			((BillettiqueConfig) _config).setNSurv(Integer.parseInt(tsurv));
			_config.updateConfig();
		}
		else{
			out.println(((BillettiqueConfig) _config).getNSurv());
		}

		return 0;
	}
	
	
	// is connected
	public final static String USAGE_ISCONNECTED = "";

	public final static String[] HELP_ISCONNECTED = new String[] { "Verifie si le billettique est connecte", };

	public int cmdIsconnected(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		
		
		Date date = ((BillettiqueImpl)_peer).getConnectionDate();
		if (date != null){
			long delta = (System.currentTimeMillis() - date.getTime())/1000;
			out.println("CONNECTED (since " + date + ", " + delta + " sec)");
		}
		else{
			out.println("NOT CONNECTED");
		}

		return 0;
	}


}
