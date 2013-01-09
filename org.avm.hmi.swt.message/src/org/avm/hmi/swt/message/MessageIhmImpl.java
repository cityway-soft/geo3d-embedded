package org.avm.hmi.swt.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.MessageText;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.avm.hmi.swt.desktop.AzertyCompleteKeyboard;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.KeyboardDialog;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.avm.hmi.swt.desktop.StateButton;
import org.avm.hmi.swt.message.bundle.ConfigImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class MessageIhmImpl extends Composite implements MessageIhm,
		MessengerInjector, ConfigurableService {

	private static final SimpleDateFormat DF = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$

	private static final int BUTTON_HEIGHT = 45;

	private static final int MESSAGE_NORMAL = 0;

	private static final int MESSAGE_WARNING = 1;

	private static final int MESSAGE_ALARM = 2;

	private Messenger _messenger;

	private Button _buttonMessage;

	private Logger _log;

	private Table _table;

	private Text _text;

	private Font _fontTable;

	private Font _fontText;

	private Group _panelSend;
	
	private int _receiveMessageCounter=0;

	public MessageIhmImpl(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		// gridLayout.makeColumnsEqualWidth = true;
		GridData gridData;

		this.setLayout(gridLayout);
		setBackground(DesktopStyle.getBackgroundColor());

		_fontTable = DesktopImpl.getFont( 5, SWT.NORMAL); //$NON-NLS-1$

		_fontText = DesktopImpl.getFont( 9, SWT.NORMAL); //$NON-NLS-1$

		_panelSend = new Group(this, SWT.NONE);
		_panelSend.setText(Messages.getString("Message.boite-envoi")); //$NON-NLS-1$
		_panelSend.setBackground(DesktopStyle.getBackgroundColor());
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		_panelSend.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_panelSend.setLayoutData(gridData);

		_buttonMessage = new Button(_panelSend, SWT.NONE);
		_buttonMessage.setText(Messages.getString("Message.message-libre")); //$NON-NLS-1$
		_buttonMessage.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.heightHint = BUTTON_HEIGHT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_buttonMessage.setLayoutData(gridData);
		KeyboardListener messagelistener = new KeyboardListener() {
			public void validation(String str) {
				if (str != null && !str.trim().equals("")) { //$NON-NLS-1$
					send(str);
				}
			}
		};
		_buttonMessage.addSelectionListener(new ButtonMessageKeyboardListener(
				"Message", messagelistener)); //$NON-NLS-1$

		Group panelReceive = new Group(this, SWT.NONE);
		panelReceive.setText(Messages.getString("Message.boite-reception")); //$NON-NLS-1$
		panelReceive.setBackground(DesktopStyle.getBackgroundColor());
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		panelReceive.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		panelReceive.setLayoutData(gridData);

		_table = new Table(panelReceive, SWT.BORDER);
		_table.setFont(_fontTable);
		_table.setBackground(DesktopStyle.getBackgroundColor());
		_table.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent arg0) {
				action(_table.getSelectionIndex());
			}
		});
		TableColumn tableColumn0 = new TableColumn(_table, SWT.NONE);
		tableColumn0.setWidth(115);
		tableColumn0.setText("n°"); //$NON-NLS-1$

		TableColumn tableColumn1 = new TableColumn(_table, SWT.NONE);
		tableColumn1.setWidth(53);
		tableColumn1.setText("Reçu"); //$NON-NLS-1$

		TableColumn tableColumn2 = new TableColumn(_table, SWT.NONE);
		tableColumn2.setWidth(0);
		tableColumn2.setText("Message"); //$NON-NLS-1$

		TableColumn tableColumn3 = new TableColumn(_table, SWT.NONE);
		tableColumn3.setWidth(0);
		tableColumn3.setText("Priority"); //$NON-NLS-1$

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_table.setLayoutData(gridData);

		_text = new Text(panelReceive, SWT.V_SCROLL | SWT.MULTI | SWT.WRAP
				| SWT.BORDER);
		_text.setFont(_fontText);
		_text.setBackground(DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		_text.setLayoutData(gridData);

	}

	private void action(int selectionIndex) {
		TableItem item = _table.getItem(selectionIndex);
		String message = item.getText(2);
		_text.setText(message);
		String prior = item.getText(3);
		int type = MESSAGE_NORMAL;
		try {
			type = Integer.parseInt(prior);
		} catch (Throwable t) {
		}
		_text.setBackground(getMessageTypeBackground(type));
		_text.setForeground(getMessageTypeForeground(type));

	}

	public Color getMessageTypeBackground(final int type) {
		Color color;
		switch (type) {

		case MESSAGE_WARNING: {
			color = getDisplay().getSystemColor(SWT.COLOR_YELLOW);
		}
			break;
		case MESSAGE_ALARM: {
			color = getDisplay().getSystemColor(SWT.COLOR_RED);
		}
			break;
		default: {
			color = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		}
			break;
		}
		return color;
	}

	public Color getMessageTypeForeground(final int type) {
		Color color;
		switch (type) {

		case MESSAGE_WARNING: {
			color = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}
			break;
		case MESSAGE_ALARM: {
			color = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		}
			break;
		default: {
			color = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}
			break;
		}

		return color;

	}


	public void addMessage(final Properties msgprops) {

		getDisplay().asyncExec(new Runnable() {
			

			public void run() {
				try {
					String id = (String) msgprops
							.get(org.avm.business.messages.Messages.ID);
//					String id = Integer.toString(++_receiveMessageCounter);
					String message = (String) msgprops
							.get(org.avm.business.messages.Messages.MESSAGE);
					String priority = (String) msgprops
							.get(org.avm.business.messages.Messages.PRIORITE);
					_log.debug("##Adding message :" + id +", msg:" + message ); //$NON-NLS-1$ //$NON-NLS-2$

					String date = DF.format(new Date());
					String val[] = new String[] { id, date, message, priority };
					TableItem item = new TableItem(_table, SWT.NONE);
					item.setText(val);
					
					int type = MESSAGE_NORMAL;
					try {
						type = Integer.parseInt(priority);
					} catch (Throwable t) {
					}
					item.setBackground(getMessageTypeBackground(type));
					layout();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});

	}

	protected void addPredefinedMessage(final String title, final String message) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					StateButton button = new StateButton(_panelSend, SWT.NONE);
					button.setText(title);
					button.setBackground(DesktopStyle.getBackgroundColor());
					GridData gridData = new GridData();
					gridData.heightHint = BUTTON_HEIGHT;
					gridData.horizontalAlignment = GridData.FILL;
					gridData.grabExcessHorizontalSpace = true;
					button.setLayoutData(gridData);
					button.addSelectionListener(new ButtonPredefinedMessageListener(
							message));
					layout();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});

	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (_fontText != null) {
			_fontText.dispose();
			_fontText = null;
		}
		if (_fontTable != null) {
			_fontTable.dispose();
			_fontTable = null;
		}
	}

	public void setMessenger(Messenger messenger) {
		_messenger = messenger;
	}

	public void unsetMessenger(Messenger messenger) {
		_messenger = messenger;
	}

	public class ButtonMessageKeyboardListener implements SelectionListener {
		private KeyboardListener _listener;
		private String _title;

		public ButtonMessageKeyboardListener(String title,
				KeyboardListener listener) {
			_listener = listener;
			_title = title;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		}

		public void widgetSelected(SelectionEvent arg0) {
			final KeyboardDialog dialog = new KeyboardDialog(getDisplay()
					.getShells()[0], SWT.NONE);
			AzertyCompleteKeyboard keyboard = new AzertyCompleteKeyboard(
					dialog.getShell(), SWT.NONE);
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
			keyboard.setListener(_listener);
		}

	}

	public class ButtonPredefinedMessageListener implements SelectionListener {
		private String _message;

		public ButtonPredefinedMessageListener(String message) {
			_message = message;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		}

		public void widgetSelected(SelectionEvent arg0) {
			StateButton button = (StateButton) arg0.getSource();
			StringBuffer message = new StringBuffer();

			if (button.getSelection()) {
				message.append(Messages.getString("Message.debut"));
			} else {
				message.append(Messages.getString("Message.fin"));
			}
			message.append(_message); //$NON-NLS-1$
			send(message.toString());
		}

	}

	private void send(final String text) {
		if (_messenger != null) {
			Hashtable d = new Hashtable();
			d.put("destination", "sam"); //$NON-NLS-1$ //$NON-NLS-2$
			d.put("binary", "true"); //$NON-NLS-1$ //$NON-NLS-2$

			StringBuffer msg = new StringBuffer();
			msg.append(Messages.getString("Message.conducteur"));
			msg.append(" - ");
			msg.append(text);
			
			MessageText message = new MessageText();
			message.setMessage(msg.toString());
			Entete entete = message.getEntete();
			entete.getChamps().setPosition(1);
			entete.getChamps().setService(1);
			try {
				_log.info("Sending message :" + text); //$NON-NLS-1$
				_messenger.send(d, message);
			} catch (Exception e) {
				_log.error("Error sendMessage", e); //$NON-NLS-1$
				_log.error(e); //$NON-NLS-1$
			}
		}
	}

	public void setLogger(Logger log) {
		_log = log;
	}

	public void clearMessages() {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					_table.removeAll();
					layout();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}

	public void configure(Config config) {
		if (config != null) {
			Properties props = ((ConfigImpl) config)
					.getProperty(MessageConfig.MESSAGES);
			Enumeration e = props.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				addPredefinedMessage(key, props.getProperty(key));
			}
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
