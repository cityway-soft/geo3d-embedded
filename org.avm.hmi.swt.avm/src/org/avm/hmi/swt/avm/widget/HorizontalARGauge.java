package org.avm.hmi.swt.avm.widget;

import org.avm.business.protocol.phoebus.AvanceRetard;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class HorizontalARGauge extends Composite {

	private HorizontalARGauge _instance;

	private Display _display;

	private static final int MAX = 9;

	private static final String[] TITLE = new String[] { "En retard",
			"A l'heure", "En Avance" };

	private Label[] _list;

	private Color[] _color;

	public HorizontalARGauge(Composite parent, int ctrl) {
		super(parent, ctrl);
		_instance = this;
		create(this);
	}

	private void create(Composite composite) {
		_display = composite.getDisplay();
		setBackground(
				DesktopStyle.getBackgroundColor());

		GridLayout grid = new GridLayout();
		grid.numColumns = 3;

		composite.setLayout(grid);
		createGauge(composite);

		activate(0);

		refresh(composite);

	}

	private void activate(int val) {
		int idx;
		if (val > 6) {
			idx = 7;
		} else if (val < -6) {
			idx = 1;
		} else {
			idx = (val / 2) + 4;
		}

		for (int i = 0; i < MAX; i++) {
			_list[i].setBackground(_display.getSystemColor(SWT.COLOR_WHITE));
		}
		_list[idx - 1].setBackground(_color[idx - 1]);
		_list[idx].setBackground(_color[idx]);
		_list[idx + 1].setBackground(_color[idx + 1]);

	}

	private void createGauge(Composite composite) {
		// Composite panel = composite;//new Composite(composite, SWT.NONE);

		_list = new Label[MAX];
		_color = new Color[MAX];
		Composite panel = null;
		for (int i = 0; i < MAX; i++) {
			if (i % 3 == 0) {
				panel = new Composite(composite, SWT.NONE);
				panel.setBackground(
						DesktopStyle.getBackgroundColor());
				GridData data = new GridData();
				// data.widthHint = (50)*3;
				// data.heightHint = 200;
				data.horizontalIndent = 6;
				panel.setLayoutData(data);
				GridLayout grid = new GridLayout();
				grid.numColumns = 3;
				panel.setLayout(grid);
				Label title = new Label(panel, SWT.NONE);
				data = new GridData();
				data.horizontalSpan = 3;
				title.setText(TITLE[i / 3]);
				title.setLayoutData(data);
			}
			GridData data = new GridData();
			data.widthHint = 39;
			data.heightHint = 200;
			data.horizontalIndent = 1;
			// data.verticalIndent = INDENT;
			data.horizontalAlignment = GridData.FILL;

			_list[i] = new Label(panel, SWT.BORDER);
			_list[i].setLayoutData(data);
			// _list[i].setEnabled(false);
			if (i < 3) {
				_color[i] = _display.getSystemColor(SWT.COLOR_RED);
			} else if (i < 6) {
				_color[i] = _display.getSystemColor(SWT.COLOR_GREEN);
			} else if (i < MAX) {
				_color[i] = _display.getSystemColor(SWT.COLOR_YELLOW);
			}
		}
	}

	private void refresh(Composite composite) {
		composite.layout();
	}

	public void setAvanceRetard(final AvanceRetard ar) {
		System.out.println("[swt.avm.HorizontalARGauge] obj ar=" + ar);
		System.out.println("[swt.avm.HorizontalARGauge] entete="
				+ ar.getEntete());
		System.out.println("[swt.avm.HorizontalARGauge] prog="
				+ ar.getEntete().getProgression());
		System.out.println("[swt.avm.HorizontalARGauge] ar="
				+ ar.getEntete().getProgression().getRetard());

		int curseur = ar.getEntete().getProgression().getRetard();

		activate(curseur);
		refresh(_instance);
	}
}