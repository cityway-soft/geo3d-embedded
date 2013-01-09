package org.avm.elementary.can.parser.kangoo;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_102 extends PGN {

	public static final Integer PGN_ID = new Integer(0x102);
    
	public VehicleSpeed vehiclespeed;


	public PGN_102() {
		vehiclespeed = new VehicleSpeed();
		_map.put(vehiclespeed.getName(),vehiclespeed);

	}

    public void get(Bitstream bs) throws IOException {             
		vehiclespeed.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		vehiclespeed.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_102();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

