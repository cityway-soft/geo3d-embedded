package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FF55 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFF55);
    
	public SPN917b spn917b;
	public MFBP mfbp;
	public MRBP mrbp;
	public SPNCEP spncep;
	public SPNCEG spnceg;
	public SPNCEC spncec;


	public PGN_FF55() {
		spn917b = new SPN917b();
		_map.put(spn917b.getName(),spn917b);
		mfbp = new MFBP();
		_map.put(mfbp.getName(),mfbp);
		mrbp = new MRBP();
		_map.put(mrbp.getName(),mrbp);
		spncep = new SPNCEP();
		_map.put(spncep.getName(),spncep);
		spnceg = new SPNCEG();
		_map.put(spnceg.getName(),spnceg);
		spncec = new SPNCEC();
		_map.put(spncec.getName(),spncec);

	}

    public void get(Bitstream bs) throws IOException {             
		spn917b.get(bs);
		mfbp.get(bs);
		mrbp.get(bs);
		spncep.get(bs);
		spnceg.get(bs);
		spncec.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn917b.put(bs);
		mfbp.put(bs);
		mrbp.put(bs);
		spncep.put(bs);
		spnceg.put(bs);
		spncec.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FF55();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

