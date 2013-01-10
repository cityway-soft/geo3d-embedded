package org.avm.device.knet.media;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsMsg;
import org.avm.device.knet.model.KmsRoot;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.MediaService;
import org.avm.elementary.common.PublisherService;

/**
 * @author
 * 
 */
public class MediaKnetImpl extends KnetDevice implements MediaKnet,
		ConfigurableService, PublisherService, ManageableService {

	private Logger _log = null;

	private MediaKnetConfig _config = null;

	private MediaListener _messenger = null;

	public MediaKnetImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (MediaKnetConfig) config;
	}

	public void setMessenger(MediaListener ml) {
		_log.debug("setMessenger(MediaListener ml)");
		_messenger = ml;
		if (_messenger != null) {
			_log.debug("setMedia(this)");
			((MediaService) _messenger).setMedia(this);
		}
	}

	public String getMediaId() {
		if (_config != null)
			return _config.getMediaId();
		return null;
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		_log.debug("Emission du message : len " + data.length);
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsMsg.ROLE);
		String dest = (String) header.get("id");
		KmsMarshaller cmdeMsg = (KmsMarshaller) ((org.avm.device.knet.model.KmsMsg.DefaultKmsFactory) kf)
				.create(KnetAgent.MEDIA_APP, KnetAgent.MEDIA_APP, dest,
						toHexaString(data));
		_log.debug("Envoie de :" + cmdeMsg);
		try {
			post(cmdeMsg);
		} catch (KnetException e) {
			_log.error("Erreur à la commande pour le MEDIA", e);
		}

	}

	public static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	public static byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));

		}
		return data;
	}

	public void start() {
		_log.info("start MEDIA [" + KnetAgent.MEDIA_APP + "]");
		try {
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.MEDIA_APP);
			startListen();
		} catch (KnetException e) {
			_log.error("Erreur à la connexion de l'agent pour le MEDIA", e);
			return;
		}
	}

	public void stop() {
		_log.debug("stop MEDIA [" + KnetAgent.MEDIA_APP + "]");
		try {
			close();
		} catch (KnetException e) {
			_log.error("Erreur à la commande stop pour le MEDIA", e);
		}
	}

	public void receive(Kms kms) {
		_log.debug("MEDIA [" + KnetAgent.MEDIA_APP + "] receive " + kms);
		if (kms == null)
			return;

		String msg;
		Kms k = ((KmsRoot) kms).getSubKms();
		if (k instanceof KmsMsg) {
			msg = ((KmsMsg) k).getMsgContent();
			if (msg == null) {
				_log.warn("Reception d'un message null !");
				return;
			}
			_log.debug("Reception du message '" + msg + "'");
			Dictionary header = new Hashtable();
			if (_messenger != null)
				_messenger.receive(header, fromHexaString(msg));
			else
				_log.warn("No messenger");
		}
	}

	public int getKnetApp() {
		return KnetAgent.MEDIA_APP;
	}

	public String getKnetAppAsString() {
		return "MEDIA_APP";
	}

}