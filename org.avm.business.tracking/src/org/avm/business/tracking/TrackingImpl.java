package org.avm.business.tracking;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.ChampsOptionnels;
import org.avm.business.protocol.phoebus.DemandeStatut;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.ReponseStatut;
import org.avm.business.tracking.bundle.Activator;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;

public class TrackingImpl implements Tracking, ConsumerService,
		ConfigurableService, MessengerInjector, Runnable, ManageableService {

	private static final String JDB_TAG = "TRACKING";

	private final Logger logger;

	private TrackingConfig _config;

	private Messenger _messenger;

	private Object _taskid;

	private final Scheduler _scheduler;

	int _frequency = 0;

	public TrackingImpl() {

		this.logger = Activator.getDefault().getLogger();
		this._scheduler = new Scheduler();
	}

	public void configure(final Config config) {

		this._config = (TrackingConfig) config;
	}

	public void notify(final Object o) {

		this.logger.info("Receive :" + o);
		if (o instanceof DemandeStatut) {
			try {
				this.sendReponseStatut((DemandeStatut) o);
			} catch (final Exception e) {
				this.logger.error(e.getMessage(), e);
			}
		}
	}

	public void run() {
		try {
			this.sendReponseStatut(null);
		} catch (final Exception e) {
		}
	}

	public void setFrequency(final int freq) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("set alarm tracking frequency : " + freq);
		}
		this.updateState();
	}

	public void setMessenger(final Messenger messenger) {

		this._messenger = messenger;
	}

	public void start() {
		this._frequency = this._config.getFrequency();
		this.resetTimer();
	}

	public void stop() {

		if (this._taskid != null) {
			this._scheduler.cancel(this._taskid);
		}
	}

	public void unsetMessenger(final Messenger messenger) {

		this._messenger = null;
	}

	public void updateState() {

		final int freq = this._frequency;

		if (freq != this._frequency) {
			this.resetTimer();
		}
	}

	private ReponseStatut createReponseStatut(final DemandeStatut request) {

		final ReponseStatut response = new ReponseStatut();
		final Entete entete = response.getEntete();
		if (request != null) {
			final ChampsOptionnels champs = request.getStatut();
			entete.getChamps().setProgression(champs.getProgression());
			entete.getChamps().setAnomalie(champs.getAnomalie());
			entete.getChamps().setService(champs.getService());
		}
		entete.getChamps().setPosition(1);
		return response;
	}

	private void resetTimer() {

		if (this._taskid != null) {
			this._scheduler.cancel(this._taskid);
		}
		if (_frequency != 0) {
			this._taskid = this._scheduler.schedule(this,
					this._frequency * 1000, false);
			this.logger.debug("Schedule tracking with frequency "
					+ this._frequency);
		} else {
			logger.info("Periodic Tracking disabled");
		}
	}

	private void sendReponseStatut(final DemandeStatut request)
			throws Exception {

		if (this._messenger != null) {
			final ReponseStatut response = this.createReponseStatut(request);
			response.getEntete().setService(null);
			this._messenger.send(new Hashtable(), response);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Message " + response
						+ " given to messenger.");
			}
		}
	}

	public void localize() {
		try {
			sendReponseStatut(null);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
