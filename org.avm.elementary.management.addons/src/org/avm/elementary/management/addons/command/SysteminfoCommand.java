package org.avm.elementary.management.addons.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;

class SysteminfoCommand extends AbstractCommand {

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {
		long freememory = Runtime.getRuntime().freeMemory();
		long totalmemory = Runtime.getRuntime().totalMemory();
		int totalActiveThreads = Thread.activeCount();
		StringBuffer buf = new StringBuffer();
		String format = p.getProperty("format");

		File f = new File("/");
		
		

		
			JSONObject obj = new JSONObject();
			try {
				
				try {
					Process proc = Runtime.getRuntime().exec("/bin/df", new String[]{"/dev/hda2"});
					proc.waitFor();
					BufferedReader in = new BufferedReader(new InputStreamReader (proc.getInputStream()));
					StringBuffer b = new StringBuffer();
					String line="";
					String value="?";
					while ((line = in.readLine()) != null) {
						if (line.indexOf("/root") != -1 || line.indexOf("/mnt/fsuser-1") != -1|| line.indexOf("/home") != -1 ){
							StringTokenizer t = new StringTokenizer(" ");
							int idx = line.lastIndexOf(" ");
							String tmp = line.substring(0, idx);
							idx = tmp.lastIndexOf(" ");
							value = tmp.substring(idx+1);
							break;
						}
					}
					
					obj.put("os.app-partition", value);
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				obj.put("os.name", System.getProperty("os.name"));
				obj.put("os.arch", System.getProperty("os.arch"));
				obj.put("os.version", System.getProperty("os.version"));
				
				obj.put("jvm.memory.free", freememory);
				obj.put("jvm.memory.total", totalmemory);
				obj.put("jvm.thread-cout", totalActiveThreads);
				obj.put("jvm.version", System.getProperty("java.version"));
				
				obj.put("osgi.framework.version",System.getProperty("osgi.framework.version"));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		if (format != null && format.equals("json")) {
			buf.append(obj.toString());

		} else {
			
			StringTokenizer t = new StringTokenizer(obj.toString(), ",");
			while(t.hasMoreElements()){
				buf.append(t.nextElement());
				if(t.hasMoreElements()){
					buf.append(",");
					buf.append(LINE_SEPARATOR);
				}
			}
			buf.append(LINE_SEPARATOR);
			buf.append("Total active threads... :");
			buf.append(totalActiveThreads);
			buf.append(LINE_SEPARATOR);

			Thread[] threads = new Thread[totalActiveThreads];
			Thread.enumerate(threads);
			for (int i = 0; i < threads.length; i++) {
				buf.append("--- #" + i + " :" + threads[i]);
				buf.append(LINE_SEPARATOR);
			}

		}

		out.println(buf.toString());
	}

	public static class SystemInfoCommandFactory extends CommandFactory {
		protected Command create() {
			return new SysteminfoCommand();
		}
	}

	static {
		CommandFactory.factories.put(SysteminfoCommand.class.getName(),
				new SystemInfoCommandFactory());
	}

}
