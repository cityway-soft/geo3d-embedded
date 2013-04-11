package org.avm.hmi.swt.management;

import org.avm.elementary.common.ManageableService;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ManagementDialog implements SelectionListener {

	Shell _shell;

	Composite _composite;

	private ManageableService _service;

	private Label _label;

	private Font _fontTitle;

	private Button _buttonClose;

	public ManagementDialog(Shell shell, String label) {
		_shell = new Shell(shell, SWT.TOP | SWT.BORDER);

		_shell.setBackground(DesktopStyle.getBackgroundColor());
		GridLayout gridLayout = new GridLayout();

		_shell.setLayout(gridLayout);
		// Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		Rectangle rect = Geometry.parse(Display.getCurrent().getClientArea(),
				System.getProperty("org.avm.hmi.swt.geometry"));
		_shell.setSize(new Point(rect.width, rect.height));
		_shell.setLocation(new Point(rect.x, rect.y));

		GridData gridData;

		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		_fontTitle = DesktopImpl.getFont(10, SWT.NORMAL);
		_label = new Label(_shell, SWT.NONE | SWT.CENTER);
		_label.setFont(_fontTitle);
		_label.setBackground(DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;

		_composite = new Composite(_shell, SWT.NONE);
		gridLayout = new GridLayout();
		_composite.setLayout(gridLayout);
		_composite.setBackground(DesktopStyle.getBackgroundColor());
		_composite.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = Management.BUTTON_HEIGHT;

		_buttonClose = new Button(_shell, SWT.NONE);
		_buttonClose.setText("Fermer");
		Font font = DesktopImpl.getFont(10, SWT.NORMAL);
		_buttonClose.setFont(font);
		_buttonClose.setLayoutData(gridData);
		_buttonClose.addSelectionListener(this);
		_buttonClose.setBackground(DesktopStyle.getBackgroundColor());

	}

	public void layout() {
		_shell.layout();
	}

	public void setName(String name) {
		_label.setText(name);
	}

	public Composite getContent() {
		return _composite;
	}

	public void setSize(int x, int y) {
		_shell.setSize(x, y);
	}

	public void setTitle(String title) {
		_shell.setText(title);
	}

	public void open() {
		_shell.open();
		if (_service != null) {
			_service.start();
		}
	}

	public void close() {
		if (isDisposed() == false) {
			_shell.close();
			_composite.dispose();
		}
		_shell = null;
		_composite = null;

		if (_service != null) {
			_service.stop();
		}
	}

	public boolean isDisposed() {
		return (_shell == null || _shell.isDisposed());
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		close();
	}

	public void widgetSelected(SelectionEvent arg0) {
		close();
	}

	public void setService(ManageableService service) {
		_service = service;
	}
}
