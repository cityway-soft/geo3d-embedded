package org.avm.business.tracking;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.DemandeStatut;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.ReponseStatut;
import org.avm.business.tracking.bundle.Activator;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;

public class TrackingImpl implements Tracking, ConsumerService,
		ConfigurableService, MessengerInjector {

	private Logger _log;

	private TrackingConfig _config;

	private Messenger _messenger;

	public TrackingImpl() {
		_log = Activator.getDefault().getLogger();
	}

	public void configure(Config config) {
		_config = (TrackingConfig) config;
	}

	public void setMessenger(Messenger messenger) {
		_messenger = messenger;
	}

	public void unsetMessenger(Messenger messenger) {
		_messenger = null;
	}

	public void notify(Object o) {
		if (o instanceof DemandeStatut) {
			try {
				sendReponseStatut((DemandeStatut) o);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
	}

	private void sendReponseStatut(DemandeStatut request) throws Exception {
		if (_messenger != null) {
			ReponseStatut response = createReponseStatut(request);
			_messenger.send(new Hashtable(), response);
		}
	}

	private ReponseStatut createReponseStatut(DemandeStatut request) {
		ReponseStatut response = new ReponseStatut();
		Entete entete = response.getEntete();
		entete.getChamps().setPosition(1);
		return response;
	}

}
