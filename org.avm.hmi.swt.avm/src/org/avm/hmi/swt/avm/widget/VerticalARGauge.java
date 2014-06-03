package org.avm.hmi.swt.avm.widget;

import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class VerticalARGauge extends AbstractARGauge {

		
	public VerticalARGauge(Composite parent, int ctrl) {
		super(parent, ctrl);
		
		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		setLayout(grid);
		create();
	}

	private void create() {
		_display = getDisplay();
		setBackground(DesktopStyle.getBackgroundColor());

		_font = DesktopImpl.getFont(5, SWT.NORMAL);
		_avanceRetard = new Label(this, SWT.CENTER | SWT.BORDER);
		_avanceRetard.setFont(DesktopImpl.getFont(5, SWT.BOLD));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		// data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 15;
		_avanceRetard.setLayoutData(data);
		_avanceRetard.setBackground(DesktopStyle.getBackgroundColor());

		createGauge(this);

		activate(0);

		refresh(this);

	}

	private void createGauge(Composite composite) {
		_list = new Label[MAX];
		_color = new Color[MAX];
		Composite panel = null;
		for (int i = 0; i < MAX; i++) {
			if (i % 3 == 0) {
				GridLayout grid = new GridLayout();
				grid.numColumns = 1;

				GridData data = new GridData();
				data.grabExcessHorizontalSpace = true;
				data.grabExcessVerticalSpace = true;
				data.horizontalAlignment = GridData.FILL;
				data.verticalAlignment = GridData.FILL;
				panel = new Composite(composite, SWT.NONE);
				panel.setBackground(DesktopStyle.getBackgroundColor());
				panel.setLayoutData(data);
				panel.setLayout(grid);

				Label title = new Label(panel, SWT.NONE);
				data = new GridData();
				data.horizontalSpan = 3;
				data.heightHint = 15;
				title.setText(TITLE[i / 3]);
				title.setFont(_font);
				title.setLayoutData(data);
				title.setBackground(DesktopStyle.getBackgroundColor());
			}
			GridData data = new GridData();
			data.grabExcessHorizontalSpace = true;
			// data.grabExcessVerticalSpace = true;
			data.horizontalAlignment = GridData.FILL;
			// data.verticalAlignment = GridData.FILL;
			data.heightHint = 12;

			_list[i] = new Label(panel, SWT.BORDER | SWT.CENTER);
			_list[i].setLayoutData(data);
			if (i < 3) {
				_color[i] = _display.getSystemColor(COLOR_AVANCE);
			} else if (i < 6) {
				_color[i] = _display.getSystemColor(COLOR_A_LHEURE);
			} else if (i < MAX) {
				_color[i] = _display.getSystemColor(COLOR_RETARD);
			}
		}
	}
}