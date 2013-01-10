package org.avm.elementary.common;

import java.io.IOException;
import java.util.Date;

public interface DataDeployer {

	public static final String URL_CONNECTION = "url.connection";

	public static final String VDR_NOM = "vdr.nom";

	public static final String VDR_ID = "vdr.id";

	public static final String VDR_DATE_EXP = "vdr.date.exp";

	public static final String JEX_DATE_DEB = "jex.date.deb";

	public static final String JEX_DATE_FIN = "jex.date.fin";

	public String getVdrId();

	public String getVdrNom();

	public Date getVdrDateExp();

	public Date getJexDateDeb();

	public Date getJexDateFin();

	public boolean isDeployed(String rootPath);

	public void deploy(String rootPath) throws IOException;

	public void undeploy(String path);

}
