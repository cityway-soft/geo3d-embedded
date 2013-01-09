package org.avm.hmi.swt.desktop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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

	public HorizontalQualityLevel(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		createCanvas();
		draw();
		setSize(new Point(118, 75));
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
		Point size = canvas.getSize();
		GC gc = new GC(canvas);
		size.x -= 1;
		size.y -= 2;
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		int x = 0, dx, dy = -size.y, y = size.y;
		int sep = (size.x / 25);
		dx = (size.x - ((_numBar - 1) * sep)) / _numBar;
		gc.setBackground(Display.getDefault()
				.getSystemColor(SWT.COLOR_DARK_RED));
		Rectangle rect;
		for (int i = 0; i < _numBar; i++) {
			x = i * (dx + sep);
			rect = new Rectangle(x, y, dx, dy);
			if (i < _quality) {

				gc.fillRectangle(rect);
			}
			gc.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
			gc.drawRectangle(rect);

		}
		gc.dispose();
		// DLA>
	}

	public void setNumberOfBar(int number) {
		_numBar = number;
	}

	public void setQuality(int quality) {
		_quality = quality;
	}

	public void paintControl(PaintEvent e) {
		draw();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
