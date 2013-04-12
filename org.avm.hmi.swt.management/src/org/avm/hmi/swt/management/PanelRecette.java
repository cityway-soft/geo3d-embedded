package org.avm.hmi.swt.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PanelRecette extends AbstractPanel implements SelectionListener,ConsoleFacadeInjector {

	private static final int INDENT = Desktop.DEFAULT_FONTSIZE / 5;

	private static final String[] ITEMS = { "afficheur", "ango",
		"phonie", "audio-voyageur-int","audio-voyageur-ext", "girouette", "gps", "can", "tft", "acces-wifi", "liaison-pcc", "comptage" };

	private static final String[] BUNDLES = { "afficheur", "leds",
		"phony", "sound","sound", "girouette", "gps", "can", "tft", "wifi", "media.ctw", "comptage" };

	private String _filePersist;
	private String _fileUpload;
	private Properties _properties;

	public PanelRecette(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		this.setLayout(gridLayout);
		_filePersist = System.getProperty("org.avm.home") + "/data/recette.txt"; //$NON-NLS-1$ //$NON-NLS-2$
		_fileUpload = System.getProperty("org.avm.home") + "/data/upload/recette.txt"; //$NON-NLS-1$ //$NON-NLS-2$
		_properties = new Properties();

		load();
	}
	
	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		create();
	}

	public void load() {
		FileInputStream in;
		Properties props = new Properties();
		try {

			in = new FileInputStream(_filePersist);
			props.load(in);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < ITEMS.length; i++) {
			if (props.get(ITEMS[i]) == null){
				_properties.put(ITEMS[i], "false");
			}
			else{
				_properties.put(ITEMS[i], props.get(ITEMS[i]));
			}
		}
		File file = new File(_filePersist);
		if (file.exists() == false) {
			return;
		}

	}

	public void save() {
		FileOutputStream out;
		try {
			out = new FileOutputStream(_filePersist);
			_properties.save(out, new Date().toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			out = new FileOutputStream(_fileUpload);
			_properties.save(out, new Date().toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes itemBundles
	 * 
	 */
	private void create() {
		for (int i = 0; i < ITEMS.length; i++) {
			boolean state = (_properties.getProperty(ITEMS[i], "false"))
					.toLowerCase().equals("true");
			addTestButton(ITEMS[i], state, BUNDLES[i]);
		}
	}
	

	private void addTestButton(String name, boolean state, String bundle) {
		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = INDENT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;

		StateButton button = new StateButton(this, SWT.BORDER);
		button.setActiveColor(getDisplay().getSystemColor(SWT.COLOR_GREEN));
		button.setText(Messages.getString("ItemTest.test-" + name));
		button.setData(name);
		button.setState(state);
		button.setEnabled(isBundleAvailable(bundle));
		button.addSelectionListener(this);
		button.setLayoutData(gridData);
//		button.setNotActiveLabel("Désac.");
//		button.setActiveLabel("Activer");
		button.setNotActiveColor(DesktopStyle.getBackgroundColor());
		button.setActiveColor(DesktopImpl.VERT);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Object obj = e.getSource();

		StateButton b = (StateButton) e.getSource();
		String name =  (String)b.getData();
		_properties.put(name, b.getSelection()?"true":"false");
		save();
	}
	
	
	public static class ItemRecetteFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelRecette(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelRecette.class.getName(),
				new ItemRecetteFactory());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
