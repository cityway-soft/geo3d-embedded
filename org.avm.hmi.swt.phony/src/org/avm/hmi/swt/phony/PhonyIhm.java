package org.avm.hmi.swt.phony;


import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Gauge;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class PhonyIhm extends Composite implements SelectionListener,
		KeyboardListener, CallAnswerSelector {

	private Display _display;

	private Composite _compositeContacts = null;

	private Composite _compositeControls = null;

	private Button _hangupButton = null;

	private Button _answerButton = null;

	private Font _fontButton;

	private Logger _log;

	private int _ringing;

	private Label _statusLabel = null;

	private PhonyIhm _instance;

	private Gauge _volumeSlider;

	private Composite _keyboardPanel;

	private Keyboard _keyb;

	private ContactView _contactView;

	private Phony _phony;

	private AnswerSelectionListener _answerSelectionListener;

	private SelectionListener _previousListener;

	private GridData _contactGridData;

	private GridData _keyboardGridData;

	private int _currentVolume;

	public PhonyIhm(Composite parent, int style) {
		super(parent, style);
		_instance = this;
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_display = parent.getDisplay();
		initialize();
	}

	public void setEnabled(final boolean b) {
		_display.syncExec(new Runnable() {

			public void run() {
				if (_contactView != null) {
					_contactView.setEnabled(b);
				}
				if (_keyb != null) {
					_keyb.setEnabled(b);
				}
				_compositeControls.setEnabled(b);
				_hangupButton.setEnabled(b);
				_answerButton.setEnabled(b);
				if (b == false) {
					_statusLabel.setText(Messages
							.getString("PhonyIhm.non-disponible"));
				} else {
					_statusLabel.setText("OK!");
				}
			}
		});
	}

	private void initialize() {
		_answerSelectionListener = new AnswerSelectionListener();

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		this.setBackground(DesktopStyle.getBackgroundColor());
		_fontButton =DesktopImpl.getFont( 10, SWT.NORMAL); //$NON-NLS-1$
		createCompositeContacts();
		createCompositeControls();

		_statusLabel = new Label(this, SWT.NONE);
		_statusLabel.setText("---"); //$NON-NLS-1$
		GridData gridData4 = new GridData();
		gridData4.horizontalSpan = 3;
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.verticalAlignment = GridData.CENTER;
		gridData4.heightHint = 30;
		gridData4.grabExcessHorizontalSpace = true;
		_statusLabel.setLayoutData(gridData4);
		_statusLabel.setFont(_fontButton);
		_statusLabel.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		this.layout();

		hangup();
	}

	/**
	 * This method initializes compositeContacts
	 * 
	 */
	private void createCompositeContacts() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.verticalAlignment = GridData.FILL;
		gridData.widthHint = 40;
		_volumeSlider = new Gauge(this, SWT.NONE);
		_volumeSlider.setSteps(20);
		_volumeSlider.setMaximum(100);
		_volumeSlider.setMinimum(0);
		_volumeSlider.setBackground(DesktopStyle.getBackgroundColor());
		_volumeSlider.setLayoutData(gridData);
		_volumeSlider.addSelectionListener(this);
		_volumeSlider.setToolTipText("Volume");
		_volumeSlider.setTitle("vol");

		_contactGridData = new GridData();
		_contactGridData.horizontalAlignment = GridData.FILL;
		_contactGridData.grabExcessHorizontalSpace = true;
		_contactGridData.verticalAlignment = GridData.FILL;
		_contactGridData.grabExcessVerticalSpace = true;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		_compositeContacts = new Composite(this, SWT.NONE);
		_compositeContacts.setBackground(DesktopStyle.getBackgroundColor());
		_compositeContacts.setLayoutData(_contactGridData);
		_compositeContacts.setLayout(gridLayout);

		_keyboardGridData = new GridData();
		_keyboardGridData.widthHint = 0;
		_keyboardGridData.heightHint = 0;
		_keyboardPanel = new Composite(this, SWT.NONE);
		_keyboardPanel.setBackground(DesktopStyle.getBackgroundColor());
		_keyboardPanel.setLayoutData(_keyboardGridData);
		_keyboardPanel.setLayout(new GridLayout());

		this.layout();
	}

	public void activateKeyboard(final boolean activate) {
		_display.syncExec(new Runnable() {

			public void run() {
				if (activate) {
					_keyboardGridData.horizontalAlignment = GridData.FILL;
					_keyboardGridData.grabExcessHorizontalSpace = true;
					_keyboardGridData.grabExcessVerticalSpace = true;
					_keyboardGridData.verticalAlignment = GridData.FILL;
					_keyboardGridData.widthHint = SWT.DEFAULT;

					_contactGridData.widthHint = 250;
					_contactGridData.horizontalAlignment = GridData.BEGINNING;
					_contactGridData.grabExcessHorizontalSpace = false;

					if (_keyb == null || _keyb.isDisposed()) {
						_keyb = new Keyboard(_keyboardPanel, SWT.NONE);
					}
					_keyb.setListener(_instance);
					_keyb.setBackground(DesktopStyle.getBackgroundColor());
					GridData gridData = new GridData();
					gridData.horizontalAlignment = GridData.CENTER;
					gridData.grabExcessHorizontalSpace = true;
					gridData.grabExcessVerticalSpace = true;
					gridData.verticalAlignment = GridData.FILL;
					_keyb.setLayoutData(gridData);

					_keyboardPanel.layout();
					_contactView.layout();
					_instance.layout();
				} else {
					_keyboardGridData.widthHint = 0;
					_keyboardGridData.heightHint = 0;
					_keyboardGridData.horizontalAlignment = GridData.BEGINNING;
					_keyboardGridData.grabExcessHorizontalSpace = false;
					_keyboardGridData.verticalAlignment = GridData.BEGINNING;
					_keyboardGridData.grabExcessVerticalSpace = false;

					_contactGridData.horizontalAlignment = GridData.FILL;
					_contactGridData.grabExcessHorizontalSpace = true;

					if (_keyb != null && _keyb.isDisposed() == false) {
						_keyb.dispose();
						_keyb = null;
					}

					_instance.layout();
				}
			}
		});
	}

	/**
	 * This method initializes compositeControls
	 * 
	 */
	private void createCompositeControls() {

		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = (Desktop.DEFAULT_FONTSIZE + 6) * 5;
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		_compositeControls = new Composite(this, SWT.NONE);
		_compositeControls.setLayoutData(gridData);
		_compositeControls.setLayout(gridLayout2);
		_compositeControls.setBackground(DesktopStyle.getBackgroundColor());

		GridData gridData31 = new GridData();
		gridData31.grabExcessHorizontalSpace = true;
		gridData31.verticalAlignment = GridData.FILL;
		gridData31.grabExcessVerticalSpace = true;
		gridData31.horizontalAlignment = GridData.FILL;
		_answerButton = new Button(_compositeControls, SWT.NONE);
		_answerButton.setFont(_fontButton);
		_answerButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_GREEN));
		_answerButton.setText(Messages.getString("PhonyIhm.answer")); //$NON-NLS-1$
		// _answerButton.setImage(imgAnswer);
		// _answerButton.setBackground(Display.getCurrent().getSystemColor(
		// DesktopConfig.getBackgroundColor()));
		_answerButton.setLayoutData(gridData31);

		setMode(ANSWER_MODE, _answerSelectionListener);

		GridData gridData21 = new GridData();
		gridData21.horizontalAlignment = GridData.FILL;
		gridData21.grabExcessHorizontalSpace = true;
		gridData21.grabExcessVerticalSpace = true;
		gridData21.verticalAlignment = GridData.FILL;
		_hangupButton = new Button(_compositeControls, SWT.NONE);
		_hangupButton.setFont(_fontButton);
		_hangupButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_RED));
		_hangupButton.setText(Messages.getString("PhonyIhm.hangup")); //$NON-NLS-1$
		// _hangupButton.setImage(imgHangup);
		// _hangupButton.setBackground(Display.getCurrent().getSystemColor(
		// DesktopConfig.getBackgroundColor()));
		_hangupButton.setLayoutData(gridData21);
		_hangupButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					_log.debug("call phony hangup...");
					_phony.hangup();
				} catch (Exception e) {
					_log.error(e);
				}
			}
		});

		_compositeControls.layout();
	}

	private boolean isAlive() {
		return (getParent().isDisposed() == false);
	}

	public void ringing(final String phonenumber) {
		_ringing++;

		_display.syncExec(new Runnable() {

			public void run() {
				if (isAlive()) {
					setMode(ANSWER_MODE, _answerSelectionListener);
					_statusLabel.setText(Messages
							.getString("PhonyIhm.sonnerie")); //$NON-NLS-1$
					_answerButton.setEnabled(true);

					if (_ringing == 1
							|| (phonenumber != null && phonenumber.trim()
									.length() != 0)) {
						_contactView.ringing(phonenumber);
					}

					for (int i = 0; i < 4; i++) {
						if (i % 2 == 0) {
							_statusLabel.setBackground(_display
									.getSystemColor(SWT.COLOR_YELLOW));
						} else {
							_statusLabel.setBackground(_display
									.getSystemColor(SWT.COLOR_WHITE));
						}
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
						}
					}
					_instance.layout();
				}

			}
		});
	}

	public void hangup() {
		_ringing = 0;
		_display.asyncExec(new Runnable() {
			public void run() {
				try {
					if (isAlive()) {
						if (_contactView != null) {
							_contactView.hangup();
							_contactView.setEnabled(true);
						}
						_statusLabel.setText(""); //$NON-NLS-1$
						_statusLabel.setBackground(_display
								.getSystemColor(SWT.COLOR_WHITE));
						_answerButton.setBackground(DesktopStyle
								.getBackgroundColor());
						setMode(ANSWER_MODE, _answerSelectionListener);
						layout();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void online() {
		_ringing = 0;

		_display.asyncExec(new Runnable() {
			public void run() {
				try {
					if (isAlive()) {
						_contactView.online();
						_statusLabel.setText(Messages
								.getString("PhonyIhm.en-ligne")); //$NON-NLS-1$
						_statusLabel.setBackground(_display
								.getSystemColor(SWT.COLOR_WHITE));
						_contactView.setEnabled(false);
						_hangupButton.setEnabled(true);
						layout();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void dialing() {
		_ringing = 0;
		_display.syncExec(new Runnable() {
			public void run() {
				try {
					if (isAlive()) {
						_statusLabel.setText(Messages
								.getString("PhonyIhm.appel-en-cours")); //$NON-NLS-1$
						_contactView.setEnabled(false);
						_contactView.dialing();

						_statusLabel.setBackground(_display
								.getSystemColor(SWT.COLOR_WHITE));
						layout();
					}

				} catch (Exception e) {
					_log.error(e);
				}
			}
		});
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Gauge slider = ((Gauge) e.getSource());
		int value = slider.getSelection();
		int val = value > 95 ? 100 : value;
		setVolume(val);
		_phony.setVolume(val);
		if (_log.isDebugEnabled()) {
			_log.debug("set volume to " + val);
		}
	}

	public int getVolume() {
		return _currentVolume;
	}

	public void setVolume(final int volume) {
		_currentVolume = volume;
		_display.asyncExec(new Runnable() {
			public void run() {
				_volumeSlider.setSelection(volume);
			}
		});

	}

	public void validation(String str) {
		_phony.dial(str);
	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (_fontButton != null) {
			_fontButton.dispose();
			_fontButton = null;
		}
	}

	public void update(final ContactModel model) {
		_log.debug("Contact model size=" + model.size());
		_display.asyncExec(new Runnable() {
			public void run() {
				try {

					if (model.size() < 6) {
						if (!(_contactView instanceof ButtonContactView)) {
							if (_contactView != null) {
								_contactView.dispose();
							}
							_contactView = new ButtonContactView(
									_compositeContacts, SWT.NONE);
							_contactView.setBackground(DesktopStyle
									.getBackgroundColor());
						}
					} else {
						if (!(_contactView instanceof ListContactView)) {
							if (_contactView != null) {
								_contactView.dispose();
							}
							_contactView = new ListContactView(
									_compositeContacts, SWT.NONE);
							_contactView.setBackground(DesktopStyle
									.getBackgroundColor());
						}
					}
					_contactView.setModeSelector(_instance);
					_contactView.setFont(_fontButton);

					GridData gridData = new GridData();
					gridData.horizontalAlignment = GridData.FILL;
					gridData.grabExcessHorizontalSpace = true;
					gridData.grabExcessVerticalSpace = true;
					gridData.verticalAlignment = GridData.FILL;

					GridLayout gridLayout = new GridLayout();
					gridLayout.numColumns = 1;
					_contactView.setLayoutData(gridData);

					_contactView.setLayout(gridLayout);
					_contactView.update(model);
					_contactView.setPhony(_phony);
					_compositeContacts.layout();
					_log.debug("Update done!");
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}

	public void setPhony(Phony phony) {
		_phony = phony;
	}

	public void setMode(int mode, SelectionListener listener) {
		if (mode == ANSWER_MODE) {
			_answerButton.setText(Messages.getString("PhonyIhm.answer"));
			_answerButton.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_BLUE));
			_answerButton.setEnabled(false);
		} else {
			_answerButton.setText(Messages.getString("PhonyIhm.call"));
			_answerButton.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_GREEN));
			_answerButton.setEnabled(true);
		}
		if (_previousListener != listener) {
			if (_previousListener != null) {
				_answerButton.removeSelectionListener(_previousListener);
			}
			_answerButton.addSelectionListener(listener);
			_previousListener = listener;
		}

	}

	public class AnswerSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			try {
				_phony.answer();
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
