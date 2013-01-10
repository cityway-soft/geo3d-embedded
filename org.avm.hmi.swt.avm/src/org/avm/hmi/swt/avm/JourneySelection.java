package org.avm.hmi.swt.avm;

import java.util.Arrays;
import java.util.Calendar;

import org.avm.business.core.event.Course;
import org.avm.business.core.event.ServiceAgent;
import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class JourneySelection extends Composite {
	private ChoiceListener _listener;

	private JourneySelection _instance;

	private int LABEL_DESTINATION_MAX_LENGTH = 30;
	
	private static final String TAG_TERMINATED_JOURNEY="T";

	Font _fontTitle;

	Font _fontText;

	List _courseSelection;

	Button _ok;

	private SelectionAdapter _courseSelectionListener;

	private SelectionAdapter _okSelectionListener;

	public JourneySelection(Composite parent, int ctrl) {
		super(parent, ctrl);
		_instance = this;
		create(this);
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (_courseSelection != null) {
			_courseSelection.setEnabled(b);
		}
	}
	
	public void update(final ServiceAgent sa){
		if (_courseSelectionListener != null){
			_courseSelection.removeSelectionListener(_courseSelectionListener);
		}
		_courseSelectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int idx = _courseSelection.getSelectionIndex();
				idx = (idx < 0) ? 0 : idx;
				Course course = sa.getCourseByRang(idx);
				_ok.setEnabled(!course.isTerminee());
			}
		};
		_courseSelection.addSelectionListener(_courseSelectionListener);
		
		
		if (_okSelectionListener != null){
			_ok.removeSelectionListener(_okSelectionListener);
		}
		_okSelectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int idx = _courseSelection.getSelectionIndex();
				idx = (idx < 0) ? 0 : idx;
				int service = sa.getCourseByRang(idx).getIdu();
				_listener.validation(_instance, new Integer(service));
			}
		};
		_ok.addSelectionListener(_okSelectionListener);
		refresh(sa);
	}

	private void create(Composite composite) {
		composite.setBackground(DesktopStyle.getBackgroundColor());

		GridLayout gridLayout = new GridLayout();
		composite.setLayout(gridLayout);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		
		_fontTitle = DesktopImpl.getFont(14, SWT.NORMAL); //$NON-NLS-1$
		_fontText = DesktopImpl.getFont(6, SWT.NORMAL); //$NON-NLS-1$


		Label userLabel = new Label(composite, SWT.NONE);
		userLabel.setBackground(DesktopStyle.getBackgroundColor());

		userLabel.setText(Messages.getString("JourneySelection.titre")); //$NON-NLS-1$
		userLabel.setFont(_fontTitle);

		_courseSelection = new List(composite, SWT.NONE | SWT.SINGLE
				| SWT.V_SCROLL);
		_courseSelection.setFont(_fontText);
		_courseSelection.setBackground(DesktopStyle.getBackgroundColor());
		Point p = _courseSelection.getSize();
		p.y = 58;
		_courseSelection.setSize(p);


		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalSpan = 2;
		data.heightHint = 58;
		_courseSelection.setLayoutData(data);
//		_courseSelection.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				int idx = _courseSelection.getSelectionIndex();
//				idx = (idx < 0) ? 0 : idx;
//				Course course = sa.getCourseByRang(idx);
//				_ok.setEnabled(!course.isTerminee());
//			}
//		});
		_ok = new Button(composite, SWT.NONE);
		_ok.setFont(_fontTitle);
		_ok.setText(Messages.getString("JourneySelection.OK")); //$NON-NLS-1$
//		_ok.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				int idx = _courseSelection.getSelectionIndex();
//				idx = (idx < 0) ? 0 : idx;
//				int service = sa.getCourseByRang(idx).getIdu();
//				_listener.validation(_instance, new Integer(service));
//			}
//		});
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalSpan = 2;
		_ok.setLayoutData(data);
		
//		refresh(sa);

	}

	private void addCourse(Course course, int idx) {
		StringBuffer buf;
		buf = new StringBuffer();
		if (course.isTerminee()) {
			buf.append(TAG_TERMINATED_JOURNEY);
			buf.append(" ");
		}

		if (course.getNom() == null) {
			buf.append("("); //$NON-NLS-1$
			buf.append(course.getIdu());
			buf.append(") "); //$NON-NLS-1$
		} else {
			buf.append("["); //$NON-NLS-1$
			buf.append(course.getNom());
			buf.append("] "); //$NON-NLS-1$
		}

		buf.append(course.getHeureDepart());
		String destination = course.getDestination();
		if (destination != null) {
			buf.append(" : "); //$NON-NLS-1$
			buf.append(Messages.getString("JourneySelection.direction")); //$NON-NLS-1$
			buf.append(" \""); //$NON-NLS-1$
			if (destination.length() > LABEL_DESTINATION_MAX_LENGTH) {
				destination = destination.substring(0,
						LABEL_DESTINATION_MAX_LENGTH - 3) + "...";
			}
			buf.append(destination);
			buf.append("\""); //$NON-NLS-1$
		}
		_courseSelection.add(buf.toString(), idx);
	}

	public void setSelectionListener(ChoiceListener listener) {
		_listener = listener;
	}

	public class CourseComparator implements java.util.Comparator {

		public int compare(Object o1, Object o2) throws ClassCastException {
			if (!(o1 instanceof Course)) {
				throw new ClassCastException();
			}
			if (!(o2 instanceof Course)) {
				throw new ClassCastException();
			}
			Course c1 = (Course) o1;
			Course c2 = (Course) o2;
			return new Integer(c1.getDepart()).compareTo(new Integer(c2
					.getDepart()));
		}

	}

	public void refresh(ServiceAgent sa) {
		if (isDisposed() == true || sa == null) {
			return;
		}
		_courseSelection.removeAll();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		long t0 = cal.getTime().getTime();
		long now = System.currentTimeMillis();
		long last = t0;
		int idx = 0;

		Course[] cs = sa.getCourses();
		Arrays.sort(cs, new CourseComparator());
		int j = 0;
		for (int i = 0; i < cs.length; i++) {
			Course course = cs[i];
			long dep = t0 + course.getDepart() * 1000;
			if (dep < now && dep > last) {
				idx = i + 1;
				last = dep;
			}
			addCourse(course, j);
			// if (dep > now){
			// addCourse(courseSelection, course,j);
			// j++;
			// }
			// else{
			// idx = i;
			// System.out.println("Course : " + cs[i] + " non proposée !");
			// }
			j++;
		}
		if (_ok != null && (idx > 0 && idx < cs.length)) {
			_courseSelection.setSelection(idx);
			String crsname = _courseSelection.getItem(idx);
			_ok.setEnabled(!(crsname.startsWith(TAG_TERMINATED_JOURNEY)));
		}

	}

//	protected void finalize() throws Throwable {
//		super.finalize();
//		if (_fontTitle != null) {
//			_fontTitle.dispose();
//		}
//		if (_fontText != null) {
//			_fontText.dispose();
//		}
//	}
}