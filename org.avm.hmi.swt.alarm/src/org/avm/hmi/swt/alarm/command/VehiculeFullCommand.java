package org.avm.hmi.swt.alarm.command;

import java.util.Properties;

import org.avm.business.core.Avm;
import org.avm.elementary.alarm.Alarm;
import org.avm.hmi.swt.alarm.Command;
import org.avm.hmi.swt.alarm.CommandFactory;
import org.osgi.service.component.ComponentContext;

public class VehiculeFullCommand implements Command {

	private Alarm _alarm;
	private ComponentContext _context;

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {
		protected Command createCommand(ComponentContext context,
				Properties properties) {
			boolean notify = ((String) properties.getProperty(Command.NOTIFY,
					"false")).equalsIgnoreCase("true");
			// int priority=notify?Alarm.MAX_PRIORITY:1;
			String source = properties.getProperty(Command.SOURCE);
			return new VehiculeFullCommand(context, new Alarm(new Integer(3))); //$NON-NLS-2$
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CommandFactory factory = new DefaultCommandFactory();
		DefaultCommandFactory.factories.put(
				VehiculeFullCommand.class.getName(), factory);
	}

	public VehiculeFullCommand(ComponentContext context, Alarm alarm) {
		_context = context;
		_alarm = alarm;
	}

	public Alarm getAlarm() {
		return _alarm;
	}

	public void activate(boolean b) {
		Avm avm = (Avm) _context.locateService("avm");
		if (avm != null) {
			avm.setVehiculeFull(b);
		}
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}
}
