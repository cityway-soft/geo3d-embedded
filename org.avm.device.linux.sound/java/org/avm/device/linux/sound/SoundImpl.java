package org.avm.device.linux.sound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.linux.sound.bundle.Activator;
import org.avm.device.sound.Sound;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;

public class SoundImpl implements Sound, ConfigurableService {

	private static final String DEFAULT = "default";
	protected HashMap _map = new HashMap();
	private SoundConfig _config;
	private int _priority;
	private Logger _log;

	public SoundImpl() {
		_log = Activator.getDefault().getLogger();
	}

	public void configure(String name) throws Exception {
		Properties p = _config.get(name);
		String text = p.getProperty(Sound.PRIORITY);
		if (text != null) {
			int priority = Integer.parseInt(text);
			if (_priority != 0 && priority > _priority) {
				throw new Exception("Sound already configured");
			}
			_priority = priority;
			String tmp = System.getProperty("java.io.tmpdir");
			String url = p.getProperty(Sound.URL);
			String path = tmp
					+ url.substring(url.lastIndexOf(File.separatorChar));
			int result = execute(
					"alsactl -f " + path + " restore 0");
			String level = p.getProperty(Sound.VOLUME);
			if (level != null) {
				short volume = Short.parseShort(level);
				setMasterVolume(volume);
			}

			_log.debug("sound " + name + " configured (cr="+result+")");
		}

	}

	public void configure(Config config) {
		_config = (SoundConfig) config;
		_log = Activator.getDefault().getLogger();
		initilize();
		try {
			configure(DEFAULT);
		} catch (Exception e) {
			_log.error(e.toString());
		}

	}

	protected void initilize() {
		Dictionary map = _config.get();
		for (Enumeration ennum = map.elements(); ennum.hasMoreElements();) {
			Object o = ennum.nextElement();
			if (o != null) {
				Properties p = (Properties) o;
				String url = p.getProperty(Sound.URL);
				String name = p.getProperty(Sound.NAME);
				if (url != null && name != null) {
					InputStream in = getClass().getResourceAsStream(url);
					String tmp = System.getProperty("java.io.tmpdir");
					String path = tmp
							+ url.substring(url.lastIndexOf(File.separatorChar));
					save(in, path);
					_log.debug("sound " + name + " initilized");
				}
			}
		}
	}

	protected void save(InputStream in, String url) {
		BufferedReader reader = null;
		OutputStreamWriter writer = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			writer = new OutputStreamWriter(new FileOutputStream(url));

			while (true) {
				String text = reader.readLine();
				if (text == null) {
					break;
				}
				writer.write(text);
			}
		} catch (IOException e) {
			_log.error(e.getMessage());
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				_log.error(e.getMessage());
			}
		}
	}

	private short convert(short value, short max) {
		return (short) (value * max / 100d);
	}

	public void setMasterVolume(short volume) {
		try {
			int result = execute("amixer -c 0 sset Master "
							+ convert(volume, _config.getMaxVolume()));
			_log.debug("set master volume : " + volume +" (cr="+result+")");
		} catch (IOException e) {
			_log.error(e.getMessage());
		} catch (InterruptedException e) {
			_log.error(e.getMessage());
		}
	}

	private int execute(String command) throws IOException, InterruptedException {
		int result = -1;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
		}  finally {
			if (process != null) {
				result = process.exitValue();
			}
		}
		return result;
	}
}
