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
	private MessengerContext _context;
	private Avm _avm;

	public boolean execute(Context context) throws Exception {
		_context = (MessengerContext) context;
		if (_context == null)
			return true;
		Message message = (Message) _context.getMessage();
		if (message == null)
			return true;

		ComponentContext cc = _context.getComponentContext();
		_avm = (Avm) cc.locateService("avm");

		if (_avm != null) {
			AvmModel model = _avm.getModel();

			// message
			Entete entete = message.getEntete();
			entete.setVersion((model != null) ? model.getDatasourceVersion()
					: 0);
			ChampsOptionnels options = entete.getChamps();

			// service
			Service service = entete.getService();
			options.setService(1);
			if (service == null) {
				entete.setService(new Service());
				service = entete.getService();
				int conducteur = (model.getAuthentification() != null) ? model
						.getAuthentification().getMatricule() : 0;
				service.setConducteur(conducteur);
				int course = (_avm.getModel().getCourse() != null) ? model
						.getCourse().getIdu() : 0;
				service.setCourse(course);
				int serviceAgent = (_avm.getModel().getServiceAgent() != null) ? model
						.getServiceAgent().getIdU()
						: 0;
				service.setServiceAgent(serviceAgent);
			}

			// progression
			Progression progression = entete.getProgression();
			if (progression == null && options.getProgression() == 1) {
				int avanceretard = model.getAvanceRetard();
				progression = new Progression();
				progression.setRetard(avanceretard);
				progression.setDeviation(model.isHorsItineraire() ? 1 : 0);
				Point point = model.getDernierPoint();
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
			Entete entete = message.getEntete();
			entete.setVersion(0);
			ChampsOptionnels options = entete.getChamps();
		
			// service
			Service service = entete.getService();
			if (service == null && options.getService() == 1) {
				options.setService(0);
			}

			// progression
			Progression progression = entete.getProgression();
			if (progression == null && options.getProgression() == 1) {
				options.setProgression(0);
			}
		}

		return false;
	}

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {
		protected Command createCommand() {
			return new ServiceCommand();
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CommandFactory factory = new DefaultCommandFactory();
		DefaultCommandFactory.factories.put(ServiceCommand.class.getName(),
				factory);
	}
}
