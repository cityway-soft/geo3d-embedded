package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.position.Position;

public class PositionProcessor extends Processor {

	private static final long TIME_FOR_IMMOBILISATION = 15 * 1000;
	private static final double SPEED_LIMIT_UP = (90.0d / 3.6d);
	private static final double SPEED_LIMIT_DOWN = (85.0d / 3.6d);
	private static String TARGET = Position.class.getName();
	private int _capReference;
	private int _dernierCap;
	private boolean _variationCap;

	private Logger _log = Logger.getInstance(PositionProcessor.class.getName());
	private Position _lastNonZeroPosition;
	private long T0Immo;
	private boolean fSpeedUp=false;

	protected PositionProcessor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		_log.setPriority(Priority.DEBUG);
	}

	public Map evaluate() {
		Map result = new HashMap();
		return result;
	}

	public Map merge(Map measures) {
		return evaluate();
	}

	public void reset() {
		// NOTHING TO DO
	}

	public void update(Object o) {
		if (o instanceof Position) {
			Position position = (Position) o;
			computeCap(position);
			computeZeroPosition(position);
			computeImmo(position);
			computeSpeed(position);
		}
	}

	private void computeCap(Position position) {
		if (position.getSpeed().getValue() > 1d) {
			int cap = (int) (position.getTrack().getValue() * 180d / Math.PI);
			int delta = Math.abs((cap - _dernierCap));
			if (delta <= 1) {
				delta = Math.abs((cap - _capReference));
				if (delta > 4 && _variationCap == true) {
					_variationCap = false;
					_capReference = cap;
					journalize("AUTO;d"); //$NON-NLS-1$
				}
			} else {
				delta = Math.abs((cap - _capReference));
				if (delta > 2 && _variationCap == false) {
					_capReference = cap;
					journalize("AUTO;v"); //$NON-NLS-1$
				}
				_variationCap = true;
			}
			_dernierCap = cap;
		}
	}

	private void computeZeroPosition(Position position) {
		if (position.getLatitude().getValue() == 0
				&& position.getLongitude().getValue() == 0) {
			if (_lastNonZeroPosition != null) {
				double lat = _lastNonZeroPosition.getLatitude().getValue();
				double lon = _lastNonZeroPosition.getLongitude().getValue();
				journalize("GPS;LOST;" + lat + ";" + lon);
				_lastNonZeroPosition = null;
			}
		} else {
			if (_lastNonZeroPosition == null) {
				journalize("GPS;FIND");
			}
			_lastNonZeroPosition = position;
		}
	}

	private void computeImmo(Position position) {
		if (position.getLatitude().getValue() != 0
				&& position.getLongitude().getValue() != 0) {
			double speed = position.getSpeed().getValue();
			if (speed < 1d) {
				if (T0Immo == 0L) {
					T0Immo = System.currentTimeMillis();
				}
			} else {
				if (T0Immo != 0L) {
					long delta = System.currentTimeMillis()-T0Immo;
					if (delta > TIME_FOR_IMMOBILISATION) {
						journalize("IMMO;" + (delta / 1000));
					}
					T0Immo = 0;
				}
			}
		}
	}

	private void computeSpeed(Position position) {
		if (position.getLatitude().getValue() != 0
				&& position.getLongitude().getValue() != 0) {
			double speed = position.getSpeed().getValue();
			
			if (speed > SPEED_LIMIT_UP) {
				if (fSpeedUp == false) {
					fSpeedUp = true;
					journalize("SPEED;90");
				}
			} else if (speed < SPEED_LIMIT_DOWN) {
				if (fSpeedUp == true) {
					fSpeedUp = false;
					journalize("SPEED;0");
				}
			}
		}
	}

	private void journalize(String value) {
		_jdb.journalize("PositionProcessor", value);
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {

		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new PositionProcessor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}

}
