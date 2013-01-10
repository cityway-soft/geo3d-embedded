package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.bo.BoState;
import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.avm.elementary.can.parser.fms.PGN_F003;
import org.avm.elementary.can.parser.fms.PGN_FEF1;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.measurement.State;

public class FreeWheel {

	// TODO -> ihm
	public static final String FREEWHEEL_STATE = "freewheel-state";
	public static final String FREEWHEL_TIME = "freewheel-time";
	public static final String FREEWHELL_DISTANCE = "freewheel-distance";

	private boolean _acceleratorPedalState;
	private boolean _acceleratorPedalPositionEnabled = false;
	private boolean _brakePedalState;
	private boolean _brakePedalPositionEnabled = false;
	private boolean _stateValid;

	protected IndicatorService _owner;
	protected IndicatorConfig _config;
	protected ProducerManager _producer;
	protected JDB _jdb;

	// measure
	private Measure _ft, _fd, _fs;

	private double _freewheelDistanceOffset;
	private long _freewheelTimeOffset;

	private boolean _freewheel = false;

	private static FreeWheel _instance;

	protected FreeWheel(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		_owner = owner;
		_config = config;
		_producer = producer;
		_jdb = jdb;
		initialize();
	}

	public static FreeWheel getInstance(IndicatorService owner,
			IndicatorConfig config, ProducerManager producer, JDB jdb) {
		if (_instance == null) {
			_instance = new FreeWheel(owner, config, producer, jdb);
		}
		return _instance;
	}

	private void initialize() {
		_ft = new Measure(FreeWheel.FREEWHEL_TIME, Unit.s);
		_owner.add(_ft);
		_fd = new Measure(FreeWheel.FREEWHELL_DISTANCE, Unit.m);
		_owner.add(_fd);
		_fs = new Measure(FreeWheel.FREEWHEEL_STATE, Unit.unity);
		_owner.add(_fs);
		_freewheelDistanceOffset = 0;
		_freewheelTimeOffset = System.currentTimeMillis();
	}

	public Map evaluate() {
		Map result = new HashMap();

		if (_freewheel) {
			Measure measure = _owner.get(KineticProcessor.DISTANCE);
			if (measure == null)
				return null;
			double distance = measure.getValue();
			long now = System.currentTimeMillis();
			long delay = now - _freewheelTimeOffset;
			if (delay > 0) {
				_ft.add(delay);
			}
			double delta = distance - _freewheelDistanceOffset;
			if (delta > 0) {
				_fd.add(delta);
			}
			_freewheelTimeOffset = now;
			_freewheelDistanceOffset = distance;
		}

		result.put(_ft.getName(), new Measure(_ft));
		result.put(_fd.getName(), new Measure(_fd));
		return result;
	}

	public Map merge(Map measures) {
		_ft.add(((Measure) measures.get(_ft.getName())));
		_fd.add(((Measure) measures.get(_fd.getName())));
		return null;
	}

	public void reset() {
		_ft.setValue(0);
		_fd.setValue(0);
		_freewheelDistanceOffset = 0;
		_freewheelTimeOffset = System.currentTimeMillis();
	}

	public void update(PGN_F003 pgn) {
		if (pgn.spn91.isValid()) {
			_acceleratorPedalPositionEnabled = true;
			_acceleratorPedalState = (pgn.spn91.getValue() != 0);
			update();
		}
	}

	public void update(PGN_FEF1 pgn) {
		if (pgn.spn597.isValid()) {
			_brakePedalPositionEnabled = true;
			_brakePedalState = (pgn.spn597.getValue() != 0);
			update();
		}
	}

	public void update(State o) {
		_stateValid = ((((State) o).getValue() > BoState.VminState) && (((State) o)
				.getValue() < BoState.VmaxState));
		update();
	}

	private void update() {
		boolean freewheel = _acceleratorPedalPositionEnabled
				&& _brakePedalPositionEnabled && _stateValid
				&& (!(_acceleratorPedalState || _brakePedalState));

		if (_freewheel != freewheel) {
			Measure measure = _owner.get(KineticProcessor.DISTANCE);
			if (measure == null)
				return;
			double distance = measure.getValue();
			long now = System.currentTimeMillis();
			_fs.setValue(freewheel ? 1 : 0);
			_producer.publish(new State(freewheel ? 1 : 0, FREEWHEEL_STATE));

			_freewheel = freewheel;
			if (freewheel) {
				_freewheelTimeOffset = now;
				_freewheelDistanceOffset = distance;
			} else {

				long delay = now - _freewheelTimeOffset;
				if (delay > 0) {
					_ft.add(delay);
				}

				double delta = distance - _freewheelDistanceOffset;
				if (delta > 0) {
					_fd.add(delta);
				}
			}
		}
	}

}
