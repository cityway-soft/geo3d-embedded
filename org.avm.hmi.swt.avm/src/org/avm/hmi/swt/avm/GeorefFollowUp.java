package org.avm.hmi.swt.avm;

import org.avm.business.core.Avm;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GeorefFollowUp extends Composite implements FollowUp,
		SelectionListener {
	private Display _display;

	private Text _text;

	private Label _label;

	private Button _buttonGeoref;

	private Button _buttonPrev;

	private Button _buttonNext;

	private Font _fontTitle;

	private Course _course;

	private int _currentRang = 1;

	private Avm _avm;

	public GeorefFollowUp(Composite arg0, int arg1) {
		super(arg0, arg1);
		_display = arg0.getDisplay();


		_fontTitle = DesktopImpl.getFont(5, SWT.NORMAL); //$NON-NLS-1$
		create();
	}

	private void create() {
		setBackground(
				DesktopStyle.getBackgroundColor());

		GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		_label = new Label(this, SWT.NONE);
		_label.setText(Messages
				.getString("GeorefFollowUp.arret-non-geolocalise")); //$NON-NLS-1$
		_label.setLayoutData(gridData);
		_label.setVisible(false);
		_label.setFont(_fontTitle);
		_label.setBackground(
				DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_text = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI
				| SWT.WRAP);
		_text.setLayoutData(gridData);
		_text.setVisible(false);
		_text.setFont(_fontTitle);
		
		createButtons();

		layout();
	}

	private void createButtons() {
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setBackground(
				DesktopStyle.getBackgroundColor());

		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		Button button;
		button = new Button(composite, SWT.NONE);
		button.setLayoutData(gridData);
		button.setFont(_fontTitle);
		button.addSelectionListener(this);
		_buttonPrev = button;
		_buttonPrev.setText(Messages.getString("GeorefFollowUp.precedent")); //$NON-NLS-1$

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		button = new Button(composite, SWT.NONE);
		button.setLayoutData(gridData);
		button.setFont(_fontTitle);
		button.addSelectionListener(this);
		_buttonGeoref = button;
		_buttonGeoref.setText(Messages.getString("GeorefFollowUp.georef")); //$NON-NLS-1$
		_buttonGeoref.setBackground(getDisplay().getSystemColor(
				SWT.COLOR_RED));

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		button = new Button(composite, SWT.NONE);
		button.setLayoutData(gridData);
		button.setFont(_fontTitle);
		button.addSelectionListener(this);
		_buttonNext = button;
		_buttonNext.setText(Messages.getString("GeorefFollowUp.suivant")); //$NON-NLS-1$

	}

	public void updateCourse() {
		if (_avm == null)
			return;
		_course = _avm.getModel().getCourse();
		_currentRang = _avm.getModel().getRang();

		Point prochain = _avm.getModel().getProchainPoint();
		_label.setVisible(true);
		_label.setText(Messages.getString("FollowJourney.prochain_arret")); //$NON-NLS-1$
		_text.setVisible(true);
		if (prochain != null) {
			_text
					.setText(prochain.getNom()
							+ "\n" + Messages.getString("FollowJourney.arrivee_a") + prochain.getHeureArriveeTheorique()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void updatePoint() {
		if (_avm == null)
			return;
		if (_avm.getModel().isHorsItineraire()) {
			setHorsItineraire(true);
			return;
		}
		_text.setVisible(true);
		_label.setVisible(true);
		Point point = _avm.getModel().getDernierPoint();
		if (point != null && point.getLongitude() == 0 && point.getLatitude() == 0
				&& point.isDesservi()) {
			_label
					.setText(Messages
							.getString("GeorefFollowUp.cet-arret-est-maintenant-geolocalise")); //$NON-NLS-1$
			_text.setText(point.getNom());
		}
		point = _avm.getModel().getProchainPoint();
		if (point != null && point.getLongitude() == 0 && point.getLatitude() == 0
				&& point.isDesservi() == false) {
			_label
					.setText(Messages
							.getString("GeorefFollowUp.prochain-arret-non-geolocalise")); //$NON-NLS-1$
			_text.setText(point.getNom());
		}
		layout();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void widgetSelected(final SelectionEvent e) {
		_buttonGeoref.setText(Messages.getString("GeorefFollowUp.En.cours")); //$NON-NLS-1$
		_buttonGeoref.setEnabled(false);
		layout();
		_display.asyncExec(new Runnable() {

			public void run() {
				if (e.getSource() == _buttonGeoref) {
					Point point = _course.getPointAvecRang(_currentRang + 1);
					if (point != null) {
						_avm.entree(point.getId());
						try {
							Thread.sleep(3000);

						} catch (InterruptedException e1) {
						}
						_avm.sortie(point.getId());
					}
					updatePoint();
				} else {
					if (e.getSource() == _buttonPrev) {
						_currentRang = (_currentRang <= 0) ? 0
								: (_currentRang - 1);
					} else if (e.getSource() == _buttonNext) {
						_currentRang = (_currentRang >= _course
								.getNombrePoint()) ? _course.getNombrePoint()
								: (_currentRang + 1);
					}

					Point point = _course.getPointAvecRang(_currentRang + 1);
					if (point != null 
							&& point.isDesservi() == false) {
						if (point.getLongitude() == 0 && point.getLatitude() == 0){
						_label
								.setText(Messages
										.getString("GeorefFollowUp.prochain-arret-non-geolocalise")); //$NON-NLS-1$
						}
						_text.setText(point.getNom());
					}
				}
				_buttonGeoref.setText(Messages
						.getString("GeorefFollowUp.georef")); //$NON-NLS-1$
				_buttonGeoref.setEnabled(true);
				layout();
			}

		});
	}

	public void setAvm(Avm avm) {
		_avm = avm;
		_course = _avm.getModel().getCourse();

		updateCourse();
		updatePoint();
	}

	public void setHorsItineraire(boolean b) {

	}


}
