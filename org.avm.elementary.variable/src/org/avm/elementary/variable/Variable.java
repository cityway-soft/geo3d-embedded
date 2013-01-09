package org.avm.elementary.variable;

import org.osgi.service.device.Constants;
import org.osgi.util.measurement.Measurement;

public interface Variable extends Constants {

	public static final String NAME = "org.avm.elementary.variable.name";

	public static final String TYPE = "org.avm.elementary.variable.type";

	public static final String DEVICE_CATEGORY = "org.avm.elementary.variable.device.category";

	public static final String DEVICE_SERIAL = "org.avm.elementary.variable.device.serial";

	public static final String DEVICE_INDEX = "org.avm.elementary.variable.device.index";

	public static final String LOW_VALUE = "org.avm.elementary.variable.low";

	public static final String HIGH_VALUE = "org.avm.elementary.variable.high";

	public static final String UNIT = "org.avm.elementary.variable.unit";

	public String getName();

	public String getType();

	public String getDeviceCategory();

	public String getDeviceSerial();

	public int getDeviceIndex();

	public Measurement getValue();

	public void setValue(Measurement value);

}
