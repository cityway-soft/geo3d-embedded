package org.avm.elementary.wifi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.avm.device.wifi.Wifi;
import org.avm.device.wifi.WifiInjector;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.database.Database;
import org.avm.elementary.geofencing.Balise;

public class WifiManagerImpl implements WifiManager, ConfigurableService,
		ConsumerService, ManageableService, WifiInjector {

	private WifiManagerStateMachine _sm;

	private Logger _log;

	private Hashtable _map;

	private Wifi _wifi;

	private WifiManagerConfig _config;
	
	private Database database;

	public WifiManagerImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void notify(Object obj) {

		if (obj instanceof Balise) {
			Balise balise = (Balise) obj;
			if (isBaliseWifi(balise)) {
				if (balise.isInside()) {
					_sm.entryWifiZone();
				} else {
					_sm.exitWifiZone();
				}
			}
		}
	}

	private boolean isBaliseWifi(Balise balise) {
		boolean result = false;
		if (_map != null) {
			result = (_map.get(Integer.toString(balise.getId())) != null) ? true
					: false;
		}
		return result;
	}

	public void entryWifiZone() {
		_sm.entryWifiZone();
	}

	public void exitWifiZone() {
		_sm.exitWifiZone();
	}

	public void configure(Config config) {
		_config = ((WifiManagerConfig) config);
		if (config != null && database != null) {
			try {
				int timeout = Integer.parseInt(((WifiManagerConfig) config)
						.getDisconnectTimeout());
				_sm.setTimeout(timeout);
			} catch (Exception e) {
				_log.debug(e);
			}
			
			_map = new Hashtable();
			
			
			String attr = ((WifiManagerConfig) config).getBaliseAttr();
			if (attr != null){
				fillMapBaliseWithAttr(attr);
			}
				
		} else {
			_log.error ("No Config or No Database");
			_map = null;
		}
	}

	public void setWifi(org.avm.device.wifi.Wifi wifi) {
		_wifi = wifi;
	}

	public void unsetWifi(org.avm.device.wifi.Wifi wifi) {
		_wifi = null;
	}
	

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void unsetDatabase(Database database) {
		database = null;
	}

	public void start() {
		_sm = new WifiManagerStateMachine(_wifi);
		if (_config != null) {
			try {
				int timeout = Integer.parseInt(_config.getDisconnectTimeout());
				_sm.setTimeout(timeout);
			} catch (Exception e) {
				_log.debug(e);
			}
		} 
	}

	public void stop() {
		_sm = null;
	}
	
	private void fillMapBaliseWithAttr(String attr) {
		if (database != null) {
			ResultSet rs = database.sql("select * from attribut_point where ATT_ID='" + attr
					+ "'");
			try {
				if (rs.next()) {
					int ret = rs.getInt("PNT_ID");
					_log.debug(ret + "is a WiFi point");
					_map.put(Integer.toString(ret), Integer.toString(ret));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else {
			_log.error("no db");
		}
	}

}
