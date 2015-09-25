package org.avm.elementary.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.net.URL;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.knopflerfish.shared.cm.CMDataReader;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;

public abstract class AbstractConfig implements Config, ManagedService,
		ManageableService {

	private static final String CONFIG_VERSION = "org.avm.config.version";

	protected static final String PLATEFORM = "org.avm.plateform";

	protected Logger _log = Logger.getInstance(this.getClass());

	protected ConfigurationAdmin _cm;

	protected Dictionary _config;

	protected boolean _initialize = false;

	protected ComponentContext _context;

	protected ServiceRegistration _registration;

	public static String getProperty(String key, String value) {
		String result = value;
		try {
			result = System.getProperty(key);
			if (result == null) {
				result = value;
			}
		} catch (Exception e) {
			result = value;
		}
		return result;
	}

	public AbstractConfig(ComponentContext context, ConfigurationAdmin cm) {
		_context = context;
		_cm = cm;
	}

	public void updated(Dictionary config) throws ConfigurationException {
		if (isModified()) {
			String pid=getPid();
			_context.disableComponent(pid);
			if (config == null) {
				_config = getDefault();
				updateConfig();
			} else {
				_config = config;
				_initialize = false;
			}
			
			//-- wait for component end before enable it again.
			ServiceReference sr = _context.getServiceReference();
			int cpt=0;
			while(cpt < 20 && sr != null){
				cpt++;
				try {
					_log.warn("Component " +pid + " is not yet disabled. Wait...");
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				sr = _context.getServiceReference();
			}
			
			if( sr != null){
				_log.error("Component " +pid + " cannot not be disabled. ");
			}else{
				_log.debug("Component " +pid + " is disabled. ");
			}
			
			_context.enableComponent(pid);
		}
	}

	private Dictionary loadDefaultConfig(InputStream in) throws Exception {
		CMDataReader cmDataReader = new CMDataReader();
		PushbackReader r = new PushbackReader(new BufferedReader(
				new InputStreamReader(in, CMDataReader.ENCODING), 8192), 8);
		Hashtable result = cmDataReader.readCMData(r);
		return result;
	}

	protected String getResourceFileName(){
		String result = "config.xml";
		Bundle bundle = _context.getBundleContext().getBundle();
		Enumeration e = bundle.findEntries("/", "config*.xml", true);
		while (e.hasMoreElements()) {
			URL url = (URL) e.nextElement();
			String suffix = "config-"+ System.getProperty(PLATEFORM)+".xml";
			if(url.toExternalForm().endsWith(suffix)){
				result = url.getFile();				
				break;
			}
		}
		return result;
	}
	
	protected Dictionary getDefault() {
		Dictionary result = null;

		try {
			Bundle bundle = _context.getBundleContext().getBundle();
			URL url = bundle.getResource(getResourceFileName());
			if (url != null) {
				result = loadDefaultConfig(url.openStream());
			}
		} catch (Exception e) {
		}

		if (result == null) {
			result = new Hashtable();
		}
		result.remove("service.bundleLocation");
		result.remove("org.knopflerfish.dummy.property");
		result.put("config.date", (new Date())
						.toString());
		result.put(Constants.SERVICE_PID, getPid());
		
		if(result.get(CONFIG_VERSION) == null){
			result.put(CONFIG_VERSION, new Integer(1));
		}

		return result;
	}

	public void updateConfig() {
		updateConfig(true);
	}

	public void updateConfig(boolean initialize) {

		if (_cm != null) {
			Configuration config;
			try {
				config = _cm.getConfiguration(getPid());
				if (config != null) {
					_initialize = initialize;
					config.update(_config);
					_log.debug("Config updated ");
				}
			} catch (Exception e) {
				_log.error(e.getMessage());
			}
		}
	}

	public void delete() {
		Configuration config;
		try {
			config = _cm.getConfiguration(getPid());
			config.delete();
		} catch (IOException e) {
			_log.error(e.getMessage());
		}

	}

	public void start() {

		try {
			Configuration config = _cm.getConfiguration(getPid());

			if (config != null && config.getProperties() != null) {
				_config = config.getProperties();
				Dictionary defaultConfig  = getDefault();
				
				int v1 = (_config.get(CONFIG_VERSION) == null)?0:((Integer)_config.get(CONFIG_VERSION)).intValue();
				int v2 = ((Integer)defaultConfig.get(CONFIG_VERSION)).intValue();
				
				if (v2 > v1){
					_config = getDefault();
					updateConfig(false);
				}				
			} else {
				_config = getDefault();
				updateConfig(false);
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		Hashtable properties = new Hashtable();
		properties.put("service.pid", getPid());
		_registration = _context.getBundleContext().registerService(
				ManagedService.class.getName(), this, properties);
	}

	public void stop() {
		_registration.unregister();
		_config = null;
	}

	public boolean equals(Dictionary config) {

		if (config == _config)
			return true;

		if (config.size() != _config.size())
			return false;

		for (Enumeration iter = config.keys(); iter.hasMoreElements();) {
			Object key = iter.nextElement();
			Object value = config.get(key);
			if (!value.equals(_config.get(key)))
				return false;
		}

		return true;
	}

	public String toString() {
		if (_config != null) {
			return _config.toString();
		}

		return "null";
	}

	protected String getPid() {
		if (_context != null) {
			return (String) _context.getProperties().get("service.pid");
		}
		return null;
	}

	protected boolean isModified() {
		return _initialize;
	}

	protected Properties load(String text) {
		Properties p = new Properties();
		ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
		try {
			p.load(in);
			in.close();
		} catch (IOException e) {
		}

		return p;
	}

	protected String save(Properties p) {
		return save(p, null);
	}

	protected String save(Properties p, String comment) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			p.store(out, comment);
			out.close();
		} catch (IOException e) {
		}

		return new String(out.toByteArray());
	}
}
