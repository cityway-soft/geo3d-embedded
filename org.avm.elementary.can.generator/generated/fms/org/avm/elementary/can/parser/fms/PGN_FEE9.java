package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEE9 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEE9);
    
	public SPN250 spn250;


	public PGN_FEE9() {
		spn250 = new SPN250();
		_map.put(spn250.getName(),spn250);

	}

    public void get(Bitstream bs) throws IOException {             
		spn250.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn250.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEE9();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

