package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.PGNFactory;

public class PGN_FEE6 extends PGN {

	public static final Integer PGN_ID = new Integer(0xFEE6);
    
	public SPN959 spn959;
	public SPN960 spn960;
	public SPN961 spn961;
	public SPN963 spn963;
	public SPN962 spn962;
	public SPN964 spn964;


	public PGN_FEE6() {
		spn959 = new SPN959();
		_map.put(spn959.getName(),spn959);
		spn960 = new SPN960();
		_map.put(spn960.getName(),spn960);
		spn961 = new SPN961();
		_map.put(spn961.getName(),spn961);
		spn963 = new SPN963();
		_map.put(spn963.getName(),spn963);
		spn962 = new SPN962();
		_map.put(spn962.getName(),spn962);
		spn964 = new SPN964();
		_map.put(spn964.getName(),spn964);

	}

    public void get(Bitstream bs) throws IOException {             
		spn959.get(bs);
		spn960.get(bs);
		spn961.get(bs);
		spn963.get(bs);
		spn962.get(bs);
		spn964.get(bs);

    }

    public void put(Bitstream bs) throws IOException {
		spn959.put(bs);
		spn960.put(bs);
		spn961.put(bs);
		spn963.put(bs);
		spn962.put(bs);
		spn964.put(bs);

    }
    
    public Integer getId() {
		return PGN_ID;
    }
    
    public static class DefaultPGNFactory extends PGNFactory {
    	protected PGN makeObject() throws Exception {
        	return new PGN_FEE6();
        }    
    }

    static {
    	PGNFactory._factories.put(PGN_ID, new DefaultPGNFactory());
    }
}

