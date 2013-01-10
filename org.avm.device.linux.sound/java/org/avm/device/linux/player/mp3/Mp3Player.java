package org.avm.device.linux.player.mp3;

import java.io.File;

import org.apache.log4j.Logger;
import org.avm.device.linux.player.mp3.bundle.Activator;
import org.avm.device.player.Player;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;

public class Mp3Player implements Player, ConfigurableService {

	public static final String PLAYER = "mpg123 -o alsa";
	protected String _name;
	protected Process _process;
	protected Logger _log;
	private PlayerConfig _config;

	public Mp3Player() {
		_log = Activator.getDefault().getLogger();
	}

	public void open(String name) {
		if (_process == null && name != null) {
			File fd = new File(name);
			if (fd.exists()) {
				_name = name;
				return;
			}
		}
		throw new RuntimeException("[DSU] echec opening mp3 " + name);
	}

	public void play() {
		if (_process == null && _name != null) {
			try {
				_process = Runtime.getRuntime().exec(
						_config.getPlayerCommand() + " " + _name);
				_process.waitFor();
			} catch (Exception e) {
				_log.error(e.getMessage());
			} finally {
				_process = null;
			}
		}
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		if (_process != null) {
			_process.destroy();
		}
	}

	public void close() {
		if (_process == null) {
			_name = null;
		}
	}

	public void configure(Config config) {
		_config = (PlayerConfig) config;

	}

}
