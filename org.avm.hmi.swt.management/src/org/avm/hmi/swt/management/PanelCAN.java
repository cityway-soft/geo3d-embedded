package org.avm.hmi.swt.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.MessageBox;
import org.avm.hmi.swt.desktop.StateButton;
import org.avm.hmi.swt.management.bundle.ConfigImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PanelCAN extends AbstractPanel implements ConfigurableService {
	private static final String GROUPNAME = "can";

	private Text _text;

	private PrintStream _defaultOut;

	private PrintStream _defaultErr;

	private PanelCAN _instance;

	private ManagementAppender _appender;

	private Font _font;

	private Button _buttonCopyFiles;

	private Composite _compositeButtons;

	private boolean _initialized = false;

	private StateButton _buttonCanlogger;

	private Composite _compositeFrames;

	private Hashtable _hashFrameItems;

	public PanelCAN(Composite parent, int style) {
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

		_compositeButtons = new Composite(this, SWT.NONE);
		_compositeButtons.setBackground(DesktopStyle.getBackgroundColor());
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		_compositeButtons.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_compositeButtons.setLayoutData(gridData);

		_buttonCanlogger = new StateButton(_compositeButtons, SWT.NONE);
		_buttonCanlogger.setEnabled(false);
		_buttonCanlogger.setActiveColor(getDisplay().getSystemColor(
				SWT.COLOR_GREEN));
		_buttonCanlogger.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				String result = runConsoleCommand("/canlogger activate");
				getLogger().info("canlogger activated ? : string= " + result);
				StateButton b = (StateButton)arg0.getSource();
				boolean current = b.getSelection();
				_buttonCanlogger.setState(current);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		_buttonCanlogger.setText("Canlogger");
		gridData = new GridData();
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.horizontalIndent = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_buttonCanlogger.setLayoutData(gridData);

		_buttonCopyFiles = new Button(_compositeButtons, SWT.NONE);
		_buttonCopyFiles.setBackground(DesktopStyle.getBackgroundColor());

		_buttonCopyFiles.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				String mount = runConsoleCommand("/usbmass showmount");// Mount
																		// point:/mnt/sda1
				String tag = "Mount point:";
				int idx = mount.indexOf(tag);
				if (idx != -1) {
					mount = mount.substring(tag.length());
					mount = mount.trim();
				}
				File destdir = new File(mount);
				System.out.println("[DLA]-----> dir " + destdir + " exist ? "
						+ destdir.exists());

				destdir = new File(mount + "/Logs_"
						+ System.getProperty("org.avm.vehicule.id"));
				if (destdir.exists() == false) {
					destdir.mkdirs();
					System.out.println("[DLA]-----> dir " + destdir
							+ " not exist => mkdirs");
				}

				System.out.println("[DLA]-----> dir " + destdir + " exist ? "
						+ destdir.exists());

				String filename = runConsoleCommand("/canlogger showfilename");
				Object[] arguments = { System.getProperty("org.avm.home") };
				filename = MessageFormat.format(filename, arguments);
				idx = filename.indexOf('.');
				if (idx != -1) {
					filename = filename.substring(0, idx);
				}

				File file = new File(filename);
				String filter = file.getName();
				file = file.getParentFile();

				String[] list = file.list(new CanFilenameFilter(filter));
				String dest = destdir.getAbsolutePath();
				int cpt = 0;
				if (list != null) {
					MessageBox.setMessage("Copie....", "Copie en cours....",
							MessageBox.MESSAGE_WARNING, SWT.NONE);
					for (int i = 0; i < list.length; i++) {
						String src = file.getAbsolutePath() + "/" + list[i];
						String text = ".can.Copy [" + (i + 1) + "/"
								+ list.length + "] " + list[i] + ": ";
						try {
							copy(src, dest);
							cpt++;
							println(text + "ok\n");

						} catch (IOException e) {
							println(text + "ECHEC\n");
						}
					}
					MessageBox
							.setMessage("Copie des fichiers Canlogger",
									"Copie termin�e (" + cpt + "/"
											+ list.length + ")",
									MessageBox.MESSAGE_WARNING, SWT.NONE);

				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		_buttonCopyFiles.setText("Copy files");
		gridData = new GridData();
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.horizontalIndent = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_buttonCopyFiles.setLayoutData(gridData);

		_compositeFrames = new Composite(this, SWT.NONE);
		_compositeFrames.setBackground(DesktopStyle.getBackgroundColor());
		layout = new GridLayout();
		layout.numColumns = 6;
		layout.makeColumnsEqualWidth = true;
		_compositeFrames.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_compositeFrames.setLayoutData(gridData);
		addFrameItem("F003");
		addFrameItem("F004");
		addFrameItem("F005");
		addFrameItem("FEF1");
		addFrameItem("FEF2");
		addFrameItem("FEE9");

		_text = new Text(this, SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		_font = DesktopImpl.getFont(0, SWT.NORMAL); //$NON-NLS-1$
		_text.setFont(_font);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 10;
		_text.setLayoutData(gridData);
		_text.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
		_text.setText("\n              AUCUNE TRAME CAN  ");

	}

	private void addFrameItem(String name) {
		if (name != null) {
			Label item = getFrameItem(name);
			if (item == null) {
				item = new Label(_compositeFrames, SWT.NONE);
				item.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
				item.setText(name);
				GridData gridData = new GridData();
				gridData.heightHint = 20;
				gridData.horizontalIndent = 10;
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				item.setLayoutData(gridData);
				if (_hashFrameItems == null) {
					_hashFrameItems = new Hashtable();
				}

				_hashFrameItems.put(name, item);
			}
		}
	}

	private Label getFrameItem(String name) {
		if (_hashFrameItems != null) {
			return (Label) _hashFrameItems.get(name);
		}
		return null;
	}

	private void increase(String name) {
		Label item = getFrameItem(name);
		if (item != null) {
			item.setBackground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
			_hashFrameItems.remove(name);
		}
	}

	private void copy(String fromFile, String toFile) throws IOException {
		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			File src = new File(fromFile);
			from = new FileInputStream(src);

			File dest = new File(toFile);
			if (dest.isDirectory()) {
				dest = new File(toFile + "/" + src.getName());
				System.out.println("destfile===>" + dest.getAbsolutePath());
			}
			to = new FileOutputStream(dest);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					;
				}
		}
	}

	private class CanFilenameFilter implements FilenameFilter {

		private String _filter;

		public CanFilenameFilter(String filter) {
			_filter = filter;
		}

		public boolean accept(File dir, String filename) {
			return (filename.startsWith(_filter));
		}

	}

	private void println(final String s) {
		_display.asyncExec(new Runnable() {

			public void run() {

				if (_text != null && _text.isDisposed() == false
						&& isDisposed() == false) {
					if (s != null && s.indexOf(".can.") != -1) {
						if (!_initialized) {
							_initialized = true;
							_text.setForeground(getDisplay().getSystemColor(
									SWT.COLOR_BLACK));
						}
						_text.append(s);

						int idx = s.indexOf(":{\"SPN");
						if (idx != -1 && (idx - 7) > 0) {
							String pgn = s.substring(idx - 5, idx-1);
							increase(pgn);
						}
						// _text.append("\n");
					}
				}
			}
		});
	}

	private void begin() {
		PrintStream pout = new PrintStream(_defaultOut) {
			public void println(String s) {
				_instance.println(s);
				if (System.out != _defaultOut) {
					_defaultOut.println(s);
				}
			}
		};

		PrintStream perr = new PrintStream(_defaultErr) {
			public void println(String s) {
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

	protected void finalize() throws Throwable {
		super.finalize();
		if (_font != null) {
			_font.dispose();
			_font = null;
		}
	}

	public void dispose() {
		super.dispose();
		end();
	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		debug(true);
		String usbavailable = runConsoleCommand("/usbmass isavailable");
		System.out.println("USB : " + usbavailable);
		_buttonCopyFiles.setEnabled(usbavailable != null
				&& usbavailable.indexOf("NOT available") == -1);

		_buttonCanlogger
				.setEnabled(isBundleAvailable(" org.ango.elementary.canlogger"));

		String result = runConsoleCommand("/canlogger activate");
		getLogger().info("canlogger activated ? : state= '" + result +"'");
		boolean current = (result != null) && result.trim().equalsIgnoreCase("true");
		getLogger().info("canlogger activated ? : state= " + current);

		_buttonCanlogger.setState(current);
	}

	private void debug(boolean b) {
		if (b) {
			runConsoleCommand("/can0 setlevel debug");
		} else {
			runConsoleCommand("/can0 setlevel info");
		}

	}

	public void configure(Config config) {
		if (config != null) {
			Properties props = ((ConfigImpl) config).getProperty(null);
			Enumeration e = props.elements();
			while (e.hasMoreElements()) {
				Properties p = (Properties) e.nextElement();
				String group = p.getProperty(GROUP);
				if (group != null && group.equalsIgnoreCase(GROUPNAME)) {
					String startcmd = p.getProperty(START);
					String stopcmd = p.getProperty(STOP);
					String cmd[];
					if (stopcmd != null) {
						String[] command = { startcmd, stopcmd };
						cmd = command;
					} else {
						String[] command = { startcmd };
						cmd = command;
					}
					String cmdname = p.getProperty("name");
					addTestButton(_compositeButtons, cmdname, cmd);
				}
			}

		}

	}

	public void start() {
		debug(true);
	}

	public void stop() {
		debug(false);
	}

	public static class ItemCANFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelCAN(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelCAN.class.getName(),
				new ItemCANFactory());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
