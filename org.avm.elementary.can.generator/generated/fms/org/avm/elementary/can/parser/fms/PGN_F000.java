package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_F000 extends PGN {

	public static final Integer PGN_ID = new Integer(0xF000);
    
	public SPN900 spn900;
	public SPN520 spn520;


	public PGN_F000() {
		spn900 = new SPN900();
		_map.put(spn900.getName(),spn900);
		spn520 = new SPN520();
		_map.put(spn520.getName(),spn520);

	}

    public void get(Bitstream bs) throws IOException {             
		spn900.get(bs);
		spn520.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn900.put(bs);
		spn520.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_F000();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

