package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEEE extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEEE);
    
	public SPN174 spn174;


	public PGN_FEEE() {
		spn174 = new SPN174();
		_map.put(spn174.getName(),spn174);

	}

    public void get(Bitstream bs) throws IOException {             
		spn174.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn174.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEEE();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

