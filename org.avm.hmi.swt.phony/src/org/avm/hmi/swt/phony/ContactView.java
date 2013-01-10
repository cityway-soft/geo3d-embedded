package org.avm.hmi.swt.phony;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public abstract class ContactView extends Composite {
	
	protected  CallAnswerSelector _selector;
	public ContactView(Composite arg0, int arg1) {
		super(arg0, arg1);
		GridLayout layout = new GridLayout();
		this.setLayout(layout);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		this.setLayoutData(gridData);
	}
	
	public void setModeSelector(CallAnswerSelector selector){
		_selector = selector;
	}
		
	abstract void update(ContactModel model);
	abstract void ringing(String phonenumber);
	abstract void hangup();
	abstract void online();
	abstract void setPhony(Phony phony);
	abstract void dialing();
}
