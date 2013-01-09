package org.avm.hmi.swt.management;

import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.hmi.swt.management.bundle.ConfigImpl;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PanelTest extends AbstractPanel implements ConfigurableService {
	private static final String GROUPNAME = "test";

	public PanelTest(Composite parent, int style) {
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
			Enumeration e = props.elements();
			while (e.hasMoreElements()) {
				Properties p = (Properties) e.nextElement();
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
	
	public static class ItemTestFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelTest(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelTest.class.getName(),
				new ItemTestFactory());
	}



} // @jve:decl-index=0:visual-constraint="10,10"
