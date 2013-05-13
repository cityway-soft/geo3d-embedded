package org.avm.hmi.swt.management;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class PanelGPS extends AbstractPanel {

	private Hashtable _hash = new Hashtable();

	private static final int INDENT = Desktop.DEFAULT_FONTSIZE / 5;

	private Button _refresh = null;

	private Text _text;

	private Font _fontText;

	public PanelGPS(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		this.setLayout(gridLayout);

		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = INDENT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		_refresh = new Button(this, SWT.NONE);
		_refresh.setLayoutData(gridData);
		_refresh.setText(Messages.getString("ItemGPS.raffraichir")); //$NON-NLS-1$
		_refresh.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_YELLOW));
		Font font = DesktopImpl.getFont(8, SWT.NORMAL);
		_refresh.setFont(font);

		_refresh.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		
		_text = new Text(this, SWT.MULTI | SWT.WRAP);
		_fontText = DesktopImpl.getFont(8, SWT.NORMAL); //$NON-NLS-1$
		_text.setFont(_fontText);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		_text.setLayoutData(gridData);

	}

	private void refresh() {
			String result = runConsoleCommand("/gps showp"); //$NON-NLS-1$
			StringTokenizer t = new StringTokenizer(result, "\n");
			StringBuffer buf = new StringBuffer();
			while (t.hasMoreElements()) {
				String line = (String) t.nextElement();
				if (line.indexOf("Current") == -1){
					line = line.replace('-', ' ');
					line = line.trim();
					buf.append(line);
					buf.append("\n");
				}
				
			}
			_text.setText(buf.toString());

	}

	public void start() {
		refresh();
	}

	public void stop() {

	}




	
	public static class ItemGPSFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelGPS(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelGPS.class.getName(),
				new ItemGPSFactory());
	}

}
