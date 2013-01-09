package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FE4E extends PGN {

	public static final Integer PGN_ID = new Integer(0xFE4E);
    
	public SPN1821 spn1821;


	public PGN_FE4E() {
		spn1821 = new SPN1821();
		_map.put(spn1821.getName(),spn1821);

	}

    public void get(Bitstream bs) throws IOException {             
		spn1821.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn1821.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FE4E();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

