package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_F003 extends PGN {

	public static final Integer PGN_ID = new Integer(0xF003);
    
	public SPN91 spn91;


	public PGN_F003() {
		spn91 = new SPN91();
		_map.put(spn91.getName(),spn91);

	}

    public void get(Bitstream bs) throws IOException {             
		spn91.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn91.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_F003();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

