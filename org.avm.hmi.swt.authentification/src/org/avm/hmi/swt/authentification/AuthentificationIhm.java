package org.avm.hmi.swt.authentification;

import org.avm.elementary.useradmin.UserSessionService;
import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class AuthentificationIhm implements Authentification, SelectionListener {

	protected static final String NAME = Messages
			.getString("AuthentificationIhm.prise-de-poste"); //$NON-NLS-1$

	private Desktop _desktop;

	private MatriculeSelection _matriculeSelection;

	private PasswordSelection _passwordSelection;

	private Composite _activeIHM;

	private Composite _loginPanel;

	private Button _logoutButton;

	private Composite _logoutPanel;

	private ChoiceListener _listener;

	private AuthentificationIhm _instance;

	private UserSessionService _usersession;

	private StackLayout _stacklayout;

	private Font _font;

	public AuthentificationIhm(Desktop desktop) {
		_desktop = desktop;
		_instance = this;
	}

	/**
	 * This method initializes auth
	 */
	private void activateLoginPanel() {
		_desktop.getDisplay().asyncExec(new Runnable() {

			public void run() {
				if (_loginPanel == null) {
					GridData gridData = new GridData();
					gridData.verticalAlignment = GridData.FILL;
					gridData.grabExcessVerticalSpace = true;

					_loginPanel = new Composite(_desktop.getMainPanel(),
							SWT.NONE);
					_loginPanel.setLayoutData(gridData);
					_stacklayout = new StackLayout();
					_loginPanel.setLayout(_stacklayout);
					_loginPanel.setBackground(DesktopStyle.getBackgroundColor());

				}
				_desktop.addTabItem(NAME, _loginPanel, 0);
				_desktop.activateItem(NAME);
				_desktop.setFavorite(NAME);
				activateMatricule();
				_desktop.getRightPanel().layout();
			}
		});
	}

	public void activateLogoutPanel(final String matricule, final String name) {
		_desktop.getDisplay().asyncExec(new Runnable() {

			public void run() {

				if (_logoutPanel == null) {
					_logoutPanel = new Composite(_desktop.getRightPanel(),
							SWT.NONE | SWT.BORDER);
					GridData gridData = new GridData();
					gridData.horizontalAlignment = GridData.FILL;
					gridData.grabExcessHorizontalSpace = true;
					GridLayout layout = new GridLayout();
					layout.marginWidth = 0;
					layout.marginHeight = 0;
					_logoutPanel.setLayout(layout);
					_logoutPanel.setLayoutData(gridData);
					_logoutPanel.setBackground(_desktop.getDisplay()
							.getSystemColor(SWT.COLOR_YELLOW));

					_logoutButton = new Button(_logoutPanel, SWT.NONE);
					Font font = DesktopImpl.getFont(5, SWT.NORMAL);
					_logoutButton.setFont(font);
					_logoutButton.setText(Messages
							.getString("AuthentificationIhm.fin-de-poste")); //$NON-NLS-1$
					_logoutButton.setBackground(_desktop.getDisplay()
							.getSystemColor(SWT.COLOR_YELLOW));

					_logoutButton.addSelectionListener(_instance);
					gridData = new GridData();
					gridData.heightHint = 40;
					gridData.horizontalAlignment = GridData.FILL;
					gridData.grabExcessHorizontalSpace = true;
					gridData.verticalAlignment = GridData.FILL;
					gridData.grabExcessVerticalSpace = true;
					_logoutButton.setLayoutData(gridData);

					_logoutPanel.layout();
					_desktop.getRightPanel().layout();
				} else {

					_logoutPanel.setVisible(true);
				}
				String text = matricule;
				if (name != null) {
					text += "/ " + name;
				}
				_logoutButton.setToolTipText(text);
			}
		});

	}

	private void disposePreviousIHM() {

	}

	private void activeCurrentIHM() {
		_stacklayout.topControl = _activeIHM;
		_loginPanel.layout();
	}

	public void setErrorMessage(final String message, final int type) {
		_desktop.getDisplay().asyncExec(new Runnable() {
			public void run() {
				_desktop.setMessageBox(
						Messages.getString("AuthentificationIhm.prise-de-poste"), message, type); //$NON-NLS-1$
				_loginPanel.layout();
			}
		});
	}

	public void activateMatricule() {
		_desktop.getDisplay().asyncExec(new Runnable() {

			public void run() {
				disposePreviousIHM();

				if (_matriculeSelection == null) {
					_matriculeSelection = new MatriculeSelection(_loginPanel,
							SWT.NONE);

					_matriculeSelection.setSelectionListener(_listener);

				}
				_matriculeSelection.reset();
				_activeIHM = _matriculeSelection;
				activeCurrentIHM();

			}

		});
	}

	public void activatePassword() {
		_desktop.getDisplay().asyncExec(new Runnable() {
			public void run() {
				disposePreviousIHM();
				if (_passwordSelection == null) {
					_passwordSelection = new PasswordSelection(_loginPanel,
							SWT.NONE);
					_passwordSelection.setSelectionListener(_listener);
					//
					// _desktop.getMiddlePanel().layout();

				}
				_passwordSelection.reset();
				_activeIHM = _passwordSelection;
				activeCurrentIHM();
			}
		});
	}

	public void setChoiceListener(ChoiceListener listener) {
		_listener = listener;
	}

	public void stop() {
		_desktop.getDisplay().asyncExec(new Runnable() {

			public void run() {
				if (_loginPanel != null) {
					_loginPanel.dispose();
					_loginPanel = null;
				}
				if (_logoutPanel != null) {
					_logoutPanel.dispose();
					_logoutPanel = null;
				}
				_desktop.removeTabItem(NAME);
			}

		});
	}

	public void loggedOn(final String matricule, final String name) {
		_desktop.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (_loginPanel != null) {
					_desktop.removeTabItem(NAME);
				}
				activateLogoutPanel(matricule, name);
			}
		});
	}

	public void loggedOut() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (_logoutPanel != null) {
					_logoutPanel.setVisible(false);
					_desktop.setInformation(""); //$NON-NLS-1$
				}
				activateLoginPanel();
			}
		});
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	public void widgetSelected(SelectionEvent arg0) {
		System.out
				.println("****[AuthentificationIhm] buttonSelection => logout [usersession service="
						+ _usersession + "]");
		if (_usersession != null) {
			_usersession.logout();
			// loggedOut();
		}
	}

	public void setUserSession(UserSessionService us) {
		_usersession = us;
	}

	public void setEnabled(final boolean b) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (_matriculeSelection != null
						&& _matriculeSelection.isDisposed() == false) {
					_matriculeSelection.setEnabled(b);
				}
				if (_passwordSelection != null
						&& _passwordSelection.isDisposed() == false) {
					_passwordSelection.setEnabled(b);
				}
			}
		});
	}


}
