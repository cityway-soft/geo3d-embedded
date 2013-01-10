package org.avm.business.tad;

import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.measurement.State;

public class TADImpl implements TAD, ConfigurableService,ProducerService,ManageableService {

	TADConfig _config;
	
	Service _service;

	private ProducerManager _producer;

	private Logger  _log = Logger.getInstance(this.getClass());

	public TADImpl(){
		_service = new Service();
	}
	
	public void configure(Config config) {
		_config = (TADConfig) config;
		Enumeration e = _config.elements();
		while (e.hasMoreElements()) {
			Mission mission = (Mission) e.nextElement();
			if (mission.isValid()){
				_service.add(mission);
			}
		}
	}

	public void add(Mission mission) {
		if (mission.isValid()){
			_service.add(mission);
		}
	}
	
	public void stateChanged(){
		State state = new State(1, TAD.class.getName());
		_producer.publish(state);
		_log.debug("publish state  : " + state);
	}

	public Service getService() {
		return _service;
	}
	
	public void remove(Long id) {
		Mission mission = _service.get(id);
		if (_service.get(id) != null){
			_service.remove(id);
			_log.debug("mission removed  : " + mission);
			State state = new State(0, TAD.class.getName());
			_log.debug("publish state  : " + state);
		}
		else{
			_log.debug("mission "+id+" does not exist!");
		}
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;	
	}

	public void start() {
		stateChanged();
	}

	public void stop() {
		
	}
	
}
