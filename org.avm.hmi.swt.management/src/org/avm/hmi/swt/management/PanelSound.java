package org.avm.hmi.swt.management;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.MessageBox;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PanelSound extends AbstractPanel {

	private static final String GROUPNAME = "sound";

	private static final String TEST_FILENAME = "test-voyageur-continu.mp3"; //$NON-NLS-1$
//	private static final String TEST_PLAY_AUDIO_VOYAGEUR_CONTINU = "/sound configure -n voyageur-interieur\n" //$NON-NLS-1$
//			+ "/variable write -n vioaudio 1\n" + "/mp3 play {0}\n"; //$NON-NLS-1$ //$NON-NLS-2$
//	private static final String TEST_STOP_AUDIO_VOYAGEUR_CONTINU = "/mp3 stop\n" //$NON-NLS-1$
//			+ "/variable write -n vioaudio 0\n" + "/sound configure -n default"; //$NON-NLS-1$ //$NON-NLS-2$
//	private static final String TEST_AUDIO_VOYAGEUR = "/vocal testvoyageur"; //$NON-NLS-1$
//	private static final String TEST_AUDIO_CONDUCTEUR = "/vocal testconducteur"; //$NON-NLS-1$

	private VolumeSelectionListener _volumeListener;

	private Label _labelVolume;

	private Hashtable _hashLabelVolume;

	private List _soundConfigs;

	private boolean _changed;

	private String _filename;

	public PanelSound(Composite parent, int style) {
		super(parent, style);
		_hashLabelVolume = new Hashtable();
		_soundConfigs = new ArrayList();
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		this.setLayout(gridLayout);
		_volumeListener = new VolumeSelectionListener();

		_filename = System.getProperty("org.avm.home") + "/data/sound/"; //$NON-NLS-1$ //$NON-NLS-2$
		File dir = new File(_filename);
		File[] list = dir.listFiles();
		if (list == null){
			MessageBox.setMessage("Attention", "Aucun fichier dans " + _filename, MessageBox.MESSAGE_WARNING, SWT.NONE);
		}
		else{
		for (int i = 0; i < list.length; i++) {
			File f = list[i];
			if (f.isDirectory()) {
				_filename += f.getName();
				break;
			}
		}
		}
		_filename += "/" + TEST_FILENAME; //$NON-NLS-1$

	}

	public void start() {
		super.start();
		_changed = false;
	}

	public void stop() {
		super.stop();
		if (_changed) {
			saveSoundConfiguration();
		}
	}

	private void createVolumeManager(String name, Properties configuration) {
		GridData gridData;

		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = Desktop.DEFAULT_FONTSIZE / 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		Label label = new Label(this, SWT.NONE);
		label.setBackground(DesktopStyle.getBackgroundColor());
		label.setText(name);

		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = Desktop.DEFAULT_FONTSIZE / 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		Button button = new Button(this, SWT.PUSH);
		button.setText("-");
		button.setData(configuration);
		button.addSelectionListener(_volumeListener);
		button.setLayoutData(gridData);
		button.setBackground(DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = Desktop.DEFAULT_FONTSIZE / 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		button = new Button(this, SWT.PUSH);
		button.setText("+");
		button.setData(configuration);
		button.addSelectionListener(_volumeListener);
		button.setLayoutData(gridData);
		button.setBackground(DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = Desktop.DEFAULT_FONTSIZE / 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		label = new Label(this, SWT.NONE);
		_hashLabelVolume.put(name, label);
		label.setBackground(DesktopStyle.getBackgroundColor());
		String svol = configuration.getProperty("volume", "100");
		int vol = 0;
		try {
			vol = Integer.parseInt(svol);
		} catch (Throwable t) {
		}
		label.setText(Integer.toString(vol));

		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = Desktop.DEFAULT_FONTSIZE / 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		button = new Button(this, SWT.PUSH);
		button.setText("Test");
		button.setData(configuration);
		button.addSelectionListener(_volumeListener);
		button.setLayoutData(gridData);
		button.setBackground(DesktopStyle.getBackgroundColor());

		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = Desktop.DEFAULT_FONTSIZE / 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = Management.BUTTON_HEIGHT;
		StateButton sbutton = new StateButton(this, SWT.PUSH);
		sbutton.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
		sbutton.setText("Test");
		sbutton.setData(configuration);
		sbutton.addSelectionListener(_volumeListener);
		sbutton.setLayoutData(gridData);
		sbutton.setBackground(DesktopStyle.getBackgroundColor());

	}

	public void setVolume(String name, String volume) {
		Label label = (Label) _hashLabelVolume.get(name);
		label.setText(volume);
		layout();
	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		loadSoundConfiguration();
	}

	private void loadSoundConfiguration() {
		String val = runConsoleCommand("/sound list");
		if (val != null) {
			try {
				StringTokenizer t = new StringTokenizer(val, "}");
				while (t.hasMoreElements()) {
					Properties p = new Properties();
					String sprop = t.nextToken();
					sprop=sprop.trim();
					if (sprop.trim().length() != 0) {
						sprop = sprop.replace('{', ' ');
						sprop = sprop.replace(',', '\n');
						p.load(new ByteArrayInputStream(sprop.getBytes()));
						p.put("oldvolume", p.getProperty("volume", "100"));
						createVolumeManager(p.getProperty("name"), p);
						_soundConfigs.add(p);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateConfig(Properties p){
		StringBuffer cmd = new StringBuffer();
		cmd.append("/sound add -n ");
		cmd.append(p.getProperty("name"));
		cmd.append(" -f ");
		cmd.append(p.getProperty("url"));
		cmd.append(" -p ");
		cmd.append(p.getProperty("priority"));
		cmd.append(" -v ");
		cmd.append(p.getProperty("volume", "100"));
		String result=runConsoleCommand(cmd.toString());
		//--Command failed; No such command group: sound
		int nbtentative=4;
		while (result.indexOf("failed") != -1 && nbtentative>0){
			try {
				Thread.sleep(550);
			} catch (InterruptedException e) {
			}
			nbtentative--;
			result=runConsoleCommand(cmd.toString());
			result+=runConsoleCommand("/sound list");
		}
	}

	private void saveSoundConfiguration() {
		String cmd="/sound updateconfig";
		String result=runConsoleCommand(cmd.toString());
		//--Command failed; No such command group: sound
		int nbtentative=4;
		while (result.indexOf("failed") != -1 && nbtentative>0){
			try {
				Thread.sleep(550);
			} catch (InterruptedException e) {
			}
			nbtentative--;
			result=runConsoleCommand(cmd.toString());
			result+=runConsoleCommand("/sound list");
		}
	}

	public class VolumeSelectionListener extends SelectionAdapter {

		public void widgetSelected(SelectionEvent ev) {
			String buttonName = null;
			Properties conf = null;
			int vol = 0;
			String name = null;
			if (ev.getSource() instanceof Button) {
				Button button = (Button) ev.getSource();
				conf = ((Properties) button.getData());
				vol = 0;
				try {
					vol = Integer.parseInt(conf.getProperty("volume", "100"));
				} catch (Throwable t) {

				}
				buttonName = button.getText();
				name = conf.getProperty("name");

				if (buttonName.equalsIgnoreCase("test")) {
					String n = conf.getProperty("name");
					boolean changed=!conf.getProperty("volume", "100").equals(conf.getProperty("oldvolume", "100"));
					if (changed){
						updateConfig(conf);
					}
					String cmd = null;
					if (n.indexOf("exterieur") != -1) {
						cmd = "/vocal testvoyageur exterieur";
					} else if (n.indexOf("interieur") != -1) {
						cmd = "/vocal testvoyageur interieur";
					} else if (n.indexOf("conducteur") != -1) {
						cmd = "/vocal testconducteur";
					}
					if (cmd != null) {
						runConsoleCommand(cmd);
					}

				} else if (buttonName.equalsIgnoreCase("+")) {
					_changed = true;
					conf.put("oldvolume", Integer.toString(vol));
					vol++;
					vol = vol > 100 ? 100 : vol;
					conf.put("volume", Integer.toString(vol));
					
					setVolume(name, Integer.toString(vol));
				} else if (buttonName.equalsIgnoreCase("-")) {
					_changed = true;
					conf.put("oldvolume", Integer.toString(vol));
					vol--;
					vol = vol < 0 ? 0 : vol;
					conf.put("volume", Integer.toString(vol));
					setVolume(name, Integer.toString(vol));
				}
			} else {
				// -- statebutton
				StateButton button = (StateButton) ev.getSource();
				boolean state = button.getSelection();
				conf = ((Properties) button.getData());
				vol = 0;
				try {
					vol = Integer.parseInt(conf.getProperty("volume", "100"));
				} catch (Throwable t) {

				}
				name = conf.getProperty("name");
				if (state) {
					int vioaudio = (name.indexOf("voyageur") != -1) ? 1 : 0;
					int cioaudio = (name.indexOf("conducteur") != -1) ? 1 : 0;
					String result="";
					boolean changed=!conf.getProperty("volume", "100").equals(conf.getProperty("oldvolume", "100"));
					if (changed){
						updateConfig(conf);
					}
					result=runConsoleCommand("/sound volume -v " + vol);
					result=runConsoleCommand("/sound configure -n " + name);
					result=runConsoleCommand("/variable write -n vioaudio-int " + vioaudio);
					result=runConsoleCommand("/variable write -n vioaudio-ext " + vioaudio);
					result=runConsoleCommand("/variable write -n cioaudio " + cioaudio);
					result=runConsoleCommand("/mp3 play " + _filename);
				} else {
					runConsoleCommand("/mp3 stop");
				}
			}

		}
	}

	protected boolean checkService() {
		return isBundleAvailable("sound");
	}
	
	
	public static class ItemSoundFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelSound(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelSound.class.getName(),
				new ItemSoundFactory());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
