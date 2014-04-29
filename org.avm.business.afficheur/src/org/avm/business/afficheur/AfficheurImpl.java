package org.avm.business.afficheur;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.afficheur.bundle.Activator;
import org.avm.business.core.AbstractAvmModelListener;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Point;
import org.avm.business.messages.MessagesInjector;
import org.avm.device.afficheur.AfficheurInjector;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.osgi.util.measurement.State;

public class AfficheurImpl implements Afficheur, ManageableService,
		ConfigurableService, AfficheurInjector, AvmInjector, ConsumerService,
		MessagesInjector {

	private Logger _log = Activator.getDefault().getLogger();
	private AfficheurConfig _config;
	private org.avm.device.afficheur.Afficheur _afficheur;
	private ModelListener _listener;
	private org.avm.business.messages.Messages _messages;
	private boolean _initialized;
	private Avm _avm;

	public void configure(Config config) {
		_config = (AfficheurConfig) config;
	}

	public void setAfficheur(org.avm.device.afficheur.Afficheur afficheur) {
		_afficheur = afficheur;
		print("");
		initialize();
	}

	public void unsetAfficheur(org.avm.device.afficheur.Afficheur afficheur) {
		_afficheur = null;
		_initialized = false;
	}

	public void setAvm(Avm avm) {
		_avm = avm;
		initialize();
	}

	public void unsetAvm(Avm avm) {
		_listener = null;
		_initialized = false;
	}

	public void start() {

	}

	public void stop() {

	}

	private void initialize() {
		if (!_initialized) {
			if (_avm != null && _afficheur != null) {
				_listener = new ModelListener(_avm);
				_listener.notify(_avm.getModel().getState());
				_initialized = true;
			}
		}
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (_listener != null) {

				if ((state.getName().equals(Messages.class.getName()) && state
						.getValue() == org.avm.business.messages.Messages.VOYAGEUR)
						|| state.getName().equals(Avm.class.getName())) {
					_listener.notify(o);
				}

			}
		}
	}

	private void print(String message) {
		_log.debug(message);
		if (_afficheur != null) {
			_afficheur.print(message);
		}
	}

	private void clear() {
		print(""); //$NON-NLS-1$
	}

	private String convertEncoding(String message) {
		String temp = message;
		String from = System.getProperty("from.charset", "UTF-8");
		String to = System.getProperty("to.charset", "iso-8859-1");
		_log.debug("from=" + from + ", to=" + to);
		if (from != null && to != null) {
			_log.debug("Avant conversion " + temp);
			try {
				byte[] m = new String(temp.getBytes(), from).getBytes(to);
				temp = new String(m);
				_log.debug("Après conversion " + temp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} else {
			_log.debug("Aucune conversion " + temp);
		}

		return temp;
	}

	class ModelListener extends AbstractAvmModelListener {

		public ModelListener(Avm avm) {
			super(avm);
		}

		protected void onStateAttenteDepart(AvmModel model) {
			StringBuffer buf = new StringBuffer();
			String ligne = model.getCourse().getLigneNom();
			buf.append(Messages.getString("org.avm.business.afficheur.ligne")); //$NON-NLS-1$
			buf.append(ligne);
			buf.append(" - ");
			buf.append(Messages
					.getString("org.avm.business.afficheur.direction")); //$NON-NLS-1$
			buf.append(model.getCourse().getDestination());
			print(buf.toString()); //$NON-NLS-1$
		}

		protected void onStateAttenteSaisieCourse(AvmModel model) {
			print(Messages
					.getString("org.avm.business.afficheur.initialisation")); //$NON-NLS-1$
		}

		protected void onStateAttenteSaisieService(AvmModel model) {
			clear();
		}

		protected void onStateEnCourseHorsItineraire(AvmModel model) {
			clear();
		}

		protected void onStateEnCourseServiceSpecial(AvmModel model) {
			print(Messages
					.getString("org.avm.business.afficheur.service-special")); //$NON-NLS-1$
		}

		protected void onStateInitial(AvmModel model) {
			clear();
		}

		protected void onStateEnCourseArretSurItineraire(AvmModel model) {
			Point point = model.getDernierPoint();
			String arret = point.getNom();
			// arret = convertEncoding(arret);
			print(Messages.getString("org.avm.business.afficheur.arret") + arret + "                   "); //$NON-NLS-1$
		}

		protected void onStateEnCourseInterarretSurItineraire(AvmModel model) {
			Point point = model.getProchainPoint();
			if (point != null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(Messages
						.getString("org.avm.business.afficheur.prochain-arret"));
				String arret = point.getNom();
				// arret = convertEncoding(arret);
				buffer.append(arret);
				if (point.getItl() == Point.ITL_NO_DOWN) {
					buffer.append(" - ");
					buffer.append(Messages
							.getString("org.avm.business.afficheur.itl.no.down"));
				}
				buffer.append("                   ");

				String[] messages = getMessages(model);

				if (messages != null && messages.length != 0) {
					buffer.append(" - Informations : ");
					for (int i = 0; i < messages.length; i++) {
						buffer.append(messages[i]);
						buffer.append(" - ");
					}
				}

				print(buffer.toString()); //$NON-NLS-1$
			}
		}
	}

	private String[] getMessages(AvmModel model) {
		String[] msg = null;
		if (_messages != null) {
			String ligneIdu = "";
			if (model.getCourse() != null) {
				ligneIdu = Integer.toString(model.getCourse().getLigneIdu());
			}

			Collection messages = _messages.getMessages(
					org.avm.business.messages.Messages.VOYAGEUR, ligneIdu);
			msg = new String[messages.size()];
			Iterator iter = messages.iterator();
			int i = 0;

			while (iter.hasNext()) {
				Properties props = (Properties) iter.next();
				String temp = props
						.getProperty(org.avm.business.messages.Messages.MESSAGE);

				// temp = convertEncoding(temp);

				// _log.debug("from="+from+", to="+to);
				// if (from != null && to != null) {
				// _log.debug("Avant conversion " + temp);
				// try {
				// byte[] m = new String(temp.getBytes(), from)
				// .getBytes(to);
				// temp = new String(m);
				// _log.debug("Après conversion " + temp);
				// } catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				// }
				//
				// } else {
				// _log.debug("Aucune conversion " + temp);
				// }

				msg[i] = temp;
				i++;
			}

		}
		return msg;
	}

	public void setMessages(org.avm.business.messages.Messages messages) {
		_messages = messages;
	}

	public void unsetMessages(org.avm.business.messages.Messages messages) {
		_messages = null;
	}

}
