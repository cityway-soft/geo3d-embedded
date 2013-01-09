package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEE8 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEE8);
    
	public SPN165 spn165;
	public SPN517 spn517;
	public SPN580 spn580;


	public PGN_FEE8() {
		spn165 = new SPN165();
		_map.put(spn165.getName(),spn165);
		spn517 = new SPN517();
		_map.put(spn517.getName(),spn517);
		spn580 = new SPN580();
		_map.put(spn580.getName(),spn580);

	}

    public void get(Bitstream bs) throws IOException {             
		spn165.get(bs);
		spn517.get(bs);
		spn580.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn165.put(bs);
		spn517.put(bs);
		spn580.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEE8();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

