package org.angolight.bo.states;

import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.angolight.bo.Bo;
import org.angolight.bo.BoState;
import org.angolight.bo.impl.BoConfig;
import org.angolight.bo.zones.CurvesThresholds;
import org.angolight.kinetic.Kinetic;
import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.measurement.State;

public class ZonesManagerImpl implements ZonesManager, ZonesManagerCmd,
		ConfigurableService, ProducerService, ManageableService {
	public static final int LEDS_IHM_PERIOD = 800;

	private ZonesManagerContext _zonesStateMachine;
	private Timer _timer = null;
	private HalfPeriodTimerTask _halfPeriodTimerTask = null;
	private int _period = LEDS_IHM_PERIOD;
	private CurvesThresholds _thresholds = null;
	private Kinetic _kinetic;
	private int _zone;
	private BoConfig _config;
	private Logger _log;
	private ProducerManager _producer = null;

	private int _previousZone;

	public ZonesManagerImpl() {
		_log = Logger.getInstance(this.getClass());
		_zonesStateMachine = new ZonesManagerContext(this);
		if (_log.isDebugEnabled()) {
			_zonesStateMachine.setDebugFlag(true);
		}
	}

	public void configure(Config config) {
		_config = (BoConfig) config;
		if (config != null) {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat.format(_config.getCurvesFileName(),
					arguments);

			_thresholds = new CurvesThresholds(_config.getTriggerPercent(),
					text);
		}
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public String getCurvesVersion() {
		return _thresholds.getCurvesVersion();
	}

	// /////////////////////////////////////////////////////////////////////////
	// Actions de la machine etat de gestion des zones (ZonesManagerContext)
	// /////////////////////////////////////////////////////////////////////////

	public void notifyState(int state) {
		if (_producer != null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Notify State " + state + "=> " + BoState.NAMES[state]);
			}
			_producer.publish(new State(state, Bo.class.getName()));
		}
	}

	public void setPeriod(int period) {
		_period = period;
	}

	// /////////////////////////////////////////////////////////////////////////
	// Points d'entrée de la machine état de gestion des zones
	// (ZonesManagerContext)
	// /////////////////////////////////////////////////////////////////////////

	public synchronized void onKinetic(Kinetic kinetic) {
		try {
			if (_kinetic != null)
				_kinetic.dispose();

			_kinetic = kinetic;

			if (_log.isDebugEnabled()) {
				_log.debug("[" + System.currentTimeMillis() + "] - Speed["
						+ _kinetic.getSpeed() + "] - Acc["
						+ _kinetic.getAcceleration() + "]");
			}

			if (_zonesStateMachine != null) {
				_zonesStateMachine.onKinetic();
			}
		} catch (Exception e) {
			_log.error("Error onKinetic:", e);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// Guards conditions pour les transitions de la machine etat de gestion des
	// zones (ZonesManagerContext1)
	// /////////////////////////////////////////////////////////////////////////

	public double getVmaxDown() {
		return _config.getVMaxDown();// _vmax_down;
	}

	public double getVmaxUp() {
		return _config.getVMaxUp();// _vmax_up;
	}

	public double getVminDown() {
		return _config.getVMinDown();// _vmin_down;
	}

	public double getVminUp() {
		return _config.getVMinUp();// _vmin_up;
	}

	public double getDownThreshold(int curve) {
		if (_kinetic != null)
			return _thresholds.getDownThreshold(curve, (int) Math
					.round(_kinetic.getSpeed() * 3.6));
		else
			return 0;
	}

	public double getUpThreshold(int curve) {
		if (_kinetic != null)
			return _thresholds.getUpThreshold(curve, (int) Math.round(_kinetic
					.getSpeed() * 3.6));
		else
			return 0;
	}

	public double getAcceleration() {
		if (_kinetic != null)
			return _kinetic.getAcceleration();
		else
			return 0;
	}

	public double getSpeed() {
		if (_kinetic != null)
			return _kinetic.getSpeed();
		else
			return 0;
	}

	public void start() {

	}

	public void stop() {

	}

	public int getCurrentZone() {
		return _zone;
	}

	private boolean isZone(int zone) {
		int indexCurveUp = 6 - zone;
		int indexCurveDown = zone + 3;

		int indexCurveUp2 = 5 - zone;
		int indexCurveDown2 = zone + 4;

		boolean result = true;

		if ((indexCurveUp < 5)) {
			result &= (getAcceleration() > getUpThreshold(indexCurveUp));
			result |= (getAcceleration() < getDownThreshold(indexCurveDown));
		}

		if (result && (indexCurveUp2 > 0)) {
			boolean res = true;
			res &= (getAcceleration() < getUpThreshold(indexCurveUp2));
			res &= (getAcceleration() > getDownThreshold(indexCurveDown2));

			result &= res;
		}

		return result;
	}

	public int getNextZone() {
		for (int i = BoState.VminState; i <= BoState.Zone5State; i++) {
			if (isZone(i)) {
				// _log.debug("NextZone :" + i + "delta => " + (i - _zone));
				return i;
			}
		}
		return 0;
	}

	public void zoneDown() {
		if (_zone > BoState.VminState) {
			_zone--;
			notifyZone();
		}
		if (_log.isDebugEnabled()) {
			_log.debug("Zone Down : " + _zone);
		}
	}

	public void zoneUp() {
		if (_zone < BoState.Zone5State) {
			_zone++;
			notifyZone();
		}
		if (_log.isDebugEnabled()) {
			_log.debug("Zone Up " + _zone);
		}
	}

	private void notifyZone() {
		if (_previousZone != _zone) {
			notifyState(_zone);
		}
		_previousZone = _zone;
	}

	public void zoneVMax() {
		_zone = BoState.VmaxState;
		notifyState(_zone);
	}

	public void zoneInit() {
		_zone = BoState.VminState;
		notifyState(_zone);
	}

	// /////////////////////////////////////////////////////////////////////////
	// Embedded classes de zone manager
	// /////////////////////////////////////////////////////////////////////////

	public void onTimer() {
		_log.debug("OnTimer");
		try {
			if (_zonesStateMachine != null)
				_zonesStateMachine.onTimer();
		} catch (Exception e) {
			_log.error(e.getMessage());
		}
	}

	public class HalfPeriodTimerTask extends TimerTask {
		public void run() {
			try {
				onTimer();
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	public void killTimer() {
		try {
			if (_timer != null) {
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
			if (_timer != null) {
				_log.debug("Killing a half period timer !");
				_timer.cancel();
				_timer = null;
			}

			if (_halfPeriodTimerTask != null) {
				_halfPeriodTimerTask.cancel();
				_halfPeriodTimerTask = null;
			}

			_timer = new Timer();
			_halfPeriodTimerTask = new HalfPeriodTimerTask();

			_timer.schedule(_halfPeriodTimerTask, _period / 2);
		} catch (Exception e) {
			_log.error(e);
			_timer = null;
		}
	}

	public void debug(String string) {
		if (_log.isDebugEnabled()) {
			_log.debug("==>>> " + string + " (zone=" + _zone + " / "+BoState.NAMES[_zone]+") <<<==");
		}
	}

}
