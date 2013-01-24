package org.avm.hmi.swt.browser;

import java.net.URL;

import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class BrowserIhm extends Composite {



	private Browser browser;


	public BrowserIhm(Composite parent, int style) {
		super(parent, style);

		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		this.setBackground(Display.getCurrent().getSystemColor(
				Desktop.DEFAULT_BACKGROUND_COLOR));
		create();
	}

	/**
	 * Create Table of missions
	 */
	private void create() {

		try {
			System.out.println("[BrowserIHM] creating browser ...");
			browser = new Browser(this, SWT.NONE);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL_HORIZONTAL;
			gridData.verticalAlignment = GridData.FILL_VERTICAL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			browser.setLayoutData(gridData);
			
			System.out.println("[BrowserIHM] browser created.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	
	public void setUrl(URL url){
		if (browser != null){
			browser.setUrl(url.toExternalForm());
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
