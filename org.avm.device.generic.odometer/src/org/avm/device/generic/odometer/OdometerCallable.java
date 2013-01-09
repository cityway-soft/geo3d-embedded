/**
 * 
 */
package org.avm.device.generic.odometer;

import org.apache.log4j.Logger;
import org.avm.device.generic.odometer.geoconv.LambertIIe;
import org.avm.device.generic.odometer.geoconv.WGS84;
import org.osgi.util.position.Position;

class OdometerCallable implements Runnable {

	private Logger _log;

	private OdometerImpl _peer;

	private Position _position;

	public OdometerCallable(OdometerImpl peer, Position position) {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		_peer = peer;
		_position = position;
	}

	/*
	 * @see http://mathforum.org/library/drmath/view/51879.html
	 */
	private double computeHaversineFormula(Position p1, Position p2) {

		final double R = 6371008.8;

		double lon1 = p1.getLongitude().getValue();
		double lat1 = p1.getLatitude().getValue();

		double lon2 = p2.getLongitude().getValue();
		double lat2 = p2.getLatitude().getValue();

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		_log.debug("dlon=" + dlon + ", dlat=" + dlat);

		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;

		return d;
	}

	private double computeLambertIIFormula(Position p1, Position p2) {

		double d = 0d;
		WGS84 pw1 = null;
		WGS84 pw2 = null;
		LambertIIe pl1 = null;
		LambertIIe pl2 = null;
		double dx = 0d;
		double dy = 0d;

		try {
			double x1 = p1.getLongitude().getValue() * 180d / Math.PI;
			double y1 = p1.getLatitude().getValue() * 180d / Math.PI;

			double x2 = p2.getLongitude().getValue() * 180d / Math.PI;
			double y2 = p2.getLatitude().getValue() * 180d / Math.PI;

			pw1 = new WGS84(x1, y1);
			pw2 = new WGS84(x2, y2);

			pl1 = pw1.convert2Lambert2();
			pl2 = pw2.convert2Lambert2();

			dx = pl2.getX() - pl1.getX();
			dy = pl2.getY() - pl1.getY();

			d = Math.sqrt(dx * dx + dy * dy);
		} catch (Exception e) {

			e.printStackTrace();
		}

		// System.out.println("--------------");
		// System.out.println("pw1=" + pw1);
		// System.out.println("pl1=" + pl1);
		// System.out.println("--------------");
		// System.out.println("pw2=" + pw2);
		// System.out.println("pl2=" + pl2);
		// System.out.println("--------------");
		// System.out.println("dx=" + dx);
		// System.out.println("dy=" + dy);

		return d;
	}

	public void run() {
		if (_peer._previous == null) {
			_peer._previous = _position;
			return;
		}
		double limit = _peer._config.getSpeedLimit().doubleValue();
		double speed = _peer._previous.getSpeed().getValue();
		_log.debug("Speed =" + speed);
		if (speed > limit) {
			double haversine = computeHaversineFormula(_peer._previous,
					_position);
			double lambertII = computeLambertIIFormula(_peer._previous,
					_position);
			_log.debug("HaversineFormula d=" + haversine);
			_log.debug("LambertIIFormula d=" + lambertII);
			_peer._counter += haversine;
			_peer._previous = _position;
		}
	}
}