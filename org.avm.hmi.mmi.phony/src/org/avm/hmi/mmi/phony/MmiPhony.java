package org.avm.hmi.mmi.phony;

import org.avm.business.ecall.EcallService;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.variable.Variable;
import org.avm.hmi.mmi.application.display.AVMDisplay;

public interface MmiPhony extends ConsumerService, ManageableService {

	void setBase(AVMDisplay base);

	void setEcall(EcallService ecall);

	void setPhony(Phony phony);

	void setBeeper(Variable var);
}
