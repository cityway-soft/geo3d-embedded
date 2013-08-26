package org.avm.hmi.swt.management;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.hmi.swt.desktop.AzertyCompleteKeyboard;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PanelIdent extends AbstractPanel implements ConsoleFacadeInjector,
		SelectionListener {

	private static final String ORG_AVM_VEHICULE_ID = "org.avm.vehicule.id"; //$NON-NLS-1$

	private static final String ORG_AVM_EXPLOITATION_ID = "org.avm.exploitation.id"; //$NON-NLS-1$

	private static final String ORG_AVM_EXPLOITATION_NAME = "org.avm.exploitation.name"; //$NON-NLS-1$

	private static final String ORG_AVM_REGION = "org.avm.region"; //$NON-NLS-1$
	private static final String ORG_AVM_BRANCH = "org.avm.branch"; //$NON-NLS-1$
	private static final String ORG_AVM_COUNTRY = "org.avm.country"; //$NON-NLS-1$

	private static final String AC97_REVERSE_RIGHT_AND_LEFT = "ac97.reverse-right-and-left"; //$NON-NLS-1$

	private static final String ORG_AVM_MODE = "org.avm.mode"; //$NON-NLS-1$

	private Text _textVehiculeId;

	private Text _textExploitantId;

	private Text _textExploitantName;

	private Combo _comboAvmMode;

	private StateButton _buttonAC97reverse;

	private StateButton _buttonCheckValidity;

	private StateButton _buttonCheckAuthentification;

	private boolean _changed;

	private boolean _restartMedia;

	private Object _vehiculeId;

	private Object _exploitantId;

	private Object _exploitantName;

	private String _avmMode;

	private boolean _checkValidity;

	private boolean _reverseRightLeft;

	private boolean _authentification;

	private Combo _comboCountry;

	private Combo _comboBranch;

	private Combo _comboRegion;

	private String _avmRegion;

	private String _avmCountry;

	private String _avmBranch;

	private static final int BUTTON_HEIGHT = 50;

	public PanelIdent(Composite parent, int style) {
		super(parent, style);
	}

	private Shell getCurrentShell(){
        return PanelIdent.this.getShell();
    }
	
	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 10;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.horizontalSpacing = 10;
		this.setLayout(gridLayout);

		_avmCountry = System.getProperty(ORG_AVM_COUNTRY, "ZZ");
		_avmBranch = System.getProperty(ORG_AVM_BRANCH, "ZZ");
		_avmRegion = System.getProperty(ORG_AVM_REGION, "ZZ");

		_vehiculeId = System.getProperty(ORG_AVM_VEHICULE_ID, "1");
		_exploitantId = System.getProperty(ORG_AVM_EXPLOITATION_ID, "1");
		_exploitantName = System.getProperty(ORG_AVM_EXPLOITATION_NAME, "???"); //$NON-NLS-1$
		_avmMode = System.getProperty(ORG_AVM_MODE, "sae");
		_reverseRightLeft = !System.getProperty(AC97_REVERSE_RIGHT_AND_LEFT,
				"false").equals("false");
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
		text.setText("" + System.getProperty(ORG_AVM_VEHICULE_ID)); //$NON-NLS-1$
		button = new Button(this, SWT.NONE);
		button.setText(Messages.getString("ItemIdent.modify")); //$NON-NLS-1$
		button.addSelectionListener(new VehiculeIdSelectionListener());
		button.setData(ORG_AVM_VEHICULE_ID); //$NON-NLS-1$
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
		text.setText("" + System.getProperty(ORG_AVM_EXPLOITATION_ID)); //$NON-NLS-1$
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gridData);
		button = new Button(this, SWT.NONE);
		button.setText(Messages.getString("ItemIdent.modify")); //$NON-NLS-1$
		button.addSelectionListener(new ExploitationIdSelectionListener());
		button.setData(ORG_AVM_EXPLOITATION_ID); //$NON-NLS-1$
		button.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		button.setLayoutData(gridData);
		_textExploitantId = text;

		label = new Label(this, SWT.NONE);
		label.setText(Messages.getString("ItemIdent.exploitant_nom")); //$NON-NLS-1$
		label.setBackground(DesktopStyle.getBackgroundColor());
		text = new Text(this, SWT.NONE);
		text.setEditable(false);
		text.setText("" + System.getProperty(ORG_AVM_EXPLOITATION_NAME)); //$NON-NLS-1$
		button = new Button(this, SWT.NONE);
		button.setText(Messages.getString("ItemIdent.modify")); //$NON-NLS-1$
		button.addSelectionListener(new ExploitationNameSelectionListener());
		button.setData(ORG_AVM_EXPLOITATION_NAME); //$NON-NLS-1$
		button.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		button.setLayoutData(gridData);
		_textExploitantName = text;

		label = new Label(this, SWT.NONE);
		label.setBackground(DesktopStyle.getBackgroundColor());
		label.setText(Messages.getString("ItemIdent.avm-mode")); //$NON-NLS-1$
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.heightHint = BUTTON_HEIGHT;
		label.setData(gridData);
		label = new Label(this, SWT.NONE);
		_comboAvmMode = new Combo(this, SWT.BORDER);
		_comboAvmMode.add("sae"); //$NON-NLS-1$
		_comboAvmMode.add("securite"); //$NON-NLS-1$
		_comboAvmMode.addSelectionListener(this);
		if (System.getProperty(ORG_AVM_MODE) != null) {

			_comboAvmMode.setText(System.getProperty(ORG_AVM_MODE));
		} else {
			_comboAvmMode.select(0);
		}

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

		label = new Label(panelActivite, SWT.NONE);
		label.setBackground(DesktopStyle.getBackgroundColor());
		label.setText(Messages.getString("ItemIdent.branch")); //$NON-NLS-1$
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		label.setData(gridData);
		_comboBranch = new Combo(panelActivite, SWT.BORDER);
		_comboBranch.add("VT"); //$NON-NLS-1$
		_comboBranch.add("VP"); //$NON-NLS-1$
		_comboBranch.add("VE"); //$NON-NLS-1$
		_comboBranch.add("VD"); //$NON-NLS-1$
		_comboBranch.setText(_avmBranch);
		checkActitityCombo(_comboBranch);
		_comboBranch.addSelectionListener(this);

		label = new Label(panelActivite, SWT.NONE);
		label.setBackground(DesktopStyle.getBackgroundColor());
		label.setText(Messages.getString("ItemIdent.country")); //$NON-NLS-1$
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		label.setData(gridData);
		_comboCountry = new Combo(panelActivite, SWT.BORDER);
		_comboCountry.add("FR"); //$NON-NLS-1$
		_comboCountry.add("US"); //$NON-NLS-1$
		_comboCountry.add("EN"); //$NON-NLS-1$
		_comboCountry.setText(_avmCountry);
		checkActitityCombo(_comboCountry);
		_comboCountry.addSelectionListener(this);

		label = new Label(panelActivite, SWT.NONE);
		label.setBackground(DesktopStyle.getBackgroundColor());
		label.setText(Messages.getString("ItemIdent.region")); //$NON-NLS-1$
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		label.setData(gridData);
		_comboRegion = new Combo(panelActivite, SWT.BORDER);
		_comboRegion.add("CEN"); //$NON-NLS-1$
		_comboRegion.add("IDF"); //$NON-NLS-1$
		_comboRegion.add("MED"); //$NON-NLS-1$
		_comboRegion.add("NO"); //$NON-NLS-1$
		_comboRegion.add("OST"); //$NON-NLS-1$
		_comboRegion.add("RAA"); //$NON-NLS-1$
		_comboRegion.add("SO"); //$NON-NLS-1$
		_comboRegion.setText(_avmRegion);
		checkActitityCombo(_comboRegion);
		_comboRegion.addSelectionListener(this);

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
		_buttonAC97reverse = new StateButton(panelButtons, SWT.BORDER);
		_buttonAC97reverse.setActiveColor(this.getDisplay().getSystemColor(
				SWT.COLOR_GREEN));
		_buttonAC97reverse.setText(Messages
				.getString("ItemIdent.reverse-right-and-left"));
		_buttonAC97reverse.setSelection(!System.getProperty(
				AC97_REVERSE_RIGHT_AND_LEFT, "false").equals("false")); //$NON-NLS-1$ //$NON-NLS-2$

		_buttonAC97reverse.addSelectionListener(this);
		_buttonAC97reverse.setLayoutData(gridData); //$NON-NLS-1$

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

	private boolean checkActitityCombo(Combo combo) {
		boolean result = false;
		String value = combo.getText();
		if (value != null && value.trim().length() != 0
				&& !value.equalsIgnoreCase("ZZ")) {
			combo.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			result = true;
		} else {
			combo.setText("ZZ");
			combo.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		return result;
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
			KeyboardDialog dialog = new KeyboardDialog(getCurrentShell(),
					SWT.NONE); //$NON-NLS-1$
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
					System.out.println("Vehicule ID changed : " + data);
					if (data != null) {
						if (!data.equals(_textVehiculeId.getText())) {
							_changed = true;
							_restartMedia = true;
							_textVehiculeId.setText(data);
							_vehiculeId = data;
							//layout();
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
			KeyboardDialog dialog = new KeyboardDialog(getCurrentShell(),
					SWT.NONE); //$NON-NLS-1$
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
					System.out.println("Exploitant ID changed : " + data);
					if (data != null) {
						if (!data.equals(_textExploitantId.getText())) {
							_changed = true;
							_restartMedia = true;
							_textExploitantId.setText(data);
							_exploitantId = data;
							layout();
						}
					}

				}
			});

		}
	}

	public class ExploitationNameSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			KeyboardDialog dialog = new KeyboardDialog(getCurrentShell(),
					SWT.NONE); //$NON-NLS-1$
			AzertyCompleteKeyboard keyboard = new AzertyCompleteKeyboard(
					dialog.getShell(), SWT.NONE);
			keyboard.setDisposeParent(true);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
	
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;
	
			keyboard.setLayoutData(gridData);
			dialog.setTitle(Messages
					.getString("ItemIdent.titre_exploitation_nom")); //$NON-NLS-1$
			dialog.open();

			dialog.getShell().layout();
			keyboard.setListener(new KeyboardListener() {
				public void validation(String data) {
					if (data != null) {
						System.out.println("validate:" + data);
						if (!data.equals(_textExploitantName.getText())) {
							_changed = true;
							_textExploitantName.setText(data);
							_exploitantName = data;
							layout();
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
				System.out.println("Sauvegarde des modifications......");
				Properties properties = new Properties();

				// vehicule.properties
				properties.put(ORG_AVM_REGION, _avmRegion);
				properties.put(ORG_AVM_COUNTRY, _avmCountry);
				properties.put(ORG_AVM_BRANCH, _avmBranch);
				properties.put(ORG_AVM_VEHICULE_ID, _vehiculeId);
				properties.put(ORG_AVM_EXPLOITATION_ID, _exploitantId);
				properties.put(ORG_AVM_EXPLOITATION_NAME, _exploitantName);
				properties.put(ORG_AVM_MODE, _avmMode);
				properties.put(AC97_REVERSE_RIGHT_AND_LEFT,
						_reverseRightLeft ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

				Enumeration en = properties.keys();
				while (en.hasMoreElements()) {
					String key = (String) en.nextElement();
					String value = properties.getProperty(key);
					System.setProperty(key, value);
				}

				if (_restartMedia) {
					runConsoleCommand("/mana stop media.avm");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					runConsoleCommand("/mana start media.avm");
				}

				runConsoleCommand("/swt.desktop refresh");

				String pathfile = System.getProperty("org.avm.home") + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + "avm.properties"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

				try {
					System.out.println("Ecriture de la configuration dans :"
							+ pathfile);
					FileOutputStream out = new FileOutputStream(pathfile);
					System.out.println("props:" + properties);
					properties.store(out, "-- Avm properties --"); //$NON-NLS-1$
					out.flush();
					out.close();
					System.out.println("Ecriture de la configuration termine.");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

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
		} else if (event.getSource() == _buttonAC97reverse) {
			_reverseRightLeft = _buttonAC97reverse.getSelection();
			_changed = true;
			System.out.println("reverse: " + _reverseRightLeft);
		} else if (event.getSource() == _comboAvmMode) {
			_avmMode = _comboAvmMode.getText();
			_changed = true;
			System.out.println("avmMode: " + _avmMode);
		} else if (event.getSource() == _comboCountry) {
			if (checkActitityCombo(_comboCountry)) {
				_avmCountry = _comboCountry.getText();
				_changed = true;
			}
			System.out.println("_avmCountry: " + _avmCountry);
		} else if (event.getSource() == _comboRegion) {
			if (checkActitityCombo(_comboRegion)) {
				_avmRegion = _comboRegion.getText();
				_changed = true;
			}
			System.out.println("_avmRegion: " + _avmRegion);
		} else if (event.getSource() == _comboBranch) {
			if (checkActitityCombo(_comboBranch)) {
				_avmBranch = _comboBranch.getText();
				_changed = true;
			}
			System.out.println("_avmBranch: " + _avmBranch);
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
