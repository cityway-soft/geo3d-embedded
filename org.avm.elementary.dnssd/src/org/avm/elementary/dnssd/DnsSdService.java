package org.avm.elementary.dnssd;

import java.io.IOException;

import org.avm.elementary.common.ConsumerService;

public interface DnsSdService extends ConsumerService {
	public void register(String serviceName, int port, String[] params)
			throws  Exception;
	
	public void unregister(String serviceName) throws IOException;
}
