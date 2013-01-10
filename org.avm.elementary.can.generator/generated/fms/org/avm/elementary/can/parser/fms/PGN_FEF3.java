package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEF3 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEF3);
    
	public SPN584 spn584;
	public SPN585 spn585;


	public PGN_FEF3() {
		spn584 = new SPN584();
		_map.put(spn584.getName(),spn584);
		spn585 = new SPN585();
		_map.put(spn585.getName(),spn585);

	}

    public void get(Bitstream bs) throws IOException {             
		spn584.get(bs);
		spn585.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn584.put(bs);
		spn585.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEF3();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

