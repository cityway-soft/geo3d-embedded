package org.avm.elementary.can.generator;

import java.net.URL;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.avm.elementary.can.generator.CAN.CANVisitor;
import org.avm.elementary.can.generator.PGN.PGNVisitor;
import org.xml.sax.InputSource;

public abstract class Generator {

	private Logger log = Logger.getLogger(Generator.class);

	protected String _name;

	protected String _target;

	private static Parser parser;

	private static Visitor visitor;

	public Generator() {
		visitor = new Visitor();
		parser = new Parser();
	}

	void generate(InputSource spec, URL rules, String target, String name) {
		_target = target;
		_name = name;
		CAN can = parser.parse(spec, rules);
		can.accept(visitor);
	}

	protected abstract void beginCAN(CAN can);

	protected abstract void endCAN(CAN can);

	protected abstract void beginPGN(PGN pgn);

	protected abstract void endPGN(PGN pgn);

	protected abstract void beginSPN(SPN spn);

	protected abstract void endSPN(SPN spn);

	class Visitor implements CANVisitor, PGNVisitor {

		public void visit(CAN can) {
			beginCAN(can);
			for (Iterator iter = can.iterator(); iter.hasNext();) {
				PGN pgn = (PGN) iter.next();
				beginPGN(pgn);
				pgn.accept(this);
				endPGN(pgn);
			}
			endCAN(can);
		}

		public void visit(PGN pgn) {
			for (Iterator iter = pgn.iterator(); iter.hasNext();) {
				SPN spn = (SPN) iter.next();
				beginSPN(spn);
				endSPN(spn);
			}
		}

	}

}