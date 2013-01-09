/* @flavorc
 *
 * Service.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'phoebus.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.business.protocol.phoebus;
import java.io.IOException;

import flavor.IBitstream;
import flavor.MapResult;

public class Service {
    int _conducteur;
    int _serviceAgent;
    int _serviceVehicule;
    int _course;
    
	public Service() {

	}

	public Service(int conducteur, int serviceAgent, int serviceVehicule, int course) {		
		_conducteur = conducteur;
		_serviceAgent = serviceAgent;
		_serviceVehicule = serviceVehicule;
		_course = course;
	}

	public int getConducteur() {
		return _conducteur;
	}

	public void setConducteur(int conducteur) {
		_conducteur = conducteur;
	}

	public int getCourse() {
		return _course;
	}

	public void setCourse(int course) {
		_course = course;
	}

	public int getServiceAgent() {
		return _serviceAgent;
	}

	public void setServiceAgent(int serviceAgent) {
		_serviceAgent = serviceAgent;
	}

	public int getServiceVehicule() {
		return _serviceVehicule;
	}

	public void setServiceVehicule(int serviceVehicule) {
		_serviceVehicule = serviceVehicule;
	}

	public String toString() {
		return "[conducteur " + _conducteur + " service-agent " + _serviceAgent
				+ " service-vehicule " + _serviceVehicule
				+ " course " + _course + "]";
	}
	

    public int get(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _conducteur = _F_bs.getbits(32);
        _serviceAgent = _F_bs.getbits(32);
        _serviceVehicule = _F_bs.getbits(32);
        _course = _F_bs.getbits(32);
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_bs.putbits(_conducteur, 32);
        _F_bs.putbits(_serviceAgent, 32);
        _F_bs.putbits(_serviceVehicule, 32);
        _F_bs.putbits(_course, 32);
        return _F_ret;
    }
}
