package org.avm.business.messages;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.messages.bundle.ConfigImpl;
import org.avm.business.protocol.phoebus.Message;
import org.avm.business.protocol.phoebus.MessageText;
import org.avm.business.protocol.phoebus.Programmation;
import org.avm.business.protocol.phoebus.Reference;
import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.osgi.util.measurement.State;

public class MessagesImpl implements Messages, ConfigurableService,
		ManageableService, ConsumerService, ProducerService, MessengerInjector {

	private Logger _log = Logger.getInstance(this.getClass());

	private MessagesConfig _config;

	private ProducerManager _producer;

	private Messenger _messenger;

	private static final List JOURS = Arrays.asList(new Object[] {
			new Integer(Calendar.MONDAY), new Integer(Calendar.TUESDAY),
			new Integer(Calendar.WEDNESDAY), new Integer(Calendar.THURSDAY),
			new Integer(Calendar.FRIDAY), new Integer(Calendar.SATURDAY),
			new Integer(Calendar.SUNDAY) });

	public MessagesImpl() {
		// _log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (MessagesConfig) config;
		purge();
	}

	public void start() {
		publish(Messages.CONDUCTEUR);
		publish(Messages.VOYAGEUR);
	}

	public void stop() {
	}

	private void purge() {
		Dictionary map = _config.getMessages();
		if (map != null) {
			for (Enumeration iterator = map.keys(); iterator.hasMoreElements();) {
				String key = (String) iterator.nextElement();
				Properties props = (Properties) map.get(key);
				if (!isValid(props)) {
					_config.removeMessage(key);
				}
			}
			((ConfigImpl) _config).updateConfig(false);
		}
	}

	public Collection getMessages(int type, String ligne) {
		Collection result = new LinkedList();
		Dictionary map = _config.getMessages();
		int i = 0;

		for (Enumeration iterator = _config.getMessages().keys(); iterator
				.hasMoreElements();) {
			String key = (String) iterator.nextElement();
			Properties props = (Properties) map.get(key);

			if (isType(type, props)) {
				if ((type == Messages.CONDUCTEUR)
						|| (type == Messages.VOYAGEUR && isValid(props)
								&& isHeure(props) && isJour(props) && isAffectation(
									"L" + ligne + ";", props))) {
					_log.debug("message " + props + " SELECTED!");
					result.add(props);
				} else {
					_log.debug("message " + props + " NOT SELECTED!!!!!!");
				}
			}
			i++;
		}
		return result;
	}

	private Date getDate(org.avm.business.protocol.phoebus.Date d,
			org.avm.business.protocol.phoebus.Heure h) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, d.getJour());
		cal.set(Calendar.MONTH, d.getMois() - 1);
		cal.set(Calendar.YEAR, d.getAnnee() + 1990);
		cal.set(Calendar.HOUR_OF_DAY, h.getHeure());
		cal.set(Calendar.MINUTE, h.getMinute());
		cal.set(Calendar.SECOND, h.getSeconde());
		return cal.getTime();
	}

	private void remove(String id) {
		if (id == null)
			return;
		if (id.equals("0")) {
			_config.removeAllMessages();
		} else {
			_config.removeMessage(id);
		}
		((ConfigImpl) _config).updateConfig(false);
	}

	private String getJours(int lun, int mar, int mer, int jeu, int ven,
			int sam, int dim) {
		StringBuffer buf = new StringBuffer();
		buf.append((lun != 0) ? "L" : "_");
		buf.append((mar != 0) ? "M" : "_");
		buf.append((mer != 0) ? "M" : "_");
		buf.append((jeu != 0) ? "J" : "_");
		buf.append((ven != 0) ? "V" : "_");
		buf.append((sam != 0) ? "S" : "_");
		buf.append((dim != 0) ? "D" : "_");
		return buf.toString();
	}

	private int add(String id, MessageText message) {
		GregorianCalendar calendar = new GregorianCalendar();
		Date debut = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date fin = calendar.getTime();
		String jours = "LMMJVSD";

		Programmation programmation = message.getEntete().getProgrammation();
		if (programmation != null) {
			debut = getDate(programmation.getDateDebut(),
					programmation.getHeureDebut());
			fin = getDate(programmation.getDateFin(),
					programmation.getHeureFin());
			jours = getJours(programmation.getLun(), programmation.getMar(),
					programmation.getMer(), programmation.getJeu(),
					programmation.getVen(), programmation.getSam(),
					programmation.getDim());

		}

		int destinataire = Messages.VOYAGEUR;
		String sAffectation = "*";
		int priorite = 0;
		boolean acquittement = false;

		try {
			destinataire = message.getType();
			if (destinataire == Messages.CONDUCTEUR) {
				debut = new Date();
				fin = null;
			}
			if (message.getEntete().getOptions() != null) {
				priorite = message.getEntete().getOptions().getPriorite();
				acquittement = (message.getEntete().getOptions()
						.getAcquittement() == 1);
			}
			int[] affectation = message.getAffectation().getValue();

			if (affectation != null) {
				Hashtable hash = new Hashtable();

				if (_log.isDebugEnabled()) {
					StringBuffer buf = new StringBuffer();
					buf.append("Affectation=>");
					for (int i = 0; i < affectation.length; i++) {
						buf.append(affectation[i]);
						buf.append(",");
					}
					_log.debug(buf);
				}
				StringBuffer buf = new StringBuffer();
				String value;
				for (int i = 0; i < affectation.length; i++) {
					buf = new StringBuffer();
					buf.append("L");
					buf.append(affectation[i]);
					buf.append(";");
					value = buf.toString();
					if (!value.equals("L0;")) {
						_log.debug("Adding '" + value + "' to hash");
						hash.put(value, value);
					}

				}
				if (hash != null) {
					buf = new StringBuffer();
					Enumeration en = hash.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						value = (String) hash.get(key);
						_log.debug("hash.get('" + key + "') = '" + value + "'");
						buf.append(value);
					}
					_log.debug("Affectation ligne=" + buf);
				}

				sAffectation = buf.toString().trim();
				if (sAffectation.length() == 0) {
					sAffectation = "*";
				}
			} else {
				_log.info("Message affecte' a toutes les lignes!");
			}
		} catch (Throwable t) {
			_log.error("Error adding message :" + t.toString());
		}

		String msg = message.getMessage();
		_log.debug(msg + " : " + debut + "-> " + fin);

		if (msg.trim().length() == 0) {
			_config.removeAllMessages();
		} else {
			_log.debug("adding message...");
			addMessage(id, DF.format(debut), (fin != null) ? DF.format(fin)
					: null, jours, destinataire, sAffectation, msg, priorite,
					acquittement);
		}

		return destinataire;
	}

	private void addMessage(String id, String debut, String fin, String jours,
			int destinataire, String sAffectation, String msg, int priorite,
			boolean acquittement) {
		if (_log.isDebugEnabled()) {
			StringBuffer buf = new StringBuffer();
			buf.append("id=");
			buf.append(id);
			buf.append(";debut=");
			buf.append(debut);
			buf.append(";fin=");
			buf.append(fin);
			buf.append(";jours=");
			buf.append(jours);
			buf.append(";dest=");
			buf.append(destinataire);
			buf.append(";affect=");
			buf.append(sAffectation);
			buf.append(";msg=");
			buf.append(msg);
			buf.append(";prio=");
			buf.append(priorite);

			_log.debug(buf);
			_log.debug("config=" + _config);
		}
		_config.addMessage(id, debut, fin, jours, destinataire, sAffectation,
				msg, priorite, acquittement);
		if (_config instanceof AbstractConfig) {
			((ConfigImpl) _config).updateConfig(false);
		}
		_log.info("message '" + msg + "' added.");
	}

	public void notify(Object o) {
		_log.info("notify : " + o);
		if (o instanceof MessageText) {
			try {
				MessageText message = (MessageText) o;
				String id = Long.toString(message.getEntete().getId());
				_log.debug("id : " + id);
				int destinataire = Messages.VOYAGEUR;
				boolean bAddMessage = true;
				if (message.getEntete().getReference() != null
						&& message.getEntete().getReference().getAnnulation() == 1) {
					String idMsgToRemove = Long.toString(message.getEntete()
							.getReference().getId());
					remove(idMsgToRemove);
					bAddMessage = !idMsgToRemove.equals(id);
				}

				if (bAddMessage) {
					destinataire = add(id, message);
				} else {
					// remove message
					remove(id);
				}
				publish(destinataire);
				_log.debug("publish msg : " + id);
			} catch (Throwable t) {
				_log.error("Error:" + t.toString());
			}

		}
	}

	private boolean isAffectation(String affectation, Properties p) {
		String sAffectation = (String) p.get(Messages.AFFECTATION);
		boolean result = false;
		if (sAffectation == null || sAffectation.equals(AFFECTATION_ALL)) {
			result = true;
		} else {
			result = sAffectation.indexOf(affectation) != -1;
		}
		_log.debug("isAffectation : " + result);

		return result;
	}

	private boolean isType(int type, Properties p) {
		String stype = (String) p.get(Messages.TYPE);
		boolean result = Integer.parseInt(stype) == type;

		_log.debug("isType : " + result);
		return result;
	}

	public boolean isValid(Properties p) {
		Date date = new Date();
		Date debut = getDate((String) p.get(Messages.DEBUT), true);
		Date fin = getDate((String) p.get(Messages.FIN), false);

		if (_log.isDebugEnabled()) {
			_log.debug("--------------------" + p.get(Messages.ID));
			if (debut == null) {
				_log.debug("-->Date debut invalide:" + p.get(Messages.DEBUT));
			} else {
				_log.debug("-->Date debut : " + debut);
			}
			if (fin == null) {
				_log.debug("-->Date fin invalide:" + p.get(Messages.FIN));
			} else {
				_log.debug("-->Date fin : " + fin);
			}
			_log.debug("--------------------");
			_log.debug("");
		}

		int type = Integer.parseInt(p.getProperty(Messages.TYPE));

		boolean result = false;
		if (type == Messages.VOYAGEUR) {
			if (debut == null && fin == null) {
				result = true;
			} else if (debut == null) {
				result = date.before(fin);
			} else if (fin == null) {
				result = date.after(debut);
			} else {
				result = date.after(debut) && date.before(fin);
			}
		} else {
			if (debut != null) {
				Calendar cal = Calendar.getInstance();
				int today = cal.get(Calendar.DAY_OF_YEAR);
				cal.setTime(debut);
				int dayofmsg = cal.get(Calendar.DAY_OF_YEAR);
				_log.info("today=" + today + " , dayofmsg=" + dayofmsg);
				result = (today == dayofmsg);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("isValid : " + result);
		}

		return result;
	}

	public boolean isJour(Properties p) {
		boolean result = false;
		String validite = (String) p.get(Messages.JOURSEMAINE);
		Calendar cal = GregorianCalendar.getInstance();
		int joursemaine = JOURS.indexOf(new Integer(cal
				.get(Calendar.DAY_OF_WEEK)));

		result = (validite.charAt(joursemaine) != '_');
		_log.debug("isJour : " + result);
		return result;
	}

	public boolean isHeure(Properties p) {
		Date date = new Date();
		Date debut = getHeure((String) p.get(Messages.DEBUT));
		Date fin = getHeure((String) p.get(Messages.FIN));

		if (_log.isDebugEnabled()) {
			if (debut == null) {
				_log.debug("Heure debut invalide:" + p.get(Messages.DEBUT));
			} else {
				_log.debug("heure debut : " + debut);
			}
			if (fin == null) {
				_log.debug("Heure fin invalide:" + p.get(Messages.FIN));
			} else {
				_log.debug("heure fin : " + fin);
			}
		}

		boolean result = false;
		if (debut == null && fin == null) {
			result = true;
		} else if (debut == null) {
			result = date.before(fin);
		} else if (fin == null) {
			result = date.after(debut);
		} else {
			result = date.after(debut) && date.before(fin);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("isPrintable : " + result);
		}

		return result;
	}

	private Date getDate(String d, boolean debut) {
		Date date = null;
		try {
			date = DF.parse(d);
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(date);
			if (debut) {
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
			} else {
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
			}
			date = cal.getTime();
		} catch (ParseException e) {
		}
		return date;
	}

	private Date getHeure(String d) {
		Date date = null;
		try {
			date = DF.parse(d);
			Calendar today = GregorianCalendar.getInstance();
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.MONTH, today.get(Calendar.MONTH));
			cal.set(Calendar.YEAR, today.get(Calendar.YEAR));

			date = cal.getTime();
		} catch (ParseException e) {
			_log.debug("date non valide : " + d);
		}
		return date;
	}

	public void publish(int destinataire) {
		if (_producer != null) {
			State state = new State(destinataire, Messages.class.getName());
			_producer.publish(state);
		}
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void acquittement(String msgId) {
		Properties msg = _config.getMessage(msgId);
		if (msg == null)
			return;
		int destinataire = Integer.parseInt(msg.getProperty(Messages.TYPE));
		boolean acquittement = msg.getProperty(Messages.ACQUITTEMENT).equals(
				"true");
		if (destinataire == Messages.CONDUCTEUR && acquittement) {
			MessageText message = new MessageText();
			Reference reference = new Reference();
			message.getEntete().setReference(reference);
			message.getEntete().getReference().setAcquittement(1);
			message.getEntete().getReference().setId(Long.parseLong(msgId));
			message.setMessage("LU '" + msg.getProperty(Messages.MESSAGE)+"'");
			_log.info("Emission du message d'acquittement : " + message);
			send(message);
		}
		String fin = DF.format(new Date());
		addMessage(msg.getProperty(Messages.ID),
				msg.getProperty(Messages.DEBUT), fin,
				msg.getProperty(Messages.JOURSEMAINE), destinataire,
				msg.getProperty(Messages.AFFECTATION),
				msg.getProperty(Messages.MESSAGE),
				Integer.parseInt(msg.getProperty(Messages.PRIORITE)), false);

		// remove(msgId);
	}

	public void setMessenger(Messenger messenger) {
		_messenger = messenger;
	}

	public void unsetMessenger(Messenger messenger) {
		_messenger = null;
	}

	public void send(Message msg) {
		if (_messenger != null) {
			Hashtable d = new Hashtable();
			d.put("destination", "sam"); //$NON-NLS-1$ //$NON-NLS-2$
			d.put("binary", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				_messenger.send(d, msg);
			} catch (Exception e) {
				_log.error("Error sendMessage", e); //$NON-NLS-1$
			}
		}
	}

}