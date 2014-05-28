package org.avm.device.afficheur;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.bundle.Activator;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.AbstractDevice;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

public class AfficheurDevice extends AbstractDevice implements AlarmProvider,
		ProducerService {

	private ProducerManager producer;
	private Alarm alarm;
	private Afficheur afficheur;

	private Logger logger = Logger.getInstance(this.getClass());

	public AfficheurDevice() {
		super();
		alarm = new Alarm(new Integer(22));
	}

	public List getAlarm() {
		List list = null;
		if (alarm.isStatus()) {
			list = new ArrayList();
			list.add(alarm);
		}
		return list;
	}

	public String getProducerPID() {
		return Activator.PID;
	}

	public void setProducer(ProducerManager producer) {
		this.producer = producer;
		checkStatus();
	}

	public void setAfficheur(Afficheur afficheur) {
		this.afficheur = afficheur;
		checkStatus();
	}

	public int checkStatus() {
		int result = -1;
		int cpt = 0;
		int max = 2;

		boolean previous = alarm.isStatus();
		while (result < 0 && cpt <= max) {
			if (logger.isDebugEnabled()) {
				if (cpt > 0) {
					logger.debug("Bad response ; try again :" + cpt + "/" + max);
				}
			}
			result = check();
			cpt++;
		}

		alarm.setStatus(result < 0);
		logger.debug("Alarm :" + alarm);
		// if (previous != alarm.isStatus()){
		logger.debug("State changed => Publish");
		producer.publish(alarm);
		// }

		return result;
	}

	private int check() {
		int result = AfficheurProtocol.STATUS_NOT_AVAILABLE;
		if (afficheur != null && producer != null) {
			try {
				result = afficheur.getStatus();
			} catch (Exception e) {
				result = -1;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			logger.warn("check impossible : afficheur="+afficheur + ", producer="+producer);
		}
		return result;
	}

	public void unsetAfficheur(Afficheur afficheur) {

	}

}
