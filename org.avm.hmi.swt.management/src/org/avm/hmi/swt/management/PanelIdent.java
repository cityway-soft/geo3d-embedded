package org.avm.hmi.swt.management;

import org.apache.log4j.Logger;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardDialog;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PanelIdent extends AbstractPanel implements ConsoleFacadeInjector,
		SelectionListener {

	private static final String ORG_AVM_TERMINAL_NAME = "org.avm.terminal.name"; //$NON-NLS-1$

	private static final String ORG_AVM_TERMINAL_OWNER = "org.avm.terminal.owner"; //$NON-NLS-1$

	private Text _textVehiculeId;

	private Text _textExploitantId;

	private Logger logger = Logger.getInstance(PanelIdent.class);

	private StateButton _buttonCheckValidity;

	private StateButton _buttonCheckAuthentification;

	private boolean _changed;

	private Object _terminalName;

	private Object _terminalOwner;

	private boolean _checkValidity;

	private boolean _authentification;

	private static final int BUTTON_HEIGHT = 50;

	public PanelIdent(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 10;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.horizontalSpacing = 10;
		this.setLayout(gridLayout);

		_terminalName = System.getProperty(ORG_AVM_TERMINAL_NAME, "1");
		_terminalOwner = System.getProperty(ORG_AVM_TERMINAL_OWNER, "1");
		create();
	}

	/**
	 * This method initializes itemBundles
	 * 
	 */
	private void create() {
		GridData gridData;

		Label label;
		Text text;
		Button button;

		label = new Label(this, SWT.NONE);
		label.setText(Messages.getString("ItemIdent.vehicule_id")); //$NON-NLS-1$
		label.setBackground(DesktopStyle.getBackgroundColor());
		text = new Text(this, SWT.NONE);
		text.setEditable(false);
		text.setText("" + System.getProperty(ORG_AVM_TERMINAL_NAME)); //$NON-NLS-1$
		button = new Button(this, SWT.NONE);
		button.setText(Messages.getString("ItemIdent.modify")); //$NON-NLS-1$
		button.addSelectionListener(new VehiculeIdSelectionListener());
		button.setData(ORG_AVM_TERMINAL_NAME); //$NON-NLS-1$
		button.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		button.setLayoutData(gridData);
		_textVehiculeId = text;

		label = new Label(this, SWT.NONE);
		label.setText(Messages.getString("ItemIdent.exploitant_id")); //$NON-NLS-1$
		label.setBackground(DesktopStyle.getBackgroundColor());

		text = new Text(this, SWT.NONE);
		text.setEditable(false);
		text.setText("" + System.getProperty(ORG_AVM_TERMINAL_OWNER)); //$NON-NLS-1$
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gridData);
		button = new Button(this, SWT.NONE);
		button.setText(Messages.getString("ItemIdent.modify")); //$NON-NLS-1$
		button.addSelectionListener(new ExploitationIdSelectionListener());
		button.setData(ORG_AVM_TERMINAL_OWNER); //$NON-NLS-1$
		button.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		button.setLayoutData(gridData);
		_textExploitantId = text;
		
		
		label = new Label(this, SWT.NONE);
		label.setText(Messages.getString("ItemIdent.terminal_id")); //$NON-NLS-1$
		label.setBackground(DesktopStyle.getBackgroundColor());
		text = new Text(this, SWT.NONE);
		text.setEditable(false);
		text.setText(System.getProperty("org.avm.terminal.id","????")); //$NON-NLS-1$

		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		

		// ----------------------
		Composite panelActivite = new Composite(this, SWT.NONE);
		panelActivite.setBackground(DesktopStyle.getBackgroundColor());

		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		panelActivite.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		panelActivite.setLayoutData(gridData);

		// -----------------
		Composite panelButtons = new Composite(this, SWT.NONE);
		panelButtons.setBackground(DesktopStyle.getBackgroundColor());

		layout = new GridLayout();
		layout.numColumns = 3;
		panelButtons.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		panelButtons.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		_buttonCheckAuthentification = new StateButton(panelButtons, SWT.BORDER);
		_buttonCheckAuthentification.setText(Messages
				.getString("ItemIdent.check-authentification")); //$NON-NLS-1$
		_buttonCheckAuthentification.setActiveColor(this.getDisplay()
				.getSystemColor(SWT.COLOR_GREEN));
		_buttonCheckAuthentification.addSelectionListener(this);
		_buttonCheckAuthentification.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		_buttonCheckValidity = new StateButton(panelButtons, SWT.BORDER);
		_buttonCheckValidity.setText(Messages
				.getString("ItemIdent.check-validite")); //$NON-NLS-1$
		_buttonCheckValidity.setActiveColor(this.getDisplay().getSystemColor(
				SWT.COLOR_GREEN));
		_buttonCheckValidity.addSelectionListener(this);
		_buttonCheckValidity.setLayoutData(gridData);
		panelButtons.layout();
	}

	private Shell getCurrentShell() {
		logger.debug("PanelIdent.this.getShell() = "
				+ PanelIdent.this.getShell());
		logger.debug("Parent getActiveShell() = " + _display.getActiveShell());
		logger.debug("Parent getShells()[0] = " + _display.getShells()[0]);

		return PanelIdent.this.getShell();
	}

	private void refresh() {
		String check = runConsoleCommand("/avmcore checkvalidite") + " ";
		_checkValidity = check.toLowerCase().trim().endsWith("true");
		_buttonCheckValidity.setSelection(_checkValidity); //$NON-NLS-1$ //$NON-NLS-2$
		_buttonCheckValidity.setEnabled(isBundleAvailable("business.core"));

		check = runConsoleCommand("/usersession authentication") + " ";
		check = check.trim();
		_authentification = check.toLowerCase().trim().endsWith("true");
		_buttonCheckAuthentification.setSelection(_authentification); //$NON-NLS-1$ //$NON-NLS-2$
		_buttonCheckAuthentification
				.setEnabled(isBundleAvailable("elementary.useradmin"));

	}

	public class VehiculeIdSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			final KeyboardDialog dialog = new KeyboardDialog(getCurrentShell(),
					SWT.NONE);
			dialog.setTitle(Messages
					.getString("ItemIdent.titre_identification_vehicule")); //$NON-NLS-1$
			Keyboard keyboard = new Keyboard(dialog.getShell(), SWT.NONE);
			keyboard.setDisposeParent(true);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;

			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;

			keyboard.setLayoutData(gridData);
			dialog.open();
			dialog.getShell().layout();
			keyboard.setListener(new KeyboardListener() {

				public void validation(String data) {
					logger.debug("Vehicule ID changed : " + data);
					if (data != null) {
						if (!data.equals(_textVehiculeId.getText())) {
							_changed = true;
							_textVehiculeId.setText(data);
							PanelIdent.this.layout();
							_terminalName = data;
							dialog.dispose();
						}
					}
				}
			});
		}
	}

	public class ExploitationIdSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			final KeyboardDialog dialog = new KeyboardDialog(getCurrentShell(),
					SWT.NONE);
			dialog.setTitle(Messages
					.getString("ItemIdent.titre_identification_exploitation")); //$NON-NLS-1$
			Keyboard keyboard = new Keyboard(dialog.getShell(), SWT.NONE);
			keyboard.setDisposeParent(true);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;

			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;

			keyboard.setLayoutData(gridData);
			dialog.open();

			dialog.getShell().layout();

			keyboard.setListener(new KeyboardListener() {
				public void validation(String data) {
					logger.debug("Exploitant ID changed : " + data);
					if (data != null) {
						if (!data.equals(_textExploitantId.getText())) {
							_changed = true;
							// _restartMedia = true;
							_textExploitantId.setText(data);
							PanelIdent.this.layout();
							_terminalOwner = data;
							dialog.dispose();
						}
					}

				}
			});

		}
	}

	public void start() {
		_changed = false;
	}

	public void stop() {
		super.stop();
		if (_changed) {
			save();
		}
	}

	private void save() {
		getScheduler().execute(new Runnable() {

			public void run() {
				logger.debug("Sauvegarde des modifications......");

				String updateCommand = "/management id -n " + _terminalName
						+ " -o " + _terminalOwner;
				logger.info("send to console:" + updateCommand);
				runConsoleCommand(updateCommand);

				runConsoleCommand("/swt.desktop refresh");

				String check = runConsoleCommand("/avmcore checkvalidite") + "";
				boolean ischecked = check.toLowerCase().trim().endsWith("true");
				boolean update = (ischecked != _checkValidity);
				if (update) {
					runConsoleCommand("/avmcore checkvalidite "
							+ _checkValidity);
				}

				check = runConsoleCommand("/usersession authentication") + "";
				check = check.trim();
				System.out.println("/usersession authentication ====> '"
						+ check + "'");
				ischecked = check.toLowerCase().trim().endsWith("true");
				update = (ischecked != _authentification);
				if (update) {
					runConsoleCommand("/usersession authentication "
							+ _authentification);
				}
			}// run
		});

	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		refresh();
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() == _buttonCheckValidity) {
			_checkValidity = _buttonCheckValidity.getSelection();
			_changed = true;
			System.out.println("checkValidity: " + _checkValidity);
		} else if (event.getSource() == _buttonCheckAuthentification) {
			_authentification = _buttonCheckAuthentification.getSelection();
			_changed = true;
			System.out.println("_authentification: " + _authentification);
		}

	}

	public static class ItemIdentFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelIdent(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelIdent.class.getName(),
				new ItemIdentFactory());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
