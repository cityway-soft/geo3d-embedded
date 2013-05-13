package org.avm.hmi.swt.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.hmi.swt.management.bundle.ConfigImpl;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PanelCommands extends AbstractPanel implements ConfigurableService {
	private static final String GROUPNAME = "system";
	public static final String NAME = "name";

	public PanelCommands(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;

		this.setLayout(gridLayout);
	}

	public void configure(Config config) {
		if (config != null) {
			Properties props = ((ConfigImpl) config).getProperty(null);

			List commands = new ArrayList();
			Enumeration e = props.elements();
			while (e.hasMoreElements()) {
				Properties prop = (Properties) e.nextElement();
				if (prop.getProperty(NAME) != null) {
					commands.add(prop);
				}
			}
			Collections.sort(commands, new OrderByCommandName());

			Iterator iter = commands.iterator();

			while (iter.hasNext()) {
				Properties p = (Properties) iter.next();
				String group = p.getProperty(GROUP);
				if (group != null && group.equalsIgnoreCase(GROUPNAME)) {
					String startcmd = p.getProperty(START);
					String stopcmd = p.getProperty(STOP);
					String cmd[];
					if (stopcmd != null) {
						String[] command = { startcmd, stopcmd };
						cmd = command;
					} else {
						String[] command = { startcmd };
						cmd = command;
					}
					String cmdname = p.getProperty("name");
					addTestButton(cmdname, cmd);
				}
			}

		}

	}

	public static class ItemCommandsFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelCommands(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelCommands.class.getName(),
				new ItemCommandsFactory());
	}

	static class OrderByCommandName implements Comparator {

		public int compare(Object object1, Object object2) {
			Properties p1 = (Properties) object1;
			Properties p2 = (Properties) object2;

			String name1 = null;
			String name2 = null;

			name1 = (String) p1.get(NAME);

			name2 = (String) p2.get(NAME);

			if (name1 == null && name2 == null) {
				return 0;
			}

			return name1.compareTo(name2);

		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
