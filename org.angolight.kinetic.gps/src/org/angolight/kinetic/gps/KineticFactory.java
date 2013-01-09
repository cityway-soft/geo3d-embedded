package org.angolight.kinetic.gps;

import org.angolight.kinetic.Kinetic;
import org.apache.commons.pool.BasePoolableObjectFactory;

public class KineticFactory extends BasePoolableObjectFactory {

	public Object makeObject() throws Exception {
		return new Kinetic();
	}
}
