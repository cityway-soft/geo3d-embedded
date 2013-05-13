package org.avm.hmi.swt.girouette;

import org.avm.business.girouette.Girouette;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.ComponentContext;

public class GirouetteIhmImpl extends Composite implements GirouetteIhm {

	private ProducerManager _producer;

	private Girouette _girouette;

	private ComponentContext _context;

	private Desktop _desktop;

	private String _tabName;

	private ChoiceListener _listener;

	private GirouetteIhmImpl _instance;

	private CodeSelection _selection;

	public CodeSelection getCodeSelection() {
		return _selection;
	}

	public GirouetteIhmImpl(Composite parent, int style, String tabName) {
		super(parent, style);
		_tabName = tabName;
		setBackground(DesktopStyle.getBackgroundColor());
		setLayout(new FillLayout());
		_selection = new CodeSelection(this, SWT.FILL);
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
	}
}
