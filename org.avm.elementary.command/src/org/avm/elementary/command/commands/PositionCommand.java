package org.avm.elementary.command.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.avm.business.protocol.phoebus.Message;
import org.avm.device.gps.Gps;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.command.impl.CommandFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.Unit;
import org.osgi.util.position.Position;

public class PositionCommand implements org.apache.commons.chain.Command {
	private MessengerContext _context;

	public boolean execute(Context context) throws Exception {
		_context = (MessengerContext) context;
		if (_context == null)
			return true;
		Message message = (Message) _context.getMessage();
		if (message == null)
			return true;
		
		if (message.getEntete().getChamps().getPosition() == 1) {
			if (message.getEntete().getPosition() == null) {
				message.getEntete().setPosition(
						new org.avm.business.protocol.phoebus.Position());
				Position current = getCurrentPosition();
				int longitude = (int) (current.getLongitude().getValue()
						* 180.00 / Math.PI * 360000.00);
				int latitude = (int) (current.getLatitude().getValue() * 180.00
						/ Math.PI * 360000.00);
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

		return false;
	}

	protected Position getCurrentPosition() {
		Position result = null;
		ComponentContext context = _context.getComponentContext();
		Gps gps = (Gps) context.locateService("gps");
		if (gps != null) {
			result = gps.getCurrentPosition();
		} else {
			long now = System.currentTimeMillis();
			double value = 0d;
			double error = -1d;
			Measurement lon = new Measurement(value, error, Unit.rad, now);
			Measurement lat = new Measurement(value, error, Unit.rad, now);
			Measurement alt = new Measurement(value, error, Unit.m, now);
			Measurement speed = new Measurement(value, error, Unit.m_s, now);
			Measurement track = new Measurement(value, error, Unit.rad, now);
			result = new Position(lat, lon, alt, speed, track);
		}
		return result;
	}

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {
		protected Command createCommand() {
			return new PositionCommand();
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CommandFactory factory = new DefaultCommandFactory();
		DefaultCommandFactory.factories.put(PositionCommand.class.getName(),
				factory);
	}
}
