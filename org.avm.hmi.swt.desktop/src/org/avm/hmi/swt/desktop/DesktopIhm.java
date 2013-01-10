package org.avm.hmi.swt.desktop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.avm.elementary.common.Config;
import org.avm.elementary.common.Scheduler;
import org.avm.hmi.swt.application.display.Application;
import org.avm.hmi.swt.desktop.bundle.ConfigImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class DesktopIhm {

	private static final boolean ENABLE_RIGHT_PANEL = Boolean.valueOf(
			System.getProperty("org.avm.hmi.swt.desktop.enable-right-panel",
					"true")).booleanValue();

	private final static SimpleDateFormat DF = new SimpleDateFormat("HH:mm");

	private Display _display;

	private Shell _shell = null;

	private Composite _compositeButtons = null;

	private Composite _compositeActivite = null;

	private Label _labelInformation2 = null;

	private Label _labelLogo = null;

	private TabFolder _tabFolder = null;

	private Label _labelHeure = null;

	private Label _labelVehicule = null;

	private Label _labelVehiculeInfo = null;

	private HashMap _hashItem = new HashMap();

	// private Font _fontNormal;
	//
	// private Font _fontInformation2;
	//
	// private Font _fontTabFolder;
	//
	// private Font _fontSmall;

	private Button _buttonNightMode;

	private Date _beginDay;

	private Date _endDay;

	private Scheduler _scheduler = new Scheduler();

	private Object _timerTaskEndDay;

	private Object _timerTaskBeginDay;

	public static HashMap _hashFonts = null;

	private void createLogo() {
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.grabExcessHorizontalSpace = false;
		gridData11.grabExcessVerticalSpace = false;
		gridData11.verticalAlignment = GridData.FILL;

		_labelLogo = new Label(_shell, SWT.NONE);
		_labelLogo.setText("Logo");
		_labelLogo.setLayoutData(gridData11);
		String version = System.getProperty("org.avm.version");
		if (version != null) {
			_labelLogo.setToolTipText("Version " + version);
		}

		Image imglogo = null;
		String filename = System.getProperty("org.avm.home") + "/data/logo.jpg";
		File file = new File(filename);
		if (file.exists()) {
			imglogo = new Image(_display, filename); //$NON-NLS-1$
		} else {
			imglogo = new Image(_display, getClass().getResourceAsStream(
					"/resources/logo.jpg")); //$NON-NLS-1$
		}
		_labelLogo.setImage(imglogo);

		if (ENABLE_RIGHT_PANEL == false) {
			_labelLogo.setEnabled(false);
			_labelLogo.setVisible(false);
		}
	}

	public void configure(Config config) {
		_beginDay = ((ConfigImpl) config).getBeginDay();
		_endDay = ((ConfigImpl) config).getEndDay();

		setDayRange(_beginDay, _endDay);
	}

	/**
	 * This method initializes compositeButtons
	 * 
	 */
	private void createCompositeButtons() {
		Layout layout;
		Object layoutData;

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.makeColumnsEqualWidth = false;
		gridLayout1.verticalSpacing = 1;
		gridLayout1.marginWidth = 1;
		gridLayout1.marginHeight = 1;
		gridLayout1.numColumns = 1;
		gridLayout1.horizontalSpacing = 1;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.verticalAlignment = GridData.FILL;

		layout = gridLayout1;
		layoutData = gridData3;

		_compositeButtons = new Composite(_shell, SWT.NONE);
		if (ENABLE_RIGHT_PANEL == false) {
			_compositeButtons.setEnabled(false);
			_compositeButtons.setVisible(false);
		}
		_compositeButtons.setBackground(DesktopStyle.getBackgroundColor());
		_compositeButtons.setLayout(layout);
		_compositeButtons.setLayoutData(layoutData);

	}

	/**
	 * This method initializes compositeActivite
	 * 
	 */
	private void createCompositeActivite() {
		GridLayout gridLayout7 = new GridLayout();
		gridLayout7.numColumns = 4;
		GridData gridData6 = new GridData();
		gridData6.verticalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = false;
		gridData6.horizontalAlignment = GridData.FILL;
		_compositeActivite = new Composite(_shell, SWT.NONE);
		_compositeActivite.setBackground(DesktopStyle.getBackgroundColor());
		_compositeActivite.setLayout(gridLayout7);
		_compositeActivite.setLayoutData(gridData6);

		_labelInformation2 = new Label(_compositeActivite, SWT.CENTER);

		Font _fontInformation2 = Application.getFont(Desktop.DEFAULT_FONTSIZE + 8,
				SWT.ITALIC);
		_labelInformation2.setFont(_fontInformation2);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		_labelInformation2.setLayoutData(gridData);
		_labelInformation2.setBackground(DesktopStyle.getBackgroundColor());
		setInformation(null);
	}

	/**
	 * This method initializes tabFolder
	 * 
	 */
	private void createTabFolder() {
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_tabFolder = new TabFolder(_shell, SWT.NONE | SWT.BOTTOM);

		Font _fontTabFolder = getFont(6, SWT.NORMAL);
		_tabFolder.setFont(_fontTabFolder);
		_tabFolder.setBackground(DesktopStyle.getBackgroundColor());
		_tabFolder.setLayoutData(gridData);
		_shell.layout();

	}

	public void addTabItem(String item, Composite composite) {
		TabItem tabItem2 = new TabItem(_tabFolder, SWT.NONE);
		removeTabItem(item);
		_hashItem.put(item, tabItem2);
		tabItem2.setText(item);
		tabItem2.setControl(composite);
		_tabFolder.layout();
	}

	public void addTabItem(String item, Composite composite, int index) {
		TabItem tabItem2 = new TabItem(_tabFolder, SWT.NONE, index);
		removeTabItem(item);
		_hashItem.put(item, tabItem2);
		tabItem2.setText(item);
		tabItem2.setControl(composite);
		_tabFolder.layout();
	}

	public void removeTabItem(String string) {
		TabItem item = (TabItem) _hashItem.get(string);
		if (item != null) {
			item.dispose();
		}
	}

	public Composite getMiddlePanel() {
		return _tabFolder;
	}

	public Shell getShell() {
		return _shell;
	}

	public Composite getRightPanel() {
		return _compositeButtons;
	}

	/**
	 * @param args
	 */
	public void open() {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {

			public void run() {
				GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = ENABLE_RIGHT_PANEL ? 2 : 1;
				gridLayout.verticalSpacing = 0;
				gridLayout.marginWidth = 1;
				gridLayout.marginHeight = 1;
				gridLayout.horizontalSpacing = 0;
				_shell = new Shell();

				_shell.setText(Desktop.APPLICATION_NAME);
				_shell.setBackground(DesktopStyle.getBackgroundColor());
				_shell.setLayout(gridLayout);

				Rectangle window = Geometry.parse(_display.getClientArea(),
						System.getProperty("org.avm.hmi.swt.geometry"));
				_shell.setLocation(new Point(window.x, window.y));
				_shell.setSize(new Point(window.width, window.height));

				createLogo();

				createCompositeActivite();

				createCompositeButtons();

				createTabFolder();

				Font _fontNormal = getFont(10, SWT.NORMAL);
				Font _fontSmall = getFont(0, SWT.NORMAL);

				GridData gridData = new GridData();
				gridData.grabExcessVerticalSpace = false;
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.heightHint = 25;
				gridData.widthHint = -1;
				gridData.grabExcessHorizontalSpace = true;
				_labelVehicule = new Label(_compositeButtons, SWT.CENTER);
				_labelVehicule.setForeground(_display
						.getSystemColor(SWT.COLOR_GRAY));
				_labelVehicule.setBackground(DesktopStyle.getBackgroundColor());
				_labelVehicule.setFont(_fontNormal);
				_labelVehicule.setLayoutData(gridData);

				gridData = new GridData();
				gridData.grabExcessVerticalSpace = false;
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.heightHint = 18;
				gridData.widthHint = -1;
				gridData.grabExcessHorizontalSpace = true;
				_labelVehiculeInfo = new Label(_compositeButtons, SWT.CENTER);
				_labelVehiculeInfo.setBackground(DesktopStyle
						.getBackgroundColor());
				_labelVehiculeInfo.setForeground(_display
						.getSystemColor(SWT.COLOR_GRAY));
				_labelVehiculeInfo.setFont(_fontSmall);
				_labelVehiculeInfo.setLayoutData(gridData);

				gridData = new GridData();
				gridData.grabExcessVerticalSpace = false;
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalAlignment = GridData.FILL;
				gridData.heightHint = 30;
				gridData.widthHint = -1;
				gridData.grabExcessHorizontalSpace = false;
				_labelHeure = new Label(_compositeButtons, SWT.CENTER
						| SWT.BOTTOM);
				_labelHeure.setBackground(DesktopStyle.getBackgroundColor());

				_labelHeure.setFont(_fontNormal);
				_labelHeure.setLayoutData(gridData);
				startClock();

				_buttonNightMode = new Button(_compositeButtons, SWT.NONE
						| SWT.TOGGLE);
				_buttonNightMode.setSelection(DesktopStyle.isNightMode());
				_buttonNightMode.setText(Messages
						.getString("DesktopImpl.mode-nuit"));
				_buttonNightMode.setBackground(DesktopStyle
						.getBackgroundColor());
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.heightHint = 25;
				_buttonNightMode.setLayoutData(gridData);
				_buttonNightMode.setBackground(DesktopStyle
						.getBackgroundColor());

				_buttonNightMode.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						setNightMode(_buttonNightMode.getSelection());
					}
				});

				_compositeButtons.layout();
				_compositeActivite.layout();
				_shell.open();
				window = Geometry.parse(_display.getClientArea(),
						System.getProperty("org.avm.hmi.swt.geometry"));
				_shell.setLocation(new Point(window.x, window.y));
				_shell.setSize(new Point(window.width, window.height));
				updateVehiculeDetails();

				checkNightMode();

				_shell.addListener(SWT.Close, new Listener() {
					public void handleEvent(Event event) {
						System.out.println("Close Windows asked : exit app");
						System.exit(0);
					}
				});
			}

		});
	}

	public void refresh() {
		_display.asyncExec(new Runnable() {
			public void run() {
				updateVehiculeDetails();
			}
		});

	}

	public void setDayRange(Date start, Date stop) {
		_beginDay = start;
		_endDay = stop;
		Calendar cal;
		if (_beginDay == null) {
			_beginDay = getDate(6, 15, 30).getTime();
		} else {
			cal = Calendar.getInstance();
			cal.setTime(_beginDay);// getDate(6, 15 , 30);
			cal = getDate(cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			_beginDay = cal.getTime();
		}

		if (_endDay == null) {
			_endDay = getDate(19, 30, 56).getTime();
		} else {
			cal = Calendar.getInstance();
			cal.setTime(_endDay);// getDate(6, 15 , 30);
			cal = getDate(cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			_endDay = cal.getTime();
		}

		if (_timerTaskBeginDay != null) {
			_scheduler.cancel(_timerTaskBeginDay);
		}

		if (_timerTaskEndDay != null) {
			_scheduler.cancel(_timerTaskEndDay);
		}

		_timerTaskBeginDay = _scheduler.schedule(new Runnable() {
			public void run() {
				setNightMode(false);
			}
		}, _beginDay);

		_timerTaskEndDay = _scheduler.schedule(new Runnable() {
			public void run() {
				setNightMode(true);
			}
		}, _endDay);
	}

	public void checkNightMode() {
		Calendar now = Calendar.getInstance();
		Calendar timeSunUp = Calendar.getInstance();
		timeSunUp.setTime(_beginDay);// getDate(6, 15 , 30);
		timeSunUp = getDate(timeSunUp.get(Calendar.HOUR_OF_DAY),
				timeSunUp.get(Calendar.MINUTE), timeSunUp.get(Calendar.SECOND));

		boolean nightmode = now.before(timeSunUp);
		if (nightmode) {
			setNightMode(nightmode);
			return;
		}
		Calendar timeSunDown = Calendar.getInstance();
		timeSunDown.setTime(_endDay);
		;// getDate(19, 30, 56);
		timeSunDown = getDate(timeSunDown.get(Calendar.HOUR_OF_DAY),
				timeSunDown.get(Calendar.MINUTE),
				timeSunDown.get(Calendar.SECOND));

		nightmode = now.after(timeSunDown);

		setNightMode(nightmode);
	}

	public void setNightMode(final boolean b) {
		_display.asyncExec(new Runnable() {
			public void run() {
				DesktopStyle.setNightMode(b);
				if (_buttonNightMode != null
						&& _buttonNightMode.isDisposed() == false) {
					_buttonNightMode.setSelection(DesktopStyle.isNightMode());
				}
			}
		});

	}

	private Calendar getDate(int hour, int min, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		return cal;
	}

	public void close() {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {
			public void run() {
				_shell.dispose();
			}
		});
		// _display.dispose();
	}

	public void startClock() {
		Runnable run = new Runnable() {
			public void run() {
				final boolean b = org.avm.device.plateform.System.isOnTime();
				try {
					if (_shell.isDisposed())
						return;
					if (b) {
						_labelHeure.setText(DF.format(new Date()));
					} else {
						_labelHeure.setText("--:--");
					}
					_labelHeure.redraw();
					_shell.getDisplay().timerExec(5 * 1000, this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		_shell.getDisplay().timerExec(1 * 1000, run);

	}

	public void setMessageBox(final String title, final String message,
			final int type, SelectionListener listener) {
		if (_display == null)
			return;
		MessageBox.setMessage(title, message, type, SWT.CENTER, listener);
	}

	private void updateVehiculeDetails() {
		_labelVehicule
				.setText(System.getProperty("org.avm.vehicule.id", "???"));
		StringBuffer buf = new StringBuffer();

		buf.append(System.getProperty("org.avm.exploitation.id", "???"));
		buf.append(" / ");
		buf.append(System.getProperty("org.avm.branch", "??"));
		buf.append("_");
		buf.append(System.getProperty("org.avm.country", "??"));
		buf.append("_");
		buf.append(System.getProperty("org.avm.region", "??"));
		_labelVehiculeInfo.setText(buf.toString());
	}

	public void setInformation(final String info) {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {
			public void run() {
				if (info == null || info.trim().equals("")) {
					_labelInformation2.setText(System.getProperty(
							"org.avm.exploitation.name", ""));
				} else {
					_labelInformation2.setText(info);
				}
				_compositeActivite.layout();
			}
		});
	}

	public void activateItem(final String name) {
		_display.asyncExec(new Runnable() {
			public void run() {
				TabItem item = (TabItem) _hashItem.get(name);
				if (item != null) {
					TabItem[] items = _tabFolder.getItems();
					int i = 0;
					int index = -1;
					while (i < items.length && index == -1) {
						index = (items[i] == item) ? i : -1;
						i++;
					}
					if (index != -1) {
						_tabFolder.setSelection(index);
					}
				}
			}
		});
	}

	public Object[] getItems() {
		Set set = _hashItem.keySet();
		return set.toArray();
	}

	public Display getDisplay() {
		return _display;
	}

	public void setDisplay(Display display) {
		if (display != null) {
			_display = display;
		}
	}
	
	public static Font getFont(int deltasize, int style) {
		return Application.getFont(deltasize, style);
	}

//	public static Font getFont(int deltasize, int style) {
//		Font font = null;
//		String key = deltasize + "_" + style;
//
//		if (_hashFonts == null) {
//			_hashFonts = new HashMap();
//		}
//		font = (Font) _hashFonts.get(key);
//		if (font == null) {
//			font = new Font(Display.getDefault(), Desktop.DEFAULT_FONT,
//					Desktop.DEFAULT_FONTSIZE + deltasize, style);
//			_hashFonts.put(key, font);
//		}
//		return font;
//	}


}
