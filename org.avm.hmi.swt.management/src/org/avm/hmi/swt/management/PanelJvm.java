package org.avm.hmi.swt.management;

import java.text.DecimalFormat;

import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PanelJvm extends AbstractPanel implements SelectionListener {
	private static final DecimalFormat DECIMALFORMAT = new DecimalFormat(
			"####.##"); // @jve:decl-index=0: //$NON-NLS-1$

	private Composite itemJvm;

	private Label freeMemory;

	private Text textFreeMemory;

	private Label totalMemory;

	private Text textTotalMemory;

	private Font _font;

	public PanelJvm(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		create();
		this.setSize(new Point(459, 286));
	}

	/**
	 * This method initializes itemJvm
	 * 
	 */
	private void create() {
		GridLayout gridLayout8 = new GridLayout();
		gridLayout8.numColumns = 2;
		
		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.horizontalSpan = 2;
		Button refresh = new Button(this, SWT.NONE);
		refresh.setLayoutData(gridData);
		refresh.setText(Messages.getString("ItemJVM.raffraichir")); //$NON-NLS-1$
		refresh.setBackground(this.getDisplay().getSystemColor(
			SWT.COLOR_YELLOW));
		refresh.addSelectionListener(this);
		
		itemJvm = new Composite(this, SWT.NONE);
		_font = DesktopImpl.getFont(4,
				SWT.NORMAL);
		itemJvm.setFont(_font);
		itemJvm.setLayout(gridLayout8);
		itemJvm.setBackground(DesktopStyle.getBackgroundColor());
		freeMemory = new Label(itemJvm, SWT.NONE);
		freeMemory.setText("Free Memory");
		freeMemory.setBackground(DesktopStyle.getBackgroundColor());
		textFreeMemory = new Text(itemJvm, SWT.BORDER | SWT.READ_ONLY);
		totalMemory = new Label(itemJvm, SWT.NONE);
		totalMemory.setText("Total Memory");
		totalMemory.setBackground(DesktopStyle.getBackgroundColor());
		textTotalMemory = new Text(itemJvm, SWT.BORDER | SWT.READ_ONLY);

		refresh();
	}

	private void refresh() {

		textTotalMemory.setText(DECIMALFORMAT.format(Runtime.getRuntime()
				.totalMemory() / 1024.0)
				+ " ko");
		textFreeMemory.setText(DECIMALFORMAT.format(Runtime.getRuntime()
				.freeMemory() / 1024.0)
				+ " ko");
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void widgetSelected(SelectionEvent arg0) {
		refresh();
	}
	
	
	
	public static class ItemJvmFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelJvm(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelJvm.class.getName(),
				new ItemJvmFactory());
	}
	
	
} // @jve:decl-index=0:visual-constraint="10,10"
