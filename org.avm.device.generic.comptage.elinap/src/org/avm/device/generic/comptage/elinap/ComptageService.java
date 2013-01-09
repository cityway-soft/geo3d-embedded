package org.avm.device.generic.comptage.elinap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.avm.device.comptage.Comptage;
import org.avm.device.comptage.ComptageException;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class ComptageService extends AbstractDriver implements Comptage,
		PortListener {

	private transient boolean _started;
	private final static int WAIT_SYNC = 0;
	private final static int WAIT_SYNC2 = 1;
	private final static int WAIT_CMD = 2;
	private final static int WAIT_CMD2 = 3;
	private final static int WAIT_DATA = 4;
	private final static int WAIT_CRC = 5;
	private final static int WAIT_CRC2 = 6;
	private final static int CMD_UKN = 0;
	private final static int CMD_CNT = 1;
	private final static int CMD_RST = 2;
	private static final int ROLL_OVER = 256;
	private int _state = WAIT_SYNC;
	private int _countData = 0;
	private int _cmd = CMD_UKN;
	private int _askCmd = CMD_UKN;
	private char _lastChar = 0;
	private int _inCount = 0;
	private int _outCount = 0;
	private int _lastInCount = 0;
	private int _lastOutCount = 0;
	private int _realInCount = 0;
	private int _realOutCount = 0;
	private int _rollInCount = 0;
	private int _rollOutCount = 0;
	
	

	private final Object _waitData = new Object();
	private boolean _waitTimeout = false;

	private Port _port;
	private int _status;

	/*
	 * exemple configuration. setmanufacturer Elinap. setdescription Comptage de
	 * passager Elinap. setmodel org.avm.device.comptage.elinap setname.
	 * org.avm.device.comptage.elinap setserial.
	 * 4df3687a-9b67-46c5-b83f-b581c98feff2 setcategory.
	 * org.avm.device.comptage.Comptage setparameters url.
	 * "comm:3;baudrate=9600;stopbits=1;parity=odd;bitsperchar=8;blocking=off".
	 */

	public ComptageService(final ComponentContext context,
			final ServiceReference device) {
		super(context, device);
	}

	private void send(final byte[] buffer) throws IOException {

		try {
			final OutputStream out = _port.getOutputStream();
			out.write(buffer);
		} catch (final IOException e) {
			_port.close();
			throw e;
		}

	}

	protected void start(final DeviceConfig config) {
		final String url = config.getParamerter("url");
		_port = new Port(url);
		try {
			_port.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_port.start();
		_port.addListener(this);
		_started = true;
		
	}

	protected void stop() {
		_started = false;
		_port.close();
	}

	public int nombrePassagers(String type) throws ComptageException {
		_log.info("nombre passager");
		if (!_started) {
			_log.error("Driver not started");
			throw new ComptageException("Driver not started");
		}
		if (! askNumberPassenger()){
			throw new ComptageException("Device not connected");
		}

		if (type.equals(Comptage.NOMBRE_MONTEES)){
			return _realInCount;
			}
		else if (type.equals(Comptage.NOMBRE_DESCENTES)){
			return _realOutCount;
		}
		else if (type.equals(Comptage.STATUS)){
			return _status%16;
		}
		else if (type.equals(Comptage.CHARGE)){
			return _status/16;
		}
		else
			throw new ComptageException("Unknown type");
	}

	private boolean askNumberPassenger() {
		final byte ASKCOUNT[] = { 0x7E, 0x7E, 0x73, 0x02, 0x73, 0x02 };
		boolean ret = false;
		try {
			_askCmd = CMD_CNT;
			send(ASKCOUNT);
			ret = waitResponse();
		} catch (IOException e) {
			_log.error(e);
		}
		return ret;
	}

	public boolean miseAZero() {
		final byte ASKRESET[] = { 0x7E, 0x7E, 0x74, 0x02, 0x74, 0x02 };
		boolean ret = false;
		try {
			_askCmd = CMD_RST;
			send(ASKRESET);
			ret = waitResponse();
			// clear roll over counter;
			_lastInCount = 0;
			_lastOutCount = 0;
			_realInCount = 0;
			_realOutCount = 0;
			_rollInCount=0;
			_rollOutCount=0;
		} catch (IOException e) {
			_log.error(e);
		}
		return ret;
	}

	/**
	 * 
	 * @return true sur un déblocage, false sur un timeout
	 */
	private boolean waitResponse() {
		try {
			_waitTimeout = true;
			synchronized (_waitData) {
				_waitData.wait(2000);
			}
		} catch (InterruptedException e) {
		}
		if (_waitTimeout) {
			_log.error("Timeout on Elinap response");
		}
		return !_waitTimeout;
	}

	private void notifyResponse() {
		_waitTimeout = false;
		synchronized (_waitData) {
			_waitData.notify();
		}
	}

	/**
	 * Callback du listener caractère de synchro : 0x7E deux commandes : - 0x73,
	 * 0x05 > nombre de passagers - 0x74, 0x02 > reset des compteurs
	 * 
	 */
	public void onChar(char b) {
		switch (_state) {
		case WAIT_SYNC:
		case WAIT_SYNC2:
			if (b == 0x7E) {
				_state++;
			}
			break;
		case WAIT_CMD:
			_lastChar = b;
			_state = WAIT_CMD2;
			break;
		case WAIT_CMD2:
			// 0x73,0x05 pour le comptage
			// 0x74,0x02 pour reset
			if (_lastChar == 0x73 && b == 0x05) {
				_cmd = CMD_CNT;
				_countData = 3;
				_state = WAIT_DATA;
			} else if (_lastChar == 0x74 && b == 0x02) {
				_cmd = CMD_RST;
				_state = WAIT_CRC; // pas de data
			} else {
				_cmd = CMD_UKN;
				_state = WAIT_SYNC; // commande inconnue
			}
			break;
		case WAIT_DATA:
			if (_countData == 3) {
				_inCount = b;
			} else if (_countData == 2) {
				_outCount = b;
			}
			else if (_countData ==1){
				_status = b;
			}
			if (--_countData == 0) {
				_state = WAIT_CRC;
			}
			break;
		case WAIT_CRC:
			_state = WAIT_CRC2;
			break;
		case WAIT_CRC2:
			_state = WAIT_SYNC;
			checkRollOver ();
			notifyResponse();
			break;
		}
	}
	
	protected void checkRollOver (){
		
		if (_lastInCount > _inCount){
			_rollInCount += ROLL_OVER;
			_lastInCount=0;
		}
		else{
			_lastInCount = _inCount;
		}
		_realInCount = _rollInCount +_inCount;
		if (_lastOutCount > _outCount){
			_rollOutCount+=ROLL_OVER;
			_lastOutCount=0;
		}
		else{
		_lastOutCount = _outCount;
		}
		_realOutCount =_rollOutCount +_outCount;
	}

	public Properties status() {
		Properties ret = null;
		if (askNumberPassenger()) {
			ret = new Properties();
			ret.put(Comptage.NOMBRE_MONTEES, Integer.toString(_realInCount));
			ret.put(Comptage.NOMBRE_DESCENTES, Integer.toString(_realOutCount));
			ret.put(Comptage.STATUS, Integer.toString(_status%16));
			ret.put(Comptage.CHARGE, Integer.toString(10*_status/16));
			
		}
		return ret;
	}

}
