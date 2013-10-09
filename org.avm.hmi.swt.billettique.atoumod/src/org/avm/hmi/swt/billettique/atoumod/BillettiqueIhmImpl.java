package org.avm.hmi.swt.billettique.atoumod;

import org.avm.business.billettique.atoumod.Billettique;
import org.avm.elementary.common.ProducerManager;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.component.ComponentContext;

public class BillettiqueIhmImpl extends Composite implements BillettiqueIhm {

	private ProducerManager _producer;

	private Billettique _billettique;

	private ComponentContext _context;

	private Desktop _desktop;

	private String _tabName;

	private BillettiqueIhmImpl _instance;

	private StateButton _enableButton;

	private Label _stateLabel;

	public BillettiqueIhmImpl(Composite parent, int style, String tabName) {
		super(parent, style);
		_tabName = tabName;
		setBackground(DesktopStyle.getBackgroundColor());

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		setLayout(layout);

		//_font = DesktopImpl.getFont(5, SWT.NORMAL); //$NON-NLS-1$

		GridData data;
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		setLayoutData(data);

		_enableButton = new StateButton(this, SWT.FILL);
		data = new GridData();
		data.heightHint = 58;
		data.widthHint = 200;
		_enableButton.setLayoutData(data);
		_enableButton.setText(Messages
				.getString("BillettiqueIhmImpl.activation")); //$NON-NLS-1$
		_enableButton.setActiveColor(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_GREEN));
//		_enableButton.setForeground(Display.getCurrent().getSystemColor(
//				SWT.COLOR_WHITE));
		_enableButton.setState(true);
		_enableButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			public void widgetSelected(SelectionEvent arg0) {
				if (_billettique != null) {
					_billettique.setEnable(_enableButton.getSelection());
				} else {
					System.err
							.println("Billettique service is null !!!!!!!!!!!!!!!!!!!!!!!!!!!"); //$NON-NLS-1$
				}
			}

		});
		
		
		_stateLabel = new Label(this, SWT.FILL|SWT.CENTER);
//		_stateLabel.setForeground(Display.getCurrent().getSystemColor(
//				SWT.COLOR_WHITE));
		data = new GridData();
		data.heightHint = 58;
		data.widthHint = 200;
		_stateLabel.setLayoutData(data);
		setConnected(true);

	}

	public void setConnected(boolean b) {
		String text;
		Color color;
		if (b) {
			color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
			text = "Billettique connectée";
		} else {
			color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			text = "Billettique déconnectée";
		}
		_stateLabel.setText("\n"+text);
		_stateLabel.setBackground(color);
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
	}

	public void setBillettique(Billettique billettique) {
		_billettique = billettique;
	}
}
