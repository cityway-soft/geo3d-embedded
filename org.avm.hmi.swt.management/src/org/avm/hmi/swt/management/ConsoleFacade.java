package org.avm.hmi.swt.management;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;

import org.knopflerfish.service.console.ConsoleService;
import org.knopflerfish.service.console.Session;
import org.knopflerfish.service.console.SessionListener;

public class ConsoleFacade {
	private ConsoleService _console;

	public void setConsoleService(ConsoleService console) {
		_console = console;
	}

	public void runSession(String cmd, final ConsoleListener listener) {
		if (_console != null && listener != null ) {
			try {
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				//OutputStream out = System.out;
				PrintWriter printWriter = new PrintWriter(out, true);
				String command = cmd + "\n";
				Reader reader = new InputStreamReader(new ByteArrayInputStream(
						command.getBytes()));
				Session session = _console.runSession("test session", reader, printWriter);
				session.addSessionListener(new SessionListener(){

					public void sessionEnd(Session arg0) {
						StringBuffer buffer = new StringBuffer();
						String data=new String(out.toByteArray());
						StringTokenizer t= new StringTokenizer(data, "\n");
						while (t.hasMoreElements()) {
							String line = (String) t.nextElement();
							if (buffer.length()!=0){
								buffer.append("\n");
							}
							if (line.trim().startsWith(">") == false){
								buffer.append(line);
							}
							
						}
						listener.publish(buffer.toString());
						
					}});
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

	}

	public String runCommand(String command) {
		String result = null;
		if (_console != null) {
			try {
				result = _console.runCommand(command);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return result;
	}

	public boolean isBundleAvailable(String bundle) {
		boolean activated = (bundle == null);
		String result = "ok";
		if (bundle != null) {
			activated = false;
			result = runCommand("/management status " + bundle);
			if (result != null && result.indexOf("no bundle matchs") == -1) {
				activated = true;
			}
		}

		return activated;
	}

}
