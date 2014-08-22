package org.avm.device.generic.gps;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.avm.device.gps.Gps;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.Unit;
import org.osgi.util.position.Position;

public class SirfStarII implements Gps, ConfigurableService, ManageableService,
		ProducerService {

	private Thread _thread;
	private Position _current;
	private boolean _running = true;
	private Logger _log = Logger.getInstance(this.getClass());
	private ProducerManager _producer;
	private GpsConfig _config;
	private Position DEFAULT_POSITION;

	private URLConnection _conn;
	private DataInputStream _in;

	public SirfStarII() {
		_current = getDefaultPosition();
	}

	public void start() {
		_current = getDefaultPosition();
		_running = true;
		_thread = new Thread(new Task());
		_thread.setName("[AVM] device gps");
		_thread.start();
	}

	public void stop() {
		_running = false;
		_thread.interrupt();
	}

	public Position getCurrentPosition() {
		if (_current == null)
			return getDefaultPosition();
		return _current;
	}

	private Position getDefaultPosition() {
		if (DEFAULT_POSITION == null) {
			long now = 0;
			double value = 0d;
			double error = Double.NaN;
			Measurement lon = new Measurement(value, error, Unit.rad, now);
			Measurement lat = new Measurement(value, error, Unit.rad, now);
			Measurement alt = new Measurement(value, error, Unit.m, now);
			Measurement speed = new Measurement(value, error, Unit.m_s, now);
			Measurement track = new Measurement(value, error, Unit.rad, now);
			DEFAULT_POSITION = new Position(lat, lon, alt, speed, track);
		}
		return DEFAULT_POSITION;
	}

	private final static int MAX_NULL_CONTENT_ERROR = 5;

	private class Task implements Runnable {

		private void execute() throws InterruptedException, IOException,
				InterruptedIOException {
			double longitude, latitude, altitude = 0, vitesse, cap;
			Measurement lat, lon, alt, speed, track;
			int satellites = 0;
			// added to check null content
			int countErrors = 0;
			while (_running) {
				Object o = getContent();
				if (o == null) {
					Thread.sleep(500);
					countErrors++;
					if (countErrors> MAX_NULL_CONTENT_ERROR){
						throw new IOException("Max null content reached");
					}
					continue;
				}else{
					countErrors = 0;
				}
				
				if (o instanceof GGASentence) {
					GGASentence gga = (GGASentence) o;
					satellites = gga.get_satellites();
					altitude = gga.get_altitude();
				}
				if (o instanceof RMCSentence) {
					RMCSentence rmc = (RMCSentence) o;
					if (rmc.isValid()) {
						longitude = NMEASentence.angleToRadian(rmc
								.getLongitude().value);
						if (!rmc.getLongitude().easting) {
							longitude = -longitude;
						}
						latitude = NMEASentence
								.angleToRadian(rmc.getLatitude().value);
						if (!rmc.getLatitude().northing) {
							latitude = -latitude;
						}
						vitesse = rmc.getSpeed() * 1852d / 3600d;
						cap = rmc.getBearing() * Math.PI / 180d;
						double error = Double.NaN;
						long now = rmc.getDate().getTime();
						lon = new Measurement(longitude, error, Unit.rad, now);
						lat = new Measurement(latitude, error, Unit.rad, now);
						alt = new Measurement(altitude, error, Unit.m, now);
						speed = new Measurement(vitesse, error, Unit.m_s, now);
						track = new Measurement(cap, error, Unit.rad, now);
						Position tmp = new Position(lat, lon, alt, speed, track);
						if (_config.getCorrect().booleanValue()) {
							double delay = _config.getDelay().doubleValue();
							tmp = correctPosition(tmp, delay);
						}
						_current = tmp;
					} else {
						_current = getDefaultPosition();
					}
					if (_producer != null) {
						double x = _current.getLongitude().getValue() * 180d
								/ Math.PI;
						double y = _current.getLatitude().getValue() * 180d
								/ Math.PI;
						_log.debug("[DSU] Position corigee : lon = " + x
								+ " lat = " + y);
						_producer.publish(_current);
					}
				}
			}
		}

		public void run() {
			InputStream in = null;
			boolean error = false;
			try {
				while (_running) {
					try {
						String uri = _config.getUrlConnection();
						URL url = new URL(uri);
						_conn = url.openConnection();
						_in = null;
						in = getInputStream();
						execute();
						error = false;
					} catch (InterruptedException e) {
					} catch (InterruptedIOException e) {
					} catch (IOException e) {
						if (in != null) {
							try {
								in.close();
							} catch (IOException ioe) {
							}
						}
						if (error == false) {
							_log.error(e);
							error = true;
						}
						try {
							Thread.sleep(3000);
						} catch (InterruptedException t) {
						}
					}
				}
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (GpsConfig) config;
	}

	double mod(double y, double x) {
		return (y - x * Math.floor(y / x));
	}

	double[] computePosition(double lon1, double lat1, double x, double tc) {
		double dlon, lat, lon;
		final double R = 6371008.8;
		double d = Math.atan(x / R);
		lat = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1)
				* Math.sin(d) * Math.cos(tc));
		if (Math.cos(lat) == 0)
			lon = lon1;
		else
			lon = mod(
					lon1
							- Math.asin(Math.sin(tc) * Math.sin(d)
									/ Math.cos(lat)) + Math.PI, 2 * Math.PI)
					- Math.PI;
		/*
		 * lat = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1)
		 * Math.sin(d) * Math.cos(tc)); dlon = Math.atan2(Math.sin(tc) *
		 * Math.sin(d) * Math.cos(lat1), Math.cos(d) - Math.sin(lat1) *
		 * Math.sin(lat)); lon = mod(lon1 - dlon + Math.PI, 2 * Math.PI) -
		 * Math.PI;
		 */
		double[] result = { lon, lat };
		return result;
	}

	Position correctPosition(Position p, double delay) {
		double x = p.getLongitude().getValue() * 180d / Math.PI;
		double y = p.getLatitude().getValue() * 180d / Math.PI;
		_log.debug("Position original : lon = " + x + " lat = " + y);
		double d = delay * p.getSpeed().getValue();
		double lon1 = p.getLongitude().getValue();
		double lat1 = p.getLatitude().getValue();
		double[] result = computePosition(lon1, lat1, d, p.getTrack()
				.getValue());
		Measurement lat, lon, alt, speed, track;
		lon = new Measurement(result[0], p.getLongitude().getError(), Unit.rad,
				p.getLongitude().getTime());
		lat = new Measurement(result[1], p.getLatitude().getError(), Unit.rad,
				p.getLatitude().getTime());
		alt = p.getAltitude();
		speed = p.getSpeed();
		track = p.getTrack();
		return new Position(lat, lon, alt, speed, track);
	}

	private InputStream getInputStream() throws IOException {
		if (_in == null) {
			try {
				InputStream in = _conn.getInputStream();
				_in = new DataInputStream(new PushbackInputStream(in));
			} catch (Throwable e) {
				throw new IOException(e.getMessage());
			}
		}
		return _in;
	}
	
	private String lastvalidTrame ="";
	private int countSimilarTrame = 0;
	private final static int MAX_SIMILAR_TRAME = 5;

	private Object getContent() throws IOException {
		Object result = null;
		DataInputStream in = (DataInputStream) getInputStream();
		String trame = in.readLine();
		if (trame != null) {
			if (trame.equals(lastvalidTrame)){
				if (countSimilarTrame++ > MAX_SIMILAR_TRAME){
					throw new IOException("Max similar trame reached");
				}
			}else{
				countSimilarTrame=0;
			}
			_log.debug(trame);
			result = parse(trame);
			lastvalidTrame = trame;
		}
		return result;
	}

	private char lineBuffer[];

	private Object parse(String sentence) {
		Object result = null;
		NMEASentence nmea = new NMEASentence();
		if (nmea.parse(sentence)) {
			result = nmea;
			if (nmea.isGGASentence()) {
				GGASentence gga = new GGASentence();
				gga.parse(nmea);
				result = gga;
			}
			if (nmea.isRMCSentence()) {
				RMCSentence rmc = new RMCSentence();
				rmc.parse(nmea);
				result = rmc;
			}
		}
		return result;
	}
}