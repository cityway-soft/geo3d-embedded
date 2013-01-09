package org.avm.hmi.swt.management;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class PanelBundles extends AbstractPanel implements BundleContextInjector {

	private Table _table;

	private HashMap _hash = new HashMap();

	private BundleContext _context;

	public PanelBundles(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		create();
	}

	public void start() {
		updateBundles();
	}

	/**
	 * This method initializes itemBundles
	 * 
	 */
	private void create() {
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.grabExcessVerticalSpace = true;
		gridData8.horizontalIndent = 0;
		gridData8.verticalAlignment = GridData.FILL;

		_table = new Table(this, SWT.NONE);
		_table.setHeaderVisible(true);
		_table.setLayoutData(gridData8);
		_table.setLinesVisible(true);

		_table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				action(_table.getSelectionIndex());
			}
		});
		TableColumn tableColumn0 = new TableColumn(_table, SWT.NONE);
		tableColumn0.setWidth(45);
		tableColumn0.setText(Messages.getString("ItemBundles.id")); //$NON-NLS-1$
		TableColumn tableColumn1 = new TableColumn(_table, SWT.NONE);
		tableColumn1.setWidth(170);
		tableColumn1.setText(Messages.getString("ItemBundles.nom")); //$NON-NLS-1$
		TableColumn tableColumn2 = new TableColumn(_table, SWT.NONE);
		tableColumn2.setWidth(150);
		tableColumn2.setText(Messages.getString("ItemBundles.version")); //$NON-NLS-1$
		TableColumn tableColumn3 = new TableColumn(_table, SWT.NONE);
		tableColumn3.setWidth(70);
		tableColumn3.setText(Messages.getString("ItemBundles.etat")); //$NON-NLS-1$
		TableColumn tableColumn4 = new TableColumn(_table, SWT.NONE);
		tableColumn4.setWidth(50);
		tableColumn4.setText(Messages.getString("ItemBundles.action")); //$NON-NLS-1$
	}

	public void action(int index) {
		TableItem item = _table.getItem(index);
		String val = item.getText(0);
		long bundleid = Long.parseLong(val);
		String result = runConsoleCommand("/mana status " + bundleid);
		if (result.indexOf("ACT") != -1) {
			runConsoleCommand("/mana stop " + bundleid);
		} else if (result.indexOf("res") != -1) {
			runConsoleCommand("/mana start " + bundleid);
		}

	}

	public void setBundleContext(BundleContext context) {
		_context = context;
	}

	private void updateBundles() {
		String buf = runConsoleCommand("/mana status");
		// ACT : 813 : 1.0.0.qualifier (13/06/10 22:24) [4] :
		// org.avm.hmi.swt.authentifica
		StringTokenizer t = new StringTokenizer(buf, "\n");
		while (t.hasMoreElements()) {
			String line = t.nextToken();
			StringTokenizer t2 = new StringTokenizer(line, " ");
			String state = t2.nextToken(); // ACT
			String ignore = t2.nextToken(); // :
			String bundleId = t2.nextToken(); // 813
			ignore = t2.nextToken(); // :
			String version = t2.nextToken(); // 1.0.0.qualifier
			ignore = t2.nextToken(); // (13/06/10
			ignore = t2.nextToken(); // 22:24)
			ignore = t2.nextToken(); // [4]
			ignore = t2.nextToken(); // :
			String bundleName = t2.nextToken(); // org.avm.hmi.swt.authentifica
			try {
				update(Long.parseLong(bundleId), bundleName, version,
						getStatus(state));
			} catch (Throwable e) {
				System.err.println("Error:" + e.getMessage());
			}
		}
	}

	public void update(long bundleId, String bundleName, String version,
			int state) {
		TableItem item = (TableItem) _hash.get(bundleName);
		if (_table.isDisposed()) {
			return;
		}
		if (item == null) {
			item = new TableItem(_table, SWT.NONE);
			_hash.put(bundleName, item);
		}
		String val[] = new String[] { Long.toString(bundleId), bundleName,
				version, getStatus(state),
				(state != Bundle.RESOLVED) ? "STOP" : "START" };
		switch (state) {
		case Bundle.ACTIVE: {
			item.setBackground(3, Display.getCurrent().getSystemColor(
					SWT.COLOR_GREEN));
		}
			break;
		case Bundle.STOPPING: {
			item.setBackground(3, Display.getCurrent().getSystemColor(
					SWT.COLOR_DARK_YELLOW));
		}
			break;
		case Bundle.STARTING: {
			item.setBackground(3, Display.getCurrent().getSystemColor(
					SWT.COLOR_YELLOW));
		}
			break;
		case Bundle.UNINSTALLED: {
			item.setBackground(3, Display.getCurrent().getSystemColor(
					SWT.COLOR_RED));
		}
			break;

		case Bundle.INSTALLED: {
			item.setBackground(3, Display.getCurrent().getSystemColor(
					SWT.COLOR_DARK_GRAY));
		}
			break;

		default: {
			item.setBackground(3, Display.getCurrent().getSystemColor(
					SWT.COLOR_WIDGET_LIGHT_SHADOW));
		}
		}
		item.setText(val);

	}

	private String getStatus(int status) {
		String result = "????"; //$NON-NLS-1$
		switch (status) {
		case Bundle.ACTIVE:
			result = Messages.getString("ItemBundles.active"); //$NON-NLS-1$
			break;
		case Bundle.INSTALLED:
			result = Messages.getString("ItemBundles.installed"); //$NON-NLS-1$
			break;
		case Bundle.RESOLVED:
			result = Messages.getString("ItemBundles.resolved"); //$NON-NLS-1$
			break;
		case Bundle.STARTING:
			result = Messages.getString("ItemBundles.starting"); //$NON-NLS-1$
			break;
		case Bundle.STOPPING:
			result = Messages.getString("ItemBundles.stopping"); //$NON-NLS-1$
			break;
		case Bundle.UNINSTALLED:
			result = Messages.getString("ItemBundles.uninstalled"); //$NON-NLS-1$
			break;
		}
		return result;
	}

	private int getStatus(String status) {
		if (status == null) {
			return Bundle.UNINSTALLED;
		} else if (status.equalsIgnoreCase("ACT")) {
			return Bundle.ACTIVE;
		} else if (status.equalsIgnoreCase("ACT")) {
			return Bundle.INSTALLED;
		} else if (status.equalsIgnoreCase("INS")) {
			return Bundle.RESOLVED;
		} else if (status.equalsIgnoreCase("RES")) {
			return Bundle.STARTING;
		} else if (status.equalsIgnoreCase("STA")) {
			return Bundle.STOPPING;
		} else if (status.equalsIgnoreCase("STO")) {
			return Bundle.UNINSTALLED;
		}
		return Bundle.UNINSTALLED;
	}

	
	public static class ItemBundlesFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelBundles(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelBundles.class.getName(),
				new ItemBundlesFactory());
	}
} // @jve:decl-index=0:visual-constraint="10,10"
