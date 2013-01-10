package org.avm.hmi.swt.management;

import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.Scheduler;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PanelComptage extends AbstractPanel implements SelectionListener,
		ConsoleFacadeInjector, ConfigurableService {

	private Button _refresh;
	private Button _reset;
	private Text _textMontees;
	private Text _textDescentes;
	private Font _fontText;

	public PanelComptage(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		create();
	}

	/**
	 * This method initializes itemBundles
	 * 
	 */
	private void create() {
		GridData gridData;

		_fontText = DesktopImpl.getFont( 15, SWT.NORMAL); //$NON-NLS-1$

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = 10;
		gridData.horizontalSpan = 2;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		_refresh = new Button(this, SWT.NONE);
		_refresh.addSelectionListener(this);
		_refresh.setLayoutData(gridData);
		_refresh.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_YELLOW));
		_refresh.setText(Messages.getString("ItemComptage.raffraichir"));

		gridData = new GridData();
		gridData.horizontalIndent = 2;
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = true;
		Label labelMontee = new Label(this, SWT.NONE);
		labelMontee.setText(Messages.getString("ItemComptage.montees")); //$NON-NLS-1$
		labelMontee.setBackground(DesktopStyle.getBackgroundColor());
		labelMontee.setLayoutData(gridData);
		labelMontee.setFont(_fontText);

		gridData = new GridData();
		gridData.horizontalIndent = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_textMontees = new Text(this, SWT.NONE);
		_textMontees.setBackground(DesktopStyle.getBackgroundColor());
		_textMontees.setLayoutData(gridData);
		_textMontees.setFont(_fontText);

		gridData = new GridData();
		gridData.horizontalIndent = 2;
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = true;
		Label labelDescente = new Label(this, SWT.NONE);
		labelDescente.setText(Messages.getString("ItemComptage.descentes")); //$NON-NLS-1$
		labelDescente.setBackground(DesktopStyle.getBackgroundColor());
		labelDescente.setLayoutData(gridData);
		labelDescente.setFont(_fontText);

		gridData = new GridData();
		gridData.horizontalIndent = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_textDescentes = new Text(this, SWT.NONE);
		_textDescentes.setLayoutData(gridData);
		_textDescentes.setBackground(DesktopStyle.getBackgroundColor());
		_textDescentes.setFont(_fontText);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = 10;
		gridData.horizontalSpan = 2;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		_reset = new Button(this, SWT.NONE);
		_reset.addSelectionListener(this);
		_reset.setLayoutData(gridData);
		_reset.setBackground(DesktopStyle.getBackgroundColor());
		_reset.setText(Messages.getString("ItemComptage.reset"));

		layout();
	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		configure();
	}
	
	public void setScheduler(Scheduler scheduler){
		super.setScheduler(scheduler);
		updateComptage();
	}

	private void configure() {

	}

	public static class ItemComptageFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelComptage(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelComptage.class.getName(),
				new ItemComptageFactory());
	}

	public void configure(Config config) {
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
	
	}

	private String getValue(String command) {
		String result = runConsoleCommand("/comptage " + command);
		if (result == null || result.toLowerCase().indexOf("failed") != -1
				|| result.toLowerCase().indexOf("err") != -1) {
			result = "Err";
		} else {
			result = result.trim();
		}

		return result;

	}

	private void reset() {
		getScheduler().execute(new Runnable() {
			public void run() {
				String result = runConsoleCommand("/comptage reset");
				if (result == null || result.toLowerCase().indexOf("failed") != -1
						|| result.toLowerCase().indexOf("err") != -1) {
					getLogger().warn("reset comptage:"+result);
				} else {
					result = result.trim();
					getLogger().debug("reset comptage:"+result);
				}
			}
		});
		
	}

	private void refresh(final String in, final String out) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				if (_textDescentes.isDisposed() == false) {
					_textDescentes.setText(out); //$NON-NLS-1$
				}
				if (_textMontees.isDisposed() == false) {
					_textMontees.setText(in); //$NON-NLS-1$
				}
			}
		});
		
		
	}

	private void updateComptage() {
		getScheduler().execute(new Runnable() {
			public void run() {
				String out = getValue("passengerscount out");
				String in = getValue("passengerscount in");
				refresh(in, out);
			}
		});
	}

	public void widgetSelected(SelectionEvent arg0) {
		if (arg0.getSource() == _reset) {
			reset();
		} else {
			updateComptage();
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
