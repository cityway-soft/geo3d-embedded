package org.avm.elementary.geofencing.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Set;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.geofencing.Balise;
import org.avm.elementary.geofencing.GeoFencing;
import org.avm.elementary.geofencing.impl.GeoFencingConfig;
import org.avm.elementary.geofencing.impl.GeoFencingImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	
	public static final String COMMAND_GROUP = "geofencing";
	private GeoFencing _peer;

	public CommandGroupImpl(ComponentContext context, GeoFencing peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the geofencing.");
		_peer = peer;
	}

	// rtree dimension
	public final static String USAGE_SETDIMENSION = "<dimension>";
	public final static String[] HELP_SETDIMENSION = new String[] { "Set rtree dimension", };

	public int cmdSetdimension(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer dimension = new Integer((String) opts.get("dimension"));
		((GeoFencingConfig) _config).setDimension(dimension);
		_config.updateConfig();
		out.println("Current rtree dimension : "
				+ ((GeoFencingConfig) _config).getDimension());
		return 0;
	}

	public final static String USAGE_SHOWDIMENSION = "";
	public final static String[] HELP_SHOWDIMENSION = new String[] { "Show current rtree dimension", };

	public int cmdShowdimension(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current rtree dimension : "
				+ ((GeoFencingConfig) _config).getDimension());
		return 0;
	}

	// rtree filename
	public final static String USAGE_SETFILENAME = "<filename>";
	public final static String[] HELP_SETFILENAME = new String[] { "Set rtree filename", };

	public int cmdSetfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String fileName = (String) opts.get("filename");
		((GeoFencingConfig) _config).setFileName(fileName);
		_config.updateConfig();
		out.println("Current rtree filename : "
				+ ((GeoFencingConfig) _config).getFileName());
		return 0;
	}

	public final static String USAGE_SHOWFILENAME = "";
	public final static String[] HELP_SHOWFILENAME = new String[] { "Show current rtree filename", };

	public int cmdShowfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current rtree filename : "
				+ ((GeoFencingConfig) _config).getFileName());
		return 0;
	}

	// rtree fillfactor
	public final static String USAGE_SETFILLFACTOR = "<fillfactor>";
	public final static String[] HELP_SETFILLFACTOR = new String[] { "Set rtree fillfactor", };

	public int cmdSetfillfactor(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Double fillFactor = new Double((String) opts.get("fillfactor"));
		((GeoFencingConfig) _config).setFillFactor(fillFactor);
		_config.updateConfig();
		out.println("Current rtree fillfactor : "
				+ ((GeoFencingConfig) _config).getFillFactor());
		return 0;
	}

	public final static String USAGE_SHOWFILLFACTOR = "";
	public final static String[] HELP_SHOWFILLFACTOR = new String[] { "Show current rtree fillfactor", };

	public int cmdShowfillfactor(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current rtree fillfactor : "
				+ ((GeoFencingConfig) _config).getFillFactor());
		return 0;
	}

	// rtree leaf capacity
	public final static String USAGE_SETLEAFCAPACITY = "<fillfactor>";
	public final static String[] HELP_SETLEAFCAPACITY = new String[] { "Set rtree leaf capacity", };

	public int cmdSetleafcapacity(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer leafCapacity = new Integer((String) opts.get("leafcapacity"));
		((GeoFencingConfig) _config).setLeafCapacity(leafCapacity);
		_config.updateConfig();
		out.println("Current rtree leaf capacity : "
				+ ((GeoFencingConfig) _config).getLeafCapacity());
		return 0;
	}

	public final static String USAGE_SHOWLEAFCAPACITY = "";
	public final static String[] HELP_SHOWLEAFCAPACITY = new String[] { "Show current rtree leaf capacity", };

	public int cmdShowleafcapacity(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current rtree leaf capacity : "
				+ ((GeoFencingConfig) _config).getLeafCapacity());
		return 0;
	}

	// rtree index capacity
	public final static String USAGE_SETINDEXCAPACITY = "<fillfactor>";
	public final static String[] HELP_SETINDEXCAPACITY = new String[] { "Set rtree index capacity", };

	public int cmdSetindexcapacity(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer indexCapacity = new Integer((String) opts.get("indexcapacity"));
		((GeoFencingConfig) _config).setIndexCapacity(indexCapacity);
		_config.updateConfig();
		out.println("Current rtree index capacity : "
				+ ((GeoFencingConfig) _config).getIndexCapacity());
		return 0;
	}

	public final static String USAGE_SHOWINDEXCAPACITY = "";
	public final static String[] HELP_SHOWINDEXCAPACITY = new String[] { "Show current rtree index capacity", };

	public int cmdShowindexcapacity(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current rtree index capacity : "
				+ ((GeoFencingConfig) _config).getIndexCapacity());
		return 0;
	}

	// rtree pagesize
	public final static String USAGE_SETPAGESIZE = "<pagesize>";
	public final static String[] HELP_SETPAGESIZE = new String[] { "Set rtree pagesize", };

	public int cmdSetpagesize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer pageSize = new Integer((String) opts.get("pagesize"));
		((GeoFencingConfig) _config).setPageSize(pageSize);
		_config.updateConfig();
		out.println("Current rtree pagesize : "
				+ ((GeoFencingConfig) _config).getPageSize());
		return 0;
	}

	public final static String USAGE_SHOWPAGESIZE = "";
	public final static String[] HELP_SHOWPAGESIZE = new String[] { "Show current rtree pagesize", };

	public int cmdShowpagesize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current rtree pagesize : "
				+ ((GeoFencingConfig) _config).getPageSize());
		return 0;
	}

	// rtree buffer capacity
	public final static String USAGE_SETBUFFERCAPACITY = "<buffercapacity>";
	public final static String[] HELP_SETBUFFERCAPACITY = new String[] { "Set rtree buffer capacity", };

	public int cmdSetbuffercapacity(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		Integer bufferCapacity = new Integer((String) opts
				.get("buffercapacity"));
		((GeoFencingConfig) _config).setBufferCapacity(bufferCapacity);
		_config.updateConfig();
		out.println("Current rtree buffer capacity : "
				+ ((GeoFencingConfig) _config).getBufferCapacity());
		return 0;
	}

	public final static String USAGE_SHOWBUFFERCAPACITY = "";
	public final static String[] HELP_SHOWBUFFERCAPACITY = new String[] { "Show current rtree buffer capacity", };

	public int cmdShowbuffercapacity(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current rtree buffer capacity : "
				+ ((GeoFencingConfig) _config).getBufferCapacity());
		return 0;
	}

	// rtree overwrite
	public final static String USAGE_SETOVERWRITE = "<overwrite>";
	public final static String[] HELP_SETOVERWRITE = new String[] { "Set rtree overwrite", };

	public int cmdSetoverwrite(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Boolean overwrite = new Boolean((String) opts.get("overwrite"));
		((GeoFencingConfig) _config).setOverwrite(overwrite);
		_config.updateConfig();
		out.println("Current rtree overwrite : "
				+ ((GeoFencingConfig) _config).getOverwrite());
		return 0;
	}

	public final static String USAGE_SHOWOVERWRITE = "";
	public final static String[] HELP_SHOWOVERWRITE = new String[] { "Show current rtree overwrite", };

	public int cmdShowoverwrite(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current rtree overwrite : "
				+ ((GeoFencingConfig) _config).getOverwrite());
		return 0;
	}

	public final static String USAGE_SIMULATE = "<id> <inside>";
	public final static String[] HELP_SIMULATE = new String[] { "Fence simulation", };

	public int cmdSimulate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Boolean inside = new Boolean((String) opts.get("inside"));
		int id = Integer.parseInt((String) opts.get("id"));
		Balise b = new Balise(id, inside.booleanValue());
		((GeoFencingImpl) _peer).publish(b);
		out.println(b);
		return 0;
	}
	
	
	public final static String USAGE_GETZONES = "";
	public final static String[] HELP_GETZONES = new String[] { "Get current zones", };

	public int cmdGetzones(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Set zone = _peer.getZone();
		StringBuffer buf = new StringBuffer();
		if (zone != null) {
			Iterator iter = zone.iterator();
			if (iter.hasNext()){
				buf.append("Current zones : ");
				while (iter.hasNext()) {
					Balise b = (Balise) iter.next();
					buf.append(b);
					buf.append(", ");
				}
			}
			else{
				buf.append("Current zones : none");
			}
		}
		out.println(buf);
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
}
