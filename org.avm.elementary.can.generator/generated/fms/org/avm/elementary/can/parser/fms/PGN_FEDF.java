package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEDF extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEDF);
    
	public SPN514 spn514;


	public PGN_FEDF() {
		spn514 = new SPN514();
		_map.put(spn514.getName(),spn514);

	}

    public void get(Bitstream bs) throws IOException {             
		spn514.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn514.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEDF();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

