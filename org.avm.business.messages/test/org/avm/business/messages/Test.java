package org.avm.business.messages;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.avm.business.protocol.phoebus.Date;
import org.avm.business.protocol.phoebus.Heure;
import org.avm.business.protocol.phoebus.IntArray;
import org.avm.business.protocol.phoebus.MessageText;
import org.avm.business.protocol.phoebus.Programmation;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ProducerManager;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;

public class Test extends MockObjectTestCase {

	private Mock mockMessageConfig;
	private Mock mockProducerManager;
	private MessagesImpl msgService;

	protected void setUp() throws Exception {
		Logger.getRoot().removeAllAppenders();
		Logger root = Logger.getRoot();

		root.addAppender(new ConsoleAppender(new PatternLayout()));
		root.setPriority(Priority.DEBUG);
		
		mockMessageConfig = mock(MessagesConfig.class);
		mockProducerManager = mock(ProducerManager.class);

		msgService = new MessagesImpl();

	}

	public MessageText createMessage(int type, Date debut, Date fin,
			Heure hdebut, Heure hfin, int[] affectation) {
		MessageText message = new MessageText();
		message.setType(type);
		message.getEntete().getChamps().setprogrammation(1);
		message.getEntete().setProgrammation(new Programmation());
		message.getEntete().getProgrammation().setDateDebut(debut);
		message.getEntete().getProgrammation().setDateFin(fin);
		message.getEntete().getProgrammation().setHeureDebut(hdebut);
		message.getEntete().getProgrammation().setHeureFin(hfin);
		message.setAffectation(new IntArray(affectation));

		return message;
	}

	public void testVoyageurAffecteADeuxLignes() {

		mockMessageConfig.expects(once()).method("getMessages").withNoArguments();

		msgService.configure((Config) mockMessageConfig.proxy());
		msgService.setProducer((ProducerManager) mockProducerManager.proxy());

		mockProducerManager.expects(once()).method("publish").with(ANYTHING);

		Constraint[] parameters = { ANYTHING, eq("2012-04-02 00:00:00"),
				eq("2012-04-02 23:59:00"), eq("LMMJVSD"), eq(1), eq("L2;L1;"),
				eq("msgtest"), eq(0), eq(false) };

		mockMessageConfig.expects(once()).method("addMessage").with(parameters);

		MessageText message = createMessage(1, new Date(22, 4, 2), new Date(22, 4, 2), new Heure(0, 0, 0), new Heure(23, 59, 0), new int[] { 1, 2 });

		message.setMessage("msgtest");
		System.out.println("Message:" + message);
		msgService.notify(message);
	}
	
	public void testVoyageurAffecteAToutesLesLignes() {

		mockMessageConfig.expects(once()).method("getMessages").withNoArguments();

		msgService.configure((Config) mockMessageConfig.proxy());
		msgService.setProducer((ProducerManager) mockProducerManager.proxy());

		mockProducerManager.expects(once()).method("publish").with(ANYTHING);

		Constraint[] parameters = { ANYTHING, eq("2012-04-02 00:00:00"),
				eq("2012-04-02 23:59:00"), eq("LMMJVSD"), eq(1), eq("*"),
				eq("msgtest"), eq(0), eq(false) };

		mockMessageConfig.expects(once()).method("addMessage").with(parameters);

		MessageText message = createMessage(1, new Date(22, 4, 2), new Date(22, 4, 2), new Heure(0, 0, 0), new Heure(23, 59, 0), new int[] {  });

		message.setMessage("msgtest");
		System.out.println("Message:" + message);
		msgService.notify(message);

	}
}
