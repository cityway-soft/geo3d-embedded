package org.avm.hmi.swt.application.splash;

import java.io.File;
import java.util.NoSuchElementException;

import org.avm.hmi.swt.application.display.AVMDisplay;
import org.avm.hmi.swt.application.display.Application;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class SplashIhm implements Splash, MouseListener {

	private static final boolean DEBUG = false;

	private Shell _shell = null; // @jve:decl-index=0:visual-constraint="9,8"

	private ProgressBar _progressBar = null;

	private Label _labelSplashImage = null;

	private Label _labelTitle = null;

	private Label _labelTask = null;

	protected int _max;

	private Label _labelVehicule;

	private Label _labelExploitant;

	private Display _display;

	private Image _imageDefault;

	private Image _imageWarning;

	private SplashIhm _instance;

	private Font _fontLabelTask;
	private Font _fontLabelTitle;
	private Font _fontLabelVehicule;
	private Font _fontLabelExploitant;

	private int _mouseY;

	public SplashIhm() {
		_instance = this;
	}

	public void start() {
		open();
	}

	public void stop() {
		close();
	}

	/**
	 * This method initializes sShell
	 */
	private void open() {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {

			public void run() {
				// fenetre splash
				GridLayout gridLayout = new GridLayout();
				gridLayout.verticalSpacing = 0;
				gridLayout.marginWidth = 1;
				gridLayout.marginHeight = 1;
				gridLayout.horizontalSpacing = 0;
				_shell = new Shell(_display, SWT.ON_TOP | SWT.BORDER);
				_shell.setText("Splash AVM"); //$NON-NLS-1$
				_shell.setBackground(_display.getSystemColor(SWT.COLOR_WHITE));
				_shell.setLayout(gridLayout);

				// logo
				_labelSplashImage = new Label(_shell, SWT.CENTER);
				_labelSplashImage.setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));
				_labelSplashImage.addMouseListener(_instance);
				Image img = getDefaultImage();
				if (img != null) {
					_labelSplashImage.setImage(img);
				}
				GridData gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.verticalAlignment = GridData.FILL;
				_labelSplashImage.setLayoutData(gridData);

				// id vehicule
				_labelVehicule = new Label(_shell, SWT.NONE);
				_labelVehicule.setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));
				_labelVehicule
						.setText(Messages.getString("SplashIhm.vehicule") + " " + System.getProperty("org.avm.terminal.name", //$NON-NLS-1$
												"???")); //$NON-NLS-1$
				_fontLabelVehicule = Application.getFont( 20, SWT.NORMAL);
				_labelVehicule.setFont(_fontLabelVehicule);

				_labelVehicule.setAlignment(SWT.CENTER);
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.heightHint = 40;
				gridData.verticalAlignment = GridData.CENTER;
				_labelVehicule.setLayoutData(gridData);

				// id exploitant
				_labelExploitant = new Label(_shell, SWT.NONE);
				_labelExploitant.setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));

				_labelExploitant
						.setText(System.getProperty(
								"org.avm.exploitation.name", //$NON-NLS-1$
								"") + " (" + System.getProperty("org.avm.terminal.owner", //$NON-NLS-1$
												"???") + ")"); //$NON-NLS-1$
				_fontLabelExploitant =  Application.getFont( 5, 
						SWT.NORMAL);
				_labelExploitant.setFont(_fontLabelExploitant);
				_labelExploitant.setAlignment(SWT.CENTER);
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.heightHint = 20;
				gridData.verticalAlignment = GridData.CENTER;
				_labelExploitant.setLayoutData(gridData);

				// id titre ("demarrage du systeme...")
				_labelTitle = new Label(_shell, SWT.NONE);
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				gridData.heightHint = 30;
				_labelTitle.setLayoutData(gridData);
				_labelTitle
						.setText(Messages.getString("SplashIhm.demarrage") + getVersion()); //$NON-NLS-1$
				_fontLabelTitle =  Application.getFont( 4, //$NON-NLS-1$
						SWT.NORMAL);
				_labelTitle.setFont(_fontLabelTitle);
				_labelTitle.setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));

				// progressbar
				_progressBar = new ProgressBar(_shell, SWT.NONE);
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				gridData.heightHint = 15;
				gridData.verticalAlignment = GridData.END;
				_progressBar.setLayoutData(gridData);
				_progressBar.setMaximum(_max);

				// nom des service en cours de demarrage
				_labelTask = new Label(_shell, SWT.NONE);
				_labelTask.setText(""); //$NON-NLS-1$
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessVerticalSpace = false;
				gridData.heightHint = 30;
				gridData.horizontalAlignment = GridData.FILL;
				_labelTask.setLayoutData(gridData);
				_labelTask.setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));
				_fontLabelTask =  Application.getFont( 3, //$NON-NLS-1$
						SWT.NORMAL);
				_labelTask.setFont(_fontLabelTask);

				 setGeometry(System.getProperty("org.avm.hmi.swt.geometry"));

				_shell.open();
//				_shell.pack();
//				 setGeometry(System.getProperty("org.avm.hmi.swt.geometry"));
//				 System.out.println("2-Splash Window Size: " +
//						 _shell.getSize().x
//						 + " x " + _shell.getSize().y + " - " + _shell.getLocation());

			}
		});
	}

	private void close() {
		_display.asyncExec(new Runnable() {
			public void run() {
				if (_shell != null && _shell.isDisposed() == false) {
					_shell.close();
				}
			}
		});
	}

	public void setDefaultSplashImage() {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {
			public void run() {
				if (isShellDisposed() == false) {
					if (_labelSplashImage != null) {
						Image img = getDefaultImage();
						_labelSplashImage.setImage(img);
						//getShell().layout();
					}
				}
			}
		});
	}

	public void setManagementSplashImage() {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {
			public void run() {
				if (isShellDisposed() == false) {
					Image image = _labelSplashImage.getImage();
					Image img = getWarningImage();
					if (image != img) {
						_labelTitle.setText(Messages
								.getString("SplashIhm.maintenance")); //$NON-NLS-1$
						_labelSplashImage.setImage(img);
						//getShell().layout();
					}
				}
			}
		});
	}

	public String getVersion() {
		String v = System.getProperty("org.avm.version");
		String result = "";
		if (v != null) {
			result = "   (v" + v + ")";
		}
		return result;
	}

	public void setProgressBarMax(final int val) {
		if (_display == null)
			return;
		_max = val;
		_display.asyncExec(new Runnable() {
			public void run() {
				if (isShellDisposed() == false) {
					if (_progressBar != null) {
						_progressBar.setMaximum(_max);
						_progressBar.setSelection(0);
					}
				}
			}
		});

	}

	public void setTask(final String task) {
		if (_display == null)
			return;
		_display.asyncExec(new Runnable() {
			public void run() {
				debug("Settask " + task + " ...");
				if (isShellDisposed() == false) {
					if (_labelTask != null) {
						_labelTask.setText(task);
					}
					if (_progressBar != null) {
						int val = _progressBar.getSelection() + 1;
						_progressBar.setSelection(val);
					}
				}
				debug("Settask " + task + " done !");
			}
		});
	}

	public boolean isVisible() {
		return (_shell != null && _shell.isVisible());
	}

	private Image getWarningImage() {
		if (_display == null)
			return null;
		if (_imageWarning == null) {
			_imageWarning = new Image(_display, getClass().getResourceAsStream(
					"/resources/warning-splash.jpg")); //$NON-NLS-1$
		}
		return _imageWarning;
	}

	private Image getDefaultImage() {
		String imgSource = null;
		try {
			if (_display == null)
				return null;
			if (_imageDefault == null) {

				String filename = System.getProperty("org.avm.home")
						+ "/data/splash.jpg";
				File file = new File(filename);
				if (file.exists()) {
					_imageDefault = new Image(_display, filename); //$NON-NLS-1$
				} else {
					_imageDefault = new Image(_display, getClass()
							.getResourceAsStream("/resources/splash.jpg")); //$NON-NLS-1$
				}

			}
		} catch (Throwable e) {
			System.err.println("Error for default image : " + imgSource);
			e.printStackTrace();
		}
		return _imageDefault;
	}

	public void setVisible(final boolean visible) {
		_display.asyncExec(new Runnable() {

			public void run() {
				if (_shell != null && !_shell.isDisposed()
						&& _shell.isVisible() != visible) {
					_shell.setVisible(visible);

				}
			}
		});
	}

	/**
	 * geometry <width>x<height>[+-]<x>[+-]<y>
	 * 
	 * @param geometry
	 */
	 private void setGeometry(String geometry) {
		Point size, pos;

		size = new Point(0, 0);
		pos = new Point(0, 0);
		Rectangle screen = _display.getClientArea();
		try {
			int[] val;

			val = new int[4];
			StringBuffer buf = new StringBuffer();
			int ii = 0;
			boolean rightOrigin = true, topOrigin = true;
			for (int i = 0; i < geometry.length(); i++) {
				char c = geometry.charAt(i);
				if (Character.isDigit(c)) {
					buf.append(c);
				} else {
					switch (c) {
					case 'x':
						break;
					case '+':
						if (ii == 1) {
							rightOrigin = true;
						} else if (ii == 2) {
							topOrigin = true;
						}
						break;
					case '-':
						if (ii == 1) {
							rightOrigin = false;
						} else if (ii == 2) {
							topOrigin = false;
						}
						break;

					default:
						throw new NoSuchElementException("separator: " + c);
					}
					val[ii] = Integer.parseInt(buf.toString());
					buf = new StringBuffer();
					ii++;
				}

			}
			val[ii] = Integer.parseInt(buf.toString());
			double ratio = 1.0;
			size.x = (int) (val[0] * ratio);
			size.y = (int) (val[1] * ratio);
			pos.x = (rightOrigin) ? (val[2] + (val[0] - size.x) / 2)
					: (screen.width - val[2] + (val[0] - size.x) / 2);
			pos.y = (topOrigin) ? (val[3] + (val[1] - size.y) / 2)
					: (screen.height - val[3] + (val[1] - size.y) / 2);

		} catch (Exception e) {
			e.printStackTrace();
			size.x = screen.width / 3;
			size.y = screen.height / 3;// !!!
			pos.x = (screen.width - size.x) / 2;
			pos.y = (screen.height - size.y) / 2;
		}
		_shell.setLocation(pos);
		size.x+=4;
		size.y+=4;
		_shell.setSize(size);
	}

	private boolean isShellDisposed() {
		Shell shell = getShell();
		if (shell != null) {
			return shell.isDisposed();
		} else {
			return true;
		}
	}

	public Shell getShell() {
		return _shell;
	}

	protected boolean isDisposed() {
		return _shell.isDisposed();
	}

	private void debug(String debug) {
		if (DEBUG) {
			System.out.println(debug);
		}
	}

	public void mouseDoubleClick(MouseEvent arg0) {

	}

	public void mouseDown(MouseEvent event) {
		_mouseY = event.y;
	}

	public void mouseUp(MouseEvent event) {
		int delta = event.y - _mouseY;
		if (delta > 200) {
			close();
		}
	}

//	protected void finalize() throws Throwable {
//		super.finalize();
//		if (_fontLabelTask != null) {
//			_fontLabelTask.dispose();
//			_fontLabelTask = null;
//		}
//		if (_fontLabelTitle != null) {
//			_fontLabelTitle.dispose();
//			_fontLabelTitle = null;
//		}
//		if (_fontLabelVehicule != null) {
//			_fontLabelVehicule.dispose();
//			_fontLabelVehicule = null;
//		}
//		if (_fontLabelExploitant != null) {
//			_fontLabelExploitant.dispose();
//			_fontLabelExploitant = null;
//		}
//	}

	public void setDisplay(Display display) {
		_display = display;
	}
}
