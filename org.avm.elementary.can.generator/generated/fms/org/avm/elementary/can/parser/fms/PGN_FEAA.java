package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEAA extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEAA);
    
	public SPN582 spn582;
	public SPN180 spn180;
	public SPN181 spn181;


	public PGN_FEAA() {
		spn582 = new SPN582();
		_map.put(spn582.getName(),spn582);
		spn180 = new SPN180();
		_map.put(spn180.getName(),spn180);
		spn181 = new SPN181();
		_map.put(spn181.getName(),spn181);

	}

    public void get(Bitstream bs) throws IOException {             
		spn582.get(bs);
		spn180.get(bs);
		spn181.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn582.put(bs);
		spn180.put(bs);
		spn181.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEAA();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

