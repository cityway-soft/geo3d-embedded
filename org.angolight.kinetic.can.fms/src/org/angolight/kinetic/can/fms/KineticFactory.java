package org.angolight.kinetic.can.fms;

import org.angolight.kinetic.Kinetic;
import org.apache.commons.pool.BasePoolableObjectFactory;

public class KineticFactory extends BasePoolableObjectFactory {

	public Object makeObject() throws Exception {
		return new Kinetic();
	}
}
