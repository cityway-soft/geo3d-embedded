package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.BundleAction;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.Constants;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class StatusCommand extends AbstractCommand implements BundleAction {
	private static final String SEP = " : ";

	private static final SimpleDateFormat _dateFormatter = new SimpleDateFormat(
			"dd/MM/yy HH:mm");

	private static final DecimalFormat _decimalFormatter = new DecimalFormat(
			"000");

	private StringBuffer buffer;

	private boolean isColored;

	private ManagementService _management;

	private boolean isComplete;

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		_management = management;

		String filter = null;
		if (p != null) {
			filter = p.getProperty("filter");
		}

		isColored = Boolean.valueOf(p.getProperty("colored", "false"))
				.booleanValue();

		isComplete = Boolean.valueOf(p.getProperty("complete", "false"))
				.booleanValue();

		buffer = new StringBuffer();
		execute(this, filter, context, out, false);

		out.println(buffer);

	}

	private String getStatus(int status, boolean colored) {
		String result = "????";
		switch (status) {
		case Bundle.ACTIVE:
			result = (colored ? Constants.SETCOLOR_SUCCESS : "") + "ACT";
			break;
		case Bundle.INSTALLED:
			result = (colored ? Constants.SETCOLOR_FAILURE : "") + "ins";
			break;
		case Bundle.RESOLVED:
			result = (colored ? Constants.SETCOLOR_WARNING : "") + "res";
			break;
		case Bundle.STARTING:
			result = (colored ? Constants.SETCOLOR_NORMAL : "") + "sta";
			break;
		case Bundle.STOPPING:
			result = (colored ? Constants.SETCOLOR_NORMAL : "") + "stop";
			break;
		case Bundle.UNINSTALLED:
			result = (colored ? Constants.SETCOLOR_NORMAL : "") + "uni";
			break;
		}
		return result;
	}

	public static class StatusCommandFactory extends CommandFactory {
		protected Command create() {
			return new StatusCommand();
		}
	}

	static {
		CommandFactory.factories.put(StatusCommand.class.getName(),
				new StatusCommandFactory());
	}

	public void execute(Bundle b, BundleContext context, PrintWriter out) {
		buffer.append(getStatus(b.getState(), isColored));
		buffer.append(SEP);
		buffer.append(_decimalFormatter.format(b.getBundleId()));
		buffer.append(SEP);
		buffer.append(b.getHeaders().get("Bundle-Version"));
		buffer.append(" (");
		buffer.append(_dateFormatter.format(new Date(b.getLastModified())));
		buffer.append(")");
		buffer.append(" [");
		buffer.append(_management.getStartLevelService().getBundleStartLevel(b));
		buffer.append("] ");
		buffer.append(SEP);
		buffer.append(b.getHeaders().get("Bundle-SymbolicName"));
		Object packName = b.getHeaders().get("TAB-Pack");
		
		if (isComplete) {
			buffer.append(" (");
			buffer.append(b.getHeaders().get("Bundle-Name"));

			Object packComment = b.getHeaders().get("TAB-Comment");


			Object builtby = b.getHeaders().get("Built-By");

			String commitId = null;

			Object commitIdTemp = b.getHeaders()
					.get("Eclipse-SourceReferences");

			if (commitIdTemp != null) {
				StringTokenizer t = new StringTokenizer((String)commitIdTemp, ";");
				int idx;
				while (t.hasMoreElements()) {
					String token = t.nextToken();
					if (token.startsWith("commitId")) {
						idx = token.indexOf("=");
						commitId = token.substring(idx + 1, Math.min(idx+1+7, token.length()-1));
					}
				}
			}
			
			if (commitId != null){
				buffer.append(" ");
				buffer.append(commitId);

			}
			
			if (builtby != null){
				buffer.append(" ");
				buffer.append("by ");
				buffer.append(builtby);
			}
			
			if (packName != null){
				buffer.append(" ");
				buffer.append("pack=");
				buffer.append(packName);
				buffer.append("/");
				buffer.append(packComment==null?"-":packComment);
			}else{
				buffer.append(" ");
				buffer.append("pack=*default*");
			}

			buffer.append(")");
		}
		else{
			if (packName != null){
				buffer.append(" (");
				buffer.append(packName);
				buffer.append(")");
			}
		}
		buffer.append(System.getProperty("line.separator"));
		if (isColored) {
			buffer.append(Constants.SETCOLOR_NORMAL);
		}

	}

}
