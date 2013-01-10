package org.avm.device.fm6000.sound;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.fm6000.ac97.jni.COMVS_AC97;
import org.avm.device.fm6000.mp3.jni.COMVS_MP3;
import org.avm.device.fm6000.sound.Configuration.AudioEntry;
import org.avm.device.fm6000.sound.Configuration.AudioMonoEntry;
import org.avm.device.fm6000.sound.Configuration.AudioStereoEntry;
import org.avm.device.fm6000.sound.bundle.Activator;
import org.avm.device.sound.Sound;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;

public class SoundImpl implements Sound, ConfigurationVisitor,
		ConfigurableService {

	private static final String DEFAULT = "default";
	protected HashMap _map = new HashMap();
	private SoundConfig _config;
	private int _priority;
	private Logger _log;

	public void configure(String name) throws Exception {
		Properties p = _config.get(name);
		String text = p.getProperty(Sound.PRIORITY);
		if (text != null) {
			int priority = Integer.parseInt(text);
			if (_priority != 0 && priority > _priority) {
				throw new Exception("Sound all ready configured");
			}
			_priority = priority;
			Object o = _map.get(name);
			if (o != null & o instanceof Configuration) {
				Configuration config = (Configuration) o;
				config.accept(this);
				Logger log = Activator.getDefault().getLogger();
				log.debug("sound "+ name +" configured");
			}
		}

	}

	public void visit(Configuration config) {

		Logger log = Activator.getDefault().getLogger();
		log.debug("configure : \n" + config.toString());

		AudioStereoEntry stereo = null;
		AudioMonoEntry mono = null;
		AudioEntry entry = null;
		stereo = ((AudioStereoEntry) config.get(Configuration.MASTER));
		COMVS_AC97.Comvs_SetGain_out_Master(convert(stereo.getLeft()),
				convert(stereo.getRight()));
		COMVS_AC97.Comvs_Mute_out_Master((short) (stereo.isMute() ? 1 : 0));
		stereo = ((AudioStereoEntry) config.get(Configuration.HEADPHONE));
		COMVS_AC97.Comvs_SetGain_out_HeadPhone(convert(stereo.getLeft()),
				convert(stereo.getRight()));
		COMVS_AC97.Comvs_Mute_out_HeadPhone((short) (stereo.isMute() ? 1 : 0));
		mono = ((AudioMonoEntry) config.get(Configuration.MONO_OUT));
		COMVS_AC97.Comvs_SetGain_out_MonoOut(convert(mono.getValue()));
		COMVS_AC97.Comvs_Mute_out_MonoOut((short) (mono.isMute() ? 1 : 0));
		mono = ((AudioMonoEntry) config.get(Configuration.BEEP));
		COMVS_AC97.Comvs_SetGain_in_Beep(convert(mono.getValue()));
		COMVS_AC97.Comvs_Mute_in_Beep((short) (mono.isMute() ? 1 : 0));
		mono = ((AudioMonoEntry) config.get(Configuration.MICROPHONE));
		COMVS_AC97.Comvs_SetGain_in_Microphone(convert(mono.getValue()));
		COMVS_AC97.Comvs_Mute_in_Microphone((short) (mono.isMute() ? 1 : 0));
		entry = ((AudioEntry) config.get(Configuration.MICROPHONE_EXTRA));
		COMVS_AC97
				.Comvs_SetExtraGain_in_Microphone((short) (entry.isEnable() ? 0
						: 1));
		stereo = ((AudioStereoEntry) config.get(Configuration.LINE_IN));
		COMVS_AC97.Comvs_SetGain_in_LineIn(convert(stereo.getLeft()),
				convert(stereo.getRight()));
		COMVS_AC97.Comvs_Mute_in_LineIn((short) (stereo.isMute() ? 1 : 0));
		mono = ((AudioMonoEntry) config.get(Configuration.PHONE));
		COMVS_AC97.Comvs_SetGain_in_Phone(convert(mono.getValue()));
		COMVS_AC97.Comvs_Mute_in_Phone((short) (mono.isMute() ? 1 : 0));
		stereo = ((AudioStereoEntry) config.get(Configuration.CD));
		COMVS_AC97.Comvs_SetGain_in_CD(convert(stereo.getLeft()),
				convert(stereo.getRight()));
		COMVS_AC97.Comvs_Mute_in_CD((short) (stereo.isMute() ? 1 : 0));
		stereo = ((AudioStereoEntry) config.get(Configuration.VIDEO));
		COMVS_AC97.Comvs_SetGain_in_Video(convert(stereo.getLeft()),
				convert(stereo.getRight()));
		COMVS_AC97.Comvs_Mute_in_Video((short) (stereo.isMute() ? 1 : 0));
		stereo = ((AudioStereoEntry) config.get(Configuration.AUX));
		COMVS_AC97.Comvs_SetGain_in_Aux(convert(stereo.getLeft()),
				convert(stereo.getRight()));
		COMVS_AC97.Comvs_Mute_in_Aux((short) (stereo.isMute() ? 1 : 0));
		stereo = ((AudioStereoEntry) config.get(Configuration.PCM));
		COMVS_AC97.Comvs_SetGain_in_PCM(convert(stereo.getLeft()),
				convert(stereo.getRight()));
		COMVS_AC97.Comvs_Mute_in_PCM((short) (stereo.isMute() ? 1 : 0));
		entry = ((AudioEntry) config.get(Configuration.MIC_ONLY));
		COMVS_AC97.Comvs_SetMicro_MonoOut((short) (entry.isEnable() ? 0 : 1));

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

	private short convert(short value) {
		return (short) (31 - (((float) value) * 31d / 100d));
	}

	private void initilize() {
		Dictionary map = _config.get();
		for (Enumeration ennum = map.elements(); ennum.hasMoreElements();) {
			Object o = ennum.nextElement();
			if (o != null) {
				Properties p = (Properties) o;
				String url = p.getProperty(Sound.URL);
				String name = p.getProperty(Sound.NAME);
				if (url != null && name != null) {
					InputStream in = getClass().getResourceAsStream(url);
					Configuration value = Configuration.parse(in);
					_map.put(name, value);
					_log.debug("sound "+ name +" initilized");
				}
			}
		}
	}

	public void setMasterVolume(short volume) {
		_log.debug("set master volume : " + volume);
		COMVS_AC97.Comvs_SetGain_out_Master(convert(volume), convert(volume));
		COMVS_AC97.Comvs_Mute_out_Master((short) ((volume == 0) ? 1 : 0));
		_log.debug("set master volume : ok");
	}
}
