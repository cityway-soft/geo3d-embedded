package org.avm.device.girouette;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.device.girouette.bundle.Activator;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.AbstractDevice;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

public class GirouetteDevice extends AbstractDevice implements AlarmProvider,
		ProducerService {

	private ProducerManager producer;
	private Alarm alarm;
	private Girouette girouette;

	private Logger logger = Logger.getInstance(this.getClass());

	public GirouetteDevice() {
		super();
		alarm = new Alarm(new Integer(18));
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
//		checkStatus();
	}

	public void setGirouette(Girouette girouette) {
		this.girouette = girouette;
		checkStatus();
	}

	public int checkStatus() {
		int result = -1;
		int cpt = 0;
		int max = 5;

//		boolean previous = alarm.isStatus();
		while (result < 0 && cpt <= max) {
			if (logger.isDebugEnabled()) {
				if (cpt > 0) {
					logger.debug("Bad response ; try again :" + cpt + "/" + max);
				}
			}
			result = check();
			cpt++;
		}

		if (producer != null) {
			alarm.setStatus(result < 0);
			logger.debug("Alarm :" + alarm);
			// if (previous != alarm.isStatus()){
			logger.debug("State changed => Publish");
			producer.publish(alarm);
			// }
		}

		return result;
	}

	private int check() {
		int result = -3;
		if (girouette != null && producer != null) {
			try {
				result = girouette.getStatus();
			} catch (Exception e) {
				result = -1;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public void unsetGirouette(Girouette girouette) {

	}

}
