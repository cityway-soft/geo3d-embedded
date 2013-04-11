package org.avm.hmi.swt.management;

import java.net.Socket;

import org.avm.elementary.common.Scheduler;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PanelNetwork extends AbstractPanel {
	private final String CMD_MANAGEMENT_HOST = "/management host ";

	private ItemWiFi _wifi;

	private ItemGPRS _gprs;

	public PanelNetwork(Composite parent, int style) {
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
		setBackground(DesktopStyle.getBackgroundColor());

		_wifi = new ItemWiFi(this, SWT.NONE);
		_wifi.setListener(new WiFiListener());
		_gprs = new ItemGPRS(this, SWT.NONE);
		_gprs.setListener(new GPRSListener());
	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
	}

	public void setScheduler(Scheduler scheduler) {
		super.setScheduler(scheduler);
		updateWiFiAdress(0);
		updateGPRSAdress(0);
	}

	private void updateWiFiAdress(final long waitalitle) {
		getScheduler().execute(new Runnable() {
			public void run() {
				String client = null;
				String server = null;
				try {
					_wifi.activate(false);
					try{
					Thread.sleep(waitalitle);
					}
					catch(Throwable t){}
					_wifi.setAdress("", "");
					server = getAdressFromHosts("ftpserver.avm.org");
					_wifi.setServerAdressChangeEnabled(true);
					_wifi.setServerInHost(true);
					if (server == null) {
						_wifi.setServerInHost(false);
						_wifi.setServerAdressChangeEnabled(false);
						Socket socket = new Socket("ftpserver.avm.org", 8021);
						client = socket.getLocalAddress().getHostAddress();
						server = socket.getInetAddress().getHostAddress();
					}
					_wifi.setAdress(client, server);

				} catch (Exception e) {
					_wifi.setAdress(client, server);
				} finally {
					_wifi.activate(true);
				}
			}
		});
	}

	private void updateGPRSAdress(final long waitalitle) {
		getScheduler().execute(new Runnable() {
			public void run() {
				String client = null;
				String server = null;
				try {
					_gprs.activate(false);
					try{
						Thread.sleep(waitalitle);
						}
						catch(Throwable t){}
					_gprs.setAdress("", "");
					server = getAdressFromHosts("saml.avm.org");
					_gprs.setServerInHost(true);
					if (server == null) {
						_gprs.setServerInHost(false);
					}
					_gprs.setAdress(null, server);
					Socket socket = new Socket("saml.avm.org", 8094);
					client = socket.getLocalAddress().getHostAddress();
					_gprs.setAdress(client, server);

				} catch (Exception e) {
					_gprs.setAdress(client, server);
				} finally {
					_gprs.activate(true);
				}
			}
		});
	}

	private String getAdressFromHosts(String hostname) {
		String result = runConsoleCommand(CMD_MANAGEMENT_HOST + hostname);
		if (result != null) {
			result = result.trim();
		}
		if (result.equalsIgnoreCase("unknown")){
			result=null;
		}
		return result;
	}

	private void updateAdress(String hostname, String address) {
		String command=CMD_MANAGEMENT_HOST + hostname + " " + address;
		runConsoleCommand(command);
		getLogger().debug(command);
	}
	
	public static class ItemNetworkFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelNetwork(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelNetwork.class.getName(),
				new ItemNetworkFactory());
	}
	
	public class WiFiListener implements ItemNetworkListener{
		public void adressChanged(String oldad, String newad) {
			updateAdress("ftpserver.avm.org", newad);
		}

		public void connect() {
			getLogger().debug("/wifi connect");
			runConsoleCommand("/wifi connect");
			updateWiFiAdress(2000);
		}

		public void disconnect() {
			getLogger().debug("/wifi disconnect");
			runConsoleCommand("/wifi disconnect");
			updateWiFiAdress(2000);
		}

		public void isConnected() {
			getLogger().debug("isconnected");
			updateWiFiAdress(0);
		}
	}
	
	public class GPRSListener implements ItemNetworkListener{
		public void adressChanged(String oldad, String newad) {
			updateAdress("saml.avm.org", newad);
			runConsoleCommand("/mana exec sh '/etc/ppp/ip-down ppp0'");
			runConsoleCommand("/mana exec sh '/etc/ppp/ip-up ppp0'");
		}

		public void connect() {
			getLogger().debug("Not yet implemented");
		}

		public void disconnect() {
			getLogger().debug("Not yet implemented");
		}

		public void isConnected() {
			updateGPRSAdress(0);
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
