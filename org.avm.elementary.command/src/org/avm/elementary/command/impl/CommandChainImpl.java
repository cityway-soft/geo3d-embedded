package org.avm.elementary.command.impl;

import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.chain.Command;
import org.apache.log4j.Logger;
import org.avm.elementary.command.CommandChain;
import org.avm.elementary.command.CommandChainContext;
import org.avm.elementary.command.bundle.Activator;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;

public class CommandChainImpl implements CommandChain, ConfigurableService,
		ManageableService {

	private Logger _log = Logger.getInstance(this.getClass());

	private CommandChainConfig _config;

	private Command _command;

	private URL _url;

	public CommandChainImpl() {
	}

	public void configure(Config config) {
		_config = (CommandChainConfig) config;
	}

	public void start() {
		try {
			if (_url != null) {
				InputStreamReader xml;
				xml = new InputStreamReader(_url.openStream());
				XmlCatalogFactory factory = new XmlCatalogFactory(xml);
				_command = factory.getCommand(_config.getCommand());
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public void stop() {
		_command = null;
	}

	public boolean execute(CommandChainContext context) throws Exception {
		if (_command != null) {
			context.setComponentContext(Activator.getDefault().getContext());
			return _command.execute(context);
		}
		return false;
	}

	public URL getUrl() {
		return _url;
	}

	public void setUrl(URL url) {
		_url = url;
	}
}