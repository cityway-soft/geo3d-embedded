package org.angolight.hmi.leds;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.angolight.device.leds.Leds;
import org.angolight.device.leds.LedsInjector;
import org.angolight.hmi.leds.bundle.Activator;
import org.angolight.hmi.leds.bundle.ConfigImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.util.measurement.State;

public class LedsImpl implements org.angolight.hmi.leds.Leds, LedsInjector,
		ConfigurableService, ConsumerService, ManageableService,
		SequenceManager {

	private Logger _log;
	private ConfigImpl _config;
	private Hashtable _hashstates = new Hashtable();
	private Leds _leds;
	private Hashtable _hashPriorityStatesIndexes = new Hashtable();
	private int _statesValue[];
	private String[] _statesName;

	private boolean _initialized = false;
	private String _previousState = "";

	private static int TIMEOUT = 5000;// millisecondes

	private Scheduler scheduler = new Scheduler();
	private DisplayTask displayTask;

	public LedsImpl() {
		_log = Activator.getDefault().getLogger();
		_log.setPriority(Priority.DEBUG);
		displayTask = new DisplayTask();
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
	}

	public void setLeds(org.angolight.device.leds.Leds leds) {
		_leds = leds;
		if (_leds != null) {
			_initialized = false;
			try {
				int result = _leds.M((byte) 0, (byte) 0, false);
				if (result == 0) {
					initializeSequence();
					_leds.X(getSequenceAddress("" + 100), (byte) 1, (byte) 0,
							false);
				} else {
					_initialized = true;
				}

			} catch (RuntimeException e) {
				_initialized = true;
			}
		}
	}

	public void unsetLeds(org.angolight.device.leds.Leds leds) {
		_leds = null;
	}

	private void waitForInitialization() {
		int cpt = 0;
		long fraction = TIMEOUT / 25;
		_log.debug("initialized:" + _initialized);
		while (!_initialized) {
			try {
				_log.debug("sleep:" + fraction);
				Thread.sleep(fraction);
			} catch (InterruptedException e) {
			}
			cpt += fraction;
			_initialized = _initialized || (cpt >= TIMEOUT);
		}
	}

	public void notify(Object obj) {
		if (obj instanceof State) {
			State state = (State) obj;
			_log.debug(state);
			if (state.getName().equals(UserSessionService.class.getName())) {
				if (state.getValue() == UserSessionService.AUTHENTICATED) {
					_statesValue[NOT_AUTHENTICATED_STATE_ID] = 0;
				} else {
					_statesValue[NOT_AUTHENTICATED_STATE_ID] = 1;
				}
				// display();
				scheduler.execute(displayTask);
			} else if (state.getName().equals(
					org.angolight.device.leds.Leds.class.getName())) {
				_log.debug("notify: state 'end sequence'.............................................");
				_initialized = true;
				// display();
				scheduler.execute(displayTask);
			} else {
				// waitForInitialization();
				// for (int i = 0; i < _statesName.length; i++) {
				// if (state.getName().equals(_statesName[i])) {
				// _statesValue[i] = state.getValue();
				// break;
				// }
				// }
				// display();
				scheduler.execute(new InitializeAndDisplayTask(state));
			}
		}
	}

	class InitializeAndDisplayTask implements Runnable {
		private State state;

		InitializeAndDisplayTask(State state) {
			this.state = state;
		}

		public void run() {
			_log.debug("Wait for initialization...");
			waitForInitialization();
			for (int i = 0; i < _statesName.length; i++) {
				if (state.getName().equals(_statesName[i])) {
					_statesValue[i] = state.getValue();
					break;
				}
			}
			display();
		}

	}

	class DisplayTask implements Runnable {

		public void run() {
			_log.debug("Displaying leds...");
			display();
		}

	}

	public void start() {
		if (_config != null) {
			String list = _config.getStateList();
			StringTokenizer t = new StringTokenizer(list, ",");
			_statesName = new String[t.countTokens()];
			int i = 0;
			while (t.hasMoreElements()) {
				_statesName[i] = t.nextToken();
				i++;
			}

			_statesValue = new int[_statesName.length];
		}
		_statesValue[NOT_AUTHENTICATED_STATE_ID] = 1;
	}

	public void stop() {
		_leds.M((short) 0, (byte) 1, false);
		clear();
	}

	private void display() {
		_log.debug("Display ango state...");
		String state = getPriorState();
		if (!state.equals(_previousState)) {
			display(state);
			_previousState = state;
		}
	}

	public int getState(int i) {
		try {
			return _statesValue[i];
		} catch (RuntimeException e) {
			for (int j = 0; j < _statesValue.length; j++) {
				_log.error(_statesName[j] + "=" + _statesValue[j]);
			}
			throw e;
		}

	}

	public String[] getStatesName() {
		return _statesName;
	}

	/*
	 * retourne l'état prioritaire (NotAutenticated, FreeWheel,
	 * EngineOnVehiculeStopped ou BO) à afficher sur l'IHM Led
	 */
	public String getPriorState() {
		// -- existe-t-il des états plus prioritaires que l'état du BO, à
		// présenter sur l'IHM Led ?
		int specificCase[] = getPriorityEvents(getState(BO_STATE_ID));

		String ledState = null;
		if (specificCase != null) {
			for (int i = 0; i < specificCase.length; i++) {
				_log.debug("specificCase[" + i + "]=" + specificCase[i]
						+ ", getState => " + getState(specificCase[i]));
				if (getState(specificCase[i]) > 0) {
					// -- oui, il est nécessaire présenter un état plus
					// prioritaire que BO
					ledState = _statesName[specificCase[i]];
					_log.debug("prioritary state: " + ledState);
					break;
				}
			}
		}
		if (ledState == null) {
			// -- non ; on peut présenter sur l'IHM Led le jeux de Leds
			// correspondant à l'état courant de du BO
			ledState = "BO" + getState(BO_STATE_ID) + STATE_TAG;
		}
		return ledState;
	}

	public int[] getPriorityEvents(int bostate) {
		String sBoState = Integer.toString(bostate);
		String sPriorityEvents = _config.getPriorityEvents(sBoState);
		if (sPriorityEvents == null) {
			return null;
		}
		int[] priority = (int[]) _hashPriorityStatesIndexes.get(sBoState);
		if (priority == null) {
			StringTokenizer t = new StringTokenizer(sPriorityEvents, ",");
			priority = new int[t.countTokens()];
			for (int i = 0; i < priority.length; i++) {
				priority[i] = Integer.parseInt(t.nextToken());
			}
			_hashPriorityStatesIndexes.put(sBoState, priority);
		}
		return priority;
	}

	public int display(String state) {
		String cmd = _config.getState(state);
		if (cmd == null) {
			_log.error("Unknown state '" + state + "'");
			return ERR_UNKNOWN_STATE;
		}
		LedCommand ledcommand = LedCommandFactory.getInstance(cmd, this);
		_log.debug(ledcommand);
		int result = ledcommand.execute(_leds);
		_log.debug("=>result:" + result);

		return result;
	}

	public Enumeration getLedStates() {
		return _hashstates.elements();
	}

	public byte getSequenceAddress(String name) {
		byte address = 0;
		Properties p = _config.getSequences();
		TreeSet list = new TreeSet(new Comparator() {
			public int compare(Object object1, Object object2) {
				return Integer.parseInt((String) object1)
						- Integer.parseInt((String) object2);
			}
		});
		list.addAll(p.keySet());
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (key.equals(name)) {
				break;
			}
			String text = p.getProperty(key);
			StringTokenizer st = new StringTokenizer(text, ";");
			address += st.countTokens();
		}
		return address;
	}

	public int initializeSequence() {
		int result = Leds.OK;
		byte address = 0;
		Properties p = _config.getSequences();
		TreeSet list = new TreeSet(new Comparator() {
			public int compare(Object object1, Object object2) {
				return Integer.parseInt((String) object1)
						- Integer.parseInt((String) object2);
			}
		});
		list.addAll(p.keySet());

		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String name = (String) iterator.next();
			String text = p.getProperty(name);
			StringTokenizer st = new StringTokenizer(text, ";");
			while (st.hasMoreTokens()) {
				String element = st.nextToken();
				int index = element.indexOf(',');
				short states = Short
						.parseShort(element.substring(0, index), 16);
				byte period = (byte) (Integer.valueOf(
						element.substring(index + 1)).intValue() / 10);
				if (st.countTokens() == 0) {
					result = _leds.J(address++, states, period, true);
				} else {
					result = _leds.I(address++, states, period, true);
				}
				if (result != Leds.OK) {
					return result;
				}
			}
		}
		return Leds.OK;
	}

	public void clear() {
		LedCommandFactory.clear();
	}

	public static class LedCommandFactory {
		public static Map _hash = new Hashtable();

		public static void clear() {
			_hash.clear();
		}

		public static LedCommand getInstance(String cmd,
				SequenceManager sequenceManager) {
			LedCommand result = (LedCommand) _hash.get(cmd);
			if (result == null) {
				result = LedCommand.getInstance(cmd, sequenceManager);
				_hash.put(cmd, result);
			}
			return result;
		}

	}

	// callbacks command group
	public int S(boolean check) {
		return _leds.S(check);
	}

	public int T(boolean check) {
		return _leds.T(check);
	}

	public int X(byte address, byte cycle, byte period, boolean check) {
		return _leds.X(address, cycle, period, check);
	}
}
