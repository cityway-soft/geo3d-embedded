package org.angolight.bo.impl;

import org.angolight.bo.Bo;
import org.angolight.bo.states.ZonesManagerCmd;
import org.angolight.bo.states.ZonesManagerImpl;
import org.angolight.kinetic.Kinetic;
import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

public class BoImpl implements Bo, ConfigurableService,
		ConsumerService, ManageableService, ProducerService {

	private Logger _log = Logger.getInstance(this.getClass());

	private ZonesManagerCmd _zonesManager = null;

	public BoImpl() {
		_zonesManager = (ZonesManagerCmd) new ZonesManagerImpl();
	}

	public void configure(Config config) {
		((ConfigurableService) _zonesManager).configure(config);
	}

	public void start() {
		((ManageableService) _zonesManager).start();
	}

	public void stop() {
		((ManageableService) _zonesManager).stop();
	}

	public void setProducer(ProducerManager producer) {
		((ProducerService) _zonesManager).setProducer(producer);
	}

	public void notify(Object o) {
		if (_zonesManager == null)
			return;
		try {
			if (o instanceof Kinetic) {
				onKinetic((Kinetic) o);
			}
		} catch (Exception e) {
			_log.error("Notify " + o + " Erreur : " + e.getMessage());
		}
	}

	public void onKinetic(Kinetic kinetic) {
		_zonesManager.onKinetic(kinetic);
	}

	public String getCurvesVersion() {
		if (_zonesManager != null) {
			return ((ZonesManagerImpl) _zonesManager).getCurvesVersion();
		} else
			return "noZonesManager";
	}
}
