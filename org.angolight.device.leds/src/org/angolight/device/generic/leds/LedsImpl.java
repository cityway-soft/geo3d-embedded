package org.angolight.device.generic.leds;

import java.io.IOException;

import org.angolight.device.leds.Leds;
import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.measurement.State;

public class LedsImpl implements Leds, LedsListener, ConfigurableService,
		ProducerService, ManageableService {

	private Logger _log;
	private ProducerManager _producer;
	private LedsConfig _config;
	private LedsDevice _peer;

	public LedsImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public synchronized int I(byte address, short states, byte period,
			boolean check) {

		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('I');
		else
			buffer.append('i');
		b = new byte[1];
		b[0] = address;
		buffer.append(Util.toHexaString(b));
		b = new byte[2];
		Util.putShort(b, 0, true, states);
		buffer.append(Util.toHexaString(b));
		b = new byte[1];
		b[0] = period;
		buffer.append(Util.toHexaString(b));

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}
		// send message
		return send(buffer.toString());
	}

	public synchronized int J(byte address, short states, byte period,
			boolean check) {
  
		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('J');
		else
			buffer.append('j');
		b = new byte[1];
		b[0] = address;
		buffer.append(Util.toHexaString(b));
		b = new byte[2];
		Util.putShort(b, 0, true, states);
		buffer.append(Util.toHexaString(b));
		b = new byte[1];
		b[0] = period;
		buffer.append(Util.toHexaString(b));

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		return send(buffer.toString());
	}

	public synchronized int L(byte brightgness, boolean check) {
		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('L');
		else
			buffer.append('l');
		b = new byte[1];
		b[0] = brightgness;
		buffer.append(Util.toHexaString(b));

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		return send(buffer.toString());

	}

	public synchronized int M(short states, byte period, boolean check) {

		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('M');
		else
			buffer.append('m');
		b = new byte[2];
		Util.putShort(b, 0, true, states);
		buffer.append(Util.toHexaString(b));
		b = new byte[1];
		b[0] = period;
		buffer.append(Util.toHexaString(b));

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		return send(buffer.toString());
	}

	public synchronized int R(byte address, boolean check) {
		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('R');
		else
			buffer.append('r');
		b = new byte[1];
		b[0] = address;
		buffer.append(Util.toHexaString(b));

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		String result = null;
		try {
			result = _peer.send(buffer.toString());
		} catch (LedsException e) {
			if (e.getMessage().equals(LedsException.ERROR)) {
				return Leds.ERROR;
			} else if (e.getMessage().equals(LedsException.BUSY)) {
				return Leds.BUSY;
			}
		} catch (IOException e) {
			_log.error(e);
			return Leds.ERROR;
		}

		if (result.length() > 0) {
			b = Util.fromHexaString("00" + result.substring(0, 6));
			return Util.getInt(b, 0, true);
		} else {
			return Leds.ERROR;
		}
	}

	public synchronized int S(boolean check) {

		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('S');
		else
			buffer.append('s');

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		return send(buffer.toString());
	}

	public synchronized int T(boolean check) {
		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('T');
		else
			buffer.append('t');
		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		return send(buffer.toString());
	}

	public synchronized int V(boolean check) {

		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('V');
		else
			buffer.append('v');

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		String result = null;
		try {
			result = _peer.send(buffer.toString());
		} catch (LedsException e) {
			if (e.getMessage().equals(LedsException.ERROR)) {
				return Leds.ERROR;
			} else if (e.getMessage().equals(LedsException.BUSY)) {
				return Leds.BUSY;
			}
		} catch (IOException e) {
			_log.error(e);
			return Leds.ERROR;
		}
		if (result.length() > 0) {
			b = Util.fromHexaString(result.substring(1));
			return Util.getShort(b, 0, true);
		} else {
			return Leds.ERROR;
		}
	}

	public synchronized int X(byte address, byte cycle, byte period,
			boolean check) {
		// format message
		byte[] b;
		StringBuffer buffer = new StringBuffer();
		if (check)
			buffer.append('X');
		else
			buffer.append('x');
		b = new byte[1];
		b[0] = address;
		buffer.append(Util.toHexaString(b));
		;
		b[0] = cycle;
		buffer.append(Util.toHexaString(b));
		b[0] = period;
		buffer.append(Util.toHexaString(b));

		if (check) {
			b = buffer.toString().getBytes();
			short value = checksum(b, 0, b.length);
			b = new byte[2];
			Util.putShort(b, 0, true, value);
			buffer.append(Util.toHexaString(b));
		}

		// send message
		return send(buffer.toString());
	}

	public void ledsClosed() {
		_log.info("[DSU] leds closed");
	}

	public void ledsOpened() {
		_log.info("[DSU] leds opened");
	}

	public void sequenceStopped() {
		_producer.publish(new State(SEQUENCE_STOPPED,
				org.angolight.device.leds.Leds.class.getName()));
	}

	public void configure(Config config) {
		_config = (LedsConfig) config;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void start() {
		try {
			initialize();
		} catch (IOException e) {
			_log.error(e);
		}
	}

	public void stop() {
		dispose();
	}

	private void initialize() throws IOException {
		_peer = new LedsDeviceImpl(this);

	}

	private void dispose() {
		_peer.close();
	}

	private int send(String command) {
		try {
			if (!_peer.isOpen()) {
				_peer.open(_config.getUrlConnection());
			}
			_peer.send(command);
		} catch (LedsException e) {
			if (e.getMessage().equals(LedsException.ERROR)) {
				return Leds.ERROR;
			} else if (e.getMessage().equals(LedsException.BUSY)) {
				return Leds.BUSY;
			}
		} catch (IOException e) {
			_peer.close();
			_log.error(e);
			return Leds.ERROR;
		}
		return Leds.OK;
	}

	private short checksum(byte[] data, int offset, int length) {
		short result = 0;
		for (int i = offset; i < offset + length; i++) {
			result += data[i];
		}
		return result;
	}

}
