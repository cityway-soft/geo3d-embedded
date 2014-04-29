package org.avm.business.vocal.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.business.vocal.Vocal;
import org.avm.business.vocal.VocalConfig;
import org.avm.business.vocal.VocalImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "vocal";
	private VocalImpl _peer;

	CommandGroupImpl(ComponentContext context, Vocal peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for vocal .");
		_peer = (VocalImpl) peer;
	}

	public final static String USAGE_SETSLEEPBEFOREPLAY = "[<time>]";
	public final static String[] HELP_SETSLEEPBEFOREPLAY = new String[] { "Set time (in milliseconds) to sleep (to wait for amplifier) before play sound", };

	public int cmdSetsleepbeforeplay(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String time = (String) opts.get("time");
		if(time != null){
			((VocalConfig) _config).setSleepBeforePlay(Long.parseLong(time));
			_config.updateConfig();
		}
		out.println("Sleep time : "
				+ ((VocalConfig) _config).getSleepBeforePlay());
		return 0;
	}
	
	
	
	public final static String USAGE_SETFILENAME = "<path>";
	public final static String[] HELP_SETFILENAME = new String[] { "Set sound path", };

	public int cmdSetfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String path = (String) opts.get("path");
		((VocalConfig) _config).setFileName(path);
		_config.updateConfig();
		out.println("Current sound path : "
				+ ((VocalConfig) _config).getFileName());
		return 0;
	}

	public final static String USAGE_SHOWFILENAME = "";
	public final static String[] HELP_SHOWFILENAME = new String[] { "Show current sound path", };

	public int cmdShowfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current sound path : "
				+ ((VocalConfig) _config).getFileName());
		return 0;
	}

	public final static String USAGE_TESTCONDUCTEUR = "";
	public final static String[] HELP_TESTCONDUCTEUR = new String[] { "Test audio conducteur .", };

	public int cmdTestconducteur(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String mp3 = _peer.getMP3Filename(Vocal.TEST_CONDUCTEUR);
		String[] messages = { mp3 };
		try {
			_peer.annonce(messages, Vocal.CONDUCTEUR);
		} catch (Exception e) {
			out.println("Erreur : " + e.getMessage());
			e.printStackTrace();
		}
		out.flush();
		return 0;
	}

	public final static String USAGE_TESTVOYAGEUR = "[<exterieur>]";
	public final static String[] HELP_TESTVOYAGEUR = new String[] { "Test audio voyageur (interieur/exterieur] .", };

	public int cmdTestvoyageur(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String exterieur = (String)opts.get("exterieur");
		String mp3 = _peer.getMP3Filename(Vocal.TEST_VOYAGEUR);
		String[] messages = { mp3 };
		try {
			if (exterieur!=null && exterieur.toLowerCase().startsWith("ext")){
				out.println("Annonce Exterieure : " + mp3);
				_peer.annonce(messages, Vocal.VOYAGEUR_EXTERIEUR);			
			}
			else{
				out.println("Annonce Interieure : " + mp3);
				_peer.annonce(messages, Vocal.VOYAGEUR_INTERIEUR);						
			}
		} catch (Exception e) {
			out.println("Erreur : " + e.getMessage());
			e.printStackTrace();
		}


		out.flush();
		return 0;
	}

	
	// Add
	public final static String USAGE_SETCONFEXT = "[<args>] ...";

	public final static String[] HELP_SETCONFEXT = new String[] { "Add properties", };

	public int cmdSetconfext(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String[] args = (String[]) opts.get("args");
		addConf(Vocal.CONFIGURATION_VOYAGEUR_EXTERIEUR, args, out);
		_config.updateConfig(false);
		return 0;
	}
	
	// Add
	public final static String USAGE_SETCONFINT = "[<args>] ...";

	public final static String[] HELP_SETCONFINT = new String[] { "Add properties", };

	public int cmdSetconfint(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String[] args = (String[]) opts.get("args");
		addConf(Vocal.CONFIGURATION_VOYAGEUR_INTERIEUR, args, out);
		_config.updateConfig(false);
		return 0;
	}

	// Add
	public final static String USAGE_SETCONFCONDUCTEUR = "[<args>] ...";

	public final static String[] HELP_SETCONFCONDUCTEUR = new String[] { "Add properties", };

	public int cmdSetconfconducteur(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String[] args = (String[]) opts.get("args");
		addConf(Vocal.CONFIGURATION_CONDUCTEUR, args, out);
		_config.updateConfig(false);
		return 0;
	}	

	// Add
	public final static String USAGE_SETCONFDEFAUT = "[<args>] ...";

	public final static String[] HELP_SETCONFDEFAUT = new String[] { "Add properties", };

	public int cmdSetconfdefaut(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String[] args = (String[]) opts.get("args");
		addConf(Vocal.CONFIGURATION_DEFAUT, args, out);
		_config.updateConfig(false);
		return 0;
	}	
	
	public int addConf(String name, String[] digi, PrintWriter out) {
		Properties properties = new Properties();
		properties.put(Vocal.KEY, name);
		
		if (digi != null) {
			int j=0;
			for (int i = 0; i < digi.length && i < 3; i++) {
				String value = digi[i];
				if (!value.equals("0") && !value.equals("1")){
					out.println("La valeur de sortie doit etre 0 ou 1 : " + value + " est incorrect");
					break;
				}
				String key = "do"+j;
				j++;
				properties.put(key, value);
			}
		}
		((VocalConfig) _config).addProperty(name, properties);
		_config.updateConfig(false);
		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-n #name#";

	public final static String[] HELP_REMOVE = new String[] { "Remove properties", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		((VocalConfig) _config).removeProperty(name);
		_config.updateConfig(false);
		return 0;
	}
	
	// Languages
	public final static String USAGE_SETLANG = "[<langs>]";

	public final static String[] HELP_SETLANG = new String[] { "Set/Get languages", };

	public int cmdSetlang(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		
		String langs = ((String) opts.get("langs"));
		
		if (langs != null){
			((VocalConfig) _config).setLanguages(langs);
			_config.updateConfig(false);
			((VocalImpl)_peer).configure(_config);
		}
		
		String[] languages = ((VocalConfig) _config).getLanguages();
		
		StringBuffer list = new StringBuffer();
		for (int i = 0; i < languages.length; i++) {
			if (i>0){
				list.append(", ");
			}
			list.append(languages[i]);
		}
		out.println(list);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all properties", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties props = ((VocalConfig) _config).getProperty(null);
		Enumeration keys = props.keys();
		
		
		StringBuffer buf = new StringBuffer();
		buf.append("                    do0  do1  do2");
		buf.append(System.getProperty("line.separator"));
		while(keys.hasMoreElements()){
			String key = (String)keys.nextElement();
			if (key.equals("filename")) continue;
			Properties digi = (Properties)props.get(key);
			buf.append((key + "......................").substring(0, 20));
			buf.append(" ");
			buf.append(digi.getProperty("do0"));buf.append("    ");
			buf.append(digi.getProperty("do1"));buf.append("    ");
			buf.append(digi.getProperty("do2"));buf.append("    ");
			buf.append(System.getProperty("line.separator"));			
		}
		out.println(buf);
		return 0;
	}
	
	






}
