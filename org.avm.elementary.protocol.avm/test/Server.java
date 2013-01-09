import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.avm.elementary.protocol.avm.Connection;
import org.avm.elementary.protocol.avm.ConnectionEvent;
import org.avm.elementary.protocol.avm.ConnectionListener;
import org.avm.elementary.protocol.avm.MessageEvent;
import org.avm.elementary.protocol.avm.MessageListener;
import org.avm.elementary.protocol.avm.ServerConnection;
import org.avm.elementary.protocol.avm.parser.C_MSG;
import org.avm.elementary.protocol.avm.parser.Message;
import org.avm.elementary.protocol.avm.parser.S_MSG;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

public class Server implements ConnectionListener, MessageListener {

	private static Server _instance;

	public static void main(String[] args) {
		_instance = new Server();
		_instance.start();
	}

	private ServerSocket _server;

	private List _connecxions = new LinkedList();

	private ServerConnection _connection;

	protected QueuedExecutor _executor = new QueuedExecutor();

	private void start() {

		try {
			_server = new ServerSocket(8094);

			while (true) {
				Socket socket = _server.accept();

				_connection = new ServerConnection(socket, 180 + 30, this);
				_connection.setDebugEnabled(true);
				_connection.addMessageEventListener(this);
				_connection.connect();
				_connecxions.add(_connection);
			}

		} catch (Throwable e) {
			System.out.println("Server.start()");
		}
	}

	public void connected(ConnectionEvent event) {
		System.out.println("Server.connected()");
	}

	public void disconnected(ConnectionEvent event) {
		System.out.println("Server.disconnected()");
	}

	public void acknoledge(MessageEvent event) {
		System.out.println("Server.acknoledge() " + event.getMessage());

	}

	public void receive(MessageEvent event) {
		System.out.println("Server.receive() " + event.getMessage());
		execute(new Runnable() {

			public void run() {
				Message msg = new S_MSG("AVM_00100001", "MESSAGE DE TEST"
						.getBytes());
				try {
					_connection.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void execute(Runnable action) {
		try {
			_executor.execute(action);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void keepalive(ConnectionEvent event) {
		System.out.println("Server.keepalive()");	
	}
}
