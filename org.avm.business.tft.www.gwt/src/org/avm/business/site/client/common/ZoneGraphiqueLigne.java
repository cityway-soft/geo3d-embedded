package org.avm.business.site.client.common;

public class ZoneGraphiqueLigne extends AbstractZone {
	private final String NAME = "ZoneGraphiqueLigne";

	int crsIdu = -1;

	public ZoneGraphiqueLigne(final int delay) {
		super(delay);
	}

	@Override
	public void setData(final AVMModel model) {
		try {
			if (model.getCourse() != null) {
				final int idu = model.getCourse().getCRS_IDU();
				Page7.clearTitle();
				if (crsIdu != idu) {
					ExchangeControler.debug("[ZoneGraphiqueLigne] crs=" + idu
							+ " != previous=" + crsIdu);
					Page7.reset();

					final String style = "ligne"
							+ model.getCourse().getLGN_IDU();
					Page7.setLineStyle(style);
					for (int i = 0; i < model.getCourse().getPOINTS().length; i++) {
						final int id = model.getCourse().getPOINTS()[i]
								.getPNT_ID();
						final String name = model.getCourse().getPOINTS()[i]
								.getPNT_NOM();
						final int rang = model.getCourse().getPOINTS()[i]
								.getHOD_RANG();
						Page7.addStop(id, name, rang);
					}
					crsIdu = idu;
				}
				if (model.getArret() != null) {

					Page7.setCurrent(
							model.getArret().getPNT_ID(),
							model.getState() == Constantes.STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
							model.getArret().getHOD_RANG());
				}
			} else {
				Page7.reset();
			}
		} catch (final Throwable t) {
			ExchangeControler.error("Error setData ZoneGraphline : "
					+ t.getMessage());
		}

	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void activate(final boolean b) {
		setVisible(Page7.NAME, b);
	}
}