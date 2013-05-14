package org.angolight.hmi.swt.leds;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class LedsDeviceImpl extends Composite implements LedsDevice,
		PaintListener, Runnable, MouseListener { // implements ChoiceListener {

	private Display _display;

	private Canvas _canvas;

	private short _adress;

	private Timer _timer;

	private Logger _log = Logger.getInstance(this.getClass().getName());

	private LedsTimerTask _ledsTimerTask;

	private byte _period;

	private boolean _visible = true;

	private double _ledSize = 30;

	private double _ledsSpacing;

	private boolean _runningSequence = false;

	// private Thread _thread = null;

	private byte _cycle;

	private static final int BUFFER_SIZE = 85;

	private LedCommand[] _sequences = new LedCommand[BUFFER_SIZE];

	private LedsListener _listener;

	private boolean _manuallyHide;
	private Point _mouseDown;

	private short _indexSequence;

	private boolean _ovalForm;

	private static final int[] LEDS = { SWT.COLOR_RED, SWT.COLOR_RED,
			SWT.COLOR_RED, SWT.COLOR_GREEN, SWT.COLOR_GREEN, SWT.COLOR_GREEN,
			SWT.COLOR_RED, SWT.COLOR_RED, SWT.COLOR_RED, };

	public LedsDeviceImpl(LedsListener listener, Composite parent, int style) {
		super(parent, style);
		_display = parent.getDisplay();
		// _log.setPriority(Priority.DEBUG);
		initialize();
		_listener = listener;
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
		_canvas.setLayoutData(gridData);
		_mouseDown = new Point(0, 0);

		pack();
		layout();
	}

	public void open() {
	}

	public void close() {
		dispose();
	}

	public void enableMouseGesture(boolean b) {
		if (b) {
			_canvas.addMouseListener(this);
		} else {
			_canvas.removeMouseListener(this);
		}
	}

	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		getParent().setVisible(visible);
		_visible = visible;
	}

	public boolean isOpen() {
		return _visible;
	}

	public void paintControl(PaintEvent arg0) {
		_ledSize =  ((getSize().y / 2d) / (LEDS.length));
		draw(_adress);
	}

	
	
	private void drawLeds(GC gc, int i, boolean state) {
		if (_ovalForm) {
			drawOvalLeds(gc, i, state);
		} else {
			drawRectangleLeds(gc, i, state);
		}
	}

	private void drawOvalLeds(GC gc, int i, boolean state) {
		double size = _ledSize;
		// int x = (getParent().getSize().x - _ledSize) / 2;
		int x = (int) ( (getSize().x - _ledSize) / 2d);
		int y = (int)( (LEDS.length - i) * _ledsSpacing - (size / 2d));
		gc.setBackground(_display.getSystemColor(SWT.COLOR_BLACK));
		gc.fillOval(x, y, (int)_ledSize, (int)_ledSize);
		gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawOval(x, y, (int)size, (int)size);

		if (state) {
			gc.setBackground(_display.getSystemColor(LEDS[i]));
			gc.fillOval(x, y, (int)size, (int)size);
			gc.setForeground(_display.getSystemColor(SWT.COLOR_GRAY));
			gc.drawOval(x, y, (int)size, (int)size);
		} else {
			size = _ledSize / 2d;
			gc.setBackground(_display.getSystemColor(SWT.COLOR_WHITE));
			int xx = (int)(x + size / 2d);
			int yy = (int) (y + size / 2d);
			gc.fillOval(xx, yy, (int)size, (int)size);
		}
	}

	private void drawRectangleLeds(GC gc, int i, boolean state) {
		double size = _ledSize;
		double x = 0;
		double y = ( (LEDS.length - i) * _ledsSpacing - (size / 2d));
		gc.setBackground(_display.getSystemColor(SWT.COLOR_BLACK));
		int xx=(int)x;
		int yy = (int) (y - (_ledSize / 2d));
		gc.fillRectangle(xx, yy, getSize().x, (int)_ledSize);
		gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(xx, yy, getSize().x, (int)_ledSize);

		if (state) {
			gc.setBackground(_display.getSystemColor(LEDS[i]));
			gc.fillRectangle(xx, yy, getSize().x, (int)_ledSize);
			gc.setForeground(_display.getSystemColor(SWT.COLOR_GRAY));
			gc.fillRectangle(xx, yy, getSize().x, (int)_ledSize);
		} else {
			size = _ledSize / 2d;
			gc.setBackground(_display.getSystemColor(SWT.COLOR_GRAY));
			xx= (int)(x + size / 2d);
			yy = (int) (y + size / 2d - (_ledSize / 2d));
			gc.fillRectangle(xx, yy,
					(int)(getSize().x - size), (int) size);
		}
	}

	private void draw(final short state) {
		_display.asyncExec(new Runnable() {

			public void run() {
				if (isDisposed() == false) {
					// Point size = getParent().getSize();
					Point size = getSize();
					_ledsSpacing = ((double)size.y / (double)(LEDS.length));

					int ss = state;
					GC gc = new GC(_canvas);
					gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
					for (int i = 0; i < LEDS.length; i++) {
						boolean s = ((ss >> i) & 0x01) > 0;
						drawLeds(gc, i, s);
					}
					gc.dispose();
				}
			}
		});

	}

	private void drawSequence(final int delay, final short state) {
		stopSequence();
		_runningSequence = true;
		// _thread = new Thread(this);
		// _thread.start();
		Display.getDefault().syncExec(this);
	}

	public void stopSequence() {
		// if (_thread != null) {
		// _thread.interrupt();
		// _thread = null;
		_runningSequence = false;
		// }
	}

	public int setState(short state, byte period) {
		if (_visible == false) {
			return 0;
		}
		_adress = state;
		_period = period;
		stopSequence();
		killTimer();
		if (period > 0) {
			resetTimer();
		}
		draw(state);
		return 0;
	}

	public int executeSequence(byte address, byte cycle, byte period) {
		_adress = address;
		_indexSequence = _adress;
		_period = period;
		_cycle = cycle;
		if (_visible == false) {
			return 0;
		}
		killTimer();
		drawSequence(400, address);
		return 0;
	}

	public void killTimer() {
		try {
			_log.debug("Kill Timer ");
			if (_timer != null) {
				draw((short) 0);
				_timer.cancel();
				_timer = null;
			}
		} catch (Exception e) {
			_log.error(e.getMessage());
			_timer = null;
		}
	}

	public void resetTimer() {
		try {
			_log.debug("Reset Timer ");
			if (_timer != null) {
				_timer.cancel();
				_timer = null;
			}

			if (_ledsTimerTask != null) {
				_ledsTimerTask.cancel();
				_ledsTimerTask = null;
			}

			_timer = new Timer();
			_ledsTimerTask = new LedsTimerTask();

			_timer.schedule(_ledsTimerTask, 0, _period * 10);
		} catch (Exception e) {
			_log.error(e);
			_timer = null;
		}
	}

	class LedsTimerTask extends TimerTask {
		boolean on = false;

		public void run() {

			if (on) {
				draw(_adress);
			} else {
				draw((short) 0);
			}
			on = !on;
		}
	}

	public void haltSequence() {
		stopSequence();
	}

	public void setBrightness(byte brightness) {
	}

	public int addState(byte address, short states, byte period) {
		if (address >= 0 && address < BUFFER_SIZE) {
			_sequences[address] = LedCommand.createI(states, period);
			return 0;
		}
		return 1;
	}

	public int addStop(byte address, short states, byte period) {
		if (address >= 0 && address < BUFFER_SIZE) {
			_sequences[address] = LedCommand.createJ(states, period);
			return 0;
		}
		return 1;
	}

	public void run() {

		LedCommand ledcommand = _sequences[_indexSequence];
		_log.debug("Running: " + ledcommand);
		if (ledcommand == null)
			return;
		if (ledcommand.getType() == LedCommand.TYPE_J) {
			_indexSequence = _adress;
			ledcommand = _sequences[_indexSequence];
			_cycle--;
			if (_cycle == 0) {
				_runningSequence = false;
				_listener.sequenceStopped();
			}
		}
		draw(ledcommand.getState());
		// try {
		// _log.debug("Sleep " + (ledcommand.getPeriod() * 10) + " ms");
		// Thread.sleep(ledcommand.getPeriod() * 10);
		// } catch (InterruptedException e) {
		// _runningSequence = false;
		// }
		_indexSequence++;

		if (_runningSequence) {
			Display.getDefault().timerExec(ledcommand.getPeriod() * 10, this);
		}
	}

	public void mouseDoubleClick(MouseEvent arg0) {
	}

	public void mouseDown(MouseEvent event) {
		_mouseDown.x = event.x;
		_mouseDown.y = event.y;
	}

	public void mouseUp(MouseEvent event) {
		int delta = event.y - _mouseDown.y;
		delta = (delta > 0) ? delta : -delta;
		if (delta > (getParent().getSize().y / 3)) {
			_manuallyHide = true;
			getParent().setVisible(false);
		}
	}

	public void setFormOval(boolean oval) {
		_ovalForm=oval;
	}
}
