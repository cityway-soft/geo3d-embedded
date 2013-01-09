package org.angolight.kinetic.can.kangoo;

import org.angolight.kinetic.Kinetic;
import org.apache.commons.pool.BasePoolableObjectFactory;

public class KineticFactory extends BasePoolableObjectFactory {

	public Object makeObject() throws Exception {
		return new Kinetic();
	}
}
