package org.avm.hmi.swt.avm.widget;

import org.avm.hmi.swt.avm.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractARGauge extends Composite implements Runnable {

	protected int COLOR_AVANCE = SWT.COLOR_RED;
	protected int COLOR_A_LHEURE = SWT.COLOR_DARK_GREEN;// SWT.COLOR_BLUE;//
	protected int COLOR_RETARD = SWT.COLOR_YELLOW;// SWT.COLOR_DARK_GREEN;//
	protected int COLOR_BLANK = SWT.COLOR_WHITE;

	protected Display _display;

	protected static final int MAX = 9;

	protected static final String[] TITLE = new String[] {
			Messages.getString("VerticalARGauge.en-avance"), //$NON-NLS-1$
			Messages.getString("VerticalARGauge.a-l-heure"), Messages.getString("VerticalARGauge.en-retard") }; //$NON-NLS-1$ //$NON-NLS-2$

	protected Label[] _list;

	protected Color[] _color;

	protected int _currentIndex;

	protected Font _font;

	protected boolean _clignot;

	protected boolean _running;

	protected int FREQ = 700;

	protected Label _avanceRetard;

	protected int limitLow = 30;

	protected int limitHigh = 180;

	public AbstractARGauge(Composite arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	protected void refresh(Composite composite) {
		composite.layout();
	}

	protected String format(int val) {
		StringBuffer buffer = new StringBuffer((val > 0) ? "+" : "");
		// on a des minutes
		if ((val * val) >= 3600) {
			buffer.append((val / 60));
			buffer.append(Messages.getString("VerticalARGauge.unite_minute"));
		}
		// on a des secondes
		else {
			buffer.append(val);
			buffer.append(Messages.getString("VerticalARGauge.unite_seconde"));
		}
		return buffer.toString();
	}

	protected void activate(int val) {
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

		clignote(clig);

		for (int i = 0; i < MAX; i++) {
			_list[i].setBackground(_display.getSystemColor(COLOR_BLANK));
			_list[i].setText(""); //$NON-NLS-1$
		}
		_list[idx - 1].setBackground(_color[idx - 1]);
		_list[idx].setBackground(_color[idx]);
		// _list[idx].setFont(_font);
		// _list[idx].setText(format(val));
		_list[idx + 1].setBackground(_color[idx + 1]);

	}

	// FLA : AR en secondes et plus en minute
	public void setAvanceRetard(final int ar) {
		// int curseur = getCursor(ar);

		activate(ar);
		_avanceRetard.setText(format(ar));
		refresh(this);
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
				if (AbstractARGauge.this.isDisposed() || !_running)
					return;
				_list[_currentIndex - 1]
						.setBackground(_color[_currentIndex - 1]);
				_list[_currentIndex].setBackground(_color[_currentIndex]);
				_list[_currentIndex + 1].setBackground(_color[_currentIndex]);
				refresh(AbstractARGauge.this);
			}

		});
	}

	private void off() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (AbstractARGauge.this.isDisposed() || !_running)
					return;
				_list[_currentIndex - 1].setBackground(_display
						.getSystemColor(COLOR_BLANK));
				_list[_currentIndex].setBackground(_display
						.getSystemColor(COLOR_BLANK));
				_list[_currentIndex + 1].setBackground(_display
						.getSystemColor(COLOR_BLANK));
				refresh(AbstractARGauge.this);
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
