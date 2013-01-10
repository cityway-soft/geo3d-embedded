package org.avm.device.knet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

public class KnetSocketManager implements KmsHandler {

	private static final long TIMEOUT = 1000;
	private static final int TRIALS = 5;

	private Thread _readerThread;

	private int _status = CLOSED;

	public static final int CLOSED = 0;

	public static final int OPENING = 1;

	public static final int OPENED = 2;

	public static final int CLOSING = 3;

	private String _host;

	private int _port;

	private Socket _socket;

	private InputStream _in;

	private OutputStream _out;

	private QueuedExecutor _writer;
	private BoundedBuffer _writerBuffer;

	private KnetRecvThread _reader;

	private ConcurrentHashMap _reply;

	private LinkedQueue _readerChannel;

	private QueuedExecutor _kmsHandler;

	private Logger _log = null;

	public KnetSocketManager(String host, int port) {
		_log = Logger.getInstance(this.getClass().getPackage().getName());
		// _log.setPriority(Priority.DEBUG);
		_host = host;
		_port = port;
	}

	public int getStatus() {
		return _status;
	}

	public synchronized void open() {
		if (_status != CLOSED) {
			_log.warn("_status != CLOSED");
			return;
		}
		try {
			_status = OPENING;
			_log.info("opening socket [" + _host + "," + _port + "]");
			_socket = new Socket(_host, _port);
			_socket.setKeepAlive(true);
			_socket.setTcpNoDelay(true);
			_in = _socket.getInputStream();
			_out = _socket.getOutputStream();
			_writerBuffer = new BoundedBuffer(3);
			_writer = new QueuedExecutor(_writerBuffer);
			_reply = new ConcurrentHashMap();
			_readerChannel = new LinkedQueue();
			_kmsHandler = new QueuedExecutor();
			_reader = new KnetRecvThread(_in, this);
			_reader.start();
			_status = OPENED;
			_log.info("socket opened");
		} catch (Exception e) {
			_log.error("Error opening socket [" + _host + "," + _port + "]");
			_status = CLOSED;
			// throw new KnetException("Error opening socket
			// ["+_host+","+_port+"]");
		}
	}

	public synchronized void close() {
		if (_status != OPENED) {
			_log.warn("_status != OPENED");
			return;
		}
		try {
			_status = CLOSING;
			_log.info("Socket manager closing");

			// deblocage des clients
			for (Enumeration iter = _reply.elements(); iter.hasMoreElements();) {
				KnetKmsWrapper wrapper = (KnetKmsWrapper) iter.nextElement();
				synchronized (wrapper) {
					wrapper.notifyAll();
				}
			}
			if (_readerThread != null)
				_readerThread.interrupt();

			// liberation des ressources
			_reader.shutdownNow();
			_writer.shutdownNow();
			_kmsHandler.shutdownNow();
			_in.close();
			_out.close();
			_socket.close();
		} catch (Exception e) {
			_log.error("Error while closing socketManager", e);
			_status = CLOSED;
		}
		_log.info("Socket manager closed");
	}

	public void post(KmsMarshaller kms) throws KnetException {
		_log.debug("post " + kms);
		submitKms(kms, false);
	}

	public KmsMarshaller send(KmsMarshaller kms) throws KnetException {
		_log.debug("send " + kms);
		return submitKms(kms, true);
	}

	private KmsMarshaller submitKms(KmsMarshaller kms, boolean wait)
			throws KnetException {
		_log.debug("submitKms (" + kms + "," + wait + ")");
		if (_status != OPENED) {
			_log.warn("_status != OPENED");
			throw new KnetException("Connection fermee");
		}

		KmsMarshaller response = null;
		KnetSendTask sendTask = new KnetSendTask(kms);
		_log.debug("Thread " + _writerBuffer.peek());
		try {
			if (wait) {
				KnetKmsWrapper wrapper = new KnetKmsWrapper(kms, wait);
				synchronized (wrapper) {
					_log.debug("Ajout " + kms.getIdentifiant());
					_reply.put(kms.getIdentifiant(), wrapper);
					_writer.execute(sendTask);
					// long now = System.currentTimeMillis();
					// plusieurs tentatives pour obtenir la reponse ...
					int tentative = 0;
					while (tentative++ < TRIALS) {
						response = wrapper.getResponse();
						if (response != null)
							break;
						wrapper.wait(TIMEOUT);
					}
				}

				if (response == null) {
					throw new KnetException(
							"Echec emission d'un message (response==null)");
				}
			} else {
				_writer.execute(sendTask);
			}
		} catch (InterruptedException e) {
			throw new KnetException("Thread Interromptu");
		} catch (Exception e) {
			throw new KnetException(e.getMessage());
		}
		return response;
	}

	public KmsMarshaller recv() throws KnetException {
		if (_status != OPENED) {
			_log.warn("_status != OPENED");
			throw new KnetException("Connection fermee");
		}
		KmsMarshaller message = null;
		try {
			// _readerThread = Thread.currentThread();
			message = (KmsMarshaller) _readerChannel.take();
			_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
					+ message.getIdentifiant() + " supprimé de la queue"
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
			// _readerThread = null;
		} catch (InterruptedException e) {
			// throw new KnetException(e.getMessage());
		}
		return message;
	}

	public void handleMsg(KmsMarshaller msg) {
		// _log.debug("handleMessage : "+msg);
		KnetKmsWrapper wrapper = (KnetKmsWrapper) _reply.remove(msg
				.getIdentifiant());
		if (wrapper != null) {
			_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
					+ "wrapper existant pour " + msg.getIdentifiant()
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
			synchronized (wrapper) {
				wrapper.setResponse(msg);
				wrapper.notifyAll();
			}
		} else {
			try {
				_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_WARNING
						+ "wrapper inexistant pour " + msg.getIdentifiant()
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
				boolean success = _readerChannel.offer(msg, TIMEOUT);
				if (!success)
					_log
							.warn(msg.getIdentifiant()
									+ " ne peut etre remis dans la queue de lecture ...");

			} catch (InterruptedException e) {
				_log.warn("InterruptedException");
			}
		}
	}

	class KnetRecvThread extends Thread {

		private KnetInputStream _in;

		private KmsHandler _handler;

		private byte[] _buffer = new byte[8192];

		private volatile boolean _shutdown;

		public KnetRecvThread(InputStream in, KmsHandler handler) {
			super();
			_handler = handler;
			_in = new KnetInputStream(in);
		}

		public void run() {
			try {
				while (!_shutdown) {
					KmsMarshaller kms = read();
					if (kms == null)
						continue;
					kms.setHandler(_handler);
					_kmsHandler.execute(kms);
				}
			} catch (Exception e) {
				_log.error("[KnetRecvThread::run] " + e.getMessage(), e);
				try {
					close();
				} catch (Exception e1) {
				}
			}
		}

		public synchronized void shutdownNow() {
			_shutdown = true;
			this.interrupt();
		}

		private KmsMarshaller read() throws Exception {
			Kms result = null;
			int n = -1;
			try {
				n = _in.read(_buffer, 0, _buffer.length);
			} catch (SocketException s) {
				_log.error("SocketException ", s);
			}
			if (n < 0) {
				// End of stream has been reached
				return null;
			}
			// _log.debug("[KnetRecvThread] reception " + n+" bytes ("+new
			// String(_buffer, 0, n)+")");
			ByteArrayInputStream in = new ByteArrayInputStream(_buffer, 0, n);
			result = KmsFactory.unmarshal(in);
			return (KmsMarshaller) result;
		}
	}

	class KnetSendTask implements Runnable {

		private KmsMarshaller _kms;

		public KnetSendTask(KmsMarshaller message) {
			_log.debug("KnetSendTask( " + message + ")");
			_kms = message;
		}

		public String toString() {
			return "KnetSendTask " + _kms;
		}

		public void run() {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				_kms.marshal(out);
				_log.debug("[KnetSendTask] emission " + out);
				out.close();
				_kms.marshal(_out);
			} catch (Exception e) {
				try {
					_log.error("KnetSendTask::run Exception ; force closing "
							+ e.getMessage(), e);
					close();
				} catch (Exception e1) {
				}
			}
		}
	}

}
