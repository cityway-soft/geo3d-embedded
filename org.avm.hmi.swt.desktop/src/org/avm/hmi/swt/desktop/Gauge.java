package org.avm.hmi.swt.desktop;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public class Gauge extends Composite implements PaintListener, MouseListener {

	private Vector _list = new Vector();
	private Canvas _canvas;
	private Display _display;
	private int _level;
	private int _maximum;
	private int _minimum;
	private String _title;
	private int _steps=5;

	public Gauge(Composite parent, int ctrl) {
		super(parent, ctrl);
		_display = parent.getDisplay();
		initialize();
	}

	private void initialize() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		// gridData.heightHint=150;
		setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		setLayout(layout);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_canvas = new Canvas(this, SWT.NONE);
		_canvas.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_BLACK));
		_canvas.addPaintListener(this);
		_canvas.addMouseListener(this);
		_canvas.setLayoutData(gridData);

		pack();
		layout();
	}

	public void setTitle(String title) {
		_title = title;
	}

	private void draw() {
		GC gc = new GC(_canvas);

		Point size = getSize();
		Rectangle rect;

		gc.setForeground(_display.getSystemColor(SWT.COLOR_WHITE));
		gc.setBackground(_display.getSystemColor(SWT.COLOR_WHITE));
		rect = new Rectangle(0, 0, size.x, size.y);
		gc.fillRectangle(rect);

		gc.setForeground(_display.getSystemColor(SWT.COLOR_DARK_YELLOW));
		gc.setBackground(_display.getSystemColor(SWT.COLOR_DARK_GREEN));

		double a = (double) size.y / (double) (_maximum - _minimum);
		double b = (size.y * _minimum) / (_maximum - _minimum);
		int val = size.y - (int) (((double) _level) * a + b);
		rect = new Rectangle(0, val, size.x, size.y);
		gc.fillRectangle(rect);
		gc.drawRectangle(rect);
		gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
		if (_title != null) {
			if (_level < ((_maximum - _minimum) / 2)) {
				gc.setBackground(_display.getSystemColor(SWT.COLOR_WHITE));
			}
			gc.drawText(_title, size.x / 2 - _title.length() * 5, size.y / 2);
		}
		gc.dispose();

	}

	public void paintControl(PaintEvent arg0) {
		draw();
	}

	public void mouseDoubleClick(MouseEvent arg0) {
	}

	public void setSteps(int s){
		_steps = s;
	}
	
	public void mouseDown(MouseEvent event) {
		Point size = getSize();
//		double a = (double) (_maximum - _minimum) / (double) size.y;
//		double b = _minimum;
//		_level = (int) (a * (double) (size.y - event.y) + b);
//		System.out.println("mouse LEVEL = " + _level);
		int step=_steps;
		if (event.y > size.y/2){
			step=-_steps;
		}
		_level += step;
		_level = (_level>100)?100:_level;
		_level = (_level<0)?0:_level;
		
		draw();
		fireGaugeChanged();
	}

	public void mouseUp(MouseEvent event) {
	}

	public void setMaximum(int i) {
		_maximum = i;
	}

	public void setMinimum(int i) {
		_minimum = i;
	}

	public void setSelection(int i) {
		_level = i;
		draw();
	}

	private void fireGaugeChanged() {
		Event detail = new Event();
	    detail.widget = this;
	    detail.data = new Integer(_level);
	    
	    SelectionEvent event = new SelectionEvent(detail);

		Enumeration e = _list.elements();
		while (e.hasMoreElements()) {
			SelectionListener listener = (SelectionListener) e.nextElement();
			listener.widgetSelected(event);
		}
	}

	public void addSelectionListener(SelectionListener listener) {
		_list.add(listener);

	}

	public void removeSelectionListener(SelectionListener listener) {
		_list.remove(listener);

	}

	public int getSelection() {
		return _level;
	}

}
