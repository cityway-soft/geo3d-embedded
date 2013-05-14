package org.avm.hmi.swt.desktop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class HorizontalQualityLevel extends Composite implements PaintListener {

	private Canvas canvas = null;

	private int _numBar = 5;
	private int _quality;

	private Display _display;
	private Color _fgcolor;

	private Color _bgcolor;

	public HorizontalQualityLevel(Composite parent, int style) {
		super(parent, style);
		_display = parent.getDisplay();
		_fgcolor = _display.getSystemColor(SWT.COLOR_DARK_RED);
		_bgcolor = _display.getSystemColor(SWT.COLOR_BLACK);
		initialize();
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (!b) {
			_bgcolor = _display.getSystemColor(SWT.COLOR_GRAY);
		} else {
			_bgcolor = _display.getSystemColor(SWT.COLOR_BLACK);
		}
	}
	
	private void initialize() {
		GridLayout grid = new GridLayout();
		grid.horizontalSpacing = 0;
		grid.marginWidth = 0;
		grid.marginHeight = 0;
		grid.verticalSpacing = 0;
		grid.numColumns = 1;
		setLayout(grid);
		createCanvas();
		draw(null);

	}

//	public void layout(){
//		super.layout();
//		canvas.layout();
//		canvas.redraw();
//	}
	
	/**
	 * This method initializes canvas
	 * 
	 */
	private void createCanvas() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		canvas = new Canvas(this, SWT.NONE);
		canvas.setBackground(DesktopStyle.getBackgroundColor());
		canvas.addPaintListener(this);
		canvas.setLayoutData(gridData);

	}

	private void draw(Rectangle b) {
		Rectangle bounds = null;
		if (b == null) {
			bounds = canvas.getBounds();
		} else {
			bounds = b;
		}
		bounds.width -= 1;
		bounds.height -= 35;
		GC gc = new GC(canvas);

		double x = 0, dy, dx, y = (double)bounds.height;
		dx = 5d;

		Rectangle rect;
		double spaceSize = 3d;
		double dymin = 5d;
		double f = ((double)bounds.height - dymin) / (double) _numBar;
		double h;
		double reverse=(double)bounds.height;
		for (int i = _numBar; i > 0; i--) {
			x = (i * dx + (i - 1) * spaceSize);
			h = ((f) * (double)( i))+dymin;
			y=0+reverse;
			dy = bounds.height-h-reverse;
			rect = new Rectangle((int) x, (int) y, (int) dx, (int) (dy));
			if (i <= _quality) {
				gc.setBackground(_fgcolor);
			} else {
				gc.setBackground(DesktopStyle.getBackgroundColor());
			}
			gc.fillRectangle(rect);
			gc.setForeground(_bgcolor);
			gc.drawRectangle(rect);
		}
		gc.dispose();
	}

	public void setForegroundColor(Color color) {
		_fgcolor = color;
	}

	public void setBackgroundColor(Color color) {
		_bgcolor = color;
	}

	public void setNumberOfBar(int number) {
		_numBar = number;
	}

	public void setQuality(int quality) {
		_quality = quality;
	}

	public void paintControl(PaintEvent e) {
		draw( ((Canvas) e.widget).getBounds());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
