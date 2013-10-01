package org.avm.business.billettique.atoumod;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;

import fr.cityway.avm.billettique.atoumod.model.Message;
import fr.cityway.avm.billettique.atoumod.model.MessageFactory;
import fr.cityway.avm.billettique.atoumod.model.MessageInterrogationSurveillance;

public class PCE415 implements Runnable {

	private int port;
	private DatagramSocket clientSocket;
	private InetAddress serverAdress;
	private int t_surv;
	private int n_surv;
	private MessageInterrogationSurveillance interrogation;
	private Thread thread;
	private boolean running;
	private PCE415Listener listener;

	public PCE415(String host, int port, int t_surv, int n_surv) throws SocketException,
			UnknownHostException, ParseException {
		this.port = port;
		this.t_surv = t_surv;
		this.n_surv = n_surv;
		clientSocket = new DatagramSocket();
		clientSocket.setSoTimeout(t_surv * 1000);
		serverAdress = InetAddress.getByName(host);
		interrogation = (MessageInterrogationSurveillance) MessageFactory
				.create(MessageInterrogationSurveillance.ID);

	}

	public void setMatricule(int matricule) {
		interrogation.setDriver(matricule);
	}

	public void setEtatExploitation(int etat) {
		interrogation.setOperatingState(etat);
	}

	public void setCourse(int course) {
		interrogation.setJourney(course);
	}

	public void setLigne(int ligne) {
		interrogation.setLine(ligne);
	}

	public static void main(String[] args) throws IOException, ParseException {

		PCE415 client = new PCE415("localhost", 33333, 20, 5);
		client.launch();
	}

	public void launch() {
		running = true;
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void shutdown() {
		running = false;
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	public void setListener(PCE415Listener listener) {
		this.listener = listener;
	}

	private void fireStateChanged(boolean state) {
		if (listener != null) {
			listener.connected(state);
		}
	}

	public void run() {
		int errCount = 0;
		while (running) {

			long t0 = System.currentTimeMillis();
			String sResponse = null;
			try {
				sendMessageInterrogation();

				byte[] receiveData = new byte[1024];
				t0 = System.currentTimeMillis();
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				System.out.println("Waiting for answer...");
				clientSocket.receive(receivePacket);
				sResponse = new String(receivePacket.getData());

			} catch (IOException e) {
				System.err.println("Warn:" + e.getMessage());
			}

			try {
				if (sResponse != null && (sResponse.trim().length() != 0)) {
					Message response = MessageFactory.parse(sResponse);
					System.out.println("Response:" + response);
					fireStateChanged(true);
					errCount = 0;
				} else {
					errCount++;
					System.err.println("No response from Billettique ATOUMOD ! (#"+errCount+")");
					if (errCount >= n_surv) {
						fireStateChanged(false);
					}
				}
			} catch (Exception e) {
				System.err
						.println("Error on process response from Billettique : "
								+ e.getMessage());
				e.printStackTrace();
			}

			long delta = (t_surv * 1000) - (System.currentTimeMillis() - t0);
			if (delta > 0) {
				try {
					System.out.println("Wait " + delta
							+ " ms before next request");

					Thread.sleep(delta);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void sendMessageInterrogation() throws IOException {
		byte[] sendData = interrogation.toString().getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, serverAdress, port);
		clientSocket.send(sendPacket);
		System.out.println("Sent request to Billettique : " + interrogation);
	}

	public void setPoint(int point) {
		interrogation.setStopPoint(point);
	}

	public void setDate(Date date) {
		interrogation.setDate(date);
	}

}
