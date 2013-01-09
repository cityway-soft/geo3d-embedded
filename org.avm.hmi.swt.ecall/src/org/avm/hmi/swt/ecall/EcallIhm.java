package org.avm.hmi.swt.ecall;

import org.avm.business.ecall.EcallService;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.osgi.util.measurement.State;

public class EcallIhm extends Composite implements Ecall, SelectionListener {
	private static String END_ALERT = "END_ALERT"; //$NON-NLS-1$

	private static String BEGIN_ALERT = "BEGIN_ALERT"; //$NON-NLS-1$

	private static String IMAGE_OK = "/resources/ok.jpg";
	private static String IMAGE_SOS = "/resources/sos.jpg";
	private static String IMAGE_BOUEE = "/resources/bouee.jpg";

	private Composite composite = null;

	private Composite compositeButton = null;

	private Button buttonAlerte = null;

	private Label label = null;

	private Image imgOK = new Image(Display.getCurrent(), getClass()
			.getResourceAsStream(IMAGE_OK)); //$NON-NLS-1$

	private Image imgSOS = new Image(Display.getCurrent(), getClass()
			.getResourceAsStream(IMAGE_SOS)); //$NON-NLS-1$

	private Image imgBouee = new Image(Display.getCurrent(), getClass()
			.getResourceAsStream(IMAGE_BOUEE)); //$NON-NLS-1$

	private Font _fontButton;

	private EcallService _ecallService;

	private State _state;

	private Button buttonFinAlerte;

	private boolean isBigSize = true;

	public EcallIhm(Composite parent, int ctrl) {
		super(parent, ctrl);
		initialize();
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;

		setLayoutData(gridData);

		setBackground(DesktopStyle.getBackgroundColor());
		etatInitial();
		parent.layout();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		_fontButton = DesktopImpl.getFont(0, SWT.NORMAL);
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.horizontalSpacing = 2;
		gridLayout2.marginWidth = 2;
		gridLayout2.marginHeight = 2;
		gridLayout2.verticalSpacing = 2;

		Rectangle rect = this.getDisplay().getClientArea();
		int size = Math.min(rect.width, rect.height);// new
		isBigSize = size > 320;

		this.setLayout(gridLayout2);
		this.setBackground(DesktopStyle.getBackgroundColor());
		createCompositeImage();
		createCompositeButton();
		addAlerteButton();
		addFinAlerteButton();
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createCompositeImage() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.horizontalSpacing = 3;
		gridLayout1.marginWidth = 3;
		gridLayout1.marginHeight = 3;
		gridLayout1.verticalSpacing = 3;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.heightHint = (Desktop.DEFAULT_FONTSIZE + 1) * 5;
		gridData3.verticalAlignment = GridData.FILL;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		composite = new Composite(this, SWT.NONE); // BORDER
		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout1);
		composite.setBackground(DesktopStyle.getBackgroundColor());
		label = new Label(composite, SWT.CENTER);// | SWT.BORDER);
		label.setBackground(DesktopStyle.getBackgroundColor());
		label.setText(""); //$NON-NLS-1$
		label.setLayoutData(gridData3);
	}

	public void etatInitial() {
		if (isDisposed() == false) {
			if (isBigSize) {
				label.setImage(imgOK);
			} else {
				label.setBackground(getDisplay()
						.getSystemColor(SWT.COLOR_GREEN));
			}
			activate(buttonAlerte);
			desactivate(buttonFinAlerte);
			layout();
		}

	}

	public void etatAttentePriseEnCharge() {
		if (isDisposed() == false) {
			if (isBigSize) {
				label.setImage(imgSOS);
			} else {
				label.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
			}

			activate(buttonFinAlerte);
			desactivate(buttonAlerte);
			layout();
		}
	}

	public void etatEcouteDiscrete() {
		if (isDisposed() == false) {
			if (isBigSize) {
				label.setImage(imgBouee);
			} else {
				label.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
			}

			activate(buttonFinAlerte);
			desactivate(buttonAlerte);
			layout();
		}
	}

	private void activate(Button button) {
		if (button == null)
			return;
		button.setEnabled(true);
		button.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
	}

	private void desactivate(Button button) {
		if (button == null)
			return;
		button.setEnabled(false);
		button.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createCompositeButton() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.FILL;
		compositeButton = new Composite(this, SWT.NONE);
		compositeButton.setLayoutData(gridData1);
		compositeButton.setLayout(gridLayout);

		compositeButton.setBackground(DesktopStyle.getBackgroundColor());
	}

	private void addAlerteButton() {
		if (isDisposed() == false && buttonAlerte == null) {
			GridData gridData2 = new GridData();
			gridData2.horizontalAlignment = GridData.FILL;
			gridData2.heightHint = (Desktop.DEFAULT_FONTSIZE + 3) * 3;
			gridData2.verticalAlignment = GridData.FILL;
			buttonAlerte = new Button(compositeButton, SWT.NONE);
			buttonAlerte.setLayoutData(gridData2);
			buttonAlerte.addSelectionListener(this);
			buttonAlerte.setText(Messages.getString("EcallIhm.Alerte")); //$NON-NLS-1$
			buttonAlerte.setData(new String(BEGIN_ALERT));
			buttonAlerte.setFont(_fontButton);
			buttonAlerte.setBackground(DesktopStyle.getBackgroundColor());
			layout();
		}
	}

	private void removeAlerteButton() {
		if (isDisposed() == false && buttonAlerte != null) {
			buttonAlerte.dispose();
			buttonAlerte = null;
			layout();
		}
	}

	private void addFinAlerteButton() {
		if (isDisposed() == false && buttonFinAlerte == null) {
			GridData gridData2 = new GridData();
			gridData2.horizontalAlignment = GridData.FILL;
			gridData2.grabExcessHorizontalSpace = true;
			gridData2.grabExcessVerticalSpace = true;
			gridData2.heightHint = (Desktop.DEFAULT_FONTSIZE + 3) * 3;
			gridData2.verticalAlignment = GridData.FILL;
			buttonFinAlerte = new Button(compositeButton, SWT.NONE);
			buttonFinAlerte.setLayoutData(gridData2);
			buttonFinAlerte.addSelectionListener(this);
			buttonFinAlerte.setText(Messages.getString("EcallIhm.Fin")); //$NON-NLS-1$
			buttonFinAlerte.setData(new String(END_ALERT));
			buttonFinAlerte.setFont(_fontButton);
			buttonFinAlerte.setBackground(DesktopStyle.getBackgroundColor());
			layout();
		}
	}

	private void removeFinAlerteButton() {
		if (isDisposed() == false && buttonFinAlerte != null) {
			buttonFinAlerte.dispose();
			buttonFinAlerte = null;
			layout();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void widgetSelected(SelectionEvent e) {
		Button b = (Button) e.getSource();
		String action = (String) b.getData();
		if (_ecallService != null) {
			if (action.equals(BEGIN_ALERT)) {
				if (_ecallService.startEcall()) {
					etatAttentePriseEnCharge();
				}
			} else {// END_ALERT
				if (_ecallService.endEcall()) {
					etatInitial();
				}
			}
		}
	}

	public void setEcallService(EcallService ecallService) {
		if (ecallService == null) {
			if (isDisposed() == false) {
				this.setEnabled(false);
			}
		} else {
			if (isDisposed() == false) {
				_ecallService = ecallService;
				State s = ecallService.getState();
				this.setEnabled(true);
				stateChange(s);
			}
		}
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (buttonAlerte != null) {
			buttonAlerte.setEnabled(b);
			buttonAlerte.setForeground(b ? getDisplay().getSystemColor(
					SWT.COLOR_BLACK) : getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		}
		if (buttonFinAlerte != null) {
			buttonFinAlerte.setEnabled(b);
			buttonFinAlerte.setForeground(b ? getDisplay().getSystemColor(
					SWT.COLOR_BLACK) : getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		}
		label.setEnabled(b);
		if (b) {
			label.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));

		} else {
			label.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			label.setImage(null);
		}
		getParent().layout();
	}

	public void stateChange(State state) {
		_state = state;
		if (state == null)
			return;
		switch (state.getValue()) {
		case 0: {
			etatInitial();
		}
			break;
		case 1: {
			etatAttentePriseEnCharge();
		}
			break;
		case 2: {
			etatEcouteDiscrete();
		}
			break;
		}
	}


} // @jve:decl-index=0:visual-constraint="10,10"
