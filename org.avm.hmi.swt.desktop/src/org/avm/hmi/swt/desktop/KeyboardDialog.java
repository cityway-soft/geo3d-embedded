package org.avm.hmi.swt.desktop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class KeyboardDialog { // extends Shell {

	String _value = "";

	Shell _shell;

	public KeyboardDialog(Shell parent, int style) {
//		super(parent, SWT.ON_TOP | SWT.BORDER);
//		_shell = this;
		_shell = new Shell(parent, SWT.ON_TOP | SWT.BORDER);
		Rectangle rect = Geometry.parse(parent.getDisplay().getClientArea(),
				System.getProperty("org.avm.hmi.swt.geometry"));
		_shell.setSize(new Point(rect.width, rect.height));
		_shell.setLocation(new Point(rect.x, rect.y));

		_shell.setBackground(DesktopStyle.getBackgroundColor());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 0;

		_shell.setLayout(gridLayout);
	}

	public void setTitle(String title) {
		_shell.setText(title);
	}

	public String getText() {
		return _value;
	}

	public Shell getShell() {
		return _shell;
	}

	public void open() {
		_shell.open();
	}
	
	public void dispose() {
		_shell.dispose();
	}

	public void layout() {
		_shell.layout();
	}

}
