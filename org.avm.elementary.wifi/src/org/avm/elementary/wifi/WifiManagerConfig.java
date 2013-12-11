package org.avm.elementary.wifi;

import org.avm.elementary.common.Config;

public interface WifiManagerConfig extends Config {

	public static String TAG_BALISE_LIST = "balise-list";

	public static String TAG_BALISE_ATTR = "balise-attr-id";

	public static String DEFAULT_BALISE_LIST = "";

	public static String DEFAULT_BALISE_ATTR = "50";

	public static String TAG_DISCONNECT_TIMEOUT = "disconnect.timeout";

	public static String DEFAULT_DISCONNECT_TIMEOUT = "60";

	public abstract String getBaliseList();

	public abstract String getBaliseAttr();

	public abstract void setBaliseList(String list);

	public abstract void setBaliseAttr(String list);

	public abstract String getDisconnectTimeout();

	public abstract void setDisconnectTimeout(String timeout);
}