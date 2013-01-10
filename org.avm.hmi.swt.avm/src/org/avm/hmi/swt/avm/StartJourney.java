package org.avm.hmi.swt.avm;

import java.util.Calendar;

import org.avm.elementary.common.Scheduler;
import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class StartJourney extends Composite implements Runnable {
	private ChoiceListener _listener;

	private StartJourney _instance;

	Font _fontTitle;

	Font _fontText;

	Scheduler _scheduler;

	Object _taskId;

	private Label _waitLabel;
	
	private Label _firstStopLabel;
	private Label _destinationLabel;

	private int _startTime;

	private Button _startButton;

	public StartJourney(Composite parent, int ctrl) {
		super(parent, ctrl);
		_instance = this;
		_scheduler = new Scheduler();
		create(this);

	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (_startButton != null) {
			_startButton.setEnabled(b);
		}
	}

	private void create(Composite composite) {
		setBackground(
				DesktopStyle.getBackgroundColor());

		
		_fontTitle = DesktopImpl.getFont(14, SWT.NORMAL); //$NON-NLS-1$
		_fontText = DesktopImpl.getFont(12, SWT.NORMAL); //$NON-NLS-1$

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		Composite compositeWait = composite;
		compositeWait.setBackground(
				DesktopStyle.getBackgroundColor());
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.verticalSpacing = 20;
		gridLayout1.horizontalSpacing = 80;
		gridLayout1.numColumns = 1;
		compositeWait.setLayout(gridLayout1);
		compositeWait.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		_waitLabel = new Label(compositeWait, SWT.NONE);
		_waitLabel.setText(""); //$NON-NLS-1$
		_waitLabel.setFont(_fontTitle);
		_waitLabel.setLayoutData(gridData);
		_waitLabel.setAlignment(SWT.CENTER);
		_waitLabel.setBackground(
				DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_startButton = new Button(compositeWait, SWT.NONE | SWT.CENTER);
		_startButton.setLayoutData(gridData);
		_startButton.setFont(_fontTitle);
		_startButton.setText(Messages.getString("StartJourney.depart")); //$NON-NLS-1$
		_startButton.setAlignment(SWT.CENTER);
		_startButton.setBackground(
				DesktopStyle.getBackgroundColor());
		System.out.println("[DEPART addSelectionListener]");
		_startButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DEPART button push!!!!]");
				_listener.validation(_instance, null);
			}
		});
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		_firstStopLabel = new Label(compositeWait, SWT.NONE);
		_firstStopLabel.setText(""); //$NON-NLS-1$
		_firstStopLabel.setFont(_fontTitle);
		_firstStopLabel.setLayoutData(gridData);
		_firstStopLabel.setAlignment(SWT.CENTER);
		_firstStopLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
		
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		_destinationLabel= new Label(compositeWait, SWT.NONE);
		_destinationLabel.setText(""); //$NON-NLS-1$
		_destinationLabel.setFont(_fontTitle);
		_destinationLabel.setLayoutData(gridData);
		_destinationLabel.setAlignment(SWT.CENTER);
		_destinationLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				
	}

	public void setSelectionListener(ChoiceListener listener) {
		_listener = listener;
	}

	public void dispose() {
		super.dispose();
		if (_taskId != null) {
			_scheduler.cancel(_taskId);
		}
	}

	private int getCurrentHour() {
		Calendar cal = Calendar.getInstance();
		int heure = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		return (heure * 3600) + min * 60 + sec;
	}

	public void update(org.avm.business.core.event.Course course){
		setStartTime(course.getDepart());
		_firstStopLabel.setText(Messages.getString("StartJourney.from")+ "\r\n" +course.getTerminusDepart().getNom());
		_destinationLabel.setText(Messages.getString("StartJourney.to")+ "\r\n" +course.getDestination());
	}
	
	public void setStartTime(int hour) {
		_startTime = hour;
		if(_taskId != null){
			_scheduler.cancel(_taskId);
			_taskId=null;
		}
		_taskId = _scheduler.schedule(this, 1000, true);
	}

	private void updateTime(final String msg, final boolean important) {
		if (isDisposed() == false) {
			this.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						if (_waitLabel != null
								&& _waitLabel.isDisposed() == false) {
							_waitLabel.setText(msg);
							if (important) {
								_waitLabel.setForeground(_instance.getDisplay()
										.getSystemColor(SWT.COLOR_RED));
							} else {
								_waitLabel.setForeground(_instance.getDisplay()
										.getSystemColor(SWT.COLOR_BLACK));
							}
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}

			});
		}
	}

	public void run() {
		int last = 0;
		int min = (getCurrentHour() - _startTime) / 60;
		String msg;
		if (min == 0) {
			int sec = (getCurrentHour() - _startTime);
			msg = "H " + ((sec > 0) ? ("+ " + sec) : ("- " + -sec)) + " sec.";
			updateTime(msg, true);
		} else if (last != min) {
			msg = "H " + ((min > 0) ? ("+ " + min) : ("- " + -min)) + " min.";
			updateTime(msg, false);
		}
		last = min;

	}



}