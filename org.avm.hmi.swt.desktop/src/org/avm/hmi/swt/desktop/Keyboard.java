package org.avm.hmi.swt.desktop;


import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Keyboard extends Composite {

	protected static final String EMPTY = "-EMPTY-";

	protected static final int DEFAULT_FONTSIZE = Integer.parseInt(System
			.getProperty("org.avm.hmi.swt.fontsize", //$NON-NLS-1$
					"10")); //$NON-NLS-1$

	protected final static String BACKSPACE = Messages
			.getString("Keyboard.effacer"); //$NON-NLS-1$

	protected final static String CLEAR = Messages.getString("Keyboard.vider"); //$NON-NLS-1$

	protected final static String CANCEL = Messages
			.getString("Keyboard.annuler"); //$NON-NLS-1$

	protected final static String OK = Messages.getString("Keyboard.valider"); //$NON-NLS-1$

	protected static String[][] KEYS = { { "1", "2", "3" }, { "4", "5", "6" },
			{ "7", "8", "9" }, { BACKSPACE, "0", OK } }; //$NON-NLS-1$ //$NON-NLS-2$

	protected String _text;

	private Button[] _buttons;

	protected KeyboardListener _validationListener;

	protected StateChangedListener _stateChangedListener;

	private Font _fontTitle;

	private Font _fontText;

	private Font _fontsmallTitle;

	protected Text _field;

	private KeyPressedTimerTask _task;

	private String _lastKey = "";

	private int _index = 0;

	private Composite _parent;

	protected boolean _refreshAfterValidation = true;

	private String CURSEUR = "_";

	private String _curseur = "";

	private boolean _disposeParent;


	private Label _title;

	private static final int INDENT = DEFAULT_FONTSIZE / 5;



	public String getText() {
		return getTextInBox();
	}

	public void setText(String text) {
		setTextInBox(text);
	}
	
	public void setTextColor(Color color) {
		_title.setForeground(color);
	}

	public Keyboard(Composite parent, int ctrl) {
		this("", parent, ctrl);
	}
	
	public Keyboard(String name, Composite parent, int ctrl) {
		super(parent, ctrl);
		_parent = parent;
		_text = name;

		create();
		//parent.layout();
	}

	public void setCursorEnable(boolean b) {
		_curseur = b ? CURSEUR : "";
	}

	public void setDisposeParent(boolean b) {
		_disposeParent = b;
	}

	public void setListener(KeyboardListener listener) {
		_validationListener = listener;
	}

	public void validation(String text) {
		if (_validationListener != null) {
			try {
				_validationListener.validation(text);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		if (_disposeParent) {
			System.out.println("[DLA] Keyboard : Dispose parent....");
			if (_parent.isDisposed() == false) {
				_parent.dispose();
			}
		}
	}

	public void setStateChangedListener(StateChangedListener listener) {
		_stateChangedListener = listener;
	}

	public void setRefreshAfterValidation(boolean b) {
		_refreshAfterValidation = b;
	}

	public void setPassword(boolean b) {
		if (b) {
			setCursorEnable(false);
			_field.setText("");
			_field.setEchoChar('*');
		}
	}

	protected String[][] getKeys() {
		return KEYS;
	}

	private int getNumCol() {
		int n = getKeys()[0].length;
		return n;
	}

	private int getNumLig() {
		int n = getKeys().length;
		return n;
	}

	private void create() {
		Composite composite = this;
		GridData data;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = getNumCol();
		gridLayout.makeColumnsEqualWidth = true;

		
		_disposeParent = false;


		composite.setLayout(gridLayout);
		composite.setBackground(DesktopStyle.getBackgroundColor());
		_fontTitle = DesktopImpl.getFont( 10, SWT.NORMAL);
		_fontText = DesktopImpl.getFont( 10, SWT.NORMAL);
		_fontsmallTitle = DesktopImpl.getFont( 3, SWT.NORMAL);

		if (_text != null) {
			data = new GridData();
			data.horizontalSpan = getNumCol();
			data.horizontalAlignment = GridData.FILL;
			data.horizontalIndent = INDENT;

		    _title = new Label(composite, SWT.NONE);
			_title.setBackground(DesktopStyle.getBackgroundColor());
			_title.setLayoutData(data);
			_title.setFont(_fontTitle);
			_title.setText(_text);
			
		}

		_field = new Text(composite, SWT.BORDER);
		setTextInBox("");

		data = new GridData();
		data.horizontalSpan = getNumCol();
		data.horizontalIndent = INDENT;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;

		_field.setLayoutData(data);
		_field.setFont(_fontText);

		String[][] keytab = getKeys();

		_buttons = new Button[getNumLig() * getNumCol()];
		int cpt = 0;
		for (int i = 0; i < getNumLig(); i++) {
			for (int j = 0; j < getNumCol(); j++) {
				String key = keytab[i][j];
				Control widget;
				if (key == null) {
					int spanCtrl = 1;
					while ((j + spanCtrl) < getNumCol()
							&& keytab[i][(j + spanCtrl)] == null) {
						spanCtrl++;
					}
					if (data != null) {
						data.horizontalSpan = spanCtrl + 1;
						j = j + spanCtrl - 1;
						continue;
					} else {
						key = EMPTY;
					}
				}
				if (key == EMPTY) {
					widget = new Label(composite, SWT.NONE);
				} else {

					Button button = new Button(composite, SWT.NONE);
					button.setText(key);
					widget = button;
					_buttons[cpt] = button;
					addButtonSelectionAdapter(button);
					cpt++;
				}

				data = new GridData();

				data.verticalAlignment = GridData.FILL;
				data.grabExcessVerticalSpace = true;
				data.grabExcessHorizontalSpace = true;

				data.horizontalIndent = INDENT;
				data.horizontalAlignment = GridData.FILL;
				widget.setLayoutData(data);

				if (key != null
						&& (key.equals(OK) || key.equals(BACKSPACE)
								|| key.equals(CLEAR) || key.equals(CANCEL))) {
					widget.setFont(_fontsmallTitle);
					if (key.equals(OK)) {
						widget.setBackground(getDisplay().getSystemColor(
								SWT.COLOR_GREEN));
					} else if (key.equals(BACKSPACE)) {
						widget.setBackground(getDisplay().getSystemColor(
								SWT.COLOR_RED));
					} else if (key.equals(CANCEL)) {
						widget.setBackground(getDisplay().getSystemColor(
								SWT.COLOR_BLUE));
					} else {
						widget.setBackground(getDisplay().getSystemColor(
								SWT.COLOR_YELLOW));
					}
				} else {
					widget.setFont(_fontsmallTitle);
					widget.setBackground(DesktopStyle.getBackgroundColor());
				}

			}// for
		}// for
	}

	public void setEnabled(boolean b) {
		if (this.isDisposed() == false) {
			super.setEnabled(b);
			for (int i = 0; i < _buttons.length; i++) {
				_buttons[i].setEnabled(b);
			}
		}
	}

	private String getTextInBox() {
		String text = _field.getText();
		String result = text;
		if (text.length() > 0 && text.endsWith(CURSEUR)) {
			int length = text.length() - 1;
			result = text.substring(0, length);
		}
		return result;
	}

	private void appendTextInBox(String text) {
		String t = getTextInBox();
		setTextInBox(t + text);
	}

	private void setTextInBox(String text) {
		_field.setText(text + _curseur);
	}

	protected void addButtonSelectionAdapter(Button button) {
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Button b = (Button) e.getSource();
					String key = b.getText();
					if (key.equals(CANCEL)) {
						validation(null);
					} else if (key.equals(CLEAR)) {
						String text = getTextInBox();
						if (text.length() > 0) {
							setTextInBox("");
						}
					} else if (key.equals(BACKSPACE)) {
						String text = getTextInBox();
						if (text.length() > 0) {
							text = text.substring(0, text.length() - 1);
							setTextInBox(text);
						}
					} else if (key.equals(OK)) {
						validation(getTextInBox());
					} else {
						if (key.length() > 1) {
							int i = _index % key.length();
							if (!key.equals(_lastKey)) {
								i = 0;
								if (_task != null) {
									_task.cancel();
									_task.run();
								}
							} else if (_task != null) {
								_task.cancel();
								_task = null;
								String text = getTextInBox();
								if (text.length() > 0) {
									text = text.substring(0, text.length() - 1);
									setTextInBox(text);
								}
							}
							appendTextInBox(String.valueOf(key.charAt(i)));
							_task = new KeyPressedTimerTask();
							Timer timer = new Timer();
							timer.schedule(_task, 1000);
							_index++;
							_lastKey = key;
						} else {
							appendTextInBox(key);
						}
					}
				} catch (Throwable t) {
					System.err.println(t);
				}

			}
		});

	}

	class KeyPressedTimerTask extends TimerTask {

		public KeyPressedTimerTask() {
		}

		public void run() {
			if (_field.isDisposed() == false) {
				_field.getDisplay().syncExec(new Runnable() {
					public void run() {
						try {
							_index = 0;
							_task = null;
							if (_stateChangedListener != null) {
								_stateChangedListener
										.stateChanged(getTextInBox());
							}
						} catch (Exception e) {

						}
					}
				});
			}
		}
	}

}
