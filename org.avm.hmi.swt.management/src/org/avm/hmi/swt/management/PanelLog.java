package org.avm.hmi.swt.management;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class PanelLog extends AbstractPanel {

	private Text _text;

	private PrintStream _defaultOut;

	private PrintStream _defaultErr;

	private PanelLog _instance;

	private ManagementAppender _appender;

	private Font _fontText;

	private Font _fontTable;

	private Table _table;

	public PanelLog(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		_instance = this;
		_defaultOut = System.out;
		_defaultErr = System.err;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		create();
		begin();
	}

	/**
	 * This method initializes itemBundles
	 * 
	 */
	private void create() {
		GridData gridData;
		GridLayout layout;

		Composite composite = this;
		layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setBackground(DesktopStyle.getBackgroundColor());

		_fontTable = DesktopImpl.getFont( 5, SWT.NORMAL); //$NON-NLS-1$
		_table = new Table(composite, SWT.BORDER);
		_table.setFont(_fontTable);
		_table.setBackground(DesktopStyle.getBackgroundColor());
		_table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				action(_table.getSelectionIndex());
			}
		});
		TableColumn tableColumn0 = new TableColumn(_table, SWT.NONE);
		tableColumn0.setWidth(100);
		tableColumn0.setText("Command"); //$NON-NLS-1$

		TableColumn tableColumn1 = new TableColumn(_table, SWT.NONE);
		tableColumn1.setWidth(0);
		tableColumn1.setText("Log"); //$NON-NLS-1$
		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_table.setLayoutData(gridData);

		_text = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.WRAP | SWT.BORDER);
		_fontText = DesktopImpl.getFont(0, SWT.NORMAL); //$NON-NLS-1$
		_text.setFont(_fontText);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		_text.setLayoutData(gridData);

	}

	private void action(int index) {
		TableItem item = _table.getItem(index);
		String cmdgrp = item.getText(0);
		String tmp = item.getText(1);
		boolean activated = !(tmp != null && tmp.equalsIgnoreCase("true"));
		item.setText(1, (activated ? "true" : "false"));
		item.setBackground(activated ? getDisplay().getSystemColor(
				SWT.COLOR_BLUE) : DesktopStyle.getBackgroundColor());
		System.out.println((activated ?"Enable":"Disable") + " log for " + cmdgrp);
		setLevelDebug(cmdgrp, activated);

	}

	private void println(final String s) {
		_display.asyncExec(new Runnable() {
			public void run() {
				if (_text != null && _text.isDisposed() == false
						&& isDisposed() == false) {
					_text.append(s);
				}
			}
		});
	}

	private void begin() {
		PrintStream pout = new PrintStream(_defaultOut) {
			public void println(String str) {
				String s = str + "\n";
				_instance.println(s);
				if (System.out != _defaultOut) {
					_defaultOut.println(s);
				}
			}
		};

		PrintStream perr = new PrintStream(_defaultErr) {
			public void println(String str) {
				String s = str + "\n";
				_instance.println(s);
				if (System.err != _defaultErr) {
					_defaultErr.println(s);
				}
			}
		};

		PrintWriter w = new PrintWriter(_defaultOut) {
			public void write(String s) {
				_instance.println(s);
			}
		};

		System.setOut(pout);
		System.setErr(perr);
		_appender = new ManagementAppender(w);
		Logger.getRoot().addAppender(_appender);
	}

	private void end() {
		System.setOut(_defaultOut);
		System.setErr(_defaultErr);
		Logger.getRoot().removeAppender(_appender);
	}


	public void dispose() {
		super.dispose();
		end();
	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		configure();
	}

	private void configure() {
		runConsoleSession("/session help", new ConsoleListener() {

			public void publish(String response) {
				StringTokenizer t = new StringTokenizer(response, "\n");
				String[] list = new String[t.countTokens()];
				int i = 0;
				while (t.hasMoreElements()) {
					String line = (String) t.nextElement();
					int idx = line.indexOf(" - ");
					if (idx != -1) {
						String cmdgrp = line.substring(0, idx);
						//setLevelDebug(cmdgrp, false);
						list[i] = cmdgrp.trim();
						i++;
					}
				}
				createCategory(list);
			}
		});

	}

	private void createCategory(final String[] list) {
		_display.asyncExec(new Runnable() {
			public void run() {
				for (int i = 0; i < list.length; i++) {
					String val[] = new String[] { list[i], ""+isLevelDebug( list[i]) };
					TableItem item = new TableItem(_table, SWT.NONE);
					item.setText(val);

				}

			}
		});
	}

	private boolean isLevelDebug(String cmdgrp) {
		String cmd="/" + cmdgrp + " showlevel";
		String result = runConsoleCommand(cmd);
		return (result != null && result.equals("true"));
	}
	
	private void setLevelDebug(String cmdgrp, boolean active) {
		String cmd;
		if (active) {
			cmd = "/" + cmdgrp + " setlevel debug";
		} else {
			cmd = "/" + cmdgrp + " setlevel info";
		}
		// System.out.println("==>" + cmd);
		runConsoleCommand(cmd);
	}

	public static class ItemLogFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelLog(parent, style);
		}
	}

	static {
		PanelFactory.factories
				.put(PanelLog.class.getName(), new ItemLogFactory());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
