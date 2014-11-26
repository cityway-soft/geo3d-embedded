package org.avm.hmi.swt.management;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Scheduler;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopIhm;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.MessageBox;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractPanel extends Composite implements Listener,
		ConsoleFacadeInjector, ManageableService, SchedulerInjector, LoggerInjector {
	public static final String GROUP = "group";
	public static final String START = "start";
	public static final String STOP = "stop";

	private static final int INDENT = Desktop.DEFAULT_FONTSIZE / 5;

	private ConsoleFacade _console;

	protected Display _display;

	private ButtonSelectionListener _buttonSelectionListener;
	private Scheduler _scheduler;
	private Logger _log;

	public AbstractPanel(Composite parent, int style) {
		super(parent, style);

		_display = parent.getDisplay();
		initialize();
		addListener(SWT.Show, this);
		addListener(SWT.Hide, this);
		_buttonSelectionListener = new ButtonSelectionListener();
	}
 
	protected abstract void initialize();

	public void start() {
		setEnabled(checkService());
	}

	public void stop() {

	}

	protected boolean checkService() {
		return true;
	}

	public void handleEvent(Event event) {
		if (event.type == SWT.Show) {
			start();
		} else if (event.type == SWT.Hide) {
			stop();
		}
	}

	public void setConsoleFacade(ConsoleFacade console) {
		_console = console;
	}
	
	public void setScheduler(Scheduler scheduler){
		_scheduler = scheduler;
	}
	
	public Scheduler getScheduler(){
		return _scheduler;
	}
	
	public void setLogger(Logger logger){
		_log = logger;
	}
	
	public Logger getLogger(){
		return _log;
	}

	public void runConsoleSession(String command, ConsoleListener listener) {
		_console.runSession(command, listener);
	}
	
	protected String runConsoleCommand(String command) {
			return _console.runCommand(command);
	}

	protected boolean isBundleAvailable(String bundle) {
		return _console.isBundleAvailable(bundle);
	}

	protected void forkConsoleCommand(final String command) {
		if (_console != null) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String result = _console.runCommand(command);
					if (!result.trim().equals("")  && !result.startsWith("OK")) {
						MessageBox.setMessage("Resultat", result,
								MessageBox.MESSAGE_NORMAL, SWT.NONE);
					}
				}
			});
			thread.start();

		}
	}

	protected void addTestButton(String name, String[] cmdgroup) {
		addTestButton(this, name, cmdgroup);
	}

	protected void addTestButton(Composite composite, String name,
			String[] cmdgroup) {
		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = INDENT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;

		if (cmdgroup.length == 1) {
			Button button = new Button(composite, SWT.PUSH);
			button.setText(name);
			button.setData(cmdgroup);
			button.addSelectionListener(_buttonSelectionListener);
			button.setLayoutData(gridData);
			button.setBackground(DesktopStyle.getBackgroundColor());
			button.setEnabled( check(cmdgroup[0]) );
			Font font = DesktopImpl.getFont(8, SWT.NORMAL);
			button.setFont(font);
		} else {
			StateButton button = new StateButton(composite, SWT.BORDER);
			button.setActiveColor(DesktopIhm.VERT);	
			button.setNotActiveColor(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			button.setText(name);
			button.setNotActiveLabel("Désact");
			button.setActiveLabel("Activer");
			button.setData(cmdgroup);
			button.addSelectionListener(_buttonSelectionListener);
			button.setLayoutData(gridData);
			button.setBackground(DesktopStyle.getBackgroundColor());
			button.setEnabled( check(cmdgroup[0]) );
			
		}
	}
	
	private boolean  check(String cmdgroup){
		int idx = cmdgroup.indexOf(" ");
		String cmd=null;
		if (idx != -1){
			cmd = cmdgroup.substring(0, idx);
		}
		else{
			cmd=cmdgroup;
		}
		
		cmd+=" help";
		String result = runConsoleCommand(cmd);		
		return ! (result == null || result.indexOf("No such command group:") != -1);
	}

	private void test(String cmd, boolean fork) {
		StringTokenizer t = new StringTokenizer(cmd, "\n"); //$NON-NLS-1$
		while (t.hasMoreElements()) {
			String command = (String) t.nextElement();
			// System.out.println("Run command :" + command);
			if (fork) {
				forkConsoleCommand(command);
			} else {
				String result = runConsoleCommand(command);
				if (!result.trim().equals("") && !result.startsWith("OK")) { //$NON-NLS-1$
					MessageBox.setMessage(
							Messages.getString("ItemTest.resultat"), result, //$NON-NLS-1$
							MessageBox.MESSAGE_NORMAL, SWT.NONE);
				}
			}
		}
	}

	public class ButtonSelectionListener extends SelectionAdapter {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			Object obj = e.getSource();
			String command = ""; //$NON-NLS-1$
			boolean fork = false;
			if (obj instanceof Button) {
				Button b = (Button) e.getSource();
				String[] cmdgroup = (String[]) b.getData();
				int idx = 0;
				if (cmdgroup.length > 1) {
					idx = b.getSelection() ? 0 : 1;
				}
				command = cmdgroup[idx];
			} else {
				StateButton b = (StateButton) e.getSource();
				String[] cmdgroup = (String[]) b.getData();
				int idx = 0;
				if (cmdgroup.length > 1) {
					idx = b.getSelection() ? 0 : 1;
				}
				fork = true;
				command = cmdgroup[idx];
			}

			test(command, fork);
		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"
