
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.avm.elementary.geofencing.Balise;
import org.avm.elementary.wifi.WifiManagerConfig;
import org.avm.elementary.wifi.WifiManagerImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class Test extends MockObjectTestCase {

	private WifiManagerConfigStub stubConfig;
	private Mock mockWifi;
	private WifiManagerImpl service;

	protected void setUp() throws Exception {
		Logger.getRoot().removeAllAppenders();
		Logger root = Logger.getRoot();
		root.addAppender(new ConsoleAppender(new PatternLayout()));
		root.setPriority(Priority.DEBUG);

		stubConfig = new WifiManagerConfigStub();
		mockWifi = mock(org.avm.device.wifi.Wifi.class);

		service = new WifiManagerImpl();
	}

	public void testEntreeBaliseDepot() {
		stubConfig.setBaliseList("10000");
		stubConfig.setDisconnectTimeout("10");
		service.configure(stubConfig);
		service.setWifi((org.avm.device.wifi.Wifi) mockWifi.proxy());
		service.start();
		mockWifi.expects(once()).method("connect").withNoArguments();
		Balise balise = new Balise(10000, true);
		service.notify(balise);
	}

	public void testEntreeSortieBaliseDepot() {
		stubConfig.setBaliseList("10000");
		stubConfig.setDisconnectTimeout("0");
		service.configure(stubConfig);
		service.setWifi((org.avm.device.wifi.Wifi) mockWifi.proxy());
		service.start();
		mockWifi.expects(once()).method("connect").withNoArguments();
		Balise balise = new Balise(10000, true);
		service.notify(balise);

		mockWifi.expects(once()).method("disconnect").withNoArguments();
		balise = new Balise(10000, false);
		service.notify(balise);

		// attente du declenchement du timeout de la machine à état
		Thread t = new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testEntreeBaliseHorsDepot() {
		stubConfig.setBaliseList("1");
		stubConfig.setDisconnectTimeout("10000");
		service.configure(stubConfig);
		service.setWifi((org.avm.device.wifi.Wifi) mockWifi.proxy());
		service.start();
		Balise balise = new Balise(10000, true);
		service.notify(balise);
	}

	// -------------------------------

	public class WifiManagerConfigStub implements WifiManagerConfig {

		private String baliselist;
		private String timeout;

		public WifiManagerConfigStub() {

		}

		public void updateConfig() {
			// TODO Auto-generated method stub

		}

		public void updateConfig(boolean modified) {
			// TODO Auto-generated method stub

		}

		public void delete() {
			// TODO Auto-generated method stub

		}

		public String getBaliseList() {
			return baliselist;
		}

		public void setBaliseList(String list) {
			this.baliselist = list;
		}

		public String getDisconnectTimeout() {
			return timeout;
		}

		public void setDisconnectTimeout(String timeout) {
			this.timeout = timeout;

		}

	}

}
