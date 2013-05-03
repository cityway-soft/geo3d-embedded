package org.avm.elementary.messenger.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.avm.elementary.command.CommandChain;
import org.avm.elementary.command.CommandChainInjector;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Media;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.MediaService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.bundle.Activator;
import org.avm.elementary.parser.Parser;
import org.avm.elementary.parser.ParserService;

public class MessengerImpl implements Messenger, MediaListener, MediaService,
		ParserService, ConfigurableService, ProducerService,
		CommandChainInjector, ManageableService {

	private Logger _log = Activator.getDefault().getLogger();

	private MessengerConfig _config;
	private ProducerManager _producer;
	private CommandChain _command;
	private Hashtable _medias = new Hashtable();
	private Hashtable _parsers = new Hashtable();

	public MessengerImpl() {
		super();
	}

	public void configure(Config config) {
		_config = (MessengerConfig) config;
	}

	public void setMedia(Media media) {
		_medias.put(media.getMediaId(), media);
	}

	public void unsetMedia(Media media) {
		_medias.remove(media.getMediaId());
	}

	public void setParser(Parser parser) {
		_parsers.put(parser.getProtocolName(), parser);
	}

	public void unsetParser(Parser parser) {
		_parsers.remove(parser.getProtocolName());
	}

	public void setCommandChain(CommandChain command) {
		_command = command;
	}

	public void unsetCommandChain(CommandChain command) {
		_command = null;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void start() {
	}

	public void stop() {
		Sender.getInstance().shutdownAfterProcessingCurrentlyQueuedTasks();
	}

	public void receive(Dictionary header, byte[] data) {
		try {
			Object m = parse(header, data);
			if (m == null) {
				throw new Exception("No parser for message " //$NON-NLS-1$
						+ toHexaString(data));
			}

			if (_producer == null) {
				throw new Exception("No producer for message " //$NON-NLS-1$
						+ toHexaString(data));
			}
			if (_log.isDebugEnabled()) {
				_log.debug("Reception du message : " + data + "; binaire= "
						+ toHexaString(data));
			}

			_producer.publish(m);

		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

	}

	public void send(Dictionary header, Object data) throws Exception {
		if (data != null) {
			MessengerContext context = new MessengerContext();
			context.setHeader(header);
			context.setMessage(data);
			context.setMedias(_medias);

			boolean result = _command.execute(context);
			byte[] buffer = parse(header, data);
			if (buffer != null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Emission du message : " + data + "; binaire= "
							+ toHexaString(buffer));
				}
				String name = (String) context.getHeader().get("MEDIA_ID");
				if (name == null) {
					_log.error("Property MEDIA_ID not found in context !");
				} else {
					Media media = (Media) _medias.get(name);
					if (media == null) {
						_log.error("Media named " + name + " not found !");
					} else {
						Sender.getInstance().send(media, header, buffer);
					}
				}
			} else {
				_log.error("No parser for message " + data);
			}
		}
	}

	private Object parse(Dictionary header, byte[] data) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		Object result = null;
		for (Enumeration iter = _parsers.elements(); iter.hasMoreElements();) {
			Parser parser = (Parser) iter.nextElement();
			in.reset();
			try {
				result = parser.get(in);
				if (result != null) {
					break;
				}
			} catch (RuntimeException e) {
			}
		}
		return result;
	}

	private byte[] parse(Dictionary header, Object data) {
		byte[] buffer = null;
		if (_parsers != null) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				for (Enumeration iter = _parsers.elements(); iter
						.hasMoreElements();) {
					Parser parser = (Parser) iter.nextElement();
					try {
						parser.put(data, out);
						break;
					} catch (Exception e) {
						if (_log.isDebugEnabled()) {
							_log.debug(e);
						}
					}
				}
				buffer = out.toByteArray();
				out.close();
			} catch (IOException e) {
			}
		}
		return buffer;
	}

	private String toHexaString(byte[] data) {
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
}
