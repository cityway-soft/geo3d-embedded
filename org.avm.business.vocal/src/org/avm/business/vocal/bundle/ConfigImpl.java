package org.avm.business.vocal.bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.business.vocal.VocalConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements VocalConfig {

	public static final String FILENAME_TAG = "filename";
	public static final String SLEEP_TAG = "sleep-before-play";
	public static final String LANGUAGES_TAG = "languages";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getFileName() {
		return (String) _config.get(FILENAME_TAG);
	}

	public void setFileName(String fileName) {
		_config.put(FILENAME_TAG, fileName);

		Object[] arguments = { System.getProperty("org.avm.home") };
		String dir = MessageFormat.format(fileName, arguments);

		File file = new File(dir + "/" + LANGUAGES_TAG);
		String languages = "fr";
		if (file.exists()) {
			_log.debug("File "+ (dir + "/" + LANGUAGES_TAG) + " exists");
			BufferedReader br = null;

			try {

				String line;

				br = new BufferedReader(new FileReader(file));

				if((line = br.readLine()) != null) {
					languages = line;
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} else{
			_log.warn("File "+ (dir + "/" + LANGUAGES_TAG) + " not found");
		}
		_config.put(LANGUAGES_TAG, languages);
	}

	public long getSleepBeforePlay() {
		String time = (String) _config.get(SLEEP_TAG);
		if (time == null) {
			time = "0";
		}
		return Long.parseLong(time);
	}

	public void setSleepBeforePlay(long time) {
		_config.put(SLEEP_TAG, Long.toString(time));
	}

	public void addProperty(String key, Properties p) {
		String text = save(p);
		_config.put(key, text);
	}

	public Properties getProperty(String key) {
		if (key == null) {
			return getProperties();
		} else {
			String text = (String) _config.get(key);
			return load(text);
		}
	}

	public void removeProperty(String key) {
		_config.remove(key);
	}

	private Properties getProperties() {
		Properties props = new Properties();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			Properties p = getProperty(key);
			if (p != null) {
				props.put(key, p);
			}
		}
		return props;
	}

	public String[] getLanguages() {
		String languages = (String) _config.get(LANGUAGES_TAG);
		if (languages == null){
			languages = "fr";
		}
		StringTokenizer t = new StringTokenizer(languages, ",");
		String[] result = new String[t.countTokens()];
		int i=0;
		String lang;
		while(t.hasMoreElements()){
			lang=t.nextToken();
			result[i] = lang.trim();
			i++;
		}
		return result;
	}

	public void setLanguages(String langs) {
		_config.put(LANGUAGES_TAG, langs);
	}

}
