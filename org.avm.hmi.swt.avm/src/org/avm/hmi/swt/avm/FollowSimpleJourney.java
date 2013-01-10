package org.avm.hmi.swt.avm;

import org.avm.business.core.event.Course;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FollowSimpleJourney extends FollowJourney {
	Font _fontText;

	private Text _textCourseId;
	private Text _textCourseHeure;

	public FollowSimpleJourney(Composite parent, int ctrl) {
		super(parent, ctrl);
		create();
	}

	public void create() {
		setBackground(DesktopStyle.getBackgroundColor());

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns=2;

		setLayout(gridLayout);

		_fontText = DesktopImpl.getFont(15, SWT.NORMAL); //$NON-NLS-1$
		
		
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		Label labelCourse = new Label(this, SWT.BORDER | SWT.READ_ONLY);
		labelCourse.setLayoutData(gridData);
		labelCourse.setFont(_fontText);
		labelCourse.setText(Messages.getString("FollowSimpleJourney.course_numero")); //$NON-NLS-1$
		gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_textCourseId = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		_textCourseId.setLayoutData(gridData);
		_textCourseId.setFont(_fontText);
		
		gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelCourse = new Label(this, SWT.BORDER | SWT.READ_ONLY);
		labelCourse.setLayoutData(gridData);
		labelCourse.setFont(_fontText);
		labelCourse.setText(Messages.getString("FollowSimpleJourney.course_de")); //$NON-NLS-1$
		gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_textCourseHeure = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		_textCourseHeure.setLayoutData(gridData);
		_textCourseHeure.setFont(_fontText);
		layout();
	}



	public void setCourse(final Course co) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					if (isDisposed() == false) {
						_textCourseId.setText(Integer.toString( co.getIdu() )); //$NON-NLS-1$
						_textCourseHeure.setText( co.getHeureDepart() ); //$NON-NLS-1$
					}
				} catch (Throwable t) {
				}
			}
		});
	}
	
	public void updateMessage() {
		_desktop.setInformation(Messages.getString("AvmImpl.SERVICE-PLANIFIE") + " " + _avm.getModel().getServiceAgent().getIdU()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}



}