package org.avm.elementary.can.parser.kangoo;

import java.util.Properties;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.CANParser;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.can.parser.Util;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements CANParser, BundleActivator  {

    public  static final int _pgn_mask = 0x000fff;
	public static final byte _mode = CANParser.CANMODE_NORMAL;
	public static final int _decalage = 0;
	public static final String PID = "org.avm.elementary.can.parser.kangoo";
	
    protected KeyedPoolableObjectFactory _factory;

	protected KeyedObjectPool _pool;

	protected Bitstream _in;

	protected Bitstream _out;

	protected ServiceRegistration _sr;

	public Activator() {
		_factory = new Factory();
		_pool = new StackKeyedObjectPool(_factory);
		_in = new Bitstream();
		_out = new Bitstream();
	}

	public PGN makeObject(int key) throws Exception {
		PGN pgn = (PGN) _pool.borrowObject(new Integer(key));
		pgn.setPool(_pool);
		return pgn;
	}
	
	public int getPgnId(byte[] buffer) {
		return (Util.getInt(buffer, 0, false) & _pgn_mask) >>> _decalage;
	}
	
	public PGN get(byte[] buffer) throws Exception {
		int key = (Util.getInt(buffer, 0, false) & _pgn_mask) >>> _decalage;
		PGN pgn = (PGN) _pool.borrowObject(new Integer(key));
		if (pgn == null) {
			return null;
		}
		pgn.setPool(_pool);
		_in.initialize(buffer);
		pgn.get(_in);
		pgn.setBuffer(buffer);
		return pgn;
	}

	public void put(PGN pgn, byte[] buffer) throws Exception {
	
		Util.putInt(buffer, 0, false, pgn.getId().intValue() << _decalage
				& _pgn_mask);
		buffer[4] = _mode;
		buffer[5] = 8;
		
		for (int i = 6; i < buffer.length; i++) {
			buffer[i] = (byte) 0xFF;
		}

		_out.initialize(buffer);
		pgn.put(_out);
	}

	public void start(BundleContext context) throws Exception {
		Properties properties = new Properties();
		properties.put(Constants.SERVICE_PID, PID);
		_sr = context.registerService(CANParser.class.getName(), this,
				properties);
	}

	public void stop(BundleContext context) throws Exception {
		_sr.unregister();
	}
}

