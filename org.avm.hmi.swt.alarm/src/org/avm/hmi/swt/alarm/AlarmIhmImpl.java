package org.avm.hmi.swt.alarm;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.hmi.swt.alarm.bundle.ConfigImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.ComponentContext;

public class AlarmIhmImpl extends Composite implements AlarmIhm,
		ConfigurableService {

	private static final int INDENT = Desktop.DEFAULT_FONTSIZE / 5;

	private Font _font; // @jve:decl-index=0:

	private ProducerManager _producer;

	private Alarm _alarm;

	private ComponentContext _context;

	private Config _config;

	private void send(Alarm alarm) {
		// MessageBox.setMessage("Info", "Send alarm " + alarm.description +
		// "("+alarm.status+")", MessageBox.MESSAGE_NORMAL, SWT.NONE);
		if (_producer != null) {
			System.out.println("Publish alarm:" + alarm);
			_producer.publish(alarm);
		}
	}

	public void addCommand(final Alarm alarm) {
		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = INDENT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 70;

		StateButton button = new StateButton(this, SWT.BORDER);
		button.setLayoutData(gridData);
		button.setText(alarm.getName());
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				StateButton b = (StateButton) e.getSource();
				alarm.setStatus(b.getSelection());
				send(alarm);
			}
		});
		this.layout();
	}

	public void addCommand(final Command command) {
		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = INDENT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 70;

		StateButton button = new StateButton(this, SWT.BORDER);
		button.setLayoutData(gridData);
		final Alarm alarm = command.getAlarm();
		button.setText(alarm.getName());
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				StateButton b = (StateButton) e.getSource();
				alarm.setStatus(b.getSelection());
				command.activate(b.getSelection());
				send(alarm);
			}
		});
		this.layout();
	}

	public AlarmIhmImpl(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;

		this.setLayout(gridLayout);
		setBackground(DesktopStyle.getBackgroundColor());
	}


	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public List getAlarm() {
		if (_alarm != null && _alarm.isStatus()) {
			LinkedList list = new LinkedList();
			list.add(_alarm);
			return list;
		} else {
			return null;
		}
	}

	public String getProducerPID() {
		return AlarmIhm.class.getName();
	}

	private void initialize() {
		// _alarm = new Alarm(false, "Pleine Charge",new Date(),
		// AlarmIhm.class.getName()+"@PleineCharge", Alarm.MAX_PRIORITY);
		// addCommand(_alarm);
		// addCommand(new Alarm(false, "Verglas",new Date(),
		// AlarmIhm.class.getName()+"@Verglas" , 1));
		// addCommand(new Alarm(false, "Bouchons",new Date(),
		// AlarmIhm.class.getName()+"@Bouchons" , 1));
		// addCommand(new Alarm(false, "Panne",new Date(),
		// AlarmIhm.class.getName()+"@Panne" , 1));
		// addCommand(new Alarm(false, "PMR",new Date(),
		// AlarmIhm.class.getName()+"@PMR" , 1));
		// addCommand(new Alarm(false, "Panne Girouette",new Date(),
		// AlarmIhm.class.getName()+"@PanneGirouette" , 1));
		// addCommand(new Alarm(false, "Panne Billettique",new Date(),
		// AlarmIhm.class.getName()+"@PanneBillettique" , 1));
		if (_config != null && _context != null) {
			Properties list = ((ConfigImpl) _config).getProperty(null);
			Enumeration enum = list.keys();
			while (enum.hasMoreElements()) {
				String clazz = (String) enum.nextElement();
				try {
					Properties p = ((ConfigImpl) _config).getProperty(clazz);
					Command cmd = CommandFactory.createCommand(clazz,
							_context, p);
					addCommand(cmd);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void configure(Config config) {
		_config = config;
		initialize();
	}

	public void setContext(ComponentContext context) {
		_context = context;
		initialize();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
