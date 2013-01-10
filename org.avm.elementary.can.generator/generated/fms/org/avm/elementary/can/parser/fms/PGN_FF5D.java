package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FF5D extends PGN {

	public static final Integer PGN_ID = new Integer(0xFF5D);
    
	public SPN917 spn917;
	public SPN96b spn96b;


	public PGN_FF5D() {
		spn917 = new SPN917();
		_map.put(spn917.getName(),spn917);
		spn96b = new SPN96b();
		_map.put(spn96b.getName(),spn96b);

	}

    public void get(Bitstream bs) throws IOException {             
		spn917.get(bs);
		spn96b.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn917.put(bs);
		spn96b.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FF5D();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

