package org.avm.hmi.swt.management;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.IPKeyboard;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class AbstractItemNetwork extends Composite implements SelectionListener {

	private Label _labelServerAdress;

	private Text _textServerAdress;

	private Label _labelClientAdress;

	private Text _textClientAdress;

	private Font _font;

	private AbstractItemNetwork _instance = this;
	private Button _connect;
	private Button _disconnect;
	private Button _isconnected;
	private Button _changeServerAdress;
	private Group _group;

	private Label _labelStatus;

	private ItemNetworkListener _listener;

	public AbstractItemNetwork(Composite parent, int style) {
		super(parent, style);
		create();
	}

	public void setText(String title) {
		_group.setText(title);
	}

	public void setServerTitle(String title) {
		_labelServerAdress.setText(title); //$NON-NLS-1$
	}

	public void setClientTitle(String title) {
		_labelClientAdress.setText(title); //$NON-NLS-1$
	}

	public void setListener(ItemNetworkListener listener) {
		_listener = listener;
	}

	private void create() {
		GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);
		setBackground(DesktopStyle.getBackgroundColor());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		setLayoutData(gridData);
		
		
		_group = new Group(this, SWT.NONE);
		Composite composite = _group;

		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		composite.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		composite.setLayoutData(gridData);

		_font = DesktopImpl.getFont( 5, SWT.NORMAL); //$NON-NLS-1$

		_labelServerAdress = new Label(composite, SWT.NONE);
		_labelServerAdress.setFont(_font);
		_labelServerAdress.setBackground(DesktopStyle.getBackgroundColor());

		_textServerAdress = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_textServerAdress.setText("___.___.___.___"); //$NON-NLS-1$
		_textServerAdress.setFont(_font);
		_textServerAdress.setBackground(DesktopStyle.getBackgroundColor());

		Composite compositeGrp = new Composite(composite, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		compositeGrp.setLayout(gridLayout);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		compositeGrp.setLayoutData(gridData);
		compositeGrp.setBackground(DesktopStyle.getBackgroundColor());

		_changeServerAdress = new Button(compositeGrp, SWT.NONE);
		gridData = new GridData();
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		_changeServerAdress.setLayoutData(gridData);
		_changeServerAdress.setText(Messages.getString("ItemNetwork.modifier")); //$NON-NLS-1$
		KeyboardListener listener = new KeyboardListener() {
			public void validation(String data) {
				if (data != null) {
					if (!data.equals(_textServerAdress.getText())) {
						
						try {
							String oldad = _textServerAdress.getText();
							String newad=null;
							if (data.equals("")) {
								newad="del";
								setAddress(_textServerAdress, null);
							} else {
								InetAddress add;
								add = InetAddress.getByName(data);
								newad=add.getHostAddress();
								setAddress(_textServerAdress,newad);
							}
							if (_listener != null) {
								_listener.adressChanged(oldad,
										newad);
							}
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}

					}
				}
			}

		};
		_changeServerAdress.addSelectionListener(new ButtonKeyboardListener(
				Messages.getString("ItemNetwork.addr-server-ftp"), listener));
		_changeServerAdress.setBackground(DesktopStyle.getBackgroundColor());

		_labelStatus = new Label(compositeGrp, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		_labelStatus.setLayoutData(gridData);
		_labelStatus.setFont(_font);
		_labelStatus.setText("YOUPI");
		_labelStatus.setBackground(DesktopStyle.getBackgroundColor());

		_labelClientAdress = new Label(composite, SWT.NONE);
		_labelClientAdress.setFont(_font);
		_labelClientAdress.setBackground(DesktopStyle.getBackgroundColor());

		_textClientAdress = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		_textClientAdress.setText("___.___.___.___"); //$NON-NLS-1$
		_textClientAdress.setFont(_font);
		_textClientAdress.setBackground(DesktopStyle.getBackgroundColor());

		Composite compositeButtons = new Composite(composite, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		compositeButtons.setLayout(gridLayout);
		compositeButtons.setBackground(DesktopStyle.getBackgroundColor());

		_connect = new Button(compositeButtons, SWT.NONE);
		gridData = new GridData();
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		_connect.setLayoutData(gridData);
		_connect.setText(Messages.getString("ItemNetwork.connecter")); //$NON-NLS-1$
		_connect.addSelectionListener(this);
		_connect.setBackground(DesktopStyle.getBackgroundColor());

		_disconnect = new Button(compositeButtons, SWT.NONE);
		gridData = new GridData();
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		_disconnect.setLayoutData(gridData);
		_disconnect.setText(Messages.getString("ItemNetwork.deconnecter")); //$NON-NLS-1$
		_disconnect.addSelectionListener(this);
		_disconnect.setBackground(DesktopStyle.getBackgroundColor());

		_isconnected = new Button(compositeButtons, SWT.NONE);
		gridData = new GridData();
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		_isconnected.setLayoutData(gridData);
		_isconnected.setText(Messages.getString("ItemNetwork.etat-connection")); //$NON-NLS-1$
		_isconnected.addSelectionListener(this);
		_isconnected.setBackground(DesktopStyle.getBackgroundColor());
	}

	public void setEnabled(boolean b) {
		_isconnected.setEnabled(b);
		_connect.setEnabled(b);
		_disconnect.setEnabled(b);
		_textClientAdress.setEnabled(b);
	}

	public void setServerAdressChangeEnabled(final boolean b) {
//		getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				if (_changeServerAdress.isDisposed() == false) {
//					_changeServerAdress.setEnabled(b);
//				}
//			}
//		});

	}

	public void setServerInHost(final boolean b) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (_labelStatus.isDisposed() == false) {
					String text = "";
					if (b) {
						text = "(/etc/hosts)";
					} else {
						text = "(DNS)";
					}
					_labelStatus.setText(text);
				}
			}
		});

	}

	public void activate(final boolean b) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				if (_instance.isDisposed() == false) {
					_instance.setEnabled(b);
				}
			}
		});
	}

	public void setAddress(final Text c, final String ip) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				if (c.isDisposed() == false) {
					if (ip != null) {
						c.setText(ip);
					} else {
						c.setText("___.___.___.___"); //$NON-NLS-1$
					}
				}

			}
		});
	}

	public void enableChange(final Button button, final boolean b) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				button.setEnabled(b);

			}
		});
	}

	public void setAdress(final String client, final String server) {
		setAddress(_textClientAdress, client);
		setAddress(_textServerAdress, server);
	}


	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	public void widgetSelected(SelectionEvent arg0) {
		Button button = (Button) arg0.getSource();
		if (_listener != null) {
			if (button == _connect) {
				_listener.connect();
			} else if (button == _disconnect) {
				_listener.disconnect();
			} else if (button == _isconnected) {
				_listener.isConnected();
			}
		}

	}

	public class ButtonKeyboardListener implements SelectionListener {
		private KeyboardListener _listener;
		private String _title;

		public ButtonKeyboardListener(String title, KeyboardListener listener) {
			_listener = listener;
			_title = title;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		}

		public void widgetSelected(SelectionEvent arg0) {
			final KeyboardDialog dialog = new KeyboardDialog(getDisplay()
					.getShells()[0], SWT.NONE);
			IPKeyboard keyboard = new IPKeyboard(dialog.getShell(), SWT.NONE);
			keyboard.setDisposeParent(true);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
	
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;
	
			keyboard.setLayoutData(gridData);
			dialog.setTitle(_title);
			
			dialog.open();
			dialog.getShell().layout();
			keyboard.setText(_textServerAdress.getText());
			keyboard.setListener(_listener);
		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"

