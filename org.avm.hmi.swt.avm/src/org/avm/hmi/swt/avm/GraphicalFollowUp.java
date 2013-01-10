package org.avm.hmi.swt.avm;

import java.util.StringTokenizer;
import java.util.Vector;

import org.avm.business.core.Avm;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class GraphicalFollowUp extends Canvas implements PaintListener,
		FollowUp {

	Vector _points;

	Display _display;

	private Course _course;

	private Point _currentStop;

	private static final int STOPPOINT_SIZE = 20;

	private static final int NB_STOP_TO_DRAW = 5;

	private static final int NB_STOP_TO_TRANSLATE = 3;

	private int _lineY = 0;

	private StopPoint[] _stops;

	private int _deltax;

	private int _lastRang = 1;

	private int _lineX;

	private int _translatex;

	private Avm _avm;

	public GraphicalFollowUp(Composite arg0, int arg1) {
		super(arg0, SWT.BORDER | arg1);
		_display = arg0.getDisplay();
		_points = new Vector();
	//	_instance = this;
		addPaintListener(this);
	}

	public void updateCourse() {
		if (_avm == null)
			return;
		_lineY = (int) ((this.getSize().y * 1.0) / 2.0);
		_course = _avm.getModel().getCourse();
		_stops = new StopPoint[_course.getNombrePoint()];
		_deltax = (getSize().x / (NB_STOP_TO_DRAW + 1));
		for (int i = 1; i <= _course.getNombrePoint(); i++) {
			Point p = _course.getPointAvecRang(i);
			int x = i * _deltax;
			if (i == 1) {
				_lineX = x;
			}
			int y = _lineY;
			String name = p.getNom() + " (" + p.getHeureArriveeTheorique()
					+ ")";// [" + Integer.toString(p.getId()) + "]";
			StringTokenizer t = new StringTokenizer(name, " ");
			StringBuffer buf = new StringBuffer();
			int lastSize = 0;
			while (t.hasMoreElements()) {
				String tag = t.nextToken();
				lastSize += tag.length();
				buf.append(tag);
				if (lastSize > 8) {
					lastSize = 0;
					buf.append("\n");
				} else {
					buf.append(" ");
				}
			}
			name = buf.toString();
			_stops[i - 1] = new StopPoint(name, x, y, i);
		}
		redraw();
	}

	private void drawStopPoint(GC gc, int i, Color color) {
		_stops[i - 1].draw(gc, color);
	}

	public void updatePoint() {
		if (_avm == null)
			return;
		if (_avm.getModel().isHorsItineraire()) {
			setHorsItineraire(true);
			return;
		}
		_currentStop = _avm.getModel().getDernierPoint();
		_display.asyncExec(new Runnable() {
			public void run() {
				try {
					if (isDisposed() == false) {
						redraw();
					}
				} catch (Throwable t) {

				}
			}
		});
	}

	public void paintControl(PaintEvent pe) {
		int ox = _lineX;
		int oy = _lineY;
		pe.gc.setForeground(_display.getSystemColor(SWT.COLOR_RED));
		int rang;
		if (_currentStop != null) {
			rang = _currentStop.getRang();
			// if (rang % NB_STOP_TO_TRANSLATE == 0 && _lastRang < rang) {
			if ((rang - _lastRang) > NB_STOP_TO_TRANSLATE) {
				// int delta = _deltax * (rang-_lastRang-1);
				int delta = _stops[rang - 1].x - _stops[_lastRang - 1].x;
				_translatex -= delta;
				_lastRang = rang;
			}
		} else {
			rang = 0;
		}

		if (_course == null) {
			pe.gc.setLineWidth(1);
			pe.gc.drawLine(ox + _translatex, oy - 2, _deltax
					* (NB_STOP_TO_DRAW + rang + 1) + _translatex, oy - 2);
			pe.gc.drawLine(ox + _translatex, oy + 2, _deltax
					* (NB_STOP_TO_DRAW + rang + 1) + _translatex, oy + 2);
			pe.gc.setLineWidth(1);
			return;
		}
		pe.gc.setLineWidth(1);
		pe.gc.drawLine(ox + _translatex, oy - 2, _deltax
				* _course.getNombrePoint() + _translatex, oy - 2);
		pe.gc.drawLine(ox + _translatex, oy + 2, _deltax
				* _course.getNombrePoint() + _translatex, oy + 2);
		pe.gc.setLineWidth(1);

		int max = _course.getNombrePoint();
		int min = 1;

		Color color;
		for (int i = min; i <= max; i++) {
			color = (i > rang) ? _display.getSystemColor(SWT.COLOR_WHITE)
					: _display.getSystemColor(SWT.COLOR_RED);
			drawStopPoint(pe.gc, i, color);
		}

		if (_currentStop != null) {
			color = (_avm.getModel().isInsidePoint()) ? _display
					.getSystemColor(SWT.COLOR_GREEN) : _display
					.getSystemColor(SWT.COLOR_RED);
			drawStopPoint(pe.gc, _currentStop.getRang(), color);
		}

	}

	class StopPoint {
		int x, y, i;
		String name;

		public StopPoint(String name, int x, int y, int i) {
			this.x = x;
			this.y = y;
			this.i = i;
			this.name = name;

		}

		public void draw(GC gc, Color color) {
			gc.setLineWidth(2);
			gc.setForeground(_display.getSystemColor(SWT.COLOR_RED));
			gc.drawOval(x - (STOPPOINT_SIZE / 2) + _translatex, y
					- (STOPPOINT_SIZE / 2), STOPPOINT_SIZE, STOPPOINT_SIZE);
			gc.setLineWidth(1);

			gc.setBackground(color);
			gc.fillOval(x - (STOPPOINT_SIZE / 2) + 2 + _translatex, y
					- (STOPPOINT_SIZE / 2) + 2, STOPPOINT_SIZE - 4,
					STOPPOINT_SIZE - 4);
			gc.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));

			org.eclipse.swt.graphics.Point s = gc.textExtent(name,
					SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER);
			int dy = 25;
			int tx = x - (s.x / 2);
			/*
			 * if (tx < 0) { tx = 0; } if (tx + s.x > _instance.getSize().x && x <
			 * _instance.getSize().x) { tx = _instance.getSize().x - s.x - 20; }
			 */
			gc.setLineStyle(SWT.LINE_DASH);
			if (i % 2 == 0) {
				gc.drawText(name, tx + STOPPOINT_SIZE / 2 + _translatex, y
						+ STOPPOINT_SIZE + 3 + dy, SWT.DRAW_TRANSPARENT
						| SWT.DRAW_DELIMITER);
				gc.drawLine(x + _translatex, y + (STOPPOINT_SIZE / 2), x
						+ _translatex, y + STOPPOINT_SIZE + dy);
			} else {
				gc.drawText(name, tx + STOPPOINT_SIZE / 2 + _translatex, y
						- STOPPOINT_SIZE - s.y - dy, SWT.DRAW_TRANSPARENT
						| SWT.DRAW_DELIMITER);
				gc.drawLine(x + _translatex, y - (STOPPOINT_SIZE / 2), x
						+ _translatex, y - STOPPOINT_SIZE - dy);
			}
			gc.setLineStyle(SWT.LINE_SOLID);

		}

	}


	public void setAvm(Avm avm) {
		_avm = avm;
		updateCourse();
		updatePoint();
	}

	public void setHorsItineraire(boolean b) {
		// TODO Auto-generated method stub

	}



}
