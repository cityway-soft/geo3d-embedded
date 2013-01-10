package org.avm.hmi.swt.management;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class PanelDeviceIO extends AbstractPanel {

	private Hashtable _hash = new Hashtable();

	private static final int INDENT = Desktop.DEFAULT_FONTSIZE / 5;

	private Button _refresh = null;

	public PanelDeviceIO(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		this.setLayout(gridLayout);

		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = INDENT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.horizontalSpan = 3;
		_refresh = new Button(this, SWT.NONE);
		_refresh.setLayoutData(gridData);
		_refresh.setText(Messages.getString("ItemDeviceIO.raffraichir")); //$NON-NLS-1$
		_refresh.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_YELLOW));
		_refresh.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				refreshVariables();
			}
		});

	}

	private void refreshVariables() {
		Enumeration e = _hash.keys();
		while (e.hasMoreElements()) {
			String var = (String) e.nextElement();
			String result = runConsoleCommand("/variable read -n " + var); //$NON-NLS-1$
			double value = 0;
			try {
				value = Double.parseDouble(result.trim());
			} catch (NumberFormatException ex) {
//				if (result != null && !result.trim().equals("")) { //$NON-NLS-1$
//					MessageBox.setMessage(Messages.getString("ItemDeviceIO.Erreur"), result, //$NON-NLS-1$
//							MessageBox.MESSAGE_WARNING, SWT.CENTER);
//				}
			}
			StateButton button = (StateButton) _hash.get(var);
			if (button != null) {
				button.setSelection((value > 0));
			}
		}
	}

	public void start() {
		addVariables();
	}

	public void stop() {

	}

	private void addVariables() {
		String sVariables = runConsoleCommand("/variables list"); //$NON-NLS-1$
		if (sVariables != null) {
			StringTokenizer t = new StringTokenizer(sVariables, "="); //$NON-NLS-1$
			while (t.hasMoreElements()) {
				String item = (String) t.nextElement();
				if (item.indexOf("org.avm.elementary.variable.device.category") != -1) { //$NON-NLS-1$
					item = (String) t.nextElement();
					if (item.indexOf("org.avm.device.io.DigitalIODevice") != -1) { //$NON-NLS-1$
						item = (String) t.nextElement();
						int idx = item.indexOf("}"); //$NON-NLS-1$
						if (idx != -1) {
							item = item.substring(0, idx).trim();
						}
						addVariable(item);
					}
				}
			}
		}
	}

	public void addVariable(String titre) {
		if (_hash.get(titre) == null) {

			GridData gridData;
			gridData = new GridData();
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalIndent = INDENT;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.heightHint = Management.BUTTON_HEIGHT;

			StateButton button = new StateButton(this, SWT.BORDER);
			button.setActiveColor(this.getDisplay().getSystemColor(
					SWT.COLOR_GREEN));
			_hash.put(titre, button);
			button.setLayoutData(gridData);
			button.setText(titre);
			button.setBackground(DesktopStyle.getBackgroundColor());
			button.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {

				}

				public void widgetSelected(SelectionEvent e) {
					String cmd = ((StateButton) e.getSource()).getText();
					String value = ((StateButton) e.getSource()).getSelection() ? "1" //$NON-NLS-1$
							: "0"; //$NON-NLS-1$
					runConsoleCommand("/variable write -n " + cmd + " " + value); //$NON-NLS-1$ //$NON-NLS-2$
				}
			});
			this.layout();
		}
	}
	
	public static class ItemDeviceIOFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelDeviceIO(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelDeviceIO.class.getName(),
				new ItemDeviceIOFactory());
	}

}
