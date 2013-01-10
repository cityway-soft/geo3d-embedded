package org.avm.device.fm6000.player.mp3;

import org.apache.log4j.Logger;
import org.avm.device.fm6000.mp3.jni.COMVS_MP3;
import org.avm.device.fm6000.player.mp3.bundle.Activator;
import org.avm.device.player.Player;

public class Mp3Player implements Player {

	public static final int INVALID_HANDLE = -1;

	protected long _handle;

	Logger _log;

	public Mp3Player() {
		super();
		_log = Activator.getDefault().getLogger();
	}

	public void open(String name) {
		long handle = COMVS_MP3.open(name);
		if (handle != INVALID_HANDLE) {
			_handle = handle;
			_log.debug("[DSU] mp3 " + name + " opened : handle = " + handle);
		} else {
			throw new RuntimeException("[DSU] echec opening mp3 " + name);
		}
	}

	public void close() {
		COMVS_MP3.close(_handle);
	}

	public void pause() {
		COMVS_MP3.pause(_handle);
	}

	public void play() {
		COMVS_MP3.play(_handle);
	}

	public void resume() {
		COMVS_MP3.resume(_handle);
	}

	public void stop() {
		COMVS_MP3.stop(_handle);
	}
}
