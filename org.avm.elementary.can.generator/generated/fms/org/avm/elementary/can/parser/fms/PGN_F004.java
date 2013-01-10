package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_F004 extends PGN {

	public static final Integer PGN_ID = new Integer(0xF004);
    
	public SPN513 spn513;
	public SPN190 spn190;


	public PGN_F004() {
		spn513 = new SPN513();
		_map.put(spn513.getName(),spn513);
		spn190 = new SPN190();
		_map.put(spn190.getName(),spn190);

	}

    public void get(Bitstream bs) throws IOException {             
		spn513.get(bs);
		spn190.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn513.put(bs);
		spn190.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_F004();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

