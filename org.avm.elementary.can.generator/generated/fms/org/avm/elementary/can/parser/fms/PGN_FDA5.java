package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FDA5 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFDA5);
    


	public PGN_FDA5() {

	}

    public void get(Bitstream bs) throws IOException {             

    }

    public void put(Bitstream bs) throws IOException {

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FDA5();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

