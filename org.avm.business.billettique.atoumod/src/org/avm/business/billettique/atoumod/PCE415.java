package org.avm.business.billettique.atoumod;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;

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
	private Logger logger = Logger.getInstance(PCE415.class);

	public PCE415(String host, int serverPort, int t_surv, int n_surv,
			Integer clientPort) throws SocketException, UnknownHostException,
			ParseException {
		this.port = serverPort;
		this.t_surv = t_surv;
		this.n_surv = n_surv;
		if (clientPort != null) {
			clientSocket = new DatagramSocket(clientPort.intValue());
		} else {
			clientSocket = new DatagramSocket();
		}
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

	public void setSens(int sens) {
		interrogation.setWayGo(sens == 1);
	}

	public void setLigne(int ligne) {
		interrogation.setLine(ligne);
	}

	public static void main(String[] args) throws IOException, ParseException {

		PCE415 client = new PCE415("localhost", 33333, 20, 5,
				new Integer(33334));
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
				logger.debug("Waiting for answer...");
				clientSocket.receive(receivePacket);
				// sResponse = new String(receivePacket.getData());
				sResponse = toHexaString(receivePacket.getData());

			} catch (IOException e) {
				logger.warn("Warn:" + e.getMessage());
			}

			try {
				if (sResponse != null && (sResponse.trim().length() != 0)) {
					Message response = MessageFactory.parse(sResponse);
					logger.debug("Response:" + response);
					fireStateChanged(true);
					errCount = 0;
				} else {
					errCount++;
					logger.error("No response from Billettique ATOUMOD ! (#"
							+ errCount + ")");
					if (errCount >= n_surv) {
						fireStateChanged(false);
					}
				}
			} catch (Exception e) {
				logger.error("Error on process response from Billettique : "
						+ e.getMessage());
				e.printStackTrace();
			}

			long delta = (t_surv * 1000) - (System.currentTimeMillis() - t0);
			if (delta > 0) {
				try {
					logger.debug("Wait " + delta
							+ " ms before next request");

					Thread.sleep(delta);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static byte[] toBinary(final String frame) {
		final byte[] buffer = new byte[frame.length() / 2];
		int j = 0;
		for (int i = 0; i < frame.length(); i += 2) {
			String octet = frame.substring(i, i + 2);

			buffer[j] = (byte) Integer.parseInt(octet, 16);
			j++;

		}
		return buffer;
	}

	private static String toHexaString(final byte[] data) {
		final byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			final int rValue = data[i] & 0x0000000F;
			final int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	public void sendMessageInterrogation() throws IOException {
		// byte[] sendData = interrogation.toString().getBytes();
		if (running) {
			if (logger.isDebugEnabled()){
				logger.debug(interrogation.toDebug());
			}
			byte[] sendData = toBinary(interrogation.toString());
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, serverAdress, port);
			clientSocket.send(sendPacket);
			logger.debug("Sent request to Billettique : " + interrogation);
		} else {
			logger.debug("Interface disabled.");
		}

	}

	public void setPoint(int point) {
		interrogation.setStopPoint(point);
	}

	public void setDate(Date date) {
		interrogation.setDate(date);
	}

}
