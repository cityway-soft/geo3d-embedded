package org.avm.hmi.swt.management;


import org.avm.hmi.swt.desktop.AzertyCompleteKeyboard;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardDialog;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PanelSMS extends AbstractPanel implements SelectionListener {

	private Text _textDest;

	private Text _textMessage;

	private Button _buttonSend;

	private Button _buttonDest;

	private Button _buttonMessage;

	public PanelSMS(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		
		GridData gridData;
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		
		this.setLayoutData(gridData);
		create();
	}

	/**
	 * This method initializes itemBundles
	 * 
	 */
	private void create() {
		GridData gridData;


		_buttonDest = new Button(this, SWT.NONE);
		_buttonDest.setText("Edit");
		_buttonDest.setBackground(DesktopStyle.getBackgroundColor());
		KeyboardListener destlistener = new KeyboardListener() {
			public void validation(String str) {
				setDestinataire(str);
			}
		};
		_buttonDest.addSelectionListener(new ButtonDestKeyboardListener("Destinataire", destlistener));

		gridData = new GridData();
		gridData.horizontalIndent=10;
		_buttonDest.setLayoutData(gridData);

		Label labelDest = new Label(this, SWT.NONE);
		labelDest.setText("Destinataire");
		labelDest.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalIndent=10;
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
		labelDest.setLayoutData(gridData);

		_textDest = new Text(this, SWT.BORDER);
		_textDest.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent=10;
		_textDest.setLayoutData(gridData);
		

		_buttonMessage = new Button(this, SWT.NONE);
		_buttonMessage.setText("Edit");
		_buttonMessage.setBackground(DesktopStyle.getBackgroundColor());
		KeyboardListener messagelistener = new KeyboardListener() {
			public void validation(String str) {
				setMessage(str);
			}
		};
		_buttonMessage.addSelectionListener(new ButtonMessageKeyboardListener("Message", messagelistener));
		gridData = new GridData();
		gridData.horizontalIndent=10;
		//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
		_buttonMessage.setLayoutData(gridData);

		Label labelMessage = new Label(this, SWT.NONE);
		labelMessage.setText("Message");
		labelMessage.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalIndent=10;
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
		labelMessage.setLayoutData(gridData);

		_textMessage = new Text(this, SWT.V_SCROLL | SWT.MULTI | SWT.WRAP | SWT.BORDER);
		_textMessage.setBackground(DesktopStyle.getBackgroundColor());
		_textMessage.setText("");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent=10;
		_textMessage.setLayoutData(gridData);
		



		_buttonSend = new Button(this, SWT.NONE);
		_buttonSend.setText("Envoyer");
		_buttonSend.setBackground(DesktopStyle.getBackgroundColor());
		_buttonSend.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		gridData.horizontalSpan = 3;
		_buttonSend.setLayoutData(gridData);
		setDestinataire("");
		layout();
		
	}

	private void setMessage(final String message) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				_textMessage.setText(message);
			}
		});
	}
	

	private void setDestinataire(final String dest) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				_textDest.setText(dest);
				enableSend(!dest.trim().equals(""));
			}
		});
	}
	
	private void enableSend(final boolean enable) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				_buttonSend.setEnabled(enable);
			}
		});
	}


	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		configure();

		String result = runConsoleCommand("/mana id");
		setMessage(result);
	}

	private void configure() {

	}

	public void send(final String message, final String mobile) {
		getScheduler().execute(new Runnable() {
			public void run() {
				enableSend(false);
				String msg = message.replace('\n', ',');
				String cmd = "/sms sendtexto \"" + msg + "\" "
				+ mobile;
				String result = runConsoleCommand(cmd);
				System.out.print("SENDING:" + cmd);
				if (result != null && !result.trim().equals("")) {
					MessageBox.setMessage("Erreur", result, MessageBox.MESSAGE_WARNING,
							SWT.NONE);
				}
				enableSend(true);
			}
		});

	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() == _buttonSend) {
			String message = _textMessage.getText();
			String mobile = _textDest.getText();
			send(message, mobile);
		} 
	}

	public static class ItemSMSFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelSMS(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelSMS.class.getName(),
				new ItemSMSFactory());
	}
	
	
	public class ButtonMessageKeyboardListener implements SelectionListener {
		private KeyboardListener _listener;
		private String _title;

		public ButtonMessageKeyboardListener(String title, KeyboardListener listener) {
			_listener = listener;
			_title = title;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		}

		public void widgetSelected(SelectionEvent arg0) {
			final KeyboardDialog dialog = new KeyboardDialog(getDisplay()
					.getShells()[0], SWT.NONE);
			AzertyCompleteKeyboard keyboard = new AzertyCompleteKeyboard(dialog.getShell(), SWT.NONE);
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
	
	
	public class ButtonDestKeyboardListener implements SelectionListener {
		private KeyboardListener _listener;
		private String _title;

		public ButtonDestKeyboardListener(String title, KeyboardListener listener) {
			_listener = listener;
			_title = title;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		}

		public void widgetSelected(SelectionEvent arg0) {
			final KeyboardDialog dialog = new KeyboardDialog(getDisplay()
					.getShells()[0], SWT.NONE);
			Keyboard keyboard = new Keyboard(dialog.getShell(), SWT.NONE);
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

} // @jve:decl-index=0:visual-constraint="10,10"
