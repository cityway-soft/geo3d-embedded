package org.avm.hmi.swt.avm;

import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardDialog;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class ReplacementSelection extends Composite implements
		KeyboardListener, SelectionListener {
	private static final String MATRICULE = "MATRICULE";
	private static final String PARC = "PARC";

	private ChoiceListener _listener;
	private Font _font;

	private int _matriculeRemplacement = 0;
	private Text _textMatriculeRemplacement;

	private int _vehiculeRemplacement = 0;
	private Text _textVehiculeRemplacement;
	private Button _buttonMatriculeRemplacement;
	private Button _buttonVehiculeRemplacement;

	public ReplacementSelection(Composite parent, int ctrl) {
		super(parent, ctrl);
		create();
	}

	public int getMatriculeRemplacement() {
		return _matriculeRemplacement;
	}

	public int getVehiculeRemplacement() {
		return _vehiculeRemplacement;
	}

	private void create() {

		Button button;
		GridLayout layout;
		setBackground(DesktopStyle.getBackgroundColor());

		_font = DesktopImpl.getFont(8, SWT.NORMAL); //$NON-NLS-1$

		Group group = new Group(this, SWT.NONE);
		group.setText("Remplacements");
		group.setFont(_font);
		group.setBackground(DesktopStyle.getBackgroundColor());
		GridData data;
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayoutData(data);
		group.setLayout(layout);

		layout = new GridLayout();
		layout.numColumns = 2;
		setLayout(layout);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		setLayoutData(data);

		button = new Button(group, SWT.TOP);// SWT.CHECK);
		_buttonMatriculeRemplacement = button;
		button.setText(Messages
				.getString("RemplacementSelection.remplacement-matricule"));
		data = new GridData();
		data.heightHint = 58;
		data.widthHint = 200;
		button.setLayoutData(data);

		button.addSelectionListener(this);
		button.setData(MATRICULE);
		button.setBackground(DesktopStyle.getBackgroundColor());
		button.setFont(_font);

		_textMatriculeRemplacement = new Text(group, SWT.NONE | SWT.MULTI
				| SWT.WRAP | SWT.READ_ONLY);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;

		data.verticalAlignment = GridData.FILL;
		_textMatriculeRemplacement.setLayoutData(data);
		_textMatriculeRemplacement.addSelectionListener(this);
		_textMatriculeRemplacement.setData(MATRICULE);
		_textMatriculeRemplacement.setFont(_font);
		_textMatriculeRemplacement.setBackground(DesktopStyle
				.getBackgroundColor());

		button = new Button(group, SWT.TOP);
		_buttonVehiculeRemplacement = button;
		button.setText(Messages
				.getString("RemplacementSelection.remplacement-vehicule"));
		data = new GridData();
		data.heightHint = 58;
		data.widthHint = 200;
		button.setLayoutData(data);
		button.setData(PARC);
		button.setFont(_font);
		button.addSelectionListener(this);
		button.setBackground(DesktopStyle.getBackgroundColor());

		_textVehiculeRemplacement = new Text(group, SWT.NONE | SWT.MULTI
				| SWT.WRAP | SWT.READ_ONLY);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = GridData.FILL;
		_textVehiculeRemplacement.setLayoutData(data);
		_textVehiculeRemplacement.setFont(_font);
		_textVehiculeRemplacement.setBackground(DesktopStyle
				.getBackgroundColor());

		button = new Button(this, SWT.BOTTOM);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 58;
		data.horizontalSpan = 2;
		button.setLayoutData(data);
		button.setText(Messages.getString("RemplacementSelection.prise-poste")); //$NON-NLS-1$
		button.setFont(_font);
		button.setData(null);
		button.addSelectionListener(this);
		button.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_GREEN));

		updateMatriculeRemplacement();
		updateVehiculeRemplacement();
	}

	public void setSelectionListener(ChoiceListener listener) {
		_listener = listener;
	}

	public void validation(String data) {
		_listener.validation(this, data);
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	private void updateMatriculeRemplacement() {
		String buttonmsg;
		String textmsg;
		Button button = _buttonMatriculeRemplacement;
		Text text = _textMatriculeRemplacement;
		if (_matriculeRemplacement == 0) {

			textmsg = Messages
					.getString("RemplacementSelection.aucun-remplacement-en-cours"); //$NON-NLS-1$
			buttonmsg = Messages
					.getString("RemplacementSelection.remplacement-matricule");
			text.setForeground(this.getDisplay()
					.getSystemColor(SWT.COLOR_BLACK));
		} else {
			textmsg = Messages
					.getString("RemplacementSelection.matricule-remplace")
					+ " " + _matriculeRemplacement;
			text.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_RED));
			buttonmsg = Messages
					.getString("RemplacementSelection.annuler-remplacement");
		}
		button.setText(buttonmsg);
		text.setText(textmsg);
	}

	private void updateVehiculeRemplacement() {
		String buttonmsg;
		String textmsg;
		Button button = _buttonVehiculeRemplacement;
		Text text = _textVehiculeRemplacement;
		if (_vehiculeRemplacement == 0) {
			textmsg = Messages
					.getString("RemplacementSelection.aucun-remplacement-en-cours"); //$NON-NLS-1$
			buttonmsg = Messages
					.getString("RemplacementSelection.remplacement-vehicule");
			text.setForeground(this.getDisplay()
					.getSystemColor(SWT.COLOR_BLACK));
		} else {
			textmsg = Messages
					.getString("RemplacementSelection.vehicule-remplace")
					+ " "
					+ _vehiculeRemplacement;

			buttonmsg = Messages
					.getString("RemplacementSelection.annuler-remplacement");
			text.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		button.setText(buttonmsg);
		text.setText(textmsg);
	}

	public void widgetSelected(SelectionEvent arg0) {
		Object obj = arg0.getSource();
		Object data = null;
		if (obj instanceof Button) {
			data = ((Button) obj).getData();
		} else if (obj instanceof Text) {
			data = ((Button) obj).getData();
		}
		if (data == MATRICULE) {
			if (_matriculeRemplacement == 0) {
				KeyboardDialog dialog = new KeyboardDialog(ReplacementSelection.this.getShell(), SWT.NONE);
				dialog.setTitle(Messages
						.getString("RemplacementSelection.remplacement-matricule")); //$NON-NLS-1$
				Keyboard keyboard = new Keyboard(dialog.getShell(), SWT.NONE);
				keyboard.setDisposeParent(true);
				GridData gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;

				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessVerticalSpace = true;

				keyboard.setLayoutData(gridData);
				dialog.open();
				keyboard.setListener(new KeyboardListener() {
					public void validation(String data) {
						_matriculeRemplacement = Integer.parseInt(data);
						updateMatriculeRemplacement();
					}
				});
				dialog.layout();
			} else {
				_matriculeRemplacement = 0;
				updateMatriculeRemplacement();
			}
		} else if (data == PARC) {
			if (_vehiculeRemplacement == 0) {
				KeyboardDialog dialog = new KeyboardDialog(getDisplay()
						.getShells()[0], SWT.NONE);
				dialog.setTitle(Messages
						.getString("RemplacementSelection.remplacement-vehicule")); //$NON-NLS-1$
				Keyboard keyboard = new Keyboard(dialog.getShell(), SWT.NONE);
				keyboard.setDisposeParent(true);
				GridData gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;

				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessVerticalSpace = true;

				keyboard.setLayoutData(gridData);
				dialog.open();
				keyboard.setListener(new KeyboardListener() {
					public void validation(String data) {
						_vehiculeRemplacement = Integer.parseInt(data);
						updateVehiculeRemplacement();
					}
				});
				dialog.layout();

			} else {
				_vehiculeRemplacement = 0;
				updateVehiculeRemplacement();
			}
		} else {
			validation(null);
		}

	}
}