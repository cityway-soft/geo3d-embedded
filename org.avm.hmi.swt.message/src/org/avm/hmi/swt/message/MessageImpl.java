package org.avm.hmi.swt.message;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.business.messages.Messages;
import org.avm.business.messages.MessagesInjector;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.osgi.util.measurement.State;

import EDU.oswego.cs.dl.util.concurrent.BoundedChannel;
import EDU.oswego.cs.dl.util.concurrent.BoundedLinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Takable;

public class MessageImpl implements MessageIhm, ManageableService,
		ConsumerService, UserSessionServiceInjector,
		ConfigurableService, MessengerInjector, MessagesInjector {
	protected static final String NAME = "Messages";

	private MessageIhmImpl _ihm;

	private Desktop _desktop;

	private Logger _log;

	private Display _display;

	private ProducerManager _producer;

	private UserSessionService _session;

	private boolean _logged = false;

	private Config _config;

	private Messenger _messenger;

	private Messages _messages;

	private BoundedChannel _queue;

	private Scheduler _scheduler = null;

	private Hashtable _hashMessage = new Hashtable();

	private boolean _initialized;

	public MessageImpl() {
		_log = Logger.getInstance(this.getClass());
		_queue = new BoundedLinkedQueue(10);
		_scheduler = new Scheduler();
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
		if (_desktop != null) {
			_display = _desktop.getDisplay();
		}
	}

	public void start() {
		loggedOn();
	}

	public void stop() {
		loggedOut();
	}

	public String getProducerPID() {
		return MessageIhm.class.getName();
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(Messages.class.getName())) {
				try {
					_log.debug("notify state:" + o); //$NON-NLS-1$
					populate();
				} catch (Exception e) {
					_log.error("Error:" + e.toString());
				}
			} else if (state.getName().equals(
					UserSessionService.class.getName())) {
				if (state.getValue() == UserSessionService.AUTHENTICATED) {
					loggedOn();
				} else {
					loggedOut();
				}
			}

		} else if (o instanceof Alarm) {
			Alarm a = (Alarm) o;
			_log.info("#reception alarm :" + a); //$NON-NLS-1$
			if (a.getKey() == null){
				return;
			}
			if (a.getKey().equals("defmat") && a.isStatus() == true) { //$NON-NLS-1$
				MessageBox.setMessage(org.avm.hmi.swt.message.Messages
						.getString("Message.Alarm"), //$NON-NLS-1$
						org.avm.hmi.swt.message.Messages
								.getString("Message.demande-saisie-matricule"), //$NON-NLS-1$
						MessageBox.MESSAGE_ALARM, SWT.CENTER);
			} else if (a.getKey().equals("speed") && a.isStatus() == true) { //$NON-NLS-1$
				MessageBox
						.setMessage(
								org.avm.hmi.swt.message.Messages
										.getString("Message.Alarm"), org.avm.hmi.swt.message.Messages.getString("Message.recommendation-vitesse"), //$NON-NLS-1$ //$NON-NLS-2$
								MessageBox.MESSAGE_WARNING, SWT.CENTER);
			}
		}

	}

	private void populate() {
		if (_messages != null) {
			if (_ihm != null) {
				_ihm.clearMessages();
			}

			Collection messages = _messages.getMessages(Messages.CONDUCTEUR,
					null);
			Iterator iter = messages.iterator();
			int i = 0;
			try {
				while (_queue.poll(0) != null)
					;
			} catch (InterruptedException e1) {
			}
			while (iter.hasNext()) {
				Properties props = (Properties) iter.next();
				boolean showMessage = true;
				String id = (String) props.get(Messages.ID);
				boolean exist = ((String) _hashMessage.get(id) != null);

				String lu = (String) props.get(Messages.FIN);
				//_log.info("--lu:" + lu);
				showMessage = (lu == null || lu.trim().equals(""));
				//_log.info("--showMessage:" + showMessage);
				if (!exist) {
					_hashMessage.put(id, id);
				}
				if (_ihm != null) {
					_ihm.addMessage(props);
					
				}

				if (showMessage) {
					try {
						_queue.put(props);
					} catch (InterruptedException e) {
					}
					i++;
				}
			}
			_initialized=true;
			if (i > 0) {
				_scheduler.execute(new Consumer(_queue));
			}
		}// message != null
	}

	public void loggedOn() {
		if (_session != null
				&& (_session.hasRole("conducteur") || _session.hasRole("admin"))
				&& !_logged) {
			_display.asyncExec(new Runnable() {
				public void run() {
					if (_ihm == null || _ihm.isDisposed()) {
						_ihm = new MessageIhmImpl(_desktop.getMainPanel(),
								SWT.NONE);
						_ihm.setLogger(_log);
						_ihm.setMessenger(_messenger);
						_ihm.configure(_config);
						_desktop.addTabItem(NAME, _ihm);
						populate();
					}
				}
			});
			_logged = true;
		}
	}

	public void loggedOut() {
		if (_logged) {
			_display.asyncExec(new Runnable() {
				public void run() {
					if (_ihm != null && _ihm.isDisposed() == false) {
						_ihm.dispose();
						_ihm = null;
						if (_desktop != null) {
							_desktop.removeTabItem(NAME);
						}
						
					}
				}
			});
			_logged = false;
		}
	}

	public void setUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void configure(Config config) {
		_config = config;
	}

	public void setMessenger(Messenger messenger) {
		_messenger = messenger;
		if (_ihm != null) {
			_ihm.setMessenger(messenger);
		}
	}

	public void unsetMessenger(Messenger messenger) {
		_messenger = messenger;
		if (_ihm != null) {
			_ihm.unsetMessenger(messenger);
		}
	}

	public void setMessages(Messages messages) {
		_messages = messages;
		populate();
	}

	public void unsetMessages(Messages messages) {
		_messages = messages;
	}

	class Consumer implements Runnable {
		private Logger _log = Logger.getInstance(this.getClass());
		final Takable _channel;

		Consumer(Takable channel) {
			_channel = channel;
		}

		public void run() {
			try {
				Properties message = null;
				final Object _lock = new Object();
				message = (Properties) _channel.poll(4000);
				while (message != null) {
					final String msgId = message.getProperty(Messages.ID);
					String msg = message.getProperty(Messages.MESSAGE);
					_log.debug("Reception message texte :" + msg); //$NON-NLS-1$
					SelectionListener listener = new SelectionAdapter() {
						public void widgetSelected(SelectionEvent arg0) {
							if (_messages != null) {
								_messages.acquittement(msgId);
							}
							synchronized (_lock) {
								_lock.notify();
							}

						}
					};

					int type = Integer.parseInt(message
							.getProperty(Messages.TYPE));

					String titre = (type == Messages.VOYAGEUR) ? org.avm.hmi.swt.message.Messages
							.getString("Message.message-voyageurs") : org.avm.hmi.swt.message.Messages.getString("Message.message-conducteur"); //$NON-NLS-1$ //$NON-NLS-2$
					int style = (type == Messages.VOYAGEUR) ? MessageBox.MESSAGE_NORMAL
							: MessageBox.MESSAGE_WARNING;
					MessageBox.setMessage(titre, "\n" + msg, style, SWT.CENTER,
							listener);
					synchronized (_lock) {
						_lock.wait();
					}
					message = (Properties) _channel.poll(0);
				}

			} catch (InterruptedException e) {
				_log.error("[DSU] consumer InterruptedException  : " //$NON-NLS-1$
						+ e.getMessage());
			}
		}
	}

	public void addPredefinedMessag(String title, String message) {
		if (_ihm != null) {
			_ihm.addPredefinedMessage(title, message);
		}
	}
	


}