package org.avm.hmi.swt.management;

import org.eclipse.swt.widgets.Composite;

public class ItemGPRS extends AbstractItemNetwork {

	private static final String GPRS_ISCONNECTED = "gprs_isconnected";


	public ItemGPRS(Composite parent, int style) {
		super(parent, style);
		setText(Messages.getString("ItemNetwork.gprs-sam"));
		setServerTitle(Messages
				.getString("ItemNetwork.addr-server-sam"));
		setClientTitle(Messages.getString("ItemNetwork.addr-gprs"));
	}



} // @jve:decl-index=0:visual-constraint="10,10"

