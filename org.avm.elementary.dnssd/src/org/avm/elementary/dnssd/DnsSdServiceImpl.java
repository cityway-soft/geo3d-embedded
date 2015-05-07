package org.avm.elementary.dnssd;

import java.io.IOException;
import java.io.NotActiveException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.xbill.DNS.Name;
import org.xbill.mDNS.MulticastDNSService;
import org.xbill.mDNS.ServiceInstance;
import org.xbill.mDNS.ServiceName;

public class DnsSdServiceImpl implements DnsSdService, ConfigurableService,
		ManageableService, ProducerService {

	private Logger _log;

	private DnsSdServiceConfig _config;

	private boolean _started = false;

	private ProducerManager _producer;

	private MulticastDNSService mDNSService;

	private HashMap services = new HashMap();

	public DnsSdServiceImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (DnsSdServiceConfig) config;
	}

	public void start() {
		try {
			mDNSService = new MulticastDNSService();
			_started = true;
			//-- test
			register("_gps._tcp.", 5555, new String[] { "param=0" });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		unregister();
		try {
			mDNSService.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		_started = false;
	}

	public void notify(Object o) {
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void register(String serviceName, int port, String[] params)
			throws Exception {
		if (_started == false) {
			throw new NotActiveException();
		}
		int priority = 10;
		int weight = 10;
		String domain = "local.";
		String hostname = "localhost";

		Name fqn = new Name(hostname + "."
				+ (domain.endsWith(".") ? domain : domain + "."));

		ServiceName srvName = new ServiceName(hostname + "." + serviceName
				+ (domain.endsWith(".") ? domain : domain + "."));

		ServiceInstance serviceInstance = new ServiceInstance(srvName,
				priority, weight, port, fqn,
				new InetAddress[] { InetAddress.getByName("192.168.56.102") },
				params);


		ServiceInstance registeredService;
		registeredService = mDNSService.register(serviceInstance);
		if (registeredService != null) {
			services.put(serviceName, serviceInstance);
			_log.debug("Services Successfully Registered: "	+ registeredService);
		} else {
			throw new Exception("Service Registration Failed!");
		}
	}
	
	
	public void unregister(String serviceName) throws IOException{
		ServiceInstance rs = (ServiceInstance) services.get(serviceName);
			mDNSService.unregister(rs);
	}

	private void unregister() {
		Set keys = services.keySet();
		Iterator iter = keys.iterator();
		
		while(iter.hasNext()){
			String sName = (String) iter.next();
			try {
				unregister(sName);
			} catch (IOException e) {
				_log.error("unregister error", e);
			}
		}
	}
}