package org.avm.business.recorder;

import org.apache.log4j.Logger;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;

public class Journalizable implements JDBInjector {

	private static String TAG = "recorder";

	private JDB _jdb;

	protected Logger _log = Logger.getInstance(this.getClass());

	public Journalizable() {
		// _log.setPriority(Priority.DEBUG);
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	protected void journalize(String message) {
		if (_log.isDebugEnabled()) {
			_log.debug(message);
			if (_jdb == null) {
				_log.warn("JDB is null!");
			}
		}
		if (_jdb != null) {
			try {
				_jdb.journalize(TAG, message);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}
