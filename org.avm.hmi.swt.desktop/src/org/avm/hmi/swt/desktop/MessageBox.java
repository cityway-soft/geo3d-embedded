package org.avm.hmi.swt.desktop;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MessageBox implements SelectionListener {
	private Label _title;

	private Text _text;

	private Shell _dialog;

	private static MessageBox _box = null;

	private static Shell _mainShell = null;

	private Button _buttonOK;

	private Button _buttonCancel;

	private Display _display;

	private static final int FONT_COUNT = 4;

	private Font _fonts[] = new Font[FONT_COUNT];

	private static final SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$

	public static final int MESSAGE_NORMAL = 0;

	public static final int MESSAGE_ALARM = 1;

	public static final int MESSAGE_WARNING = 2;

	private static final double MESSAGE_MAX_LENGTH = 480;

	private static SelectionListener _listener = null;

	private Logger _log = Logger.getInstance(this.getClass().getName());

	private Composite _compositeButtons;

	public static void setMessage(final String title, final String message,
			final int type, final int textStyle) {
		setMessage(title, message, type, textStyle, null);
	}

	public static void setMessage(final String title, final String message,
			final int type, final int textStyle, SelectionListener listener) {
		setMessage(title, message, type, textStyle, listener, false);
	}

	public static void setMessage(final String title, final String message,
			final int type, final int textStyle, SelectionListener listener,
			final boolean confirmation) {
		_listener = listener;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					if (message != null) {
						if (_box == null || _box.isDisposed() == true) {
							Shell shell = getMainShell();
							_box = new MessageBox(shell, textStyle,
									confirmation);
						}
						_box.setConfirm(confirmation);
						_box.setMessage(message);
						_box.setTitle(title == null ? "Message" : title); //$NON-NLS-1$
						_box.setMessageType(type);
						_box.open();
					} else {
						if (_box != null && _box.isDisposed() == false) {
							_box.close();
						}
					}
				} catch (Throwable t) {
					System.err.println("MessageBox Error!!!"); //$NON-NLS-1$
					t.printStackTrace();
				}
			}

		});
	}

	protected void setConfirm(boolean confirmation) {
		if (_buttonCancel != null) {
			_buttonCancel.setVisible(confirmation);
		}
		_buttonOK
				.setText(confirmation ? Messages.getString("MessageBox.oui") : Messages.getString("MessageBox.ok")); //$NON-NLS-1$ //$NON-NLS-2$
		GridLayout layout = (GridLayout) _compositeButtons.getLayout();
		layout.numColumns = confirmation ? 2 : 1;
		_compositeButtons.layout();
	}

	private static Shell getMainShell() {
		if (_mainShell == null) {
			Shell[] shells = Display.getDefault().getShells();
			for (int i = 0; i < shells.length; i++) {
				if (shells[i].getText().equals(Desktop.APPLICATION_NAME)) {
					_mainShell = shells[i];
					break;
				}
			}
		}
		return _mainShell;
	}

	private MessageBox(final Shell parent, final int style, boolean confirmation) {
		_display = parent.getDisplay();
		Rectangle window = Geometry.parse(_display.getClientArea(),
				System.getProperty("org.avm.hmi.swt.geometry")); //$NON-NLS-1$

		_dialog = new Shell(parent, SWT.NONE | SWT.APPLICATION_MODAL); // | SWT.
		// TITLE
		_dialog.setText(Messages.getString("MessageBox.avertissement")); //$NON-NLS-1$
		Point size = new Point((int) (window.width * 0.75),
				(int) (window.height * 0.75));
		_dialog.setSize(size);
		_dialog.setBackground(DesktopStyle.getBackgroundColor());
		Point rect = parent.getSize();
		Point parentLoc = parent.getLocation();
		Point location = new Point(parentLoc.x + (rect.x - size.x) / 2,
				parentLoc.y + (rect.y - size.y) / 2);
		_dialog.setLocation(location);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		_dialog.setLayout(layout);
		_dialog.layout();
		_dialog.addListener(SWT.Hide, new Listener() {
			public void handleEvent(Event arg0) {
				_log.info("Window is hidden!");
				_dialog.forceActive();
			}
		});
		_fonts[0] = DesktopImpl.getFont(7, SWT.BOLD);
		_fonts[1] = DesktopImpl.getFont(5, SWT.NORMAL);
		_fonts[2] = DesktopImpl.getFont(4, SWT.BOLD);
		_fonts[3] = DesktopImpl.getFont(2, SWT.BOLD);
		createContent(style, confirmation);
		setVisible(false);
		_dialog.open();

	}

	public boolean isDisposed() {
		return _dialog.isDisposed();
	}

	private void createContent(int style, boolean confirm) {

		GridData gridData;

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 30;
		_title = new Label(_dialog, SWT.NONE | SWT.BORDER | SWT.CENTER);
		_title.setText(Messages.getString("MessageBox.message")); //$NON-NLS-1$
		_title.setFont(_fonts[1]);
		_title.setLayoutData(gridData);

		_text = new Text(_dialog, SWT.NONE | SWT.READ_ONLY | SWT.WRAP | style);

		setMessageType(MESSAGE_NORMAL);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_text.setLayoutData(gridData);
		_text.setEditable(false);
		_text.setFont(_fonts[0]);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 60;
		_compositeButtons = new Composite(_dialog, SWT.NONE);
		_compositeButtons.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		_buttonOK = new Button(_compositeButtons, SWT.NONE);
		_buttonOK
				.setText(confirm ? Messages.getString("MessageBox.oui") : Messages.getString("MessageBox.ok")); //$NON-NLS-1$ //$NON-NLS-2$
		_buttonOK.setData(new Boolean(true));
		_buttonOK.setFont(_fonts[0]);
		_buttonOK.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 60;
		_buttonOK.setLayoutData(gridData);

		layout.numColumns = 2;
		_buttonCancel = new Button(_compositeButtons, SWT.NONE);
		_buttonCancel.setText(Messages.getString("MessageBox.non")); //$NON-NLS-1$
		_buttonCancel.setData(new Boolean(false));
		_buttonCancel.setFont(_fonts[0]);
		_buttonCancel.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 60;
		_buttonCancel.setLayoutData(gridData);

		_compositeButtons.setLayout(layout);
		_compositeButtons.layout();
		_dialog.layout();

	}

	public void setMessage(final String message) {
		String msg = message;
		_log.debug("Message size :" + msg.length()); //$NON-NLS-1$
		if (msg.length() > (int) (MESSAGE_MAX_LENGTH * (2.0 / 3.0))) {
			_text.setFont(_fonts[3]);
			_log.debug("Font: xxsmall"); //$NON-NLS-1$

		} else if (msg.length() > (int) (MESSAGE_MAX_LENGTH * (3.0 / 8.0))) {
			_text.setFont(_fonts[2]);
			_log.debug("Font: small"); //$NON-NLS-1$
		} else {
			_log.debug("Font: normal"); //$NON-NLS-1$
			_text.setFont(_fonts[0]);
		}
		if (msg.length() > MESSAGE_MAX_LENGTH) {
			msg = message.substring(0, (int) MESSAGE_MAX_LENGTH - 3) + "..."; //$NON-NLS-1$
		}
		_text.setText(msg);
		_text.pack();
		_dialog.layout();
	}

	public void setTitle(final String title) {
		String msg = title + " - " + DF.format(new Date()); //$NON-NLS-1$
		_dialog.setText(msg);
		_title.setText(msg);
	}

	public void setMessageType(int type) {
		Color bgcolor;
		Color fgcolor;
		switch (type) {

		case MESSAGE_WARNING: {
			bgcolor = _dialog.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
			fgcolor = _dialog.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}
			break;
		case MESSAGE_ALARM: {
			bgcolor = _dialog.getDisplay().getSystemColor(SWT.COLOR_RED);
			fgcolor = _dialog.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		}
			break;
		default: {
			bgcolor = _dialog.getDisplay().getSystemColor(SWT.COLOR_WHITE);
			fgcolor = _dialog.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}
			break;
		}
		_text.setBackground(bgcolor);
		_dialog.setBackground(bgcolor);
		_text.setForeground(fgcolor);

	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		close();
		if (_listener != null) {
			_listener.widgetSelected(e);
		}
	}

	public void open() {
		setVisible(true);
	}

	public void close() {
		setVisible(false);
	}
		
	public void setVisible(boolean b) {
		_dialog.setVisible(b);
		if (b){
			_dialog.forceActive();
		}
	}

	protected void finalize() {
		for (int i = 0; i < _fonts.length; i++) {
			_fonts[i].dispose();
		}
	}

}
