package org.avm.elementary.can.parser.fms;

import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class Factory extends PGNFactory {

    protected PGN makeObject() throws Exception {
		throw new UnsupportedOperationException();
    }

    static {
		try {
		Class.forName("org.avm.elementary.can.parser.fms.PGN_F000");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_F001");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_F003");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_F004");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_F005");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FDA5");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FDD1");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FE4E");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FE58");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FE6C");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEAA");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEAE");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEBF");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEC1");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FED5");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEDF");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEE6");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEE8");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEE9");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEEE");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEEF");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEF1");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEF2");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEF3");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEF5");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEF7");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEF8");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FEFC");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FF55");
		Class.forName("org.avm.elementary.can.parser.fms.PGN_FF5D");

		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
    }
}

