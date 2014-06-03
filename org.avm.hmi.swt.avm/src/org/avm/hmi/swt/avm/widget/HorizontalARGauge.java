package org.avm.hmi.swt.avm.widget;

import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class HorizontalARGauge extends AbstractARGauge {

	
	public HorizontalARGauge(Composite parent, int ctrl) {
		super(parent, ctrl);
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

}