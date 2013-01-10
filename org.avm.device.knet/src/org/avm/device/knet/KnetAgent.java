package org.avm.device.knet;

import org.avm.device.knet.model.KmsMarshaller;

public interface KnetAgent {

	public static final int KNETDPORT = 35035;
	public static final String KNETDHOST = "localhost";
	// public static final String KNETDHOST="wirma";//pour tests
	public static final String AUTH = "auth";
	public static final String AUTH_login = "cat";
	public static final String AUTH_passwd = "catcat";

	public static final int M2M_APP = 255;
	public static final int MMI_SERVICES = 253;
	public static final int GIROUETTE_SERVICES = 21;

	public static final int LOCAL_NODE = -1;

	public static final int KNET_APP = 100;
	public static final int GPS_APP = 10;
	public static final int SENSOR_APP = 11;
	public static final int MEDIA_APP = 12;
	public static final int MMI_APP = 13;
	public static final int GSM_APP = 14;
	public static final int WIFI_APP = 15;
	public static final int GIROUETTE_APP = 16;
	public static final int PHONY_APP = 17;
	public static final int BEARER_APP = 18;

	public static final String ORIGIN_SENSOR = "SENSOR";
	public static final String ORIGIN_GSM = "GSM";
	// Ajout LBR
	public static final String ORIGIN_MEDIA = "MEDIA_KNET";
	public static final String STATUS_2CONN = "toconnect";
	public static final String STATUS_CONN = "connected";
	public static final String STATUS_DECONN = "disconnected";
	public static final String SAE_KNET_ID = "22022";
	public static final String REPORT_OK = "ok";
	public static final String REPORT_KO = "ko";

	/**
	 * Connection et autentification a l'agent knet
	 * 
	 * @param host
	 * @param port
	 * @param login
	 * @param passwd
	 * @param from
	 * @param to
	 * @param id
	 * @throws KnetException
	 *             echec connecxion et autentification
	 */
	public void open(String host, int port, String login, String passwd,
			String from, String to, String id) throws KnetException;

	public void open(String host, int port, String login, String passwd,
			int from, int to, int id) throws KnetException;

	public void open(String host, int port, String login, String passwd,
			int from) throws KnetException;

	/**
	 * Deconnection de l'agent knet
	 * 
	 * @throws KnetException
	 */
	public void close() throws KnetException;

	/**
	 * Emission d'un message
	 * 
	 * @param KmsMarshaller
	 *            message
	 * @throws KnetException
	 */
	public KmsMarshaller send(KmsMarshaller message) throws KnetException;

	/**
	 * Emission d'un message sans attendre de reponse
	 * 
	 * @param KmsMarshaller
	 *            message
	 * @throws KnetException
	 */
	public void post(KmsMarshaller message) throws KnetException;

	/**
	 * Reception d'un message
	 * 
	 * @return KmsMarshaller message ou null
	 * @throws KnetException
	 */
	public KmsMarshaller receiveKms();

}