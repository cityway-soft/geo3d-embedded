package org.avm.hmi.swt.alarm.command;

import java.util.Date;
import java.util.Properties;

import org.avm.elementary.alarm.Alarm;
import org.avm.hmi.swt.alarm.AlarmIhm;
import org.avm.hmi.swt.alarm.Command;
import org.avm.hmi.swt.alarm.CommandFactory;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.osgi.service.component.ComponentContext;

public class TestCommand implements Command {

	private Alarm _alarm;
	private ComponentContext _context;

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {
		protected Command createCommand(ComponentContext context, Properties properties) {
			boolean notify = ((String)properties.getProperty(Command.NOTIFY, "false")).equalsIgnoreCase("true");
//			int priority=notify?Alarm.MAX_PRIORITY:1;
			String source=properties.getProperty(Command.SOURCE);
			return new TestCommand(
					context,
					new Alarm(new Integer(3))); //$NON-NLS-2$
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CommandFactory factory = new DefaultCommandFactory();
		DefaultCommandFactory.factories.put(
				TestCommand.class.getName(), factory);
	}

	public TestCommand(ComponentContext context, Alarm alarm) {
		_context = context;
		_alarm = alarm;
	}

	public Alarm getAlarm() {
		return _alarm;
	}

	public void activate(boolean b) {
		if (b){
			MessageBox.setMessage("Test", "Activee", MessageBox.MESSAGE_WARNING, SWT.NONE);
		}
		else{
			MessageBox.setMessage("Test", "non Activee", MessageBox.MESSAGE_WARNING, SWT.NONE);
		}
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}
}
