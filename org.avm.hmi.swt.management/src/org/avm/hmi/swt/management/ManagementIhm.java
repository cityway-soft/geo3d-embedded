package org.avm.hmi.swt.management;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Scheduler;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.knopflerfish.service.console.ConsoleService;
import org.osgi.framework.BundleContext;

public class ManagementIhm extends Composite implements Management,
		BundleContextInjector {

	private Display _display;

	private Font _font;

	private Config _config;

	private ConsoleFacade _console;
	
	private Scheduler _scheduler;

	private Vector _items = new Vector();

	private PanelBundles _itemBundles;

	private ManagementDialog _dialog;

	private Logger _log = Logger.getInstance(Management.class);

	public ManagementIhm(Composite parent, int style) {
		super(parent, style);
		_console = new ConsoleFacade();
		_display = parent.getDisplay();
		_scheduler = new Scheduler();
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		this.setLayout(gridLayout);
		setBackground(DesktopStyle.getBackgroundColor());
		
	}

	private void addItem(MenuButtonSelectionListener listener) {
		Button button = new Button(this, SWT.NONE);
		button.setBackground(DesktopStyle.getBackgroundColor());
		button.setText(listener.getName());

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.grabExcessVerticalSpace = true;
		button.setLayoutData(gridData);
		button.addSelectionListener(listener);
		button.setEnabled(listener.isEnabled());
	}

	/**
	 * This method initializes tabFolderManagement
	 * 
	 */
	private void createMenuButtons() {
//		addItem(new MenuButtonSelectionListener(
//				Messages.getString("ManagementIhm.test"), ItemTest.class.getName(), null)); //$NON-NLS-1$
		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.ident"), PanelIdent.class.getName(), null)); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.afficheur"), PanelAfficheur.class.getName(), "business.afficheur")); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.girouette"), PanelGirouette.class.getName(), "business.girouette")); //$NON-NLS-1$
		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.sound"), PanelSound.class.getName(), "sound")); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.commands"),
				PanelCommands.class.getName(), null));
		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.deviceio"), PanelDeviceIO.class.getName(), "variable")); //$NON-NLS-1$


		
		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.comptage"), PanelComptage.class.getName(), "comptage")); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.can"), PanelCAN.class.getName(), "can")); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.communication"), PanelNetwork.class.getName(), null)); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.GPS"), PanelGPS.class.getName(), "gps"));// "wifi")); //$NON-NLS-1$

		
		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.recette"), PanelRecette.class.getName(), null)); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.log"), PanelLog.class.getName(), null)); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.bundles"), PanelBundles.class.getName(), null)); //$NON-NLS-1$

		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.jvm"), PanelJvm.class.getName(), null)); //$NON-NLS-1$
		
		addItem(new MenuButtonSelectionListener(
				Messages.getString("ManagementIhm.sms"), PanelSMS.class.getName(), "sms")); //$NON-NLS-1$


	}

	public void setConsoleService(ConsoleService console) {
		_console.setConsoleService( console );
		createMenuButtons();
	}


	public void configure(Config config) {
		_config = config;
		initializeConfiguration();
	}

	private void initializeConfiguration() {
		Enumeration e = _items.elements();
		if (_config != null) {
			while (e.hasMoreElements()) {
				Object object = (Object) e.nextElement();
				if (object instanceof ConfigurableService) {
					((ConfigurableService) object).configure(_config);
				}
			}
		}
	}

	public void update(final long bundleId, final String bundleName,
			final String version, final int state) {
		_display.syncExec(new Runnable() {

			public void run() {
				if (_itemBundles != null) {
					_itemBundles.update(bundleId, bundleName, version, state);
				}
			}
		});
	}

	public void setBundleContext(BundleContext _context) {
		Enumeration e = _items.elements();
		if (_config != null) {
			while (e.hasMoreElements()) {
				Object object = (Object) e.nextElement();
				if (object instanceof BundleContextInjector) {
					((BundleContextInjector) object).setBundleContext(_context);
				}
			}
		}
	}

	public class MenuButtonSelectionListener implements SelectionListener {
		private String name = "- - -";
		private String bundle;
		private String clazz;

		public MenuButtonSelectionListener(String name, String clazz,
				String bundle) {
			setName(name);
			this.bundle = bundle;
			this.clazz = clazz;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public boolean isEnabled() {
			boolean activated = (bundle == null);
			String result="ok";
			if (bundle != null) {
				activated = false;
				result = _console.runCommand("/management status "+ bundle);
				if (result != null && result.indexOf("no bundle matchs") == -1) {
					activated = true;
				}
			}

			return activated;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		}

		public void widgetSelected(SelectionEvent arg0) {
			if (_dialog != null && _dialog.isDisposed() == false) {
				_dialog.close();
			}
			_dialog = new ManagementDialog(_display.getShells()[0],
					"Management");

			_dialog.setName(name);

			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;

			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;

			Composite item;
			try {
				item = PanelFactory
						.create(clazz, _dialog.getContent(), SWT.NONE);
				if (item instanceof LoggerInjector) {
					((LoggerInjector) item).setLogger(_log);
				}
				if (item instanceof ManageableService) {
					_dialog.setService((ManageableService) item);
				}
				if (item instanceof ConsoleFacadeInjector) {
					((ConsoleFacadeInjector) item).setConsoleFacade(_console);
				}
				if (item instanceof ConfigurableService) {
					((ConfigurableService) item).configure(_config);
				}
				if (item instanceof SchedulerInjector) {
					((SchedulerInjector) item).setScheduler(_scheduler);
				}
				
				item.setLayoutData(gridData);
				item.setBackground(DesktopStyle.getBackgroundColor());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// createItem(_dialog.getContent());

			_dialog.open();
			_dialog.layout();
		}

		// public abstract Composite createItem(Composite parent);

	}

	public void dispose() {
		super.dispose();
		if (_dialog != null) {
			_dialog.close();
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"

