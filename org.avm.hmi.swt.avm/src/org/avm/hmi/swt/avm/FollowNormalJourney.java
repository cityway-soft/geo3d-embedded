package org.avm.hmi.swt.avm;

import org.avm.business.core.Avm;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.hmi.swt.avm.widget.VerticalARGauge;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Gauge;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FollowNormalJourney extends FollowJourney {

	private FollowNormalJourney _instance;

	private Composite _details;

	Font _fontTitle;

	Font _fontText;

	private VerticalARGauge _gauge;

	private FollowUp _followPanel;

	private Text _textService;

	private Text _textCourse;

	private Avm _avm;

	private Composite _rightPanel;

	private int _followUpPanelId;

	private boolean _isGeorefRole;

	private StackLayout _stacklayout;

	private GraphicalFollowUp _graphicalFollowUp;

	private SimpleFollowUp _simpleFollowUp;

	private GeorefFollowUp _georefFollowUp;

	private Composite _followUpPanelContainer;

	public FollowNormalJourney(Composite parent, int ctrl) {
		super(parent, ctrl);
		_instance = this;
		create();
	}

	public void create() {

		setBackground(DesktopStyle.getBackgroundColor());

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		setLayout(gridLayout);

		
		_fontTitle = DesktopImpl.getFont(10, SWT.NORMAL); //$NON-NLS-1$
		_fontText = DesktopImpl.getFont(12, SWT.NORMAL); //$NON-NLS-1$

		createLeftPanel();
		createRigthPanel();

	}

	private void createLeftPanel() {
		_gauge = new VerticalARGauge(this, SWT.NONE);

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.widthHint = 100;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		_gauge.setLayoutData(gridData);
	}

	private void createRigthPanel() {

		_rightPanel = new Composite(this, SWT.NONE);
		_rightPanel.setBackground(DesktopStyle.getBackgroundColor());

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_rightPanel.setLayoutData(gridData);

		 GridLayout gridLayout = new GridLayout();
		 gridLayout.numColumns = 1;
		 _rightPanel.setLayout(gridLayout);




		createDetails();

		createFollowUpPanel();

	}

	private void createDetails() {
		_details = new Composite(_rightPanel, SWT.NONE);
		_details.setBackground(DesktopStyle.getBackgroundColor());

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		GridLayout gridLayout = new GridLayout();
		_details.setLayoutData(gridData);
		gridLayout.numColumns = 5;
		_details.setLayout(gridLayout);

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		Label labelService = new Label(_details, SWT.NONE);
		labelService.setText(Messages.getString("FollowJourney.titre_service")); //$NON-NLS-1$
		labelService.setFont(_fontTitle);
		labelService.setLayoutData(gridData);
		labelService.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_textService = new Text(_details, SWT.BORDER | SWT.READ_ONLY);
		_textService.setFont(_fontTitle);
		_textService.setLayoutData(gridData);
		_textService.setTextLimit(7);

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		Label labelCourse = new Label(_details, SWT.NONE);
		labelCourse.setText(Messages.getString("FollowJourney.titre_course")); //$NON-NLS-1$
		labelCourse.setFont(_fontTitle);
		labelCourse.setLayoutData(gridData);
		labelCourse.setBackground(DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_textCourse = new Text(_details, SWT.BORDER | SWT.READ_ONLY);
		_textCourse.setLayoutData(gridData);
		_textCourse.setFont(_fontTitle);
		_textCourse.setTextLimit(7);

		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		Button b = new Button(_details, SWT.BORDER | SWT.READ_ONLY);
		b.setText(Messages.getString("FollowJourney.bouton_changer_vue")); //$NON-NLS-1$
		b.setLayoutData(gridData);
		b.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent e) {
				_followUpPanelId++;
				_followUpPanelId = _followUpPanelId % 3;
				activateFollowUpPanel();
			}

		});
	}

	private void activateFollowUpPanel() {
		switch (_followUpPanelId) {
		case 0:
			activateGraphicalFollowUp();
			break;
		case 1:
			activateSimpleFollowUp();


			break;

		case 2:
			if (isGeorefRole()) {
				activateGeorefFollowUp();

			} else {
				_followUpPanelId++;
				_followUpPanelId = _followUpPanelId % 3;
				activateFollowUpPanel();
			}
			break;
		}

		_rightPanel.layout();
		if (_avm != null) {
			_followPanel.setAvm(_avm);
			Point dernier = _avm.getModel().getDernierPoint();
			if (dernier != null) {
				_followPanel.updatePoint();
			}
		}

	}

	private void createFollowUpPanel() {

		_followUpPanelContainer = new Composite(_rightPanel, SWT.NONE);
		_stacklayout = new StackLayout();
		_followUpPanelContainer.setLayout(_stacklayout);
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_followUpPanelContainer.setLayoutData(gridData);
		
		_followUpPanelId = 1;
		activateFollowUpPanel();

	}

	private void activateGraphicalFollowUp() {
		if (_graphicalFollowUp == null) {

			_graphicalFollowUp = new GraphicalFollowUp(_followUpPanelContainer, SWT.NONE);
			this.setBackground(DesktopStyle.getBackgroundColor());
			_graphicalFollowUp.setAvm(_avm);
			if (_avm != null) {
				_avm.setGeorefMode(false);
			}

		}
		_followPanel = _graphicalFollowUp;
		_stacklayout.topControl = _graphicalFollowUp;
		_followUpPanelContainer.layout();
	}

	private void activateSimpleFollowUp() {
		_simpleFollowUp = new SimpleFollowUp(_followUpPanelContainer, SWT.NONE);
		_simpleFollowUp.setAvm(_avm);
		this.setBackground(DesktopStyle.getBackgroundColor());
		if (_avm != null) {
			_avm.setGeorefMode(false);
		}

		_followPanel = _simpleFollowUp;
		_stacklayout.topControl = _simpleFollowUp;
		_followUpPanelContainer.layout();
	}

	private void activateGeorefFollowUp() {
		_georefFollowUp = new GeorefFollowUp(_followUpPanelContainer, SWT.NONE);
		_georefFollowUp.setAvm(_avm);
		this.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
		MessageBox
				.setMessage(
						null,
						Messages.getString("FollowJourney.avertissement_mode_georef"), MessageBox.MESSAGE_WARNING, SWT.CENTER); //$NON-NLS-1$
		if (_avm != null) {
			_avm.setGeorefMode(true);
		}
		_followPanel = _georefFollowUp;
		_stacklayout.topControl = _georefFollowUp;
		_followUpPanelContainer.layout();
	}

	public void setAvanceRetard(final int ar) {
		if (_gauge != null) {
			
			_gauge.setAvanceRetard(ar);
		}
		_instance.layout();
	}

	public void setService(final ServiceAgent sa) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed() == false && _textService != null) {
					_textService.setText(Integer.toString(sa.getIdU()));
					_details.layout();
				}
			}
		});
	}

	public void setCourse(final Course co) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					if (isDisposed() == false && _textCourse != null) {
						_textCourse.setText((co.getNom() == null) ? "#" + co.getIdu() : co.getNom()); //$NON-NLS-1$
						_followPanel.updateCourse();
						_details.layout();
						setAvm(_avm);
						if (_gauge != null){
							Course course = _avm.getModel().getCourse();
							_gauge.setLimits(course.getChevauchement(), course.getAmplitude());
						}
					}
				} catch (Throwable t) {
				}
			}
		});
	}

	public void setAvm(org.avm.business.core.Avm avm) {
		_avm = avm;
		if (_followPanel != null) {
			_followPanel.setAvm(_avm);
		}
	}

	public void setPoint(final Point point) {
		if (isDisposed() == true)
			return;
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				activatePanel();
				setAvm(_avm);

			}
		});
	}

	public void setHorsItineraire(boolean b) {
		_followPanel.setHorsItineraire(b);
	}


	public void setGeorefRole(boolean b) {
		_isGeorefRole = b;
	}

	public boolean isGeorefRole() {
		return _isGeorefRole;
	}

	public void updateMessage() {
		_desktop.setInformation(Messages.getString("AvmIhm.direction") + " \"" + _avm.getModel().getCourse().getDestination() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}