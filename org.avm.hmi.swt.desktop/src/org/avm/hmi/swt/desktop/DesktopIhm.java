package org.avm.hmi.swt.desktop;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.Scheduler;
import org.avm.hmi.swt.application.display.AVMDisplay;
import org.avm.hmi.swt.application.display.Application;
import org.avm.hmi.swt.desktop.bundle.ConfigImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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

	public static final SimpleDateFormat DF = new java.text.SimpleDateFormat(
			"dd/MM/yyyy");

	private static final boolean ENABLE_RIGHT_PANEL = Boolean.valueOf(
			System.getProperty("org.avm.hmi.swt.desktop.enable-right-panel",
					"true")).booleanValue();

	private final static SimpleDateFormat HF = new SimpleDateFormat("HH:mm");

	public final static Color VERT = new Color(Display.getCurrent(), 146, 208,
			80);
	public final static Color BLEU = new Color(Display.getCurrent(), 153, 204,
			255);
	public final static Color JAUNE = new Color(Display.getCurrent(), 255, 255,
			153);
	public final static Color ROUGE = new Color(Display.getCurrent(), 255, 50,
			50);
	public final static Color ORANGE = new Color(Display.getCurrent(), 255,
			133, 153);

	private Display _display;

	private Shell _shell = null;

	private Composite _compositeButtons = null;

	private Composite _compositeActivite = null;

	private Label _labelInformation2 = null;

	private TabFolder _tabFolder = null;

	private Label _labelHeure = null;

	private Label _labelVehicule = null;

	private HashMap _hashItem = new HashMap();

	private Logger logger = Logger.getInstance(DesktopIhm.class.getName());
	private Button _buttonNightMode;

	private Date _beginDay;

	private Date _endDay;

	private Scheduler _scheduler = new Scheduler();

	private Object _timerTaskCheckNightMode;

	public static HashMap _hashFonts = null;

	private String _favorite;

	private void createLogo() {

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.heightHint = 30;
		gridData.widthHint = -1;
		gridData.grabExcessHorizontalSpace = false;
		_labelHeure = new Label(_shell, SWT.CENTER | SWT.BOTTOM);
		_labelHeure.setBackground(DesktopStyle.getBackgroundColor());

		_labelHeure.setFont(DesktopImpl.getFont(18, SWT.BOLD));
		_labelHeure.setLayoutData(gridData);
		Image imglogo = new Image(_display, getClass().getResourceAsStream(
				"/resources/logo.jpg")); //$NON-NLS-1$
		_labelHeure.setImage(imglogo);
		_labelHeure.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent arg0) {
			}

			public void mouseDown(MouseEvent arg0) {
				updateTime();
			}

			public void mouseUp(MouseEvent arg0) {
			}

		});

	}

	public void configure(Config config) {
		if (config != null) {
			_beginDay = ((ConfigImpl) config).getBeginDay();
			_endDay = ((ConfigImpl) config).getEndDay();

			setDayRange(_beginDay, _endDay);
		}
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

		Font _fontInformation2 = Application.getFont(
				Desktop.DEFAULT_FONTSIZE + 8, SWT.ITALIC);
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

		Font _fontTabFolder = getFont(AVMDisplay.TABFOLDER_FONT,
				AVMDisplay.TABFOLDER_FONTSIZE_DELTA, SWT.NORMAL);

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

	public Composite getMainPanel() {
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
				logger.debug("Debut (run)");
				GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = ENABLE_RIGHT_PANEL ? 2 : 1;
				gridLayout.verticalSpacing = 0;
				gridLayout.marginWidth = 1;
				gridLayout.marginHeight = 1;
				gridLayout.horizontalSpacing = 0;
				logger.debug("Creation Shell");
				_shell = new Shell();

				_shell.setText(Desktop.APPLICATION_NAME);
				_shell.setBackground(DesktopStyle.getBackgroundColor());
				_shell.setLayout(gridLayout);

				Rectangle window = Geometry.parse(_display.getClientArea(),
						System.getProperty("org.avm.hmi.swt.geometry"));
				logger.debug("Resize fenetre (bis) :  " + window + "geometry="
						+ System.getProperty("org.avm.hmi.swt.geometry"));
				logger.debug("Localisation fenetre");
				_shell.setLocation(new Point(window.x, window.y));
				logger.debug("Resize fenetre");
				_shell.setSize(new Point(window.width, window.height));

				logger.debug("Creation logo");
				createLogo();

				logger.debug("Creation activite");
				createCompositeActivite();

				logger.debug("Creation boutons");
				createCompositeButtons();

				logger.debug("Creation tabfolder");
				createTabFolder();

				logger.debug("Creation polices");
				Font _fontNormal = getFont(10, SWT.NORMAL);
				Font _fontSmall = getFont(0, SWT.NORMAL);

				logger.debug("Creation info vehicule");
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

				startClock();

				logger.debug("Creation bouton nightmode");
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
				logger.debug("Ouvertude de la fenetre");
				_shell.open();

				window = Geometry.parse(_display.getClientArea(),
						System.getProperty("org.avm.hmi.swt.geometry"));
				logger.debug("Resize fenetre (bis) :  " + window + "geometry="
						+ System.getProperty("org.avm.hmi.swt.geometry"));
				_shell.setLocation(new Point(window.x, window.y));
				_shell.setSize(new Point(window.width, window.height));
				logger.debug("Mise a jour info vehicule");
				updateVehiculeDetails();

				logger.debug("Verification nightmode");
				checkNightMode();

				_shell.addListener(SWT.Close, new Listener() {
					public void handleEvent(Event event) {
						System.out.println("Close Windows asked : exit app");
						System.exit(0);
					}
				});
				logger.debug("Fin (run)");
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
		logger.info("Begin date for nigth mode=" + _beginDay);

		if (_endDay == null) {
			_endDay = getDate(19, 30, 56).getTime();
		} else {
			cal = Calendar.getInstance();
			cal.setTime(_endDay);// getDate(6, 15 , 30);
			cal = getDate(cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			_endDay = cal.getTime();
		}
		logger.info("End date for nigth mode=" + _endDay);
	}

	private Date getNextDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.before(Calendar.getInstance())) {
			cal.add(Calendar.HOUR_OF_DAY, 24);
		}

		return cal.getTime();
	}

	public void updateNightModeTimer() {
		if (_timerTaskCheckNightMode != null) {
			_scheduler.cancel(_timerTaskCheckNightMode);
		}

		Date now = new Date();
		Date scheduleDate = _beginDay;
		if (now.after(_beginDay) || now.equals(_beginDay)) {
			_beginDay = getNextDate(_beginDay);
		} else {
			scheduleDate = _beginDay;
		}

		if (now.after(_endDay)) {
			_endDay = getNextDate(_endDay);
			scheduleDate = _beginDay;
		} else {
			scheduleDate = _endDay;
		}

		logger.info("Next schedule for night mode=" + scheduleDate
				+ ((_beginDay == scheduleDate) ? " (begin)" : " (end)"));

		_timerTaskCheckNightMode = _scheduler.schedule(new Runnable() {
			public void run() {
				checkNightMode();
			}
		}, scheduleDate);

	}

	public void checkNightMode() {
		Date now = new Date();

		if (_beginDay.after(_endDay)) {
			setNightMode(now.after(_endDay));

		} else {
			setNightMode(now.before(_beginDay) || now.after(_endDay));
		}

		updateNightModeTimer();
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

	private void updateTime() {
		final boolean b = org.avm.device.plateform.System.isOnTime();

		if (_shell.isDisposed())
			return;
		if (b) {
			_labelHeure.setText(HF.format(new Date()));
		} else {
			_labelHeure.setText("--:--");
		}
		_labelHeure.redraw();
	}

	public void startClock() {
		Runnable run = new Runnable() {
			public void run() {
				try {
					if (_shell.isDisposed()){
						System.err.println("Shell is disposed");
						return;
					}
					
					if (_shell.getDisplay().isDisposed()){
						System.err.println("Display is disposed");
						return;
					}
					updateTime();
					if (_shell.isDisposed() == false) {
						_shell.getDisplay().timerExec(5 * 1000, this);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		_shell.getDisplay().timerExec(15 * 1000, run);

	}

	public void setMessageBox(final String title, final String message,
			final int type, SelectionListener listener) {
		if (_display == null)
			return;
		MessageBox.setMessage(title, message, type, SWT.CENTER, listener);
	}

	private void updateVehiculeDetails() {
		_labelVehicule.setText(System.getProperty("org.avm.terminal.name",
				"???"));
		StringBuffer tooltip = new StringBuffer();
		tooltip.append("ID ");
		tooltip.append(System.getProperty("org.avm.terminal.id", "???"));
		tooltip.append("\n");
		tooltip.append("Exploitation ");
		tooltip.append(System.getProperty("org.avm.terminal.owner", "???"));
		tooltip.append("\n");
		tooltip.append("Version ");
		tooltip.append(System.getProperty("org.avm.version", "???"));

		_labelVehicule.setToolTipText(tooltip.toString());
	}

	public void setInformation(final String info) {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {
			public void run() {
				if (info == null || info.trim().equals("")) {
					if (!org.avm.device.plateform.System.isOnTime()) {
						launchUpdateTime();
					}
					_labelInformation2.setText(DF.format(new Date()));
				} else {
					stopUpdateTime();
					_labelInformation2.setText(info);
				}
				_compositeActivite.layout();
			}
		});
	}

	private boolean doUpdateTime = false;
	private boolean updateTimeRunning = false;

	private synchronized void stopUpdateTime() {
		doUpdateTime = false;
	}

	private synchronized void launchUpdateTime() {
		if (!updateTimeRunning) {
			doUpdateTime = true;
			updateTimeRunning = true;
			doUpdateTime();
		}
	}

	private void doUpdateTime() {
		_display.timerExec(1000, new Runnable() {
			public void run() {
				if (doUpdateTime) {
					if (org.avm.device.plateform.System.isOnTime()) {
						setInformation(null);
						updateTimeRunning = false;
						doUpdateTime = false;
					} else {
						doUpdateTime();
					}
				}
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

	public static Font getFont(String fontname, int deltasize, int style) {
		return Application.getFont(fontname, deltasize, style);
	}

	public void setTabItemImage(String name, Image image) {
		TabItem item = (TabItem) _hashItem.get(name);
		if (item != null) {
			item.setImage(image);
		}
	}

	public void setFavorite(String name) {
		_favorite = name;
	}

}
