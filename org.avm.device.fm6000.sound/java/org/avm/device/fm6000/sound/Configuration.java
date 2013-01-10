package org.avm.device.fm6000.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Configuration {

	private static final String AC97_REVERSE_RIGHT_AND_LEFT = "ac97.reverse-right-and-left";
	public static final String MASTER = "Master";
	public static final String HEADPHONE = "HeadPhone";
	public static final String MONO_OUT = "MonoOut";
	public static final String BEEP = "Beep";
	public static final String MICROPHONE = "Microphone";
	public static final String MICROPHONE_EXTRA = "MicrophoneExtra";
	public static final String LINE_IN = "LineIn";
	public static final String PHONE = "Phone";
	public static final String CD = "CD";
	public static final String VIDEO = "Video";
	public static final String AUX = "Aux";
	public static final String PCM = "PCM";
	public static final String MIC_ONLY = "MicOnly";
	private AudioStereoEntry _master;
	private AudioStereoEntry _headPhone;
	private AudioMonoEntry _monoOut;
	private AudioMonoEntry _beep;
	private AudioMonoEntry _microphone;
	private AudioEntry _microphoExtraAmp;
	private AudioStereoEntry _lineIn;
	private AudioMonoEntry _phone;
	private AudioStereoEntry _cd;
	private AudioStereoEntry _video;
	private AudioStereoEntry _aux;
	private AudioStereoEntry _pcm;
	private AudioEntry _micOnly;
	public static boolean _reversed = false;
	private Hashtable _map = new Hashtable();

	public Configuration() {
		_reversed = Boolean.valueOf(
				System.getProperty(AC97_REVERSE_RIGHT_AND_LEFT, "false"))
				.booleanValue();
		_master = new AudioStereoEntry(MASTER);
		_headPhone = new AudioStereoEntry(HEADPHONE);
		_monoOut = new AudioMonoEntry(MONO_OUT);
		_beep = new AudioMonoEntry(BEEP);
		_microphone = new AudioMonoEntry(MICROPHONE);
		_microphoExtraAmp = new AudioEntry(MICROPHONE_EXTRA);
		_lineIn = new AudioStereoEntry(LINE_IN);
		_phone = new AudioMonoEntry(PHONE);
		_cd = new AudioStereoEntry(CD);
		_video = new AudioStereoEntry(VIDEO);
		_aux = new AudioStereoEntry(AUX);
		_pcm = new AudioStereoEntry(PCM);
		_micOnly = new AudioEntry(MIC_ONLY);
		addEntry(_master);
		addEntry(_headPhone);
		addEntry(_monoOut);
		addEntry(_beep);
		addEntry(_microphone);
		addEntry(_microphoExtraAmp);
		addEntry(_lineIn);
		addEntry(_phone);
		addEntry(_cd);
		addEntry(_video);
		addEntry(_aux);
		addEntry(_pcm);
		addEntry(_micOnly);
	}

	public void accept(ConfigurationVisitor visitor) {
		visitor.visit(this);
	}

	public Entry get(String name) {
		return (Entry) _map.get(name.toLowerCase());
	}

	public void set(String name, String config) {
		Entry entry = get(name);
		if (entry != null) {
			entry.parse(config);
		}
	}

	public static Configuration parse(InputStream config) {
		StringBuffer buffer = new StringBuffer();
		try {
			int available = 0;
			while ((available = config.available()) > 0) {
				buffer.append((char) config.read());
			}
		} catch (IOException e) {
		}
		return parse(buffer.toString());
	}

	public static Configuration parse(String config) {
		String sconf = config.replace(' ', '\n');
		StringTokenizer t = new StringTokenizer(sconf, "\n");
		Configuration conf = new Configuration();
		while (t.hasMoreElements()) {
			String line = t.nextToken();
			if (line != null) {
				line = line.trim();
				try {
					StringTokenizer t2 = new StringTokenizer(line, "=");
					int count = t2.countTokens();
					if (count == 2) {
						String name = t2.nextToken();
						String params = t2.nextToken();
						Entry configurable = conf.get(name);
						configurable.parse(params);
					}

				} catch (Throwable th) {
					th.printStackTrace();
				}
			}
		}
		return conf;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(_master);
		buf.append("\n");
		buf.append(_headPhone);
		buf.append("\n");
		buf.append(_monoOut);
		buf.append("\n");
		buf.append(_beep);
		buf.append("\n");
		buf.append(_microphone);
		buf.append("\n");
		buf.append(_microphoExtraAmp);
		buf.append("\n");
		buf.append(_lineIn);
		buf.append("\n");
		buf.append(_phone);
		buf.append("\n");
		buf.append(_cd);
		buf.append("\n");
		buf.append(_video);
		buf.append("\n");
		buf.append(_aux);
		buf.append("\n");
		buf.append(_pcm);
		buf.append("\n");
		buf.append(_micOnly);
		buf.append("\n");
		return buf.toString();
	}

	private void addEntry(Entry entry) {
		_map.put(entry.getName().toLowerCase(), entry);
	}

	public interface Entry {

		public void parse(String config);

		public String getName();
	}

	public class AudioEntry implements Entry {

		protected String _name;
		protected boolean _enable = false;

		public AudioEntry(String name) {
			super();
			_name = name;
			_enable = false;
		}

		public String getName() {
			return _name;
		}

		public boolean isEnable() {
			return _enable;
		}

		public void setEnable(boolean enable) {
			_enable = enable;
		}

		public void parse(String config) {
			_enable = false;
			if (config != null) {
				_enable = config.trim().equals("1");
			}
		}

		public String toString() {
			return _name + "=" + (_enable ? "1" : "0");
		}
	}

	public class AudioMonoEntry implements Entry {

		protected String _name;
		protected short _value;
		protected boolean _mute = false;

		public AudioMonoEntry(String name) {
			super();
			_name = name;
			_value = 0;
			_mute = false;
		}

		public String getName() {
			return _name;
		}

		public short getValue() {
			return _value;
		}

		public void setValue(short value) {
			_value = value;
		}

		public boolean isMute() {
			return _mute;
		}

		public void setMute(boolean mute) {
			_mute = mute;
		}

		public String toString() {
			return _name + "=" + _value + (_mute ? ",mute" : "");
		}

		public void parse(String config) {
			StringTokenizer t = new StringTokenizer(config, ",");
			String smono = t.nextToken();
			_value = Short.parseShort(smono);
			String smute = "";
			if (t.hasMoreTokens()) {
				smute = t.nextToken();
				_mute = (smute != null && smute.equals("mute")) ? true : false;
			}
		}
	}

	public class AudioStereoEntry implements Entry {

		protected String _name;
		protected short _right;
		protected short _left;
		protected boolean _mute = false;

		public AudioStereoEntry(String name) {
			super();
			_name = name;
			_right = 0;
			_left = 0;
			_mute = false;
		}

		public String getName() {
			return _name;
		}

		public short getRight() {
			if (_reversed) {
				return _left;
			}
			return _right;
		}

		public void setRight(short right) {
			_right = right;
		}

		public short getLeft() {
			if (_reversed) {
				return _right;
			}
			return _left;
		}

		public void setLeft(short left) {
			_left = left;
		}

		public boolean isMute() {
			return _mute;
		}

		public void setMute(boolean mute) {
			_mute = mute;
		}

		public String toString() {
			return _name + "=" + _left + "," + _right + (_mute ? ",mute" : "");
		}

		public void parse(String config) {
			StringTokenizer t = new StringTokenizer(config, ",");
			String sleft = t.nextToken();
			_left = Short.parseShort(sleft);
			String sright = t.nextToken();
			_right = Short.parseShort(sright);
			String smute = "";
			if (t.hasMoreTokens()) {
				smute = t.nextToken();
				_mute = (smute != null && smute.equals("mute")) ? true : false;
			}
		}
	}
}
