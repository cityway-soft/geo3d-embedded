package org.avm.hmi.swt.avm;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.avm.business.core.Avm;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.MessageBox;
import org.avm.hmi.swt.desktop.StateButton;
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

public class AvmIhm extends Composite { // implements ChoiceListener {

	private Button _finServiceButton;

	private Button _finCourseButton;

	private Button _sortieItineraireButton;

	// private Label _avance_retard;

	private Desktop _desktop;

	private JourneySelection _journeySelection;

	private StartJourney _startJourney;

	private ServiceSelection _serviceSelection;

	private StartJourney _depart;

	private FollowJourney _suiviCourse;

	private Composite _compositePanels;

	private Composite _compositeButtons;

	private Composite _activeIHM;

	private Font _font;

	private org.avm.business.core.Avm _avm; // @jve:decl-index=0:

	private boolean _demoMode;

	private StateButton _simulationButton;

	private SimulationTask _simulationTask;

	private Timer _simulationTimer;

	private int _demoPeriode = 12;

	private static final int BUTTON_HEIGHT = 50;

	private Logger _log = Logger.getInstance(this.getClass().getName());

	private boolean _isGeorefRole;

	private StackLayout _stacklayout;

	private AvmIhm _instance = this;

	public AvmIhm(Composite parent, int style) {
		super(parent, style);
		_log.debug("Creation...");
		try {
			// _log.setPriority(Priority.DEBUG);

			_font = DesktopImpl.getFont(6, SWT.NORMAL);
			initialize();
		} catch (Throwable t) {
			t.printStackTrace();
			_log.error("Erreur creation avmihm", t);
		}
		_log.debug("Create OK");
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		this.setBackground(DesktopStyle.getBackgroundColor());

		_compositeButtons = new Composite(this, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 1;
		_compositeButtons.setLayout(gridLayout);
		_compositeButtons.setBackground(DesktopStyle.getBackgroundColor());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = false;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalIndent = 2;
		_compositeButtons.setLayoutData(gridData);

		_compositePanels = new Composite(this, SWT.BORDER);
		_stacklayout = new StackLayout();
		_compositePanels.setLayout(_stacklayout);
		// gridLayout = new GridLayout();
		// gridLayout.numColumns = 1;
		// _compositePanels.setLayout(gridLayout);
		_compositePanels.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_compositePanels.setLayoutData(gridData);

	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (_serviceSelection != null
				&& _serviceSelection.isDisposed() == false) {
			_serviceSelection.setEnabled(b);
		}
		if (_journeySelection != null
				&& _journeySelection.isDisposed() == false) {
			_journeySelection.setEnabled(b);
		}
		if (_startJourney != null && _startJourney.isDisposed() == false) {
			_startJourney.setEnabled(b);
		}

		if (_suiviCourse != null && _serviceSelection.isDisposed() == false) {
			_suiviCourse.setEnabled(b);
		}

		if (_finServiceButton != null
				&& _finServiceButton.isDisposed() == false) {
			_finServiceButton.setEnabled(b);
		}

		if (_finCourseButton != null && _finCourseButton.isDisposed() == false) {
			_finCourseButton.setEnabled(b);
		}

		if (_simulationButton != null
				&& _simulationButton.isDisposed() == false) {
			_simulationButton.setEnabled(b);
		}

	}

	public void setBase(Desktop base) {
		_desktop = base;
		init();
	}

	// public void setFont(Font font) {
	// super.setFont(font);
	// _font = font;
	// }

	private void init() {
		refresh(_compositeButtons);
	}

	private void activeCurrentIHM() {
		_stacklayout.topControl = _activeIHM;
		_compositePanels.layout();
	}

	public void activateDepart(final Course course) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				activateFinServiceButton(true);
				activateFinCourseButton(true);
				activateSortieItineraireButton(false);
				// activateAvanceRetardPanel(false);
				activateDemoButton(false);

				removeSortieItineraireButton();
				if (_depart == null) {
					_depart = new StartJourney(_compositePanels, SWT.NONE);
					GridData gridData = new GridData();
					gridData.horizontalAlignment = GridData.FILL;
					gridData.grabExcessHorizontalSpace = true;
					gridData.verticalAlignment = GridData.CENTER;
					_depart.setLayoutData(gridData);
					_depart.setSelectionListener(new ChoiceListener() {
						public void validation(Object obj, Object data) {
							_avm.depart();
						}
					});
				}
				_depart.update(course);

				_activeIHM = _depart;
				activeCurrentIHM();

				refresh(_compositePanels);
				refresh(_compositeButtons);
			}
		});
	}

	public void activateInitial() {
		Display.getDefault().asyncExec(new Runnable() {
			private ReplacementSelection _replacementSelection;

			public void run() {
				if (_compositePanels != null && !_compositePanels.isDisposed()) {
					GridData gridData;
					gridData = new GridData();
					gridData.heightHint = 0;
					_compositeButtons.setLayoutData(gridData);

					activateFinServiceButton(false);
					activateFinCourseButton(false);
					activateSortieItineraireButton(false);
					// activateAvanceRetardPanel(false);
					activateDemoButton(false);

					if (_replacementSelection == null) {
						_replacementSelection = new ReplacementSelection(
								_compositePanels, SWT.NONE);
						gridData = new GridData();
						gridData.grabExcessHorizontalSpace = true;
						gridData.grabExcessVerticalSpace = true;
						gridData.verticalAlignment = GridData.FILL;
						gridData.horizontalAlignment = GridData.FILL;
						_replacementSelection.setLayoutData(gridData);
						_replacementSelection
								.setSelectionListener(new ChoiceListener() {
									public void validation(Object obj,
											Object data) {
										int mat = _replacementSelection
												.getMatriculeRemplacement();
										int v = _replacementSelection
												.getVehiculeRemplacement();
										_avm.prisePoste(v, mat);
									}
								});
					}
					_activeIHM = _replacementSelection;
					activeCurrentIHM();
					String prenom = _avm.getModel().getAuthentification()
							.getPrenom();
					setMessage(Messages.getString("AvmImpl.bonjour")
							+ ((prenom != null) ? " " + prenom : ""));
					refresh(_compositePanels);
					refresh(_compositeButtons);
					layout();
				}
				_log.debug("AvmIhm.java activateInitial (asyncExec) : END");

			}

		});
	}

	public void activateServiceSpecial() {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				activateFinServiceButton(true);
				activateFinCourseButton(false);
				// activateAnnulerButton(false);
				activateSortieItineraireButton(false);
				// activateAvanceRetardPanel(false);
				activateDemoButton(false);

				_activeIHM = null;
				refresh(_compositePanels);
				refresh(_compositeButtons);
			}
		});
	}

	public void activateSaisieService() {
		if (_activeIHM == _serviceSelection && _activeIHM != null)
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (_compositePanels != null && !_compositePanels.isDisposed()) {
					GridData gridData = new GridData();
					gridData.heightHint = 0;
					_compositeButtons.setLayoutData(gridData);

					activateFinServiceButton(false);
					activateFinCourseButton(false);
					activateSortieItineraireButton(false);
					// activateAvanceRetardPanel(false);
					activateDemoButton(false);

					if (_serviceSelection == null) {
						_serviceSelection = new ServiceSelection(
								_compositePanels, SWT.NONE);
						_serviceSelection.setVersion(_avm.getModel()
								.getDatasourceVersion());
						gridData = new GridData();
						gridData.horizontalAlignment = GridData.CENTER;
						gridData.grabExcessHorizontalSpace = true;
						gridData.grabExcessVerticalSpace = true;
						gridData.verticalAlignment = GridData.FILL;
						_serviceSelection.setLayoutData(gridData);
						_serviceSelection
								.setSelectionListener(new ChoiceListener() {
									public void validation(Object obj,
											Object data) {
										_avm.priseService(((Integer) data)
												.intValue());
									}
								});
					}
					_serviceSelection.reset();
					_activeIHM = _serviceSelection;
					activeCurrentIHM();
					refresh(_compositePanels);
					refresh(_compositeButtons);
					layout();
				}
			}

		});
	}

	public void activateSaisieCourse(final ServiceAgent sa) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {

				if (_activeIHM != _journeySelection || _activeIHM == null) {
					GridData gridData;

					activateFinServiceButton(true);
					activateFinCourseButton(false);
					activateSortieItineraireButton(false);
					// activateAvanceRetardPanel(false);
					activateDemoButton(false);

					if (_journeySelection == null) {
						_journeySelection = new JourneySelection(
								_compositePanels, SWT.NONE);
						gridData = new GridData();
						gridData.horizontalAlignment = GridData.FILL;
						gridData.grabExcessHorizontalSpace = true;
						gridData.grabExcessVerticalSpace = true;
						gridData.verticalAlignment = GridData.FILL;
						_journeySelection.setLayoutData(gridData);
						_journeySelection
								.setSelectionListener(new ChoiceListener() {
									public void validation(Object obj,
											Object data) {
										_log.debug("AvmIhm Selected course: "
												+ data);
										_avm.priseCourse(((Integer) data)
												.intValue());
									}
								});
					}
					_activeIHM = _journeySelection;
					activeCurrentIHM();

				}

				_journeySelection.update(sa);

				layout();
				refresh(_compositePanels);
				refresh(_compositeButtons);

			}
		});
	}

	public void activateSuiviCourse(final Course course) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {

				activateFinServiceButton(true);
				activateFinCourseButton(true);
				activateSortieItineraireButton(true);
				activateDemoButton(true);

				if (_activeIHM != _suiviCourse || _activeIHM == null) {

					if (_suiviCourse == null) {
						if (course.getNombrePoint() == 0) {
							_suiviCourse = new FollowSimpleJourney(
									_compositePanels, SWT.NONE);
						} else {
							_suiviCourse = new FollowNormalJourney(
									_compositePanels, SWT.NONE);
						}
						_suiviCourse.setDesktop(_desktop);
						_suiviCourse.setAvm(_avm);
						_suiviCourse.setGeorefRole(_isGeorefRole);
						GridData gridData = new GridData();
						gridData.grabExcessVerticalSpace = true;
						gridData.horizontalAlignment = GridData.FILL;
						gridData.verticalAlignment = GridData.FILL;
						gridData.grabExcessHorizontalSpace = true;
						gridData.horizontalSpan = 2;

						_suiviCourse.setLayoutData(gridData);
						refresh(_compositePanels);
						refresh(_compositeButtons);

						_suiviCourse.activatePanel();
					}
					_activeIHM = _suiviCourse;
					activeCurrentIHM();
					_suiviCourse.updateMessage();
					setAvanceRetard(_avm.getModel().getAvanceRetard());
					_suiviCourse.setService(_avm.getModel().getServiceAgent());
					_suiviCourse.setCourse(_avm.getModel().getCourse());

				}

			}
		});
	}

	public void close() {
		if (isDisposed())
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				removeFinServiceButton();
				removeFinCourseButton();
				removeDemoButton();

				if (_journeySelection != null
						&& !_journeySelection.isDisposed()) {
					_journeySelection.dispose();
				}
				if (_startJourney != null && !_startJourney.isDisposed()) {
					_startJourney.dispose();
				}
				if (_serviceSelection != null
						&& !_serviceSelection.isDisposed()) {
					_serviceSelection.dispose();
				}
				if (_depart != null && !_depart.isDisposed()) {
					_depart.dispose();
				}
				if (_suiviCourse != null && !_suiviCourse.isDisposed()) {
					_suiviCourse.dispose();
				}

				if (_sortieItineraireButton != null
						&& !_sortieItineraireButton.isDisposed()) {
					_sortieItineraireButton.dispose();
					_sortieItineraireButton = null;
				}

			}
		});

	}

	private void refresh(Composite parent) {
		if (parent.isDisposed() == false) {
			parent.layout();
		}
	}

	public void setPoint(final Point point) {

		if (isDisposed())
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				_log.debug("AvmIhm setPoint : _suivicourse= " + _suiviCourse);
				if (_suiviCourse != null && _suiviCourse.isDisposed() == false) {
					System.err.println("Nom=" + point.getNom());
					_suiviCourse.setPoint(point);
				}

			}
		});

	}

	void setAvanceRetard(final int ar) {
		if (isDisposed())
			return;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				_log.debug("AvmIhm setAvanceRetard : _suivicourse= "
						+ _suiviCourse);
				if (_suiviCourse != null) {

					_suiviCourse.setAvanceRetard(ar);
					refresh(_suiviCourse);
				}

			}
		});

	}

	public void setAvm(Avm avm) {
		_avm = avm;
		if (_suiviCourse != null) {
			_suiviCourse.setAvm(_avm);
		}
		if (avm != null) {
			if (_serviceSelection != null) {
				_serviceSelection.setVersion(_avm.getModel()
						.getDatasourceVersion());
			}

		} else {
			if (_simulationTask != null) {
				_simulationTask.cancel();
				_simulationTask = null;
			}
		}

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				setEnabled(_avm != null);
			}
		});
	}

	public void setMessage(final String message) {
		_log.debug("AvmIhm setMessage");
		if (isDisposed())
			return;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				_desktop.setInformation(message);
			}
		});
	}

	public void setMessageBox(final String title, final String message,
			final int important) {
		_log.debug("AvmIhm setMessageBox");
		if (isDisposed())
			return;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				_desktop.setMessageBox(title, message, important);
				refresh(_compositePanels);
			}
		});
	}

	public void setHorsItineraire(boolean b) {
		_log.debug("AvmIhm.java setHorsItineraire");
		if (isDisposed())
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (_suiviCourse != null) {
					_suiviCourse.setHorsItineraire(true);
				}
			}
		});

	}

	/**
	 * Bouton de sortie d'itineraire
	 */
	protected void activateSortieItineraireButton(final boolean b) {
		if (isDisposed()) {
			return;
		}
		if (b == false) {
			removeSortieItineraireButton();
			return;
		}

		if (_sortieItineraireButton == null) {
			GridData gridData11 = new GridData();
			gridData11.horizontalAlignment = GridData.FILL;
			gridData11.grabExcessVerticalSpace = false;
			gridData11.grabExcessHorizontalSpace = true;
			gridData11.verticalAlignment = GridData.CENTER;
			gridData11.widthHint = -1;
			gridData11.heightHint = BUTTON_HEIGHT;
			// gridData11.verticalIndent = BUTTON_IDENT;
			_sortieItineraireButton = new Button(_compositeButtons, SWT.NONE);
			_sortieItineraireButton.setText(Messages
					.getString("AvmIhm.sortieItineraire")); //$NON-NLS-1$
			_sortieItineraireButton.setFont(_font);
			_sortieItineraireButton.setBackground(DesktopStyle
					.getBackgroundColor());
			_sortieItineraireButton.setLayoutData(gridData11);
			_sortieItineraireButton
					.addSelectionListener(new SelectionListener() {

						public void widgetSelected(SelectionEvent event) {
							if (_avm != null) {
								_avm.sortieItineraire();
								// dans l'etat 'hors itineraire', le bouton est
								// desactive
								_sortieItineraireButton.setEnabled(!_avm
										.getModel().isHorsItineraire());
							} else {
								_log.error("[IHMAAvm] service Avm not available."); //$NON-NLS-1$
							}
						}

						public void widgetDefaultSelected(SelectionEvent event) {

						}
					});
		} else {
			_sortieItineraireButton.setVisible(true);
		}

		// activation du bouton de mise en deviation si 'pas deja en deviation'
		// et 'vehicule entre deux arret'
		// _sortieItineraireButton
		// .setEnabled((_avm.getModel().isHorsItineraire() == false)
		// && (_avm.getModel().getState().getValue() ==
		// AvmModel.STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE));
		_sortieItineraireButton
				.setEnabled(_avm.getModel().isHorsItineraire() == false);

		layout();
	}

	private void removeSortieItineraireButton() {
		if (_sortieItineraireButton != null
				&& !_sortieItineraireButton.isDisposed()) {
			// _sortieItineraireButton.dispose();
			// _sortieItineraireButton = null;
			_sortieItineraireButton.setVisible(false);
		}
	}

	private void confirmFinService() {
		if (_avm != null) {
			ServiceAgent sag = _avm.getModel().getServiceAgent();
			boolean finished = true;

			if (sag != null) {
				finished = sag.isTermine();

				_log.info("Service Agent " + sag.getIdU() + " termine ? :"
						+ finished);
			}
			if (!finished) {
				MessageBox.setMessage(
						Messages.getString("AvmIhm.attention"), //$NON-NLS-1$
						Messages.getString("AvmIhm.confirmation-fin-service"), //$NON-NLS-1$
						MessageBox.MESSAGE_WARNING, SWT.CENTER,
						new SelectionListener() {

							public void widgetDefaultSelected(
									SelectionEvent arg0) {
							}

							public void widgetSelected(SelectionEvent arg0) {
								Button button = (Button) arg0.getSource();
								Boolean b = (Boolean) button.getData();
								if (b.booleanValue()) {
									_avm.finService();
								}
							}

						}, true);
			} else {
				_avm.finService();
			}
		} else {
			_log.error("[IHMAAvm] service Avm not available."); //$NON-NLS-1$
		}

	}

	private void confirmFinCourse() {
		if (_avm != null) {
			Course course = _avm.getModel().getCourse();
			boolean finished = true;

			if (course != null) {
				finished = course.isTerminee();

				_log.info("Course " + course.getIdu() + " terminee ? :"
						+ finished);
			}

			if (!finished) {
				MessageBox.setMessage(
						Messages.getString("AvmIhm.attention"), //$NON-NLS-1$
						Messages.getString("AvmIhm.confirmation-fin-course"), //$NON-NLS-1$
						MessageBox.MESSAGE_WARNING, SWT.CENTER,
						new SelectionListener() {

							public void widgetDefaultSelected(
									SelectionEvent arg0) {
							}

							public void widgetSelected(SelectionEvent arg0) {
								Button button = (Button) arg0.getSource();
								Boolean b = (Boolean) button.getData();
								if (b.booleanValue()) {
									_avm.finCourse();
								}
							}

						}, true);
			} else {
				_avm.finCourse();
			}
		} else {
			_log.error("[IHMAvm] service Avm not available."); //$NON-NLS-1$
		}

	}

	/**
	 * Bouton fin de service
	 */
	private void activateFinServiceButton(boolean b) {
		_log.debug("[AvmIHM] activateFinServiceButton(" + b + ")");
		if (b == false) {
			removeFinServiceButton();
			return;
		}
		if (_finServiceButton == null) {

			GridData gridData11 = new GridData();
			gridData11.horizontalAlignment = GridData.FILL;
			gridData11.grabExcessVerticalSpace = false;
			gridData11.grabExcessHorizontalSpace = true;
			gridData11.verticalAlignment = GridData.CENTER;
			gridData11.widthHint = -1;
			gridData11.heightHint = BUTTON_HEIGHT;
			_finServiceButton = new Button(_compositeButtons, SWT.NONE);
			_finServiceButton.setText(Messages.getString("AvmIhm.finService")); //$NON-NLS-1$
			_finServiceButton.setLayoutData(gridData11);
			_finServiceButton.setFont(_font);
			_finServiceButton.setBackground(DesktopStyle.getBackgroundColor());
			_finServiceButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent event) {
					confirmFinService();
				}

				public void widgetDefaultSelected(SelectionEvent event) {

				}
			});
		}
		GridData gridData = (GridData) _compositeButtons.getLayoutData();
		gridData.heightHint = BUTTON_HEIGHT;
		_log.debug("[AvmIHM] activateFinServiceButton(" + b
				+ ") : REUSE setVisible");
		_finServiceButton.setVisible(true);

		refresh(_compositeButtons);
		layout();
	}

	private void removeFinServiceButton() {
		if (_finServiceButton != null && !_finServiceButton.isDisposed()) {
			_finServiceButton.setVisible(false);
			// _finServiceButton.dispose();
			// _finServiceButton = null;
			// GridData gridData = (GridData) _compositeButtons.getLayoutData();
			// gridData.heightHint = 0;
		}
	}

	/**
	 * Bouton fin de course
	 */
	private void activateFinCourseButton(boolean b) {
		if (b == false) {
			removeFinCourseButton();
			return;
		}

		if (_finCourseButton == null) {

			GridData gridData11 = new GridData();
			gridData11.horizontalAlignment = GridData.FILL;
			gridData11.grabExcessVerticalSpace = false;
			gridData11.grabExcessHorizontalSpace = true;
			gridData11.verticalAlignment = GridData.CENTER;
			gridData11.widthHint = -1;
			gridData11.heightHint = BUTTON_HEIGHT;
			_finCourseButton = new Button(_compositeButtons, SWT.NONE);
			_finCourseButton.setText(Messages.getString("AvmIhm.finCourse")); //$NON-NLS-1$
			_finCourseButton.setFont(_font);
			_finCourseButton.setLayoutData(gridData11);
			_finCourseButton.setBackground(DesktopStyle.getBackgroundColor());
			_finCourseButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent event) {
					confirmFinCourse();
				}

				public void widgetDefaultSelected(SelectionEvent event) {

				}
			});
			layout();
		} else {
			_finCourseButton.setVisible(true);
		}
	}

	private void removeFinCourseButton() {
		if (_finCourseButton != null && !_finCourseButton.isDisposed()) {
			// _finCourseButton.dispose();
			// _finCourseButton = null;
			_finCourseButton.setVisible(false);
		}
	}

	/**
	 * Bouton Demo
	 */
	private void activateDemoButton(boolean b) {

		if (isDemoMode()) {

			if (b == false) {
				removeDemoButton();
				return;
			}
			if (_simulationButton == null) {

				GridData gridData11 = new GridData();
				gridData11.horizontalAlignment = GridData.FILL;
				gridData11.grabExcessVerticalSpace = false;
				gridData11.grabExcessHorizontalSpace = true;
				gridData11.verticalAlignment = GridData.CENTER;
				gridData11.widthHint = -1;
				gridData11.heightHint = BUTTON_HEIGHT;

				_simulationButton = new StateButton(_desktop.getRightPanel(),
						SWT.NONE);
				_simulationButton.setActiveColor(Display.getDefault()
						.getSystemColor(SWT.COLOR_MAGENTA));
				_simulationButton.setText(Messages
						.getString("AvmIhm.simulation")); //$NON-NLS-1$

				_simulationButton.setLayoutData(gridData11);
				_simulationButton.addSelectionListener(new SelectionListener() {

					public void widgetSelected(SelectionEvent event) {
						StateButton b = (StateButton) event.getSource();
						boolean state = b.getSelection();
						if (_simulationTimer != null) {
							_simulationTimer.cancel();
							_simulationTimer = null;
							_simulationTask = null;
						}

						if (state) {
							_simulationTask = new SimulationTask();
							_simulationTimer = new Timer();
							_simulationTimer.schedule(_simulationTask, 1000,
									_demoPeriode * 1000);
						}
					}

					public void widgetDefaultSelected(SelectionEvent event) {

					}
				});

				_desktop.getRightPanel().layout();

				refresh(_compositePanels);
				refresh(_compositeButtons);
				if (isDisposed() == false) {
					layout();
				}
			} else {
				_simulationButton.setVisible(true);
			}
		}
	}

	private void removeDemoButton() {
		if (_simulationTimer != null) {
			_simulationTimer.cancel();
		}
		_simulationTask = null;
		if (_simulationButton != null && !_simulationButton.isDisposed()) {
			// _simulationButton.dispose();
			// _simulationButton = null;
			//
			// _desktop.getRightPanel().layout();
			_simulationButton.setVisible(false);
			_simulationButton.setSelection(false);
		}
	}

	/**
	 * Zone Avance-Retard
	 */
	// private void activateAvanceRetardPanel(boolean b) {
	// if (b == false) {
	// removeAvanceRetardPanel();
	// return;
	// }
	//
	// if (_avance_retard != null)
	// return;
	// GridData gridData11 = new GridData();
	// gridData11.horizontalAlignment = GridData.FILL;
	// gridData11.grabExcessVerticalSpace = false;
	// gridData11.grabExcessHorizontalSpace = true;
	// gridData11.widthHint = -1;
	// gridData11.heightHint = BUTTON_HEIGHT;
	// _avance_retard = new Label(_compositeButtons, SWT.CENTER);
	//		_avance_retard.setText("--"); //$NON-NLS-1$
	// _avance_retard.setFont(_font);
	// _avance_retard.setLayoutData(gridData11);
	// _avance_retard.setBackground(DesktopStyle.getBackgroundColor());
	// }

	// private void removeAvanceRetardPanel() {
	// if (_avance_retard != null && !_avance_retard.isDisposed()) {
	// _avance_retard.dispose();
	// _avance_retard = null;
	// }
	// }

	public void setDemoMode(boolean b) {
		_demoMode = b;
	}

	private boolean isDemoMode() {
		return _demoMode;
	}

	public void setGeorefRole(boolean b) {
		_isGeorefRole = b;
		if (_suiviCourse != null) {
			_suiviCourse.setGeorefRole(b);
		}
	}

	class SimulationTask extends TimerTask {

		public void run() {
			try {
				_log.debug("execute task..."); //$NON-NLS-1$
				if (_avm != null) {
					Point dernier = _avm.getModel().getDernierPoint();

					Point prochain = _avm.getModel().getProchainPoint();
					if (_avm.getModel().isInsidePoint()) {
						_log.info("simutate entry..."); //$NON-NLS-1$
						_avm.sortie(dernier.getId());
					} else {
						_log.info("simutate outgoing..."); //$NON-NLS-1$
						if (prochain != null) {
							_avm.entree(prochain.getId());
						}
					}
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							_instance.layout();
						}
					});

				} else {
					_log.error("[IHMAAvm] service Avm not available."); //$NON-NLS-1$
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

	}

	public void logout() {
		removeDemoButton();
	}

	public void setDemoPeriode(int periode) {
		_demoPeriode = periode;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
