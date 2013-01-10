package org.avm.hmi.swt.desktop;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class Cadran extends Composite implements PaintListener {

	private Canvas canvas = null;

	private int _numValue;

	private int _rayon;

	private double _value;

	private Point _middle;

	private String _title = "no tile"; // @jve:decl-index=0:

	private String _unit = ""; // @jve:decl-index=0:

	private Display _display;

	public Cadran(Composite parent, int style) {
		super(parent, style);
		_display = parent.getDisplay();
		initialize();
	}

	private void initialize() {
		createCanvas();
		draw();
		setLayout(new GridLayout());

	}

	/**
	 * This method initializes canvas
	 * 
	 */
	private void createCanvas() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		canvas = new Canvas(this, SWT.NONE);
		canvas.setBackground(DesktopStyle.getBackgroundColor());
		canvas.addPaintListener(this);
		canvas.setLayoutData(gridData);

	}

	private void draw() {
		// <DLA
		_middle = canvas.getSize();
		_middle.x /= 2;
		_middle.y /= 2;

		_rayon = Math.min(_middle.x, _middle.y) - 5;

		GC gc = new GC(canvas);
		// gc.setAntialias(SWT.ON);
		gc.setForeground(_display.getSystemColor(SWT.COLOR_BLUE));
		gc.drawOval(_middle.x - _rayon, _middle.y - _rayon, _rayon * 2,
				_rayon * 2);

		drawArrow(gc);
		drawTitle(gc);
		drawValue(gc);
		drawUnits(gc);
		gc.dispose();
		// DLA>
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setUnit(String unit) {
		_unit = unit;
	}

	private void drawTitle(GC gc) {
		FontMetrics fm = gc.getFontMetrics();
		int charsize = gc.getCharWidth('a');
		int x = _middle.x - (_title.length() * charsize / 2);
		int y;
		if (isDown()) {
			y = _middle.y - fm.getHeight();
		} else {
			y = _middle.y;

		}
		gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(DesktopStyle.getBackgroundColor());
		gc.drawText(_title, x, y);
	}

	private boolean isDown() {
		double b = (2 * Math.PI) * (_value / _numValue) - Math.PI / 2;
		return (b > 0 && b < Math.PI);
	}

	private void drawValue(GC gc) {
		FontMetrics fm = gc.getFontMetrics();
		int charsize = gc.getCharWidth('W');
		DecimalFormat df = new DecimalFormat("###.##");
		String str = df.format(_value) + " " + _unit;

		int x = _middle.x - (str.length() * charsize / 2);
		int y;
		if (isDown()) {
			y = _middle.y - 3 * fm.getHeight();
			;

		} else {
			y = _middle.y + 2 * fm.getHeight();
		}
		gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(DesktopStyle.getBackgroundColor());
		gc.drawText(str, x, y);
	}

	private void drawArrow(GC gc) {
		double sep = Math.PI / 50;
		double fact = (4d / 5d);
		double val = (2 * Math.PI) * (_value / _numValue) - Math.PI / 2;
		double ray = (double) _rayon;

		int[] points = new int[8];

		points[0] = _middle.x;
		points[1] = _middle.y;

		int x = (int) (ray * Math.cos(val));
		int y = (int) (ray * Math.sin(val));
		points[4] = _middle.x + x;
		points[5] = _middle.y + y;

		int xa = _middle.x + (int) (ray * fact * Math.cos(val - sep));
		int ya = _middle.y + (int) (ray * fact * Math.sin(val - sep));
		points[2] = xa;
		points[3] = ya;

		int xb = _middle.x + (int) (ray * fact * Math.cos(val + sep));
		int yb = _middle.y + (int) (ray * fact * Math.sin(val + sep));
		points[6] = xb;
		points[7] = yb;

		gc.setBackground(_display.getSystemColor(SWT.COLOR_BLUE));
		gc.fillPolygon(points);
		gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawPolygon(points);
	}

	private void drawUnits(GC gc) {
		double interv = 60;
		double fact = 19d / 20d;

		gc.setBackground(_display.getSystemColor(SWT.COLOR_BLUE));
		double step = 0;
		int i = 0;
		while (i < interv) {
			step = (i * (_numValue / interv)) / _numValue;
			double val = (2 * Math.PI) * step - Math.PI / 2;
			double ray;
			if (i % 12 == 0) {
				ray = (double) _rayon * (fact * fact * fact);
			} else {
				ray = (double) _rayon * fact;
			}

			int xa = _middle.x + (int) (ray * Math.cos(val));
			int ya = _middle.y + (int) (ray * Math.sin(val));

			ray = (double) _rayon;

			int xb = _middle.x + (int) (ray * Math.cos(val));
			int yb = _middle.y + (int) (ray * Math.sin(val));

			gc.drawLine(xa, ya, xb, yb);

			i++;
		}

	}

	public void setNumberOfValue(int number) {
		_numValue = number;
	}

	public void setValue(double value) {
		_value = value;
		canvas.redraw();
	}

	public void paintControl(PaintEvent e) {
		draw();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
