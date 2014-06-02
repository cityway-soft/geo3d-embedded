package org.avm.hmi.swt.avm.widget;

import org.avm.hmi.swt.avm.Messages;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class VerticalARGauge extends Composite implements Runnable {

	private VerticalARGauge _instance;

	private Display _display;

	private static final int MAX = 9;

	private static final String[] TITLE = new String[] {
			Messages.getString("VerticalARGauge.en-avance"), //$NON-NLS-1$
			Messages.getString("VerticalARGauge.a-l-heure"), Messages.getString("VerticalARGauge.en-retard") }; //$NON-NLS-1$ //$NON-NLS-2$

	private Label[] _list;

	private Color[] _color;

	private int _currentIndex;

	private Font _font;

	private boolean _clignot;

	private boolean _running;

	private int FREQ = 700;

	private Label _avanceRetard;

	private int limitLow = 30;

	private int limitHigh = 180;

	public VerticalARGauge(Composite parent, int ctrl) {
		super(parent, ctrl);
		_instance = this;
		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		setLayout(grid);
		create();
	}

	private void create() {
		_display = getDisplay();
		setBackground(DesktopStyle.getBackgroundColor());

		_font = DesktopImpl.getFont(5, SWT.NORMAL);
		_avanceRetard = new Label(this, SWT.CENTER | SWT.BORDER);
		_avanceRetard.setFont(DesktopImpl.getFont(5, SWT.BOLD));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		// data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 15;
		_avanceRetard.setLayoutData(data);
		_avanceRetard.setBackground(DesktopStyle.getBackgroundColor());

		createGauge(this);

		activate(0);

		refresh(this);

	}

	private void activate(int val) {
		int idx;
		boolean clig = false;
		if (val > limitHigh) {
			idx = 7;
			clig = true;
		} else if (val < -limitLow) {
			idx = 1;
			clig = true;
		} else if (val < 0) {
			idx = ((limitLow + val) / (limitLow / 4)) + 1;
		} else if (val > 0) {
			idx = ((val - limitHigh) / (limitHigh / 4)) + 7;
		} else {
			idx = 4;
		}

		// idx = 8 - idx;
		_currentIndex = idx;

		for (int i = 0; i < MAX; i++) {
			_list[i].setBackground(_display.getSystemColor(SWT.COLOR_WHITE));
			_list[i].setText(""); //$NON-NLS-1$
		}
		_list[idx - 1].setBackground(_color[idx - 1]);
		_list[idx].setBackground(_color[idx]);
		// _list[idx].setFont(_font);
		// _list[idx].setText(format(val));
		_list[idx + 1].setBackground(_color[idx + 1]);

		clignote(clig);
	}

	private String format(int val) {
		return ((val > 0) ? "+" + val : "" + val) + Messages.getString("VerticalARGauge.unite_minute"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private void createGauge(Composite composite) {
		_list = new Label[MAX];
		_color = new Color[MAX];
		Composite panel = null;
		for (int i = 0; i < MAX; i++) {
			if (i % 3 == 0) {
				GridLayout grid = new GridLayout();
				grid.numColumns = 1;

				GridData data = new GridData();
				data.grabExcessHorizontalSpace = true;
				data.grabExcessVerticalSpace = true;
				data.horizontalAlignment = GridData.FILL;
				data.verticalAlignment = GridData.FILL;
				panel = new Composite(composite, SWT.NONE);
				panel.setBackground(DesktopStyle.getBackgroundColor());
				panel.setLayoutData(data);
				panel.setLayout(grid);

				Label title = new Label(panel, SWT.NONE);
				data = new GridData();
				data.horizontalSpan = 3;
				data.heightHint = 15;
				title.setText(TITLE[i / 3]);
				title.setFont(_font);
				title.setLayoutData(data);
				title.setBackground(DesktopStyle.getBackgroundColor());
			}
			GridData data = new GridData();
			data.grabExcessHorizontalSpace = true;
			// data.grabExcessVerticalSpace = true;
			data.horizontalAlignment = GridData.FILL;
			// data.verticalAlignment = GridData.FILL;
			data.heightHint = 12;

			_list[i] = new Label(panel, SWT.BORDER | SWT.CENTER);
			_list[i].setLayoutData(data);
			if (i < 3) {
				_color[i] = _display.getSystemColor(SWT.COLOR_RED);
			} else if (i < 6) {
				_color[i] = _display.getSystemColor(SWT.COLOR_DARK_GREEN);
			} else if (i < MAX) {
				_color[i] = _display.getSystemColor(SWT.COLOR_YELLOW);
			}
		}
	}

	private void refresh(Composite composite) {
		composite.layout();
	}

	// FLA : AR en secondes et plus en minute
	public void setAvanceRetard(final int ar) {
		// int curseur = getCursor(ar);

		activate(ar);
		_avanceRetard.setText(format(ar / 60));
		refresh(_instance);
	}

	private int getCursor(final int ar) {
		int cursor = 1;

		return cursor;
	}

	public void clignote(boolean b) {
		if (b) {
			start();
		} else {
			stop();
		}
	}

	private void start() {
		_display.timerExec(FREQ, this);
		_running = true;
	}

	private void stop() {
		_running = false;
	}

	private void on() {
		_display.syncExec(new Runnable() {

			public void run() {
				if (_instance.isDisposed())
					return;
				_list[_currentIndex - 1]
						.setBackground(_color[_currentIndex - 1]);
				_list[_currentIndex].setBackground(_color[_currentIndex]);
				_list[_currentIndex + 1].setBackground(_color[_currentIndex]);
				refresh(_instance);
			}

		});
	}

	private void off() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_instance.isDisposed())
					return;
				_list[_currentIndex - 1].setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));
				_list[_currentIndex].setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));
				_list[_currentIndex + 1].setBackground(_display
						.getSystemColor(SWT.COLOR_WHITE));
				refresh(_instance);
			}
		});
	}

	public void run() {
		if (_clignot) {
			on();
		} else {
			off();
		}
		if (_running) {
			_display.timerExec(FREQ, this);
		}
		_clignot = !_clignot;
	}

	public void setLimits(int limitLow, int limitHigh) {
		this.limitLow = limitLow;
		this.limitHigh = limitHigh;
	}

}