import java.net.Socket;

import org.avm.elementary.protocol.avm.ClientConnection;
import org.avm.elementary.protocol.avm.ConnectionEvent;
import org.avm.elementary.protocol.avm.ConnectionListener;
import org.avm.elementary.protocol.avm.MessageEvent;
import org.avm.elementary.protocol.avm.MessageListener;
import org.avm.elementary.protocol.avm.parser.C_MSG;
import org.avm.elementary.protocol.avm.parser.Message;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

public class Client implements ConnectionListener, MessageListener {

	private static Client _instance;

	protected QueuedExecutor _executor = new QueuedExecutor();

	public static void main(String[] args) {
		_instance = new Client();
		_instance.start();
		synchronized (_instance) {
			try {
				_instance.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private ClientConnection _connection;

	private void start() {
		Socket socket;
		try {
			socket = new Socket("localhost", 8094);

			_connection = new ClientConnection(socket, 60, this);
			_connection.setDebugEnabled(true);
			_connection.setSource("AVM_00100001");
			_connection.addMessageEventListener(this);
			_connection.connect();
		} catch (Exception e) {
			System.out.println("Client.start() e = " + e);
			e.printStackTrace();
		}

	}

	public void connected(ConnectionEvent event) {
		System.out.println("Client.connected()");
		execute(new Runnable() {

			public void run() {
				Message msg = new C_MSG(0, 0, "SAM_00100001", "MESSAGE DE TEST"
						.getBytes());
				try {
					_connection.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void disconnected(ConnectionEvent event) {
		System.out.println("Client.disconnected()");
	}

	private void execute(Runnable action) {
		try {
			_executor.execute(action);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void acknoledge(MessageEvent event) {
		System.out.println("Client.acknoledge()");
	}

	public void receive(MessageEvent event) {
		System.out.println("Server.receive() " + event.getMessage());
	}

	public void keepalive(ConnectionEvent event) {
		System.out.println("Server.keepalive()");
	}
}
