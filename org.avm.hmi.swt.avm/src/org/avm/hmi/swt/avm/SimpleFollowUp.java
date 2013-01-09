package org.avm.hmi.swt.avm;

import org.avm.business.core.Avm;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SimpleFollowUp extends Composite implements FollowUp {

	private Text _textArret;

	private Label _labelArret;

	private Font _fontTitle;

	private Course _course;

	private Avm _avm;

	public SimpleFollowUp(Composite arg0, int arg1) {
		super(arg0, arg1);

		
		_fontTitle = DesktopImpl.getFont(10, SWT.NORMAL); //$NON-NLS-1$
		create();
	}

	private void create() {
		setBackground(DesktopStyle.getBackgroundColor());

		GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		_labelArret = new Label(this, SWT.NONE);
		_labelArret.setText(Messages.getString("FollowJourney.nous_somme_a")); //$NON-NLS-1$
		_labelArret.setLayoutData(gridData);
		_labelArret.setVisible(false);
		_labelArret.setFont(_fontTitle);
		_labelArret.setBackground(
				DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_textArret = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI
				| SWT.WRAP);
		_textArret.setBackground(
				DesktopStyle.getBackgroundColor());
		_textArret.setLayoutData(gridData);
		_textArret.setVisible(false);
		_textArret.setFont(_fontTitle);
		updatePoint();
	}

	public void updateCourse() {
		if (_avm == null)
			return;
		_course = _avm.getModel().getCourse();
		Point prochain = _avm.getModel().getProchainPoint();
		if (prochain == null)
			return;
		_labelArret.setVisible(true);
		_labelArret.setText(Messages.getString("FollowJourney.prochain_arret")); //$NON-NLS-1$
		_textArret.setVisible(true);
		_textArret
				.setText(prochain.getNom()
						+ "\n" + Messages.getString("FollowJourney.arrivee_a") + prochain.getHeureArriveeTheorique()); //$NON-NLS-1$
	}

	public void updatePoint() {
		if (_avm == null)
			return;
		if (_avm.getModel().isHorsItineraire()) {
			setHorsItineraire(true);
			return;
		}
		Point dernierPoint = _avm.getModel().getDernierPoint();
		Point prochainPoint = _avm.getModel().getProchainPoint();
		boolean inside = _avm.getModel().isInsidePoint();
		if (inside && dernierPoint != null) {
			_textArret.setVisible(true);
			_labelArret.setVisible(true);
			_labelArret.setText(Messages
					.getString("FollowJourney.nous_somme_a")); //$NON-NLS-1$
			_textArret.setText(dernierPoint.getNom()
					+ "\n" + Messages.getString("FollowJourney.depart_a") + dernierPoint.getHeureDepartTheorique());
			layout();
		} else {
			if (_course != null) {
				_textArret.setVisible(true);
				_labelArret.setVisible(true);

				if (prochainPoint != null) {
					_labelArret.setText(Messages
							.getString("FollowJourney.prochain_arret")); //$NON-NLS-1$
					_textArret.setVisible(true);
					_textArret
							.setText(prochainPoint.getNom()
									+ "\n" + Messages.getString("FollowJourney.arrivee_a") + prochainPoint.getHeureArriveeTheorique()); //$NON-NLS-1$
				} else {
					_labelArret.setText(Messages
							.getString("FollowJourney.terminus")); //$NON-NLS-1$
					_textArret.setVisible(false);
				}
				layout();
			}
		}
	}

	public void setAvm(Avm avm) {
		_avm = avm;
		updateCourse();
		updatePoint();
	}

	public void setHorsItineraire(boolean b) {
		if (b) {
			_labelArret.setText(Messages
					.getString("FollowJourney.sortieItineraire")); //$NON-NLS-1$
			_textArret.setText(Messages
					.getString("FollowJourney.attente_detection_arret"));
		}
	}
	

}
