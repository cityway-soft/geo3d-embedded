package org.avm.business.vocal;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.protocol.phoebus.MessageText;
import org.avm.device.player.Player;
import org.avm.device.player.PlayerInjector;
import org.avm.device.sound.Sound;
import org.avm.device.sound.SoundInjector;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.variable.Variable;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.State;

public class VocalImpl implements Vocal, ManageableService, ConsumerService,
		ConfigurableService, AvmInjector, PlayerInjector, SoundInjector {

	private Logger _log;
	private Avm _avm;
	private VocalConfig _config;
	private Player _player;
	private Variable _ioAudioVoyageurInterieur;
	private Variable _audioConducteur;
	private boolean _defmatAlarm = false;
	private boolean _speedAlarm = false;
	private Sound _sound;

	private State _currentState = new State(-1, "init");
	private boolean _needToTellDirection;
	private Variable _ioAudioVoyageurExterieur;
	private String[] languages;

	public VocalImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void configure(Config config) {
		_config = (VocalConfig) config;
		languages = _config.getLanguages();
	}

	public void setAvm(Avm avm) {
		_avm = avm;
	}

	public void unsetAvm(Avm avm) {
		_avm = null;
	}

	public void setIOAudioVoyageurInterieur(Variable var) {
		_ioAudioVoyageurInterieur = var;
	}

	public void unsetIOAudioVoyageurInterieur(Variable var) {
		_ioAudioVoyageurInterieur = null;
	}

	public void setIOAudioVoyageurExterieur(Variable var) {
		_ioAudioVoyageurExterieur = var;
	}

	public void unsetIOAudioVoyageurExterieur(Variable var) {
		_ioAudioVoyageurExterieur = null;
	}

	public void setAudioConducteur(Variable var) {
		_audioConducteur = var;
	}

	public void unsetAudioConducteur(Variable var) {
		_audioConducteur = null;
	}

	public void setPlayer(Player player) {
		_player = player;
	}

	public void unsetPlayer(Player player) {
		_player = null;
	}

	public void setSound(Sound sound) {
		_sound = sound;
		if (_sound != null) {
			try {
				_sound.configure(CONFIGURATION_DEFAUT);
			} catch (Exception e) {
				_log.warn(e);
			}
		}
	}

	public void unsetSound(Sound sound) {
		_sound = null;
	}

	public void start() {
	}

	public void stop() {
		if (_player != null) {
			_player.stop();
		}
	}

	public void notify(Object o) {
		try {
			if (o instanceof Variable) {
				_log.debug("Receive 'Variable' : " + o);

				Variable porteAv = (Variable) o;
				if (_avm.getModel().isInsidePoint()
						&& porteAv.getName().equals("porteav")
						&& porteAv.getValue().getValue() == 0) {

					Point dernier = _avm.getModel().getDernierPoint();
					if (dernier != null) {
						if (dernier.getItl() == Point.ITL_NO_UP) {// --montée
																	// interdite
							annonceMonteeInterdite(VOYAGEUR_EXTERIEUR);
						} else {
							annonceLigne(VOYAGEUR_EXTERIEUR);
							annonceDestination(VOYAGEUR_EXTERIEUR);
						}
					}

					_needToTellDirection = false;
				}
			} else if (o instanceof State) {
				_log.debug("Receive 'State' : " + o);
				State state = (State) o;

				if (!state.getName().equals(Avm.class.getName())) {
					_log.debug("Receive 'State' : " + o + " ===> ignore ");
					return;
				}

				if (state != null
						&& state.getValue() != _currentState.getValue()) {
					switch (state.getValue()) {
					case AvmModel.STATE_ATTENTE_DEPART:
						_needToTellDirection = true;
						break;
					case AvmModel.STATE_EN_COURSE_ARRET_SUR_ITINERAIRE:

						Point dernier = _avm.getModel().getDernierPoint();
						if (dernier != null) {
							if (dernier.getItl() == Point.ITL_NO_DOWN) {
								// --descente interdite
								annonceDescenteInterdite(VOYAGEUR_INTERIEUR);
							} else {
								annonceArret(VOYAGEUR_INTERIEUR);
							}
						}
						break;

					case AvmModel.STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE:
						if (_needToTellDirection) {
							_needToTellDirection = false;
							annonceLigne(VOYAGEUR_INTERIEUR);
							annonceDestination(VOYAGEUR_INTERIEUR);
						}
						annonceProchainArret(VOYAGEUR_INTERIEUR);
						break;

					default:
						break;
					}
				}
				_currentState = state;
			} else if (o instanceof MessageText) {
				_log.debug("Receive 'MessageText' : " + o);
				onMessageConducteur((MessageText) o);
			} else if (o instanceof Alarm) {
				_log.debug("Receive 'Alarm' : " + o);
				onMessageAlerte((Alarm) o);
			}
		} catch (Throwable e) {
			_log.error("Erreur sur le traitement du notify", e);
		}
	}

	private void annonceDescenteInterdite(int canal) throws Exception {
		Point arret = _avm.getModel().getDernierPoint();
		if (arret != null) {
			List messages = getTranslatedAnnoncement(DESCENTE_INTERDITE);
			messages.add(0, getMP3Filename(arret.getNomReduitGroupePoint()));
			annonce(messages, canal);
		}
	}

	private void annonceMonteeInterdite(int canal) throws Exception {
		Point arret = _avm.getModel().getDernierPoint();
		if (arret != null) {
			List messages = getTranslatedAnnoncement(MONTEE_INTERDITE);
			messages.add(0, getMP3Filename(arret.getNomReduitGroupePoint()));
			annonce(messages, canal);
		}
	}

	protected void annonceDestination(int destinataire) throws Exception {
		Course course = _avm.getModel().getCourse();
		if (course != null) {
			int nbArret = course.getNombrePoint();
			Point dernierArret = course.getPointAvecRang(nbArret);
			List messages = getTranslatedAnnoncement(EN_DIRECTION_DE);
			messages.add(getMP3Filename(dernierArret.getNomReduitGroupePoint()));
			annonce(messages, destinataire);
		}
	}

	protected void annonceArret(int destinataire) throws Exception {
		Point arret = _avm.getModel().getDernierPoint();
		if (arret != null) {
			String name = getMP3Filename(arret.getNomReduitGroupePoint());
			List messages = new ArrayList();
			messages.add(name);
			annonce(messages, destinataire);
		}
	}

	private List getTranslatedAnnoncement(String template) throws Exception {
		List items = null;
		for (int i = 0; i < languages.length; i++) {
			StringBuffer lang = new StringBuffer();
			if (!languages[i].equals("fr")) {
				lang.append(languages[i]);
				lang.append("_");

			}

			lang.append(template);
			String mp3 = getMP3Filename(lang.toString());
			File file = new File(mp3);
			if (file.exists()) {
				if (items == null) {
					items = new ArrayList();
				}
				items.add(mp3);
			}
			if (!file.exists()) {
				_log.warn("File " + file.getAbsolutePath() + " does not exists");
			}

		}

		if (items == null) {
			throw new Exception("No language mp3 file found for '" + template
					+ "' ");
		}

		return items;
	}

	protected void annonceLigne(int destinataire) throws Exception {
		if (_avm.getModel().getCourse() != null) {
			int lgnIdu = _avm.getModel().getCourse().getLigneIdu();
			List messages = getTranslatedAnnoncement(LIGNE);
			messages.add(getMP3Filename("ligne" + lgnIdu));
			annonce(messages, destinataire);
		}
	}

	protected void annonceProchainArret(int destinataire) throws Exception {
		Point prochainArret = _avm.getModel().getProchainPoint();
		if (prochainArret != null) {

			int itl = prochainArret.getItl();

			// -- cas normal ( pas d'interdiction) ou interdiction en montée
			_log.info("ITL : non => cas normal");

			List messages = getTranslatedAnnoncement(PROCHAIN);
			messages.add(getMP3Filename(prochainArret.getNomReduitGroupePoint()));

			if (itl == Point.ITL_NO_DOWN) {
				_log.info("ITL : descente interdite");
				List descenteInterditeMessages = getTranslatedAnnoncement(DESCENTE_INTERDITE);
				messages.addAll(descenteInterditeMessages);
			}
			annonce(messages, destinataire);
		}
	}

	protected void onMessageConducteur(MessageText message) {
		List messages = new ArrayList();
		messages.add(CONDUCTEUR_RECEPTION_MESSAGE);
		try {
			annonceConducteur(messages);
		} catch (Exception e) {
			_log.error("Erreur annonce conducteur : " + e.getMessage());
		}
	}

	protected void onMessageAlerte(Alarm alarm) {
		_log.info(alarm);
		if (alarm.getKey() == null) {
			return;
		}
		if (alarm.getKey().equals("defmat")) {
			if (_defmatAlarm == false && alarm.isStatus()) {
				List messages = new ArrayList();
				messages.add(CONDUCTEUR_ALARM_MATRICULE);
				try {
					annonceConducteur(messages);
				} catch (Exception e) {
					_log.error("Erreur annonce conducteur : " + e.getMessage());
				}
			} else {
				_defmatAlarm = false;
			}
		} else if (alarm.getKey().equals("speed")) {
			if (_speedAlarm == false && alarm.isStatus()) {
				List messages = new ArrayList();
				messages.add(CONDUCTEUR_ALARM_SPEED);
				try {
					annonceConducteur(messages);
				} catch (Exception e) {
					_log.error("Erreur annonce conducteur : " + e.getMessage());
				}
			} else {
				_speedAlarm = false;
			}
		}
	}

	public void annonce(List messages, int canal) throws Exception {
		switch (canal) {
		case CONDUCTEUR:
			annonceConducteur(messages);
			break;
		case VOYAGEUR_INTERIEUR:
			annonceVoyageurInterieur(messages);
			break;
		case VOYAGEUR_EXTERIEUR:
			annonceVoyageurExterieur(messages);
			break;

		default:
			_log.error("Canal :" + canal + " inconnu.");
		}
	}

	private void play(List messages) {
		boolean check = true;
		Iterator iter = messages.iterator();
		while (iter.hasNext()) {
			String msg = (String) iter.next();
			if (msg != null) {
				File fd = new File(msg);
				if (fd.exists() == false) {
					_log.error("File " + msg + " not found!");
					check = false;
					break;
				}
			}
		}

		if (check) {
			iter = messages.iterator();
			while (iter.hasNext()) {
				String msg = (String) iter.next();
				if (msg != null) {
					play(msg);
				}
			}
		}

	}

	private void annonceConducteur(List messages) throws Exception {
		_log.debug("Annonce Conducteur...");
		modeConducteur();
		play(messages);
		modeDefaut();
		_log.debug("Annonce Conducteur ok");

	}

	private void annonceVoyageurInterieur(List messages) throws Exception {
		_log.debug("Annonce Voyageur interieur...");
		modeVoyageurInterieur();
		play(messages);
		modeDefaut();
		_log.debug("Annonce Voyageur ok");

	}

	private void annonceVoyageurExterieur(List messages) throws Exception {

		_log.debug("Annonce Voyageur exterieur...");
		modeVoyageurExterieur();
		play(messages);
		modeDefaut();
		_log.debug("Annonce Voyageur ok");

	}

	private void activateDigitalIO(String mode) throws Exception {
		Properties props = _config.getProperty(mode);
		if (props != null) {

			if (_log.isDebugEnabled()) {
				_log.debug("activate mode " + mode + ": "
						+ props.getProperty("do0") + " "
						+ props.getProperty("do1") + " "
						+ props.getProperty("do2"));
			}
			if (_ioAudioVoyageurInterieur != null) {
				setIOVariableValue(_ioAudioVoyageurInterieur, props
						.getProperty("do0").equals("1"));
			} else {
				throw new Exception(
						"Variable : ioAudioVoyageurInterieur not set");
			}

			if (_audioConducteur != null) {
				setIOVariableValue(_audioConducteur, props.getProperty("do1")
						.equals("1"));
			} else {
				throw new Exception("Variable : ioAudioConducteur not set");
			}

			if (_ioAudioVoyageurExterieur != null) {
				setIOVariableValue(_ioAudioVoyageurExterieur, props
						.getProperty("do2").equals("1"));
			} else {
				throw new Exception(
						"Variable : ioAudioVoyageurExterieur not set");
			}
		} else {
			_log.error("Erreur : mode " + mode + " inconnu.");
		}
	}

	private void sleepBeforePlay() {
		if (_log.isDebugEnabled()) {
			_log.debug("Sleep before play : " + _config.getSleepBeforePlay());
		}
		try {
			Thread.sleep(_config.getSleepBeforePlay());
		} catch (InterruptedException e) {
		}
	}

	private void modeVoyageurInterieur() throws Exception {
		if (_sound != null) {
			_sound.configure(CONFIGURATION_VOYAGEUR_INTERIEUR);
		}
		activateDigitalIO(CONFIGURATION_VOYAGEUR_INTERIEUR);
		sleepBeforePlay();
	}

	private void modeVoyageurExterieur() throws Exception {
		if (_sound != null) {
			_sound.configure(CONFIGURATION_VOYAGEUR_EXTERIEUR);
		}
		activateDigitalIO(CONFIGURATION_VOYAGEUR_EXTERIEUR);
		sleepBeforePlay();
	}

	private void modeDefaut() throws Exception {
		if (_sound != null) {
			_sound.configure(CONFIGURATION_DEFAUT);
		}
		activateDigitalIO(CONFIGURATION_DEFAUT);
	}

	private void modeConducteur() throws Exception {
		if (_sound != null) {
			_sound.configure(CONFIGURATION_CONDUCTEUR);
		}
		activateDigitalIO(CONFIGURATION_CONDUCTEUR);
	}

	private void play(String name) {
		try {
			if (_player != null) {
				_log.debug("playing " + name);

				_player.open(name);
				_player.play();
				_player.close();
			} else {
				_log.warn("Player service not available!");
			}
		} catch (RuntimeException e) {
			_log.error("RuntimeException : " + e.getMessage());
		}
	}

	private void setIOVariableValue(Variable v, boolean state) throws Exception {
		int val = state ? 1 : 0;
		try {
			v.setValue(new Measurement(val));
		} catch (Throwable t) {
			throw new Exception("Exception when setting variable '"
					+ v.getName() + "' to " + val);
		}
	}

	public String getMP3Filename(String arret) {
		Object[] arguments = { System.getProperty("org.avm.home") };
		String text = MessageFormat.format(_config.getFileName(), arguments);
		return text + "/" + arret.toLowerCase() + ".mp3";
	}
}
