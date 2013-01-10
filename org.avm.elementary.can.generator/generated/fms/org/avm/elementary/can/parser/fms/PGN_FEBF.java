package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEBF extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEBF);
    
	public SPN907 spn907;
	public SPN908 spn908;


	public PGN_FEBF() {
		spn907 = new SPN907();
		_map.put(spn907.getName(),spn907);
		spn908 = new SPN908();
		_map.put(spn908.getName(),spn908);

	}

    public void get(Bitstream bs) throws IOException {             
		spn907.get(bs);
		spn908.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn907.put(bs);
		spn908.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEBF();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

