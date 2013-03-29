package org.avm.elementary.command.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.Message;
import org.avm.device.gps.Gps;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.command.impl.CommandFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.Unit;
import org.osgi.util.position.Position;

public class PositionCommand implements org.apache.commons.chain.Command {
	private SimpleDateFormat DF = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss,SSS");

	private Logger logger = Logger.getInstance(PositionCommand.class);

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {

		protected Command createCommand() {

			return new PositionCommand();
		}
	}

	private MessengerContext _context;
	/** Referencement de la fabrique de classe */
	static {
		final CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(PositionCommand.class.getName(), factory);
	}

	public boolean execute(final Context o) throws Exception {

		final MessengerContext context = (MessengerContext) o;
		_context = context;

		computeMessage(context);

		Dictionary header = context.getHeader();

		final Position current = this.getCurrentPosition();
		final int longitude = (int) (((current.getLongitude().getValue() * 180.00d) / Math.PI) * 360000.00d);
		final int latitude = (int) (((current.getLatitude().getValue() * 180.00d) / Math.PI) * 360000.00d);
		final int cap = (int) ((current.getTrack().getValue() * 180.00d) / Math.PI);
		final int vitesse = (int) (current.getSpeed().getValue() * 3.6d);

		Integer lon = (Integer) header.get("lon");
		if (lon == null) {
			lon = new Integer(longitude);
			header.put("lon", lon);
		}

		Integer lat = (Integer) header.get("lat");
		if (lat == null) {
			lat = new Integer(latitude);
			header.put("lat", lat);
		}

		Integer trk = (Integer) header.get("trk");
		if (trk == null) {
			trk = new Integer(cap);
			header.put("trk", trk);
		}

		Integer spd = (Integer) header.get("spd");
		if (spd == null) {
			spd = new Integer(vitesse);
			header.put("spd", spd);
		}

		Date date = (Date) header.get("date");
		if (date == null) {
			date = new Date();
			header.put("date", date);
		}

		if (logger.isDebugEnabled()) {
			header.put("date-text", DF.format(date));
			logger.debug("header=" + header);
		}

		return false;
	}

	private void computeMessage(MessengerContext context) {
		if (context == null)
			return;

		if (context.getMessage() instanceof Message) {
			Message message = (Message) context.getMessage();
			if (message == null)
				return;

			if (message.getEntete().getChamps().getPosition() == 1) {
				if (message.getEntete().getPosition() == null) {
					message.getEntete().setPosition(
							new org.avm.business.protocol.phoebus.Position());
					Position current = getCurrentPosition();
					int longitude = (int) (current.getLongitude().getValue()
							* 180.00 / Math.PI * 360000.00);
					int latitude = (int) (current.getLatitude().getValue()
							* 180.00 / Math.PI * 360000.00);
					int cap = (int) (current.getTrack().getValue() * 180.00 / Math.PI);
					int vitesse = (int) (current.getSpeed().getValue());
					org.avm.business.protocol.phoebus.Position position = message
							.getEntete().getPosition();
					position.setLongitude(longitude);
					position.setLatitude(latitude);
					position.setCap(cap);
					position.setVitesse(vitesse);
				}
			}
		}

	}

	protected Position getCurrentPosition() {

		Position result = null;
		final ComponentContext context = this._context.getComponentContext();
		final Gps gps = (Gps) context.locateService("gps");
		if (gps != null) {
			result = gps.getCurrentPosition();
		} else {
			final long now = System.currentTimeMillis();
			final double value = 0d;
			final double error = -1d;
			final Measurement lon = new Measurement(value, error, Unit.rad, now);
			final Measurement lat = new Measurement(value, error, Unit.rad, now);
			final Measurement alt = new Measurement(value, error, Unit.m, now);
			final Measurement speed = new Measurement(value, error, Unit.m_s,
					now);
			final Measurement track = new Measurement(value, error, Unit.rad,
					now);
			result = new Position(lat, lon, alt, speed, track);
		}
		return result;
	}
}
