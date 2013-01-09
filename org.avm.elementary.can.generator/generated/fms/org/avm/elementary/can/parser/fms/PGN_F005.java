package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_F005 extends PGN {

	public static final Integer PGN_ID = new Integer(0xF005);
    
	public SPN524 spn524;
	public SPN523 spn523;


	public PGN_F005() {
		spn524 = new SPN524();
		_map.put(spn524.getName(),spn524);
		spn523 = new SPN523();
		_map.put(spn523.getName(),spn523);

	}

    public void get(Bitstream bs) throws IOException {             
		spn524.get(bs);
		spn523.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn524.put(bs);
		spn523.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_F005();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

