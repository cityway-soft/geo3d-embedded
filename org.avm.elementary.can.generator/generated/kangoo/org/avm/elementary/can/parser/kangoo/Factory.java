package org.avm.elementary.can.parser.kangoo;

import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class Factory extends PGNFactory {

    protected PGN makeObject() throws Exception {
		throw new UnsupportedOperationException();
    }

    static {
		try {
		Class.forName("org.avm.elementary.can.parser.kangoo.PGN_102");

		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
    }
}

