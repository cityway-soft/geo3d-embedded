package org.avm.elementary.command.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Point;
import org.avm.business.protocol.phoebus.ChampsOptionnels;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.Message;
import org.avm.business.protocol.phoebus.Progression;
import org.avm.business.protocol.phoebus.Service;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.command.impl.CommandFactory;
import org.osgi.service.component.ComponentContext;

public class ServiceCommand implements org.apache.commons.chain.Command {

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {

		protected Command createCommand() {

			return new ServiceCommand();
		}
	}

	private MessengerContext _context;

	private Avm _avm;
	/** Referencement de la fabrique de classe */
	static {
		final CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(ServiceCommand.class.getName(), factory);
	}

	private void init(Service service, AvmModel model) {
		if (model.getAuthentification() != null) {
			int conducteur = model.getAuthentification().getMatricule();
			service.setConducteur(conducteur);
		} else {
			service.setConducteur(0);
		}

		if (model.getCourse() != null) {
			service.setCourse(model.getCourse().getIdu());
		} else {
			service.setCourse(0);
		}
	}

	public boolean execute(final Context context) throws Exception {

		this._context = (MessengerContext) context;
		if (this._context == null) {
			return true;
		}
		final Object msg = this._context.getMessage();
		if (msg == null) {
			return true;
		}
		if (msg instanceof Message) {
			final Message message = (Message) msg;

			final ComponentContext cc = this._context.getComponentContext();
			this._avm = (Avm) cc.locateService("avm");
			if (this._avm != null) {
				final AvmModel model = this._avm.getModel();
				// message
				final Entete entete = message.getEntete();
				entete.setVersion(0);
				final ChampsOptionnels options = entete.getChamps();
				// service
				options.setService(1);
				Service service = entete.getService();
				if (service == null) {
					service = new Service();
					entete.setService(service);
					init(service, _avm.getModel());
				}

				// progression
				Progression progression = entete.getProgression();
				if ((progression == null) && (options.getProgression() == 1)) {
					final int avanceretard = model.getAvanceRetard();
					progression = new Progression();
					progression.setRetard(avanceretard);
					progression.setDeviation(model.isHorsItineraire() ? 1 : 0);
					final Point point = model.getDernierPoint();
					if (point != null) {
						progression.setRangDernierArret(point.getRang());
						progression.setIduDernierArret(point.getIdu());
					} else {
						progression.setRangDernierArret(0);
						progression.setIduDernierArret(0);
					}
					entete.setProgression(progression);
				}
			} else {
				// message
				final Entete entete = message.getEntete();
				entete.setVersion(0);
				final ChampsOptionnels options = entete.getChamps();
				// service
				final Service service = entete.getService();
				if ((service == null) && (options.getService() == 1)) {
					options.setService(0);
				}
				// progression
				final Progression progression = entete.getProgression();
				if ((progression == null) && (options.getProgression() == 1)) {
					options.setProgression(0);
				}
			}
		}
		return false;
	}
}
