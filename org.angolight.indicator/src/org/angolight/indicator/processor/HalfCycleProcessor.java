package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.halfcycle.Functions;
import org.angolight.halfcycle.HalfCycle;
import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public class HalfCycleProcessor extends Processor {

	private static String TARGET = HalfCycle.class.getName();

	// config

	// measure
	public static final String NOTATION_SUM = "notation-sum";

	private Measure _ns;

	protected HalfCycleProcessor(IndicatorService owner,
			IndicatorConfig config, ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		initialize();
	}

	private void initialize() {
		_ns = new Measure(NOTATION_SUM, Unit.unity);
		_owner.add(_ns);
	}

	public void update(Object o) {
		if (o instanceof HalfCycle) {
			HalfCycle halfCycle = (HalfCycle) o;
			_ns.add(notation(halfCycle));
		}
	}

	public void reset() {
		_ns.setValue(0);
	}

	public Map evaluate() {
		Map result = new HashMap();
		result.put(_ns.getName(), new Measure(_ns));
		return result;
	}

	public Map merge(Map measures) {
		return evaluate();
	}

	private double notation(HalfCycle cycle) {
		double h_high = cycle.getMSFAccBetweenH1H2();

		double deltav = cycle.getCycleDeltaSpeed();
		double deltav_grand = Functions.FuzMemShip(deltav, 45, 55);
		double deltav_petit = Math.min(Functions.FuzMemShip(deltav, 3, 10),
				1 - Functions.FuzMemShip(deltav, 10, 40));

		double vmin = cycle.getCycleVMin();
		double vm_moy_grand = Functions.FuzMemShip(vmin, 20, 30);
		double vm_grand = Functions.FuzMemShip(vmin, 30, 40);

		double cycleAccMax = cycle.getCycleAccMax();

		double acc_neg_grand = Functions.FuzMemShip(-cycleAccMax, 1.3, 1.5);
		double acc_pos_grand = Functions.FuzMemShip(cycleAccMax, 1, 1.5);

		double indice1 = h_high * 0.60;
		double indice2 = Math.min(deltav_petit, vm_grand) * 0.10;
		double indice3 = Math.min(deltav_grand, vm_moy_grand) * 0.10;
		double indice5 = Math.max(acc_neg_grand, acc_pos_grand) * 0.20;

		double indice = indice1 + indice2 + indice3 + indice5;

		return indice;
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new HalfCycleProcessor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
