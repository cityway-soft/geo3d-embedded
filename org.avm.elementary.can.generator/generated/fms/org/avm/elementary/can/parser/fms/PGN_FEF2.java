package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEF2 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEF2);
    
	public SPN183 spn183;
	public SPN184 spn184;


	public PGN_FEF2() {
		spn183 = new SPN183();
		_map.put(spn183.getName(),spn183);
		spn184 = new SPN184();
		_map.put(spn184.getName(),spn184);

	}

    public void get(Bitstream bs) throws IOException {             
		spn183.get(bs);
		spn184.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn183.put(bs);
		spn184.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEF2();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

