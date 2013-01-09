package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.elementary.variable.Variable;
import org.avm.hmi.mmi.application.display.AVMDisplay;

public interface AvmMmi {

	void setAvm(Avm avm);

	void setBase(AVMDisplay base);

	void isMainteneur();

	void setBeeper(Variable var);

}
