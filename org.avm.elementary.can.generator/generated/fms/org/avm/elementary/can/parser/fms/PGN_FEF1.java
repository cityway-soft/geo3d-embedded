package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEF1 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEF1);
    
	public SPN84 spn84;
	public SPN597 spn597;


	public PGN_FEF1() {
		spn84 = new SPN84();
		_map.put(spn84.getName(),spn84);
		spn597 = new SPN597();
		_map.put(spn597.getName(),spn597);

	}

    public void get(Bitstream bs) throws IOException {             
		spn84.get(bs);
		spn597.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn84.put(bs);
		spn597.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEF1();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

