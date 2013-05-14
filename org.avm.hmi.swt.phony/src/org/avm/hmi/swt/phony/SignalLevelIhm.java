package org.avm.hmi.swt.phony;

import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.HorizontalQualityLevel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class SignalLevelIhm extends Composite {

	private Composite composite = null;

	// private VerticalQualityLevel qualityLevel;
	private HorizontalQualityLevel qualityLevel;

	private int _nbBadQuality = 0;

	private Label labelGSM;

	private int _attached = -3;

	private static final String GSM = "Gsm";

	private static final String GPRS = "Gprs";

	public SignalLevelIhm(Composite parent, int ctrl) {
		super(parent, ctrl);
		initialize();

		setBackground(DesktopStyle.getBackgroundColor());
		//parent.layout();
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (qualityLevel != null) {
			qualityLevel.setEnabled(b);
		}
		if (labelGSM != null) {
			if (!b) {
				labelGSM.setText("---");
			}
		}
	}

	/**
	 * This method initializes qualityLevel
	 * 
	 */
	private void createQualityLevel() {
		GridData gridData4 = new GridData();
		gridData4.grabExcessHorizontalSpace = false;
		//gridData4.horizontalAlignment = GridData.FILL;
		gridData4.verticalAlignment = GridData.FILL;
		gridData4.grabExcessVerticalSpace = false;
		qualityLevel = new HorizontalQualityLevel(composite, SWT.NONE);
		qualityLevel.setBackground(DesktopStyle.getBackgroundColor());
		qualityLevel.setForegroundColor(Display.getCurrent().getSystemColor(
				SWT.COLOR_GREEN));
		qualityLevel.setLayoutData(gridData4);
		qualityLevel.setQuality(0);

		labelGSM = new Label(composite, SWT.CENTER);
		labelGSM.setBackground(DesktopStyle.getBackgroundColor());
		labelGSM.setText("---");
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		labelGSM.setLayoutData(gridData3);

	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.horizontalSpacing = 0;
		gridLayout2.marginWidth = 0;
		gridLayout2.marginHeight = 0;
		gridLayout2.verticalSpacing = 0;
		gridLayout2.numColumns = 2;
		this.setLayout(gridLayout2);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		this.setLayoutData(gridData);

		composite = this;
		createQualityLevel();
	}

	public void setSignalQuality(final int quality) {
		if (isDisposed() == false && qualityLevel != null) {
			this.getDisplay().syncExec(new Runnable() {
				public void run() {
					if (quality == 0) {
						_nbBadQuality++;
						if (_nbBadQuality > 3) {
							qualityLevel.setQuality(quality);
							qualityLevel.layout();
						}
					} else {
						_nbBadQuality = 0;
						qualityLevel.setQuality(quality);
						qualityLevel.layout();
					}
				}
			});
		}
	}

	public void setAttachment(final int attached) {
		if (isDisposed() == false && qualityLevel != null) {
			this.getDisplay().syncExec(new Runnable() {
				public void run() {
					if (_attached == attached)
						return;
					switch (attached) {
					case Phony.ATTACHEMENT_NONE:
						labelGSM.setText("ooo");
						qualityLevel.setForegroundColor(Display.getCurrent()
								.getSystemColor(SWT.COLOR_RED));
						break;
					case Phony.ATTACHEMENT_GSM_GPRS_OK:
						labelGSM.setText(GSM + "/\n" + GPRS);
						qualityLevel.setForegroundColor(Display.getCurrent()
								.getSystemColor(SWT.COLOR_GREEN));
						break;
					case Phony.ATTACHEMENT_GSM_OK:
						labelGSM.setText(GSM);
						qualityLevel.setForegroundColor(Display.getCurrent()
								.getSystemColor(SWT.COLOR_GREEN));
						break;

					default:
						break;
					}
					_attached = attached;
					layout();
				}
			});
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
